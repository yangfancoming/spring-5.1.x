

package org.springframework.web.context;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;
import javax.servlet.ServletContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Performs the actual initialization work for the root application context. Called by {@link ContextLoaderListener}.
 *
 * Looks for a {@link #CONTEXT_CLASS_PARAM "contextClass"} parameter at the {@code web.xml} context-param level to specify the context class type,
 * falling back to {@link org.springframework.web.context.support.XmlWebApplicationContext} if not found.
 * With the default ContextLoader implementation, any context class specified needs to implement the {@link ConfigurableWebApplicationContext} interface.
 * Processes a {@link #CONFIG_LOCATION_PARAM "contextConfigLocation"} context-param and passes its value to the context instance,
 * parsing it into potentially multiple file paths which can be separated by any number of commas and spaces,
 * e.g."WEB-INF/applicationContext1.xml, WEB-INF/applicationContext2.xml".
 * Ant-style path patterns are supported as well, e.g. "WEB-INF/*Context.xml,WEB-INF/spring*.xml" or "WEB-INF/&#42;&#42;/*Context.xml".
 * If not explicitly specified, the context implementation is supposed to use a default location (with XmlWebApplicationContext: "/WEB-INF/applicationContext.xml").
 *
 * Note: In case of multiple config locations, later bean definitions will override ones defined in previously loaded files, at least when using one of Spring's default ApplicationContext implementations.
 * This can be leveraged to deliberately override certain bean definitions via an extra XML file.
 *
 * Above and beyond loading the root application context, this class can optionally load or obtain and hook up a shared parent context to the root application context.
 * See the {@link #loadParentContext(ServletContext)} method for more information.
 *
 * As of Spring 3.1, {@code ContextLoader} supports injecting the root web application context via the {@link #ContextLoader(WebApplicationContext)}
 * constructor, allowing for programmatic configuration in Servlet 3.0+ environments.
 * See {@link org.springframework.web.WebApplicationInitializer} for usage examples.
 * @since 17.02.2003
 * @see ContextLoaderListener
 * @see ConfigurableWebApplicationContext
 * @see org.springframework.web.context.support.XmlWebApplicationContext
 */
public class ContextLoader {

	/**
	 * Config param for the root WebApplicationContext id,to be used as serialization id for the underlying BeanFactory: {@value}.
	 */
	public static final String CONTEXT_ID_PARAM = "contextId";

	/**
	 * Name of servlet context parameter (i.e., {@value}) that can specify the config location for the root context, falling back to the implementation's default otherwise.
	 * @see org.springframework.web.context.support.XmlWebApplicationContext#DEFAULT_CONFIG_LOCATION
	 */
	public static final String CONFIG_LOCATION_PARAM = "contextConfigLocation";

	/**
	 * Config param for the root WebApplicationContext implementation class to use: {@value}.
	 * @see #determineContextClass(ServletContext)
	 */
	public static final String CONTEXT_CLASS_PARAM = "contextClass";

	/**
	 * Config param for {@link ApplicationContextInitializer} classes to use  for initializing the root web application context: {@value}.
	 * @see #customizeContext(ServletContext, ConfigurableWebApplicationContext)
	 */
	public static final String CONTEXT_INITIALIZER_CLASSES_PARAM = "contextInitializerClasses";

	/**
	 * Config param for global {@link ApplicationContextInitializer} classes to use for initializing all web application contexts in the current application: {@value}.
	 * @see #customizeContext(ServletContext, ConfigurableWebApplicationContext)
	 */
	public static final String GLOBAL_INITIALIZER_CLASSES_PARAM = "globalInitializerClasses";

	// Any number of these characters are considered delimiters between  multiple values in a single init-param String value.
	private static final String INIT_PARAM_DELIMITERS = ",; \t\n";

	// Name of the class path resource (relative to the ContextLoader class) that defines ContextLoader's default strategy names.
	private static final String DEFAULT_STRATEGIES_PATH = "ContextLoader.properties";

	private static final Properties defaultStrategies;

	static {
		// 静态代码加载默认策略,即默认的web应用上下文
		// Load default strategy implementations from properties file.This is currently strictly internal and not meant to be customized  by application developers.
		try {
			ClassPathResource resource = new ClassPathResource(DEFAULT_STRATEGIES_PATH, ContextLoader.class);
			defaultStrategies = PropertiesLoaderUtils.loadProperties(resource);
		}catch (IOException ex) {
			throw new IllegalStateException("Could not load 'ContextLoader.properties': " + ex.getMessage());
		}
	}

	//  Map from (thread context) ClassLoader to corresponding 'current' WebApplicationContext.
	private static final Map<ClassLoader, WebApplicationContext> currentContextPerThread = new ConcurrentHashMap<>(1);

	// The 'current' WebApplicationContext, if the ContextLoader class is deployed in the web app ClassLoader itself.
	@Nullable
	private static volatile WebApplicationContext currentContext;

	// The root WebApplicationContext instance that this loader manages.
	@Nullable
	private WebApplicationContext context;

	/** Actual ApplicationContextInitializer instances to apply to the context. */
	private final List<ApplicationContextInitializer<ConfigurableApplicationContext>> contextInitializers = new ArrayList<>();

	/**
	 * Create a new {@code ContextLoader} that will create a web application context based on the "contextClass" and "contextConfigLocation" servlet context-params.
	 * See class-level documentation for details on default values for each.
	 * This constructor is typically used when declaring the {@code ContextLoaderListener} subclass as a {@code <listener>} within {@code web.xml},as a no-arg constructor is required.
	 * The created application context will be registered into the ServletContext under the attribute name {@link WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE}
	 * and subclasses are free to call the {@link #closeWebApplicationContext} method on container shutdown to close the application context.
	 * @see #ContextLoader(WebApplicationContext)
	 * @see #initWebApplicationContext(ServletContext)
	 * @see #closeWebApplicationContext(ServletContext)
	 */
	public ContextLoader() {
	}

	/**
	 * Create a new {@code ContextLoader} with the given application context.
	 * This constructor is useful in Servlet 3.0+ environments where instance-based registration of listeners is possible through the {@link ServletContext#addListener} API.
	 * The context may or may not yet be {@linkplain ConfigurableApplicationContext#refresh() refreshed}.
	 * If it (a) is an implementation of {@link ConfigurableWebApplicationContext} and (b) has <strong>not</strong> already been refreshed (the recommended approach),
	 * then the following will occur:
	 * <li>If the given context has not already been assigned an {@linkplain ConfigurableApplicationContext#setId id}, one will be assigned to it</li>
	 * <li>{@code ServletContext} and {@code ServletConfig} objects will be delegated to the application context</li>
	 * <li>{@link #customizeContext} will be called</li>
	 * <li>Any {@link ApplicationContextInitializer ApplicationContextInitializers} specified through the "contextInitializerClasses" init-param will be applied.</li>
	 * <li>{@link ConfigurableApplicationContext#refresh refresh()} will be called</li>
	 * If the context has already been refreshed or does not implement {@code ConfigurableWebApplicationContext},
	 * none of the above will occur under the assumption that the user has performed these actions (or not) per his or her specific needs.
	 * See {@link org.springframework.web.WebApplicationInitializer} for usage examples.
	 * In any case, the given application context will be registered into the
	 * ServletContext under the attribute name {@link WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE} and subclasses are
	 * free to call the {@link #closeWebApplicationContext} method on container shutdown to close the application context.
	 * @param context the application context to manage
	 * @see #initWebApplicationContext(ServletContext)
	 * @see #closeWebApplicationContext(ServletContext)
	 */
	public ContextLoader(WebApplicationContext context) {
		this.context = context;
	}

	/**
	 * Specify which {@link ApplicationContextInitializer} instances should be used  to initialize the application context used by this {@code ContextLoader}.
	 * @since 4.2
	 * @see #configureAndRefreshWebApplicationContext
	 * @see #customizeContext
	 */
	@SuppressWarnings("unchecked")
	public void setContextInitializers(@Nullable ApplicationContextInitializer<?>... initializers) {
		if (initializers != null) {
			for (ApplicationContextInitializer<?> initializer : initializers) {
				this.contextInitializers.add((ApplicationContextInitializer<ConfigurableApplicationContext>) initializer);
			}
		}
	}

	/**
	 * Initialize Spring's web application context for the given servlet context,using the application context provided at construction time,
	 * or creating a new one  according to the "{@link #CONTEXT_CLASS_PARAM contextClass}" and "{@link #CONFIG_LOCATION_PARAM contextConfigLocation}" context-params.
	 * @param servletContext current servlet context
	 * @return the new WebApplicationContext
	 * @see #ContextLoader(WebApplicationContext)
	 * @see #CONTEXT_CLASS_PARAM
	 * @see #CONFIG_LOCATION_PARAM
	 */
	public WebApplicationContext initWebApplicationContext(ServletContext servletContext) {
		/*
		 * 如果 ServletContext 中 ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE 属性值 不为空时，表明有其他监听器设置了这个属性。
		 * Spring 认为不能替换掉别的监听器设置的属性值，所以这里抛出异常。这样可以保证一个web容器中只会有一个web application context
		 */
		if (servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE) != null) {
			throw new IllegalStateException("Cannot initialize context because there is already a root application context present - check whether you have multiple ContextLoader* definitions in your web.xml!");
		}
		servletContext.log("Initializing Spring root WebApplicationContext");
		Log logger = LogFactory.getLog(ContextLoader.class);
		if (logger.isInfoEnabled()) logger.info("Root WebApplicationContext: initialization started");
		long startTime = System.currentTimeMillis();
		try {
			// Store context in local instance variable, to guarantee that it is available on ServletContext shutdown.
			if (this.context == null) {
				// 通过servlet配置创建一个WebApplicationContext对象 默认为 XmlWebApplicationContext
				this.context = createWebApplicationContext(servletContext);
			}
			// 这里在createWebApplicationContext()方法中会保证创建的WebApplicationContext本质上是ConfigurableWebApplicationContext类型的，因而这里进行类型判断的时候是能够进入if分支的
			if (this.context instanceof ConfigurableWebApplicationContext) {
				ConfigurableWebApplicationContext cwac = (ConfigurableWebApplicationContext) this.context;
				// 如果当前WebApplicationContext没有初始化过就对其进行初始化
				// 如果当前上下文环境未激活，那么其只能提供例如设置父上下文、设置上下文id等功能
				if (!cwac.isActive()) {
					// The context has not yet been refreshed -> provide services such as setting the parent context, setting the application context id, etc
					// 如果当前WebApplicationContext没有父ApplicationContext，则通过loadParentContext()方法加载一个，该方法实际上是一个空方法，
					// 这里提供出来只是为了方便用户进行容器属性的自定义，因为父容器的内容会继承到子容器中
					if (cwac.getParent() == null) {
						//  加载父 ApplicationContext，一般情况下，业务容器不会有父容器，除非进行配置
						// The context instance was injected without an explicit parent -> determine parent for root web application context, if any.
						ApplicationContext parent = loadParentContext(servletContext);
						cwac.setParent(parent);
					}
					// 对WebApplicationContext进行配置，并且调用其refresh()方法初始化配置文件中配置的Spring的各个组件
					// 2.配置并刷新 WebApplicationContext
					configureAndRefreshWebApplicationContext(cwac, servletContext);
				}
			}
			// 初始化完成之后将当前WebApplicationContext设置到ServletContext中
			// 设置 ApplicationContext 到 servletContext 中
			// 将当前上下文环境存储到ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE变量中
			servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, this.context);
			// 设置当前WebApplicationContext的类加载器
			ClassLoader ccl = Thread.currentThread().getContextClassLoader();
			if (ccl == ContextLoader.class.getClassLoader()) {
				currentContext = this.context;
			}else if (ccl != null) {
				currentContextPerThread.put(ccl, this.context);
			}
			if (logger.isInfoEnabled()) {
				long elapsedTime = System.currentTimeMillis() - startTime;
				logger.info("Root WebApplicationContext initialized in " + elapsedTime + " ms");
			}
			return this.context;
		}catch (RuntimeException | Error ex) {
			logger.error("Context initialization failed", ex);
			servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, ex);
			throw ex;
		}
	}

	/**
	 * Instantiate the root WebApplicationContext for this loader, either the default context class or a custom context class if specified.
	 * This implementation expects custom contexts to implement the {@link ConfigurableWebApplicationContext} interface.
	 * Can be overridden in subclasses.
	 * In addition, {@link #customizeContext} gets called prior to refreshing the context, allowing subclasses to perform custom modifications to the context.
	 * @param sc current servlet context
	 * @return the root WebApplicationContext
	 * @see ConfigurableWebApplicationContext
	 * 为当前类加载器实例化根WebApplicationContext,可以是默认上下文加载类或者自定义上下文加载类
	 */
	protected WebApplicationContext createWebApplicationContext(ServletContext sc) {
		// 读取配置文件创建web容器，默认类型为 XmlWebApplicationContext
		Class<?> contextClass = determineContextClass(sc);
		// 判断读取到的类是否实现了ConfigurableWebApplicationContext接口，如果没实现则抛出异常
		if (!ConfigurableWebApplicationContext.class.isAssignableFrom(contextClass)) {
			throw new ApplicationContextException("Custom context class [" + contextClass.getName() + "] is not of type [" + ConfigurableWebApplicationContext.class.getName() + "]");
		}
		// 通过反射实例化 WebApplicationContext接口实现类
		return (ConfigurableWebApplicationContext) BeanUtils.instantiateClass(contextClass);
	}

	/**
	 *  根据配置文件情况 来决定WebApplicationContext接口的实现类的全限定名称，如果没有自定义，则默认返回XmlWebApplicationContext类
	 * Return the WebApplicationContext implementation class to use, either the default XmlWebApplicationContext or a custom context class if specified.
	 * @param servletContext current servlet context
	 * @return the WebApplicationContext implementation class to use
	 * @see #CONTEXT_CLASS_PARAM
	 * @see org.springframework.web.context.support.XmlWebApplicationContext
	 * 这里读取配置文件的方式有两种：
	 * 	①读取web.xml中配置的 <context-param> 标签 其内部 <param-name> 为 contextClass 属性的值，如果存在则将其作为WebApplicationContext 容器；
	 *      <context-param>
	 *          <param-name>contextClass</param-name>
	 *          <param-value>org.springframework.web.context.support.MyWebApplicationContext</param-value>
	 *      </context-param>
	 *  ②读取Spring提供的 ContextLoader.properties 配置文件中配置的WebApplicationContext 容器。 （XmlWebApplicationContext）
	 */
	protected Class<?> determineContextClass(ServletContext servletContext) {
		// ①读取用户在web.xml中使用contextClass属性自定义的WebApplicationContext容器， 如果不为空，则直接反射创建
		String contextClassName = servletContext.getInitParameter(CONTEXT_CLASS_PARAM);
		if (contextClassName != null) {
			try {
				return ClassUtils.forName(contextClassName, ClassUtils.getDefaultClassLoader());
			}catch (ClassNotFoundException ex) {
				throw new ApplicationContextException("Failed to load custom context class [" + contextClassName + "]", ex);
			}
		}else {
			/**
			 * ②读取Spring提供的 ContextLoader.properties 配置文件
			 * defaultStrategies 再静态块中赋值： org.springframework.web.context.WebApplicationContext -> org.springframework.web.context.support.XmlWebApplicationContext
			*/
			contextClassName = defaultStrategies.getProperty(WebApplicationContext.class.getName());// org.springframework.web.context.WebApplicationContext
			try {
				return ClassUtils.forName(contextClassName, ContextLoader.class.getClassLoader());
			}catch (ClassNotFoundException ex) {
				throw new ApplicationContextException("Failed to load default context class [" + contextClassName + "]", ex);
			}
		}
	}

	protected void configureAndRefreshWebApplicationContext(ConfigurableWebApplicationContext wac, ServletContext sc) {
		// 判断当前WebApplicationContext是否具有统一的id，如果没有，首先会从web.xml中读取，
		// 具体的使用contextId属性进行制定，该属性的配置方式与上面的contextClass一致，如果没有，则通过默认规则声明一个
		if (ObjectUtils.identityToString(wac).equals(wac.getId())) {
			// 从 ServletContext 中获取用户配置的 contextId 属性
			// The application context id is still set to its original default value  -> assign a more useful id based on available information
			String idParam = sc.getInitParameter(CONTEXT_ID_PARAM);
			if (idParam != null) {
				// 设置容器 id  // 如果idParam已存在，wac id还是设置为原始值idParam
				wac.setId(idParam);
			}else {
				// 用户未配置 contextId，则设置一个默认的容器 id   // 根据可用信息分配一个更有用的ID
				// Generate default id...
				wac.setId(ConfigurableWebApplicationContext.APPLICATION_CONTEXT_ID_PREFIX + ObjectUtils.getDisplayString(sc.getContextPath()));
			}
		}
		// 获取web.xml中配置的contextConfigLocation属性值，这里也就是我们前面配置的
		// applicationContext.xml，在后面调用refresh()方法时会根据xml文件中的配置
		// 初始化Spring的各个组件 // 将web容器上下文对象记录在Spring上下文中
		wac.setServletContext(sc);
		// 获取 contextConfigLocation 配置  // 返回自定义Spring配置文件路径：如/WEB-INF/spring/applicationContext.xml
		String configLocationParam = sc.getInitParameter(CONFIG_LOCATION_PARAM);
		// 记录配置xml，可能有多个
		if (configLocationParam != null) wac.setConfigLocation(configLocationParam);
		// The wac environment's #initPropertySources will be called in any case when the context is refreshed;
		// do it eagerly here to ensure servlet property sources are in place for use in any post-processing or initialization that occurs below prior to #refresh
		// 获取当前Spring的运行环境，并且初始化其propertySources
		ConfigurableEnvironment env = wac.getEnvironment();
		if (env instanceof ConfigurableWebEnvironment) {
			((ConfigurableWebEnvironment) env).initPropertySources(sc, null);
		}
		// 这里customizeContext()方法是一个空方法，供给用户自定义实现ContextLoaderListener时
		// 对WebApplicationContext进行自定义
		customizeContext(sc, wac);
		// 这里refresh()方法用于读取上面声明的配置文件，并且初始化Spring的各个组件
		wac.refresh();
	}

	/**
	 * Customize the {@link ConfigurableWebApplicationContext} created by this ContextLoader after config locations have been supplied to the context but before the context is <em>refreshed</em>.
	 * The default implementation {@linkplain #determineContextInitializerClasses(ServletContext) determines} what (if any) context initializer classes have been specified through
	 * {@linkplain #CONTEXT_INITIALIZER_CLASSES_PARAM context init parameters} and {@linkplain ApplicationContextInitializer#initialize invokes each} with the given web application context.
	 * Any {@code ApplicationContextInitializers} implementing {@link org.springframework.core.Ordered Ordered} or marked with @{@link org.springframework.core.annotation.Order Order} will be sorted appropriately.
	 * @param sc the current servlet context
	 * @param wac the newly created application context
	 * @see #CONTEXT_INITIALIZER_CLASSES_PARAM
	 * @see ApplicationContextInitializer#initialize(ConfigurableApplicationContext)
	 */
	protected void customizeContext(ServletContext sc, ConfigurableWebApplicationContext wac) {
		List<Class<ApplicationContextInitializer<ConfigurableApplicationContext>>> initializerClasses = determineContextInitializerClasses(sc);
		for (Class<ApplicationContextInitializer<ConfigurableApplicationContext>> initializerClass : initializerClasses) {
			Class<?> initializerContextClass = GenericTypeResolver.resolveTypeArgument(initializerClass, ApplicationContextInitializer.class);
			if (initializerContextClass != null && !initializerContextClass.isInstance(wac)) {
				throw new ApplicationContextException(String.format(
						"Could not apply context initializer [%s] since its generic parameter [%s] is not assignable from the type of application context used by this " +
						"context loader: [%s]", initializerClass.getName(), initializerContextClass.getName(),wac.getClass().getName()));
			}
			this.contextInitializers.add(BeanUtils.instantiateClass(initializerClass));
		}
		AnnotationAwareOrderComparator.sort(this.contextInitializers);
		for (ApplicationContextInitializer<ConfigurableApplicationContext> initializer : this.contextInitializers) {
			initializer.initialize(wac);
		}
	}

	/**
	 * Return the {@link ApplicationContextInitializer} implementation classes to use if any have been specified by {@link #CONTEXT_INITIALIZER_CLASSES_PARAM}.
	 * @param servletContext current servlet context
	 * @see #CONTEXT_INITIALIZER_CLASSES_PARAM
	 */
	protected List<Class<ApplicationContextInitializer<ConfigurableApplicationContext>>> determineContextInitializerClasses(ServletContext servletContext) {
		List<Class<ApplicationContextInitializer<ConfigurableApplicationContext>>> classes = new ArrayList<>();
		String globalClassNames = servletContext.getInitParameter(GLOBAL_INITIALIZER_CLASSES_PARAM);
		if (globalClassNames != null) {
			for (String className : StringUtils.tokenizeToStringArray(globalClassNames, INIT_PARAM_DELIMITERS)) {
				classes.add(loadInitializerClass(className));
			}
		}
		String localClassNames = servletContext.getInitParameter(CONTEXT_INITIALIZER_CLASSES_PARAM);
		if (localClassNames != null) {
			for (String className : StringUtils.tokenizeToStringArray(localClassNames, INIT_PARAM_DELIMITERS)) {
				classes.add(loadInitializerClass(className));
			}
		}
		return classes;
	}

	@SuppressWarnings("unchecked")
	private Class<ApplicationContextInitializer<ConfigurableApplicationContext>> loadInitializerClass(String className) {
		try {
			Class<?> clazz = ClassUtils.forName(className, ClassUtils.getDefaultClassLoader());
			if (!ApplicationContextInitializer.class.isAssignableFrom(clazz)) {
				throw new ApplicationContextException("Initializer class does not implement ApplicationContextInitializer interface: " + clazz);
			}
			return (Class<ApplicationContextInitializer<ConfigurableApplicationContext>>) clazz;
		}catch (ClassNotFoundException ex) {
			throw new ApplicationContextException("Failed to load context initializer class [" + className + "]", ex);
		}
	}

	/**
	 * Template method with default implementation (which may be overridden by a subclass),
	 * to load or obtain an ApplicationContext instance which will be used as the parent context of the root WebApplicationContext.
	 * If the  return value from the method is null, no parent context is set.
	 * The main reason to load a parent context here is to allow multiple root web application contexts to all be children of a shared EAR context,
	 * or alternately to also share the same parent context that is visible to EJBs.
	 * For pure web applications, there is usually no need to worry about  having a parent context to the root web application context.
	 * The default implementation simply returns {@code null}, as of 5.0.
	 * @param servletContext current servlet context
	 * @return the parent application context, or {@code null} if none
	 */
	@Nullable
	protected ApplicationContext loadParentContext(ServletContext servletContext) {
		return null;
	}

	/**
	 * Close Spring's web application context for the given servlet context.
	 * If overriding {@link #loadParentContext(ServletContext)}, you may have to override this method as well.
	 * @param servletContext the ServletContext that the WebApplicationContext runs in
	 */
	public void closeWebApplicationContext(ServletContext servletContext) {
		servletContext.log("Closing Spring root WebApplicationContext");
		try {
			if (this.context instanceof ConfigurableWebApplicationContext) {
				((ConfigurableWebApplicationContext) this.context).close();
			}
		}finally {
			ClassLoader ccl = Thread.currentThread().getContextClassLoader();
			if (ccl == ContextLoader.class.getClassLoader()) {
				currentContext = null;
			}else if (ccl != null) {
				currentContextPerThread.remove(ccl);
			}
			servletContext.removeAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		}
	}

	/**
	 * Obtain the Spring root web application context for the current thread (i.e. for the current thread's context ClassLoader, which needs to be the web application's ClassLoader).
	 * @return the current root web application context, or {@code null} if none found
	 * @see org.springframework.web.context.support.SpringBeanAutowiringSupport
	 */
	@Nullable
	public static WebApplicationContext getCurrentWebApplicationContext() {
		ClassLoader ccl = Thread.currentThread().getContextClassLoader();
		if (ccl != null) {
			WebApplicationContext ccpt = currentContextPerThread.get(ccl);
			if (ccpt != null) {
				return ccpt;
			}
		}
		return currentContext;
	}
}
