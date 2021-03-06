

package org.springframework.web.servlet;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import javax.servlet.DispatcherType;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.i18n.LocaleContext;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.ui.context.ThemeSource;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.async.WebAsyncManager;
import org.springframework.web.context.request.async.WebAsyncUtils;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.util.NestedServletException;
import org.springframework.web.util.WebUtils;

/**
 * Central dispatcher for HTTP request handlers/controllers, e.g. for web UI controllers or HTTP-based remote service exporters. 
 * Dispatches to registered handlers for processing a web request, providing convenient mapping and exception handling facilities.
 *
 * This servlet is very flexible: It can be used with just about any workflow, with the installation of the appropriate adapter classes. 
 * It offers the following functionality that distinguishes it from other request-driven web MVC frameworks:
 *
 * It is based around a JavaBeans configuration mechanism.
 * It can use any {@link HandlerMapping} implementation - pre-built or provided as part of an application - to control the routing of requests to handler objects.
 *  Default is {@link org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping} and
 * {@link org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping}.
 * HandlerMapping objects can be defined as beans in the servlet's application context,implementing the HandlerMapping interface, 
 * overriding the default HandlerMapping if present. 
 * HandlerMappings can be given any bean name (they are tested by type).
 *
 * It can use any {@link HandlerAdapter}; this allows for using any handler interface.
 * Default adapters are {@link org.springframework.web.servlet.mvc.HttpRequestHandlerAdapter},
 * {@link org.springframework.web.servlet.mvc.SimpleControllerHandlerAdapter}, for Spring's
 * {@link org.springframework.web.HttpRequestHandler} and
 * {@link org.springframework.web.servlet.mvc.Controller} interfaces, respectively. A default
 * {@link org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter}
 * will be registered as well. HandlerAdapter objects can be added as beans in the application context, overriding the default HandlerAdapters. Like HandlerMappings,
 * HandlerAdapters can be given any bean name (they are tested by type).
 * The dispatcher's exception resolution strategy can be specified via a {@link HandlerExceptionResolver}, for example mapping certain exceptions to error pages.
 * Default are
 * {@link org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver},
 * {@link org.springframework.web.servlet.mvc.annotation.ResponseStatusExceptionResolver}, and
 * {@link org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver}.
 * These HandlerExceptionResolvers can be overridden through the application context.
 * HandlerExceptionResolver can be given any bean name (they are tested by type).
 *
 * Its view resolution strategy can be specified via a {@link ViewResolver} implementation, resolving symbolic view names into View objects. 
 * Default is {@link org.springframework.web.servlet.view.InternalResourceViewResolver}.
 * ViewResolver objects can be added as beans in the application context, overriding the
 * default ViewResolver. ViewResolvers can be given any bean name (they are tested by type).
 *
 * If a {@link View} or view name is not supplied by the user, then the configured
 * {@link RequestToViewNameTranslator} will translate the current request into a view name.
 * The corresponding bean name is "viewNameTranslator"; the default is {@link org.springframework.web.servlet.view.DefaultRequestToViewNameTranslator}.
 *
 * The dispatcher's strategy for resolving multipart requests is determined by a
 * {@link org.springframework.web.multipart.MultipartResolver} implementation.
 * Implementations for Apache Commons FileUpload and Servlet 3 are included; the typical
 * choice is {@link org.springframework.web.multipart.commons.CommonsMultipartResolver}.
 * The MultipartResolver bean name is "multipartResolver"; default is none.
 *
 * Its locale resolution strategy is determined by a {@link LocaleResolver}.
 * Out-of-the-box implementations work via HTTP accept header, cookie, or session.
 * The LocaleResolver bean name is "localeResolver"; default is
 * {@link org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver}.
 *
 * Its theme resolution strategy is determined by a {@link ThemeResolver}.
 * Implementations for a fixed theme and for cookie and session storage are included.
 * The ThemeResolver bean name is "themeResolver"; default is
 * {@link org.springframework.web.servlet.theme.FixedThemeResolver}.

 *
 * <b>NOTE: The {@code @RequestMapping} annotation will only be processed if a
 * corresponding {@code HandlerMapping} (for type-level annotations) and/or
 * {@code HandlerAdapter} (for method-level annotations) is present in the dispatcher.</b>
 * This is the case by default. However, if you are defining custom {@code HandlerMappings}
 * or {@code HandlerAdapters}, then you need to make sure that a corresponding custom
 * {@code RequestMappingHandlerMapping} and/or {@code RequestMappingHandlerAdapter}
 * is defined as well - provided that you intend to use {@code @RequestMapping}.
 *
 * <b>A web application can define any number of DispatcherServlets.</b>
 * Each servlet will operate in its own namespace, loading its own application context
 * with mappings, handlers, etc. Only the root application context as loaded by
 * {@link org.springframework.web.context.ContextLoaderListener}, if any, will be shared.
 *
 * As of Spring 3.1, {@code DispatcherServlet} may now be injected with a web
 * application context, rather than creating its own internally. This is useful in Servlet
 * 3.0+ environments, which support programmatic registration of servlet instances.
 * See the {@link #DispatcherServlet(WebApplicationContext)} javadoc for details.

 * @see org.springframework.web.HttpRequestHandler
 * @see org.springframework.web.servlet.mvc.Controller
 * @see org.springframework.web.context.ContextLoaderListener
 */
@SuppressWarnings("serial")
public class DispatcherServlet extends FrameworkServlet {

	/** Well-known name for the MultipartResolver object in the bean factory for this namespace. */
	public static final String MULTIPART_RESOLVER_BEAN_NAME = "multipartResolver";

	/** Well-known name for the LocaleResolver object in the bean factory for this namespace. */
	public static final String LOCALE_RESOLVER_BEAN_NAME = "localeResolver";

	/** Well-known name for the ThemeResolver object in the bean factory for this namespace. */
	public static final String THEME_RESOLVER_BEAN_NAME = "themeResolver";

	/**
	 * Well-known name for the HandlerMapping object in the bean factory for this namespace.
	 * Only used when "detectAllHandlerMappings" is turned off.
	 * @see #setDetectAllHandlerMappings
	 */
	public static final String HANDLER_MAPPING_BEAN_NAME = "handlerMapping";

	/**
	 * Well-known name for the HandlerAdapter object in the bean factory for this namespace.
	 * Only used when "detectAllHandlerAdapters" is turned off.
	 * @see #setDetectAllHandlerAdapters
	 */
	public static final String HANDLER_ADAPTER_BEAN_NAME = "handlerAdapter";

	/**
	 * Well-known name for the HandlerExceptionResolver object in the bean factory for this namespace.
	 * Only used when "detectAllHandlerExceptionResolvers" is turned off.
	 * @see #setDetectAllHandlerExceptionResolvers
	 */
	public static final String HANDLER_EXCEPTION_RESOLVER_BEAN_NAME = "handlerExceptionResolver";

	/**
	 * Well-known name for the RequestToViewNameTranslator object in the bean factory for this namespace.
	 */
	public static final String REQUEST_TO_VIEW_NAME_TRANSLATOR_BEAN_NAME = "viewNameTranslator";

	/**
	 * Well-known name for the ViewResolver object in the bean factory for this namespace.
	 * Only used when "detectAllViewResolvers" is turned off.
	 * @see #setDetectAllViewResolvers
	 */
	public static final String VIEW_RESOLVER_BEAN_NAME = "viewResolver";

	/**
	 * Well-known name for the FlashMapManager object in the bean factory for this namespace.
	 */
	public static final String FLASH_MAP_MANAGER_BEAN_NAME = "flashMapManager";

	/**
	 * Request attribute to hold the current web application context.
	 * Otherwise only the global web app context is obtainable by tags etc.
	 * @see org.springframework.web.servlet.support.RequestContextUtils#findWebApplicationContext
	 */
	public static final String WEB_APPLICATION_CONTEXT_ATTRIBUTE = DispatcherServlet.class.getName() + ".CONTEXT";

	/**
	 * Request attribute to hold the current LocaleResolver, retrievable by views.
	 * @see org.springframework.web.servlet.support.RequestContextUtils#getLocaleResolver
	 */
	public static final String LOCALE_RESOLVER_ATTRIBUTE = DispatcherServlet.class.getName() + ".LOCALE_RESOLVER";

	/**
	 * Request attribute to hold the current ThemeResolver, retrievable by views.
	 * @see org.springframework.web.servlet.support.RequestContextUtils#getThemeResolver
	 */
	public static final String THEME_RESOLVER_ATTRIBUTE = DispatcherServlet.class.getName() + ".THEME_RESOLVER";

	/**
	 * Request attribute to hold the current ThemeSource, retrievable by views.
	 * @see org.springframework.web.servlet.support.RequestContextUtils#getThemeSource
	 */
	public static final String THEME_SOURCE_ATTRIBUTE = DispatcherServlet.class.getName() + ".THEME_SOURCE";

	/**
	 * Name of request attribute that holds a read-only {@code Map<String,?>}
	 * with "input" flash attributes saved by a previous request, if any.
	 * @see org.springframework.web.servlet.support.RequestContextUtils#getInputFlashMap(HttpServletRequest)
	 */
	public static final String INPUT_FLASH_MAP_ATTRIBUTE = DispatcherServlet.class.getName() + ".INPUT_FLASH_MAP";

	/**
	 * Name of request attribute that holds the "output" {@link FlashMap} with
	 * attributes to save for a subsequent request.
	 * @see org.springframework.web.servlet.support.RequestContextUtils#getOutputFlashMap(HttpServletRequest)
	 */
	public static final String OUTPUT_FLASH_MAP_ATTRIBUTE = DispatcherServlet.class.getName() + ".OUTPUT_FLASH_MAP";

	/**
	 * Name of request attribute that holds the {@link FlashMapManager}.
	 * @see org.springframework.web.servlet.support.RequestContextUtils#getFlashMapManager(HttpServletRequest)
	 */
	public static final String FLASH_MAP_MANAGER_ATTRIBUTE = DispatcherServlet.class.getName() + ".FLASH_MAP_MANAGER";

	/**
	 * Name of request attribute that exposes an Exception resolved with an
	 * {@link HandlerExceptionResolver} but where no view was rendered
	 * (e.g. setting the status code).
	 */
	public static final String EXCEPTION_ATTRIBUTE = DispatcherServlet.class.getName() + ".EXCEPTION";

	/** Log category to use when no mapped handler is found for a request. */
	public static final String PAGE_NOT_FOUND_LOG_CATEGORY = "org.springframework.web.servlet.PageNotFound";

	/**
	 * Name of the class path resource (relative to the DispatcherServlet class)
	 * that defines DispatcherServlet's default strategy names.
	 */
	private static final String DEFAULT_STRATEGIES_PATH = "DispatcherServlet.properties";

	/**
	 * Common prefix that DispatcherServlet's default strategy attributes start with.
	 */
	private static final String DEFAULT_STRATEGIES_PREFIX = "org.springframework.web.servlet";

	/** Additional logger to use when no mapped handler is found for a request. */
	protected static final Log pageNotFoundLogger = LogFactory.getLog(PAGE_NOT_FOUND_LOG_CATEGORY);

	private static final Properties defaultStrategies;

	static {
		/**
		 * Load default strategy implementations from properties file.
		 * This is currently strictly internal and not meant to be customized  by application developers.
		 * 使用静态代码块 加载 两个默认的 HandlerMapping 实现类 在 DispatcherServlet.properties 文件中可以看到  这样的配置：
		 * org.springframework.web.servlet.HandlerMapping=org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping,\
		 * org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
		*/
		try {
			ClassPathResource resource = new ClassPathResource(DEFAULT_STRATEGIES_PATH, DispatcherServlet.class);
			defaultStrategies = PropertiesLoaderUtils.loadProperties(resource);
		}catch (IOException ex) {
			throw new IllegalStateException("Could not load '" + DEFAULT_STRATEGIES_PATH + "': " + ex.getMessage());
		}
	}

	/**
	 * Detect all HandlerMappings or just expect "handlerMapping" bean?.
	 * 检测所有的handlerMapping，还是只期望“handlerMapping”bean？
	 * 作用是检查所有的HandlerMappings映射解析器或使用id或name为"handlerMappping"的bean，默认为true，即从context上下文中检查所有的HandlerMapping。
	 * */
	private boolean detectAllHandlerMappings = true;

	/** Detect all HandlerAdapters or just expect "handlerAdapter" bean?. */
	private boolean detectAllHandlerAdapters = true;

	/** Detect all HandlerExceptionResolvers or just expect "handlerExceptionResolver" bean?. */
	private boolean detectAllHandlerExceptionResolvers = true;

	/** Detect all ViewResolvers or just expect "viewResolver" bean?. */
	private boolean detectAllViewResolvers = true;

	/** Throw a NoHandlerFoundException if no Handler was found to process this request? *.*/
	private boolean throwExceptionIfNoHandlerFound = false;

	/** Perform cleanup of request attributes after include request?. */
	private boolean cleanupAfterInclude = true;

	/** MultipartResolver used by this servlet. */
	@Nullable
	private MultipartResolver multipartResolver;

	/** LocaleResolver used by this servlet. */
	@Nullable
	private LocaleResolver localeResolver;

	/** ThemeResolver used by this servlet. */
	@Nullable
	private ThemeResolver themeResolver;

	/** List of HandlerMappings used by this servlet. */
	@Nullable
	private List<HandlerMapping> handlerMappings;

	/** List of HandlerAdapters used by this servlet. */
	@Nullable
	private List<HandlerAdapter> handlerAdapters;

	/** List of HandlerExceptionResolvers used by this servlet. */
	@Nullable
	private List<HandlerExceptionResolver> handlerExceptionResolvers;

	/** RequestToViewNameTranslator used by this servlet. */
	@Nullable
	private RequestToViewNameTranslator viewNameTranslator;

	/** FlashMapManager used by this servlet. */
	@Nullable
	private FlashMapManager flashMapManager;

	/** List of ViewResolvers used by this servlet. */
	@Nullable
	private List<ViewResolver> viewResolvers;


	/**
	 * Create a new {@code DispatcherServlet} that will create its own internal web
	 * application context based on defaults and values provided through servlet
	 * init-params. Typically used in Servlet 2.5 or earlier environments, where the only
	 * option for servlet registration is through {@code web.xml} which requires the use of a no-arg constructor.
	 * Calling {@link #setContextConfigLocation} (init-param 'contextConfigLocation') will dictate which XML files will be loaded by the
	 * {@linkplain #DEFAULT_CONTEXT_CLASS default XmlWebApplicationContext}
	 * Calling {@link #setContextClass} (init-param 'contextClass') overrides the
	 * default {@code XmlWebApplicationContext} and allows for specifying an alternative class,
	 * such as {@code AnnotationConfigWebApplicationContext}.
	 * Calling {@link #setContextInitializerClasses} (init-param 'contextInitializerClasses')
	 * indicates which {@code ApplicationContextInitializer} classes should be used to
	 * further configure the internal application context prior to refresh().
	 * @see #DispatcherServlet(WebApplicationContext)
	 */
	public DispatcherServlet() {
		super();
		setDispatchOptionsRequest(true);
	}

	/**
	 * Create a new {@code DispatcherServlet} with the given web application context. This
	 * constructor is useful in Servlet 3.0+ environments where instance-based registration
	 * of servlets is possible through the {@link ServletContext#addServlet} API.
	 * Using this constructor indicates that the following properties / init-params will be ignored:
	 * {@link #setContextClass(Class)} / 'contextClass'</li>
	 * {@link #setContextConfigLocation(String)} / 'contextConfigLocation'</li>
	 * {@link #setContextAttribute(String)} / 'contextAttribute'</li>
	 * {@link #setNamespace(String)} / 'namespace'</li>
	 * The given web application context may or may not yet be {@linkplain
	 * ConfigurableApplicationContext#refresh() refreshed}. If it has <strong>not</strong>
	 * already been refreshed (the recommended approach), then the following will occur:
	 * If the given context does not already have a {@linkplain
	 * ConfigurableApplicationContext#setParent parent}, the root application context
	 * will be set as the parent.</li>
	 * If the given context has not already been assigned an {@linkplain
	 * ConfigurableApplicationContext#setId id}, one will be assigned to it</li>
	 * {@code ServletContext} and {@code ServletConfig} objects will be delegated to
	 * the application context</li>
	 * {@link #postProcessWebApplicationContext} will be called</li>
	 * Any {@code ApplicationContextInitializer}s specified through the
	 * "contextInitializerClasses" init-param or through the {@link
	 * #setContextInitializers} property will be applied.</li>
	 * {@link ConfigurableApplicationContext#refresh refresh()} will be called if the
	 * context implements {@link ConfigurableApplicationContext}</li>
	 * If the context has already been refreshed, none of the above will occur, under the
	 * assumption that the user has performed these actions (or not) per their specific needs.
	 * See {@link org.springframework.web.WebApplicationInitializer} for usage examples.
	 * @param webApplicationContext the context to use
	 * @see #initWebApplicationContext
	 * @see #configureAndRefreshWebApplicationContext
	 * @see org.springframework.web.WebApplicationInitializer
	 */
	public DispatcherServlet(WebApplicationContext webApplicationContext) {
		super(webApplicationContext);
		setDispatchOptionsRequest(true);
	}


	/**
	 * Set whether to detect all HandlerMapping beans in this servlet's context. Otherwise,
	 * just a single bean with name "handlerMapping" will be expected.
	 * Default is "true". Turn this off if you want this servlet to use a single
	 * HandlerMapping, despite multiple HandlerMapping beans being defined in the context.
	 */
	public void setDetectAllHandlerMappings(boolean detectAllHandlerMappings) {
		this.detectAllHandlerMappings = detectAllHandlerMappings;
	}

	/**
	 * Set whether to detect all HandlerAdapter beans in this servlet's context. Otherwise,
	 * just a single bean with name "handlerAdapter" will be expected.
	 * Default is "true". Turn this off if you want this servlet to use a single
	 * HandlerAdapter, despite multiple HandlerAdapter beans being defined in the context.
	 */
	public void setDetectAllHandlerAdapters(boolean detectAllHandlerAdapters) {
		this.detectAllHandlerAdapters = detectAllHandlerAdapters;
	}

	/**
	 * Set whether to detect all HandlerExceptionResolver beans in this servlet's context. Otherwise,
	 * just a single bean with name "handlerExceptionResolver" will be expected.
	 * Default is "true". Turn this off if you want this servlet to use a single
	 * HandlerExceptionResolver, despite multiple HandlerExceptionResolver beans being defined in the context.
	 */
	public void setDetectAllHandlerExceptionResolvers(boolean detectAllHandlerExceptionResolvers) {
		this.detectAllHandlerExceptionResolvers = detectAllHandlerExceptionResolvers;
	}

	/**
	 * Set whether to detect all ViewResolver beans in this servlet's context. Otherwise,
	 * just a single bean with name "viewResolver" will be expected.
	 * Default is "true". Turn this off if you want this servlet to use a single
	 * ViewResolver, despite multiple ViewResolver beans being defined in the context.
	 */
	public void setDetectAllViewResolvers(boolean detectAllViewResolvers) {
		this.detectAllViewResolvers = detectAllViewResolvers;
	}

	/**
	 * Set whether to throw a NoHandlerFoundException when no Handler was found for this request.
	 * This exception can then be caught with a HandlerExceptionResolver or an
	 * {@code @ExceptionHandler} controller method.
	 * Note that if {@link org.springframework.web.servlet.resource.DefaultServletHttpRequestHandler}
	 * is used, then requests will always be forwarded to the default servlet and a
	 * NoHandlerFoundException would never be thrown in that case.
	 * Default is "false", meaning the DispatcherServlet sends a NOT_FOUND error through the
	 * Servlet response.
	 * @since 4.0
	 */
	public void setThrowExceptionIfNoHandlerFound(boolean throwExceptionIfNoHandlerFound) {
		this.throwExceptionIfNoHandlerFound = throwExceptionIfNoHandlerFound;
	}

	/**
	 * Set whether to perform cleanup of request attributes after an include request, that is,
	 * whether to reset the original state of all request attributes after the DispatcherServlet
	 * has processed within an include request. Otherwise, just the DispatcherServlet's own
	 * request attributes will be reset, but not model attributes for JSPs or special attributes
	 * set by views (for example, JSTL's).
	 * Default is "true", which is strongly recommended. Views should not rely on request attributes
	 * having been set by (dynamic) includes. This allows JSP views rendered by an included controller
	 * to use any model attributes, even with the same names as in the main JSP, without causing side
	 * effects. Only turn this off for special needs, for example to deliberately allow main JSPs to
	 * access attributes from JSP views rendered by an included controller.
	 */
	public void setCleanupAfterInclude(boolean cleanupAfterInclude) {
		this.cleanupAfterInclude = cleanupAfterInclude;
	}

	/**
	 * This implementation calls {@link #initStrategies}.
	 */
	@Override
	protected void onRefresh(ApplicationContext context) {
		initStrategies(context);
	}

	/**
	 * Initialize the strategy objects that this servlet uses.
	 * May be overridden in subclasses in order to initialize further strategy objects.
	 * Spring web九大组件初始化
	 */
	protected void initStrategies(ApplicationContext context) {
		//初始化文件上传处理类 // 文件上传解析
		initMultipartResolver(context);
		//初始化本地化Resolver // 本地解析
		initLocaleResolver(context);
		//初始化主题Resolver //主题解析
		initThemeResolver(context);
		//初始化一些个与处理的HandlerMappings // URL请求映射
		initHandlerMappings(context);
		// 初始化Controller类
		initHandlerAdapters(context);
		//初始化异常处理的handler  // 异常解析
		initHandlerExceptionResolvers(context);
		//初始化请求路径转换为ViewName 的Translator
		initRequestToViewNameTranslator(context);
		//初始化ViewResolvers 这个就是针对视图处理的Resolvers 比如jsp处理Resolvers 或者freemarker处理Resolvers
		// 视图解析
		initViewResolvers(context);
		//初始化 主要管理flashmap，比如RedirectAttributes 的属性会放到这个里面，默认使用的是SessionFlashMapManager
		initFlashMapManager(context);
	}

	/**
	 * Initialize the MultipartResolver used by this class.
	 * If no bean is defined with the given name in the BeanFactory for this namespace,no multipart handling is provided.
	 */
	private void initMultipartResolver(ApplicationContext context) {
		try {
			multipartResolver = context.getBean(MULTIPART_RESOLVER_BEAN_NAME, MultipartResolver.class);
			if (logger.isTraceEnabled()) {
				logger.trace("Detected " + multipartResolver);
			}else if (logger.isDebugEnabled()) {
				logger.debug("Detected " + multipartResolver.getClass().getSimpleName());
			}
		}catch (NoSuchBeanDefinitionException ex) {
			// Default is no multipart resolver.
			multipartResolver = null;
			if (logger.isTraceEnabled()) logger.trace("No MultipartResolver '" + MULTIPART_RESOLVER_BEAN_NAME + "' declared");
		}
	}

	/**
	 * Initialize the LocaleResolver used by this class.
	 * If no bean is defined with the given name in the BeanFactory for this namespace,we default to AcceptHeaderLocaleResolver.
	 */
	private void initLocaleResolver(ApplicationContext context) {
		try {
			// 这里LOCALE_RESOLVER_BEAN_NAME的值为localeResolver，也就是说用户如果
			// 需要自定义的LocaleResolver，那么在声明该bean是，其名称必须为localeResolver
			localeResolver = context.getBean(LOCALE_RESOLVER_BEAN_NAME, LocaleResolver.class);
			if (logger.isTraceEnabled()) {
				logger.trace("Detected " + localeResolver);
			}else if (logger.isDebugEnabled()) {
				logger.debug("Detected " + localeResolver.getClass().getSimpleName());
			}
		}catch (NoSuchBeanDefinitionException ex) {
			// We need to use the default. // 如果Spring容器中没有配置自定义的localeResolver，则通过默认策略实例化对应的bean
			localeResolver = getDefaultStrategy(context, LocaleResolver.class);
			if (logger.isTraceEnabled()) {
				logger.trace("No LocaleResolver '" + LOCALE_RESOLVER_BEAN_NAME + "': using default [" + localeResolver.getClass().getSimpleName() + "]");
			}
		}
	}

	/**
	 * Initialize the ThemeResolver used by this class.
	 * If no bean is defined with the given name in the BeanFactory for this namespace,
	 * we default to a FixedThemeResolver.
	 */
	private void initThemeResolver(ApplicationContext context) {
		try {
			themeResolver = context.getBean(THEME_RESOLVER_BEAN_NAME, ThemeResolver.class);
			if (logger.isTraceEnabled()) {
				logger.trace("Detected " + themeResolver);
			}else if (logger.isDebugEnabled()) {
				logger.debug("Detected " + themeResolver.getClass().getSimpleName());
			}
		}catch (NoSuchBeanDefinitionException ex) {
			// We need to use the default.
			themeResolver = getDefaultStrategy(context, ThemeResolver.class);
			if (logger.isTraceEnabled()) {
				logger.trace("No ThemeResolver '" + THEME_RESOLVER_BEAN_NAME + "': using default [" + themeResolver.getClass().getSimpleName() + "]");
			}
		}
	}

	/**
	 * Initialize the HandlerMappings used by this class.
	 * If no HandlerMapping beans are defined in the BeanFactory for this namespace,we default to BeanNameUrlHandlerMapping.
	 */
	private void initHandlerMappings(ApplicationContext context) {
		handlerMappings = null;
		// detectAllHandlerMappings 默认为true，可通过DispatcherServlet的init-param参数进行设置
		// 检查是否配置了获取Spring中配置的所有HandlerMapping类型对象，是则进行读取，并且按照
		// 指定的排序规则对其进行排序，否则就从Spring中读取名称为handlerMapping的bean，并将其作为指定的bean
		if (detectAllHandlerMappings) {
			// Find all HandlerMappings in the ApplicationContext, including ancestor contexts.
			// 从 SpringMVC的IOC容器及Spring的IOC容器中查找 所有的实现了HandlerMapping接口的bean
			// 在ApplicationContext中找到所有的handlerMapping，包括父上下文。
			Map<String, HandlerMapping> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerMapping.class, true, false);
			if (!matchingBeans.isEmpty()) {
				handlerMappings = new ArrayList<>(matchingBeans.values());
				// We keep HandlerMappings in sorted order. // 按一定顺序放置 HandlerMapping 对象
				// 对handlerMapping排序，可通过指定order属性进行设置，order的值为int型，数越小优先级越高
				// 对获取到的HandlerMapping进行排序
				AnnotationAwareOrderComparator.sort(handlerMappings);
			}
		}else {
			try {
				// 获取Spring容器中名称为handlerMapping的bean
				// 从ApplicationContext上下文中取id（或name）="handlerMapping"的bean
				HandlerMapping hm = context.getBean(HANDLER_MAPPING_BEAN_NAME, HandlerMapping.class);
				// 将hm转换成list，并赋值给属性handlerMappings
				handlerMappings = Collections.singletonList(hm);
			}catch (NoSuchBeanDefinitionException ex) {
				// Ignore, we'll add a default HandlerMapping later.
				// 忽略当前异常
			}
		}

		/**
		 * 	 Ensure we have at least one HandlerMapping, by registering a default HandlerMapping if no other mappings are found.
		 * 	 如果没有 HandlerMapping，则加载默认的
		 *   从context上下文中定义HandlerMapping时，Spring MVC将使用默认HandlerMapping，默认的HandlerMapping在 DispatcherServlet.properties 属性文件中定义，
		 *   该文件是在DispatcherServlet的static静态代码块中加载的
		 *   默认的是：BeanNameUrlHandlerMapping和RequestMappingHandlerMapping
		*/
		if (handlerMappings == null) {
			/**
			 *  org.springframework.web.servlet.handler.BeanNameUrlHandlerMapping
			 *  org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
			 *  从结果可知，在application.xml未进行任何配置HandlerMapping时，系统使用（支持）默认的BeanNameUrlHandlerMapping和RequestMappingHandlerMapping映射解析器。
			 *   // 如果上述方式没法获取到对应的HandlerMapping，则使用默认策略获取对应的HandlerMapping
			*/
			handlerMappings = getDefaultStrategies(context, HandlerMapping.class);
			if (logger.isTraceEnabled()) {
				logger.trace("No HandlerMappings declared for servlet '" + getServletName() + "': using default strategies from DispatcherServlet.properties");
			}
		}
	}

	/**
	 * Initialize the HandlerAdapters used by this class.
	 * If no HandlerAdapter beans are defined in the BeanFactory for this namespace,we default to SimpleControllerHandlerAdapter.
	 */
	private void initHandlerAdapters(ApplicationContext context) {
		handlerAdapters = null;
		if (detectAllHandlerAdapters) {
			// Find all HandlerAdapters in the ApplicationContext, including ancestor contexts.
			Map<String, HandlerAdapter> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerAdapter.class, true, false);
			if (!matchingBeans.isEmpty()) {
				handlerAdapters = new ArrayList<>(matchingBeans.values());
				// We keep HandlerAdapters in sorted order.
				AnnotationAwareOrderComparator.sort(handlerAdapters);
			}
		}else {
			try {
				HandlerAdapter ha = context.getBean(HANDLER_ADAPTER_BEAN_NAME, HandlerAdapter.class);
				handlerAdapters = Collections.singletonList(ha);
			}catch (NoSuchBeanDefinitionException ex) {
				// Ignore, we'll add a default HandlerAdapter later.
			}
		}

		// Ensure we have at least some HandlerAdapters, by registering
		// default HandlerAdapters if no other adapters are found.
		if (handlerAdapters == null) {
			handlerAdapters = getDefaultStrategies(context, HandlerAdapter.class);
			if (logger.isTraceEnabled()) {
				logger.trace("No HandlerAdapters declared for servlet '" + getServletName() + "': using default strategies from DispatcherServlet.properties");
			}
		}
	}

	/**
	 * Initialize the HandlerExceptionResolver used by this class.
	 * If no bean is defined with the given name in the BeanFactory for this namespace,
	 * we default to no exception resolver.
	 */
	private void initHandlerExceptionResolvers(ApplicationContext context) {
		handlerExceptionResolvers = null;
		if (detectAllHandlerExceptionResolvers) {
			// Find all HandlerExceptionResolvers in the ApplicationContext, including ancestor contexts.
			Map<String, HandlerExceptionResolver> matchingBeans = BeanFactoryUtils.beansOfTypeIncludingAncestors(context, HandlerExceptionResolver.class, true, false);
			if (!matchingBeans.isEmpty()) {
				handlerExceptionResolvers = new ArrayList<>(matchingBeans.values());
				// We keep HandlerExceptionResolvers in sorted order.
				AnnotationAwareOrderComparator.sort(handlerExceptionResolvers);
			}
		}else {
			try {
				HandlerExceptionResolver her = context.getBean(HANDLER_EXCEPTION_RESOLVER_BEAN_NAME, HandlerExceptionResolver.class);
				handlerExceptionResolvers = Collections.singletonList(her);
			}catch (NoSuchBeanDefinitionException ex) {
				// Ignore, no HandlerExceptionResolver is fine too.
			}
		}

		// Ensure we have at least some HandlerExceptionResolvers, by registering
		// default HandlerExceptionResolvers if no other resolvers are found.
		if (handlerExceptionResolvers == null) {
			handlerExceptionResolvers = getDefaultStrategies(context, HandlerExceptionResolver.class);
			if (logger.isTraceEnabled()) {
				logger.trace("No HandlerExceptionResolvers declared in servlet '" + getServletName() + "': using default strategies from DispatcherServlet.properties");
			}
		}
	}

	/**
	 * Initialize the RequestToViewNameTranslator used by this servlet instance.
	 * If no implementation is configured then we default to DefaultRequestToViewNameTranslator.
	 */
	private void initRequestToViewNameTranslator(ApplicationContext context) {
		try {
			viewNameTranslator = context.getBean(REQUEST_TO_VIEW_NAME_TRANSLATOR_BEAN_NAME, RequestToViewNameTranslator.class);
			if (logger.isTraceEnabled()) {
				logger.trace("Detected " + viewNameTranslator.getClass().getSimpleName());
			}else if (logger.isDebugEnabled()) {
				logger.debug("Detected " + viewNameTranslator);
			}
		}catch (NoSuchBeanDefinitionException ex) {
			// We need to use the default.
			viewNameTranslator = getDefaultStrategy(context, RequestToViewNameTranslator.class);
			if (logger.isTraceEnabled()) {
				logger.trace("No RequestToViewNameTranslator '" + REQUEST_TO_VIEW_NAME_TRANSLATOR_BEAN_NAME + "': using default [" + viewNameTranslator.getClass().getSimpleName() + "]");
			}
		}
	}

	/**
	 * Initialize the ViewResolvers used by this class.
	 * If no ViewResolver beans are defined in the BeanFactory for this
	 * namespace, we default to InternalResourceViewResolver.
	 */
	private void initViewResolvers(ApplicationContext context) {
		viewResolvers = null;
		if (detectAllViewResolvers) {
			// Find all ViewResolvers in the ApplicationContext, including ancestor contexts.
			Map<String, ViewResolver> matchingBeans =
					BeanFactoryUtils.beansOfTypeIncludingAncestors(context, ViewResolver.class, true, false);
			if (!matchingBeans.isEmpty()) {
				viewResolvers = new ArrayList<>(matchingBeans.values());
				// We keep ViewResolvers in sorted order.
				AnnotationAwareOrderComparator.sort(viewResolvers);
			}
		}else {
			try {
				ViewResolver vr = context.getBean(VIEW_RESOLVER_BEAN_NAME, ViewResolver.class);
				viewResolvers = Collections.singletonList(vr);
			}catch (NoSuchBeanDefinitionException ex) {
				// Ignore, we'll add a default ViewResolver later.
			}
		}
		// Ensure we have at least one ViewResolver, by registering
		// a default ViewResolver if no other resolvers are found.
		if (viewResolvers == null) {
			viewResolvers = getDefaultStrategies(context, ViewResolver.class);
			if (logger.isTraceEnabled()) {
				logger.trace("No ViewResolvers declared for servlet '" + getServletName() + "': using default strategies from DispatcherServlet.properties");
			}
		}
	}

	/**
	 * Initialize the {@link FlashMapManager} used by this servlet instance.
	 * If no implementation is configured then we default to
	 * {@code org.springframework.web.servlet.support.DefaultFlashMapManager}.
	 */
	private void initFlashMapManager(ApplicationContext context) {
		try {
			flashMapManager = context.getBean(FLASH_MAP_MANAGER_BEAN_NAME, FlashMapManager.class);
			if (logger.isTraceEnabled()) {
				logger.trace("Detected " + flashMapManager.getClass().getSimpleName());
			}else if (logger.isDebugEnabled()) {
				logger.debug("Detected " + flashMapManager);
			}
		}catch (NoSuchBeanDefinitionException ex) {
			// We need to use the default.
			flashMapManager = getDefaultStrategy(context, FlashMapManager.class);
			if (logger.isTraceEnabled()) {
				logger.trace("No FlashMapManager '" + FLASH_MAP_MANAGER_BEAN_NAME + "': using default [" + flashMapManager.getClass().getSimpleName() + "]");
			}
		}
	}

	/**
	 * Return this servlet's ThemeSource, if any; else return {@code null}.
	 * Default is to return the WebApplicationContext as ThemeSource,provided that it implements the ThemeSource interface.
	 * @return the ThemeSource, if any
	 * @see #getWebApplicationContext()
	 */
	@Nullable
	public final ThemeSource getThemeSource() {
		return (getWebApplicationContext() instanceof ThemeSource ? (ThemeSource) getWebApplicationContext() : null);
	}

	/**
	 * Obtain this servlet's MultipartResolver, if any.
	 * @return the MultipartResolver used by this servlet, or {@code null} if none (indicating that no multipart support is available)
	 */
	@Nullable
	public final MultipartResolver getMultipartResolver() {
		return multipartResolver;
	}

	/**
	 * Return the configured {@link HandlerMapping} beans that were detected by
	 * type in the {@link WebApplicationContext} or initialized based on the default set of strategies from {@literal DispatcherServlet.properties}.
	 * <strong>Note:</strong> This method may return {@code null} if invoked prior to {@link #onRefresh(ApplicationContext)}.
	 * @return an immutable list with the configured mappings, or {@code null} if not initialized yet
	 * @since 5.0
	 */
	@Nullable
	public final List<HandlerMapping> getHandlerMappings() {
		return (handlerMappings != null ? Collections.unmodifiableList(handlerMappings) : null);
	}

	/**
	 * Return the default strategy object for the given strategy interface.
	 * The default implementation delegates to {@link #getDefaultStrategies},expecting a single object in the list.
	 * @param context the current WebApplicationContext
	 * @param strategyInterface the strategy interface
	 * @return the corresponding strategy object
	 * @see #getDefaultStrategies
	 */
	protected <T> T getDefaultStrategy(ApplicationContext context, Class<T> strategyInterface) {
		List<T> strategies = getDefaultStrategies(context, strategyInterface);
		if (strategies.size() != 1) {
			throw new BeanInitializationException("DispatcherServlet needs exactly 1 strategy for interface [" + strategyInterface.getName() + "]");
		}
		return strategies.get(0);
	}

	/**
	 * Create a List of default strategy objects for the given strategy interface.
	 * The default implementation uses the "DispatcherServlet.properties" file (in the same package as the DispatcherServlet class) to determine the class names.
	 * It instantiates the strategy objects through the context's BeanFactory.
	 * @param context the current WebApplicationContext
	 * @param strategyInterface the strategy interface
	 * @return the List of corresponding strategy objects
	 */
	@SuppressWarnings("unchecked")
	protected <T> List<T> getDefaultStrategies(ApplicationContext context, Class<T> strategyInterface) {
		String key = strategyInterface.getName();
		String value = defaultStrategies.getProperty(key);
		if (value != null) {
			String[] classNames = StringUtils.commaDelimitedListToStringArray(value);
			List<T> strategies = new ArrayList<>(classNames.length);
			for (String className : classNames) {
				try {
					logger.warn("【spring-webmvc init create default bean from  DispatcherServlet.properties file 】 className： " + className);
					Class<?> clazz = ClassUtils.forName(className, DispatcherServlet.class.getClassLoader());
					Object strategy = createDefaultStrategy(context, clazz);
					strategies.add((T) strategy);
				}catch (ClassNotFoundException ex) {
					throw new BeanInitializationException("Could not find DispatcherServlet's default strategy class [" + className + "] for interface [" + key + "]", ex);
				}catch (LinkageError err) {
					throw new BeanInitializationException("Unresolvable class definition for DispatcherServlet's default strategy class [" + className + "] for interface [" + key + "]", err);
				}
			}
			return strategies;
		}else {
			return new LinkedList<>();
		}
	}

	/**
	 * Create a default strategy.
	 * The default implementation uses {@link org.springframework.beans.factory.config.AutowireCapableBeanFactory#createBean}.
	 * @param context the current WebApplicationContext
	 * @param clazz the strategy implementation class to instantiate
	 * @return the fully configured strategy instance
	 * @see org.springframework.context.ApplicationContext#getAutowireCapableBeanFactory()
	 * @see org.springframework.beans.factory.config.AutowireCapableBeanFactory#createBean
	 */
	protected Object createDefaultStrategy(ApplicationContext context, Class<?> clazz) {
		return context.getAutowireCapableBeanFactory().createBean(clazz);
	}

	/**
	 * Exposes the DispatcherServlet-specific request attributes and delegates to {@link #doDispatch} for the actual dispatching.
	 */
	@Override
	protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
		logRequest(request);
		// Keep a snapshot of the request attributes in case of an include,to be able to restore the original attributes after the include.
		// 如果是include请求，保存request attribute快照数据，并在finally中进行还原
		// 这里主要是判断当前请求是否为include请求，如果是include请求，那么就会将当前请求中的
		// 数据都放入一个快照中，在当前请求完成之后，会从该块中中取出数据，然后将其重新加载到
		// 当前request中，以便request进行后续的处理。这里默认情况下是会对所有的属性进行处理的，
		// 因为cleanupAfterInclude默认值为true，如果将其设置为false，那么就只会对Spring框架
		// 相关的属性进行处理
		Map<String, Object> attributesSnapshot = null;
		if (WebUtils.isIncludeRequest(request)) {
			attributesSnapshot = new HashMap<>();
			Enumeration<?> attrNames = request.getAttributeNames();
			while (attrNames.hasMoreElements()) {
				String attrName = (String) attrNames.nextElement();
				if (cleanupAfterInclude || attrName.startsWith(DEFAULT_STRATEGIES_PREFIX)) {
					attributesSnapshot.put(attrName, request.getAttribute(attrName));
				}
			}
		}
		// Make framework objects available to handlers and view objects.
		// 把环境上下文设置到请求域中
		// 这里分别将ApplicationContext，LoacleResolver，ThemeResolver和ThemeSource等bean添加到当前request中
		request.setAttribute(WEB_APPLICATION_CONTEXT_ATTRIBUTE, getWebApplicationContext());
		request.setAttribute(LOCALE_RESOLVER_ATTRIBUTE, localeResolver);
		request.setAttribute(THEME_RESOLVER_ATTRIBUTE, themeResolver);
		request.setAttribute(THEME_SOURCE_ATTRIBUTE, getThemeSource());
		// 这里FlashMapManager主要的作用在于当请求如果是重定向的请求，那么可以将一些属性保存在FlashMap中，然后通过FlashMapManager进行管理，从而在重定向之后能够获取到重定向之前所保存的请求
		if (flashMapManager != null) {
			// 在当前请求中获取FlashMap数据，如果不是重定向之后的请求，那么这里获取到的就是空值
			FlashMap inputFlashMap = flashMapManager.retrieveAndUpdate(request, response);
			if (inputFlashMap != null) {
				// 将获取到的FlashMap数据保存在request中
				request.setAttribute(INPUT_FLASH_MAP_ATTRIBUTE, Collections.unmodifiableMap(inputFlashMap));
			}
			// 设置默认的FlashMap和FlashMapManager
			request.setAttribute(OUTPUT_FLASH_MAP_ATTRIBUTE, new FlashMap());
			request.setAttribute(FLASH_MAP_MANAGER_ATTRIBUTE, flashMapManager);
		}
		try {
			// 函数的关键方法 真正进行用户请求的处理
			doDispatch(request, response);
		}finally {
			if (!WebAsyncUtils.getAsyncManager(request).isConcurrentHandlingStarted()) {
				// Restore the original attribute snapshot, in case of an include.
				if (attributesSnapshot != null) {
					restoreAttributesAfterInclude(request, attributesSnapshot);
				}
			}
		}
	}

	private void logRequest(HttpServletRequest request) {
		LogFormatUtils.traceDebug(logger, traceOn -> {
			String params;
			if (isEnableLoggingRequestDetails()) {
				params = request.getParameterMap().entrySet().stream().map(entry -> entry.getKey() + ":" + Arrays.toString(entry.getValue())).collect(Collectors.joining(", "));
			}else {
				params = (request.getParameterMap().isEmpty() ? "" : "masked");
			}
			String queryString = request.getQueryString();
			String queryClause = (StringUtils.hasLength(queryString) ? "?" + queryString : "");
			String dispatchType = (!request.getDispatcherType().equals(DispatcherType.REQUEST) ? "\"" + request.getDispatcherType().name() + "\" dispatch for " : "");
			String message = (dispatchType + request.getMethod() + " \"" + getRequestUri(request) + queryClause + "\", parameters={" + params + "}");
			if (traceOn) {
				List<String> values = Collections.list(request.getHeaderNames());
				String headers = values.size() > 0 ? "masked" : "";
				if (isEnableLoggingRequestDetails()) {
					headers = values.stream().map(name -> name + ":" + Collections.list(request.getHeaders(name))).collect(Collectors.joining(", "));
				}
				return message + ", headers={" + headers + "} in DispatcherServlet '" + getServletName() + "'";
			}else {
				return message;
			}
		});
	}

	/**
	 * Process the actual dispatching to the handler.
	 * The handler will be obtained by applying the servlet's HandlerMappings in order.
	 * The HandlerAdapter will be obtained by querying the servlet's installed HandlerAdapters to find the first that supports the handler class.
	 * All HTTP methods are handled by this method. It's up to HandlerAdapters or handlers themselves to decide which methods are acceptable.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @throws Exception in case of any kind of processing failure
	 * 请求处理关键方法
	 * 获取当前请求的Handler
	 * 获取当前请求的Handler Adapter
	 * 执行preHandle方法
	 * 执行Handle方法，即Controller中的方法
	 * 执行postHandle方法
	 * 处理返回结果
	 */
	protected void doDispatch(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 用户的request请求
		HttpServletRequest processedRequest = request;
		// 注意这里返回的是HandlerExecutionChain对象
		HandlerExecutionChain mappedHandler = null;
		// 判断是否解析了文件类型的数据，如果有最终需要清理
		boolean multipartRequestParsed = false;
		// 获取当前的异步任务管理器
		WebAsyncManager asyncManager = WebAsyncUtils.getAsyncManager(request);
		try {
			ModelAndView mv = null;
			Exception dispatchException = null;
			try {
				//  处理文件上传请求 // 检查此次请求 是否是文件上传请求
				// 这里判断当前请求是否为一个文件请求，这里的判断方式就是要求当前请求满足两点：①请求
				// 方式是POST；②判断contentType是否以multipart/开头。如果满足这两点，那么就认为当前
				// 请求是一个文件请求，此时会将当前请求的request对象封装为一个
				// MultipartHttpServletRequest对象，这也是我们在定义文件请求的Controller时
				// 能够将request参数写为MultipartHttpServletRequest的原因。这里如果不是文件请求， 那么会将request直接返回。
				processedRequest = checkMultipart(request);
				// 这里判断原始request与转换后的request是否为同一个request，如果不是同一个，则说明
				// 其是一个文件请求
				multipartRequestParsed = (processedRequest != request);

				// 这里getHandler()方法就是通过遍历当前Spring容器中所有定义的HandlerMapping对象，
				// 通过调用它们的getHandler()方法，看当前的HandlerMapping能否将当前request映射到某个handler，
				// 也就是某个Controller方法上，如果能够映射到，则说明该handler能够处理当前请求

				// Determine handler for the current request. 确定当前请求的处理程序 返回对应 controller 和 拦截器 // 解析请求，获取HandlerExecutionChain对象
				//向HandlerMapping请求查找HandlerExecutionChain
				// ① 获取处理器
				mappedHandler = getHandler(processedRequest);
				//如果HandlerExecutionChain为null，则没有能够进行处理的Handler，抛出异常
				if (mappedHandler == null) {
					// 如果每个HandlerMapping都无法找到与当前request匹配的handler，那么就认为
					// 无法处理当前请求，此时一般会返回给页面404状态码
					noHandlerFound(processedRequest, response);
					return;
				}

				// 通过找到的handler，然后在当前Spring容器中找到能够支持将当前request请求适配到
				// 找到的handler上的HandlerAdapter。这里需要找到这样的适配器的原因是，我们的handler
				// 一般都是Controller的某个方法，其是一个Java方法，而当前request则是一种符合http
				// 协议的请求，这里是无法直接将request直接应用到handler上的，因而需要使用一个适配器，
				// 也就是这里的HandlerAdapter。由于前面获取handler的时候，不同的HandlerMapping
				// 所产生的handler是不一样的，比如ReqeustMappingHandlerMapping产生的handler是一个
				// HandlerMethod对象，因而这里在判断某个HandlerAdapter是否能够用于适配当前handler的
				// 时候是通过其supports()方法进行的，比如RequestMappingHandlerAdapter就是判断
				// 当前的handler是否为HandlerMethod类型，从而判断其是否能够用于适配当前handler。

				// Determine handler adapter for the current request. 确定当前请求的处理程序适配器(负责执行controller )
				// 从HandlerExecutionChain对象获取HandlerAdapter对象，实际上是从HandlerMapping对象中获取
				//根据查找到的Handler请求查找能够进行处理的HandlerAdapter
				// ② 获取适配器
				// 获取可执行处理器逻辑的适配器 HandlerAdapter
				HandlerAdapter ha = getHandlerAdapter(mappedHandler.getHandler());
				// Process last-modified header, if supported by the handler.
				//判断自上次请求后是否有修改，没有修改直接返回响应
				// 处理 last-modified 消息头
				String method = request.getMethod();
				boolean isGet = "GET".equals(method);
				// 这里判断请求方式是否为GET或HEAD请求，如果是这两种请求的一种，那么就会判断
				// 当前请求的资源是否超过了其lastModified时间，如果没超过，则直接返回，
				// 并且告知浏览器可以直接使用缓存来处理当前请求
				if (isGet || "HEAD".equals(method)) {
					long lastModified = ha.getLastModified(request, mappedHandler.getHandler());
					if (new ServletWebRequest(request, response).checkNotModified(lastModified) && isGet) {
						return;
					}
				}
				/**
				 //在controller方法执行前，执行拦截器的相关方法（pre）  // 执行 其那面返回的拦截器
				 //按顺序依次执行HandlerInterceptor的preHandle方法 如果任一HandlerInterceptor的preHandle方法没有通过则不继续进行处理
                */
				// 这里在真正处理请求之前会获取容器中所有的拦截器，也就是HandlerInterceptor对象，
				// 然后依次调用其preHandle()方法，如果某个preHandle()方法返回了false，那么就说明
				// 当前请求无法通过拦截器的过滤，因而就会直接出发其afterCompletion()方法，只有在
				// 所有的preHandle()方法都返回true时才会认为当前请求是能够使用目标handler进行处理的
				// 执行拦截器 preHandle 方法
				if (!mappedHandler.applyPreHandle(processedRequest, response)) {
					return;
				}
				/**
				 // Actually invoke the handler.实际调用处理程序  调用我的 controller
				 // 执行HandlerAdapter对象的handler方法，返回ModelAndView
				 //通过HandlerAdapter执行查找到的handler
				 */
				// 在当前请求通过了所有拦截器的预处理之后，这里就直接调用HandlerAdapter.handle()
				// 方法来处理当前请求，并且将处理结果封装为一个ModelAndView对象。该对象中主要有两个
				// 属性：view和model，这里的view存储了后续需要展示的逻辑视图名或视图对象，而model
				// 中则保存了用于渲染视图所需要的属性
				// 调用处理器逻辑
				mv = ha.handle(processedRequest, response, mappedHandler.getHandler());

				// 如果当前是一个异步任务，那么就会释放当前线程，等待异步任务处理完成之后才将
				// 任务的处理结果返回到页面
				if (asyncManager.isConcurrentHandlingStarted()) {
					return;
				}
				// 如果返回的ModelAndView对象中没有指定视图名或视图对象，那么就会根据当前请求的url来生成一个视图名
				// 如果 controller 未返回 view 名称，这里生成默认的 view 名称
				applyDefaultViewName(processedRequest, mv);
				// 在controller方法执行后，执行拦截器的相关方法（post） //逆序执行HandlerInterceptor的postHandle方法
				// 在请求处理完成之后，依次调用拦截器的postHandle()方法，对请求进行后置处理
				// 执行拦截器 preHandle 方法
				mappedHandler.applyPostHandle(processedRequest, response, mv);
			}catch (Exception ex) {
				dispatchException = ex;
			}catch (Throwable err) {
				// As of 4.3, we're processing Errors thrown from handler methods as well,
				// making them available for @ExceptionHandler methods and other scenarios.
				// 将处理请求过程中产生的异常封装到dispatchException中
				dispatchException = new NestedServletException("Handler dispatch failed", err);
			}
			// 进行视图解析  // 渲染视图填充Model，如果有异常渲染异常页面
			// 这里主要是请求处理之后生成的视图进行渲染，也包括出现异常之后对异常的处理。
			// 渲染完之后会依次调用拦截器的afterCompletion()方法来对请求进行最终处理
			// 解析并渲染视图
			processDispatchResult(processedRequest, response, mappedHandler, mv, dispatchException);
		}catch (Exception ex) {
			// 如果有异常按倒序执行所有HandlerInterceptor的afterCompletion方法
			// 如果在上述过程中任意位置抛出异常，包括渲染视图时抛出异常，那么都会触发拦截器的
			// afterCompletion()方法的调用
			triggerAfterCompletion(processedRequest, response, mappedHandler, ex);
		}catch (Throwable err) {
			// 如果有异常按倒序执行所有HandlerInterceptor的afterCompletion方法
			triggerAfterCompletion(processedRequest, response, mappedHandler,new NestedServletException("Handler processing failed", err));
		}finally {
			// 如果当前异步任务已经开始，则触发异步任务拦截器的afterConcurrentHandlingStarted()方法
			if (asyncManager.isConcurrentHandlingStarted()) {
				// Instead of postHandle and afterCompletion
				if (mappedHandler != null) {
					//倒序执行所有HandlerInterceptor的afterCompletion方法
					mappedHandler.applyAfterConcurrentHandlingStarted(processedRequest, response);
				}
			}else {
				// 如果当前是一个文件请求，则清理当前request中的文件数据
				// Clean up any resources used by a multipart request.
				// 如果请求包含文件类型的数据则进行相关清理工作
				if (multipartRequestParsed) {
					cleanupMultipart(processedRequest);
				}
			}
		}
	}

	/**
	 * Do we need view name translation? 我们需要视图名称转换吗？
	 */
	private void applyDefaultViewName(HttpServletRequest request, @Nullable ModelAndView mv) throws Exception {
		if (mv != null && !mv.hasView()) {
			String defaultViewName = getDefaultViewName(request);
			if (defaultViewName != null) {
				mv.setViewName(defaultViewName);
			}
		}
	}

	/**
	 * Handle the result of handler selection and handler invocation, which is either a ModelAndView or an Exception to be resolved to a ModelAndView.
	 */
	private void processDispatchResult(HttpServletRequest request, HttpServletResponse response,@Nullable HandlerExecutionChain mappedHandler, @Nullable ModelAndView mv,@Nullable Exception exception) throws Exception {
		// 用于标记当前生成view是否是异常处理之后生成的view
		boolean errorView = false;
		if (exception != null) {
			// 如果当前的异常是ModelAndViewDefiningException类型，则说明是ModelAndView的定义
			// 异常，那么就会调用其getModelAndView()方法生成一个新的view
			if (exception instanceof ModelAndViewDefiningException) {
				logger.debug("ModelAndViewDefiningException encountered", exception);
				mv = ((ModelAndViewDefiningException) exception).getModelAndView();
			}else {
				// 如果生成的异常是其他类型的异常，就会在当前容器中查找能够处理当前异常的“拦截器”，
				// 找到之后调用这些拦截器，然后生成一个新的ModelAndView
				Object handler = (mappedHandler != null ? mappedHandler.getHandler() : null);
				mv = processHandlerException(request, response, handler, exception);
				errorView = (mv != null);
			}
		}
		// 如果得到的ModelAndView对象(无论是否为异常处理之后生成的ModelAndView)不为空，并且没有被清理，
		// 那么就会对其进行渲染，渲染的主要逻辑在render()方法中
		// Did the handler return a view to render?
		if (mv != null && !mv.wasCleared()) {
			render(mv, request, response);
			if (errorView) {
				// 如果当前是异常处理之后生成的视图，那么就请求当前request中与异常相关的属性
				WebUtils.clearErrorRequestAttributes(request);
			}
		}else {
			if (logger.isTraceEnabled()) logger.trace("No view rendering, null ModelAndView returned.");
		}
		// 如果当前正在进行异步请求任务的调用，则直接释放当前线程，等异步任务处理完之后再进行处理
		if (WebAsyncUtils.getAsyncManager(request).isConcurrentHandlingStarted()) {
			// Concurrent handling started during a forward
			return;
		}
		// 在视图渲染完成之后，依次调用当前容器中所有拦截器的afterCompletion()方法
		if (mappedHandler != null) {
			mappedHandler.triggerAfterCompletion(request, response, null);
		}
	}

	/**
	 * Build a LocaleContext for the given request, exposing the request's primary locale as current locale.
	 * The default implementation uses the dispatcher's LocaleResolver to obtain the current locale,which might change during a request.
	 * @param request current HTTP request
	 * @return the corresponding LocaleContext
	 */
	@Override
	protected LocaleContext buildLocaleContext(final HttpServletRequest request) {
		LocaleResolver lr = localeResolver;
		if (lr instanceof LocaleContextResolver) {
			return ((LocaleContextResolver) lr).resolveLocaleContext(request);
		}else {
			return () -> (lr != null ? lr.resolveLocale(request) : request.getLocale());
		}
	}

	/**
	 * Convert the request into a multipart request, and make multipart resolver available.
	 *  将请求转换为多部分请求，并使多部分解析程序可用。
	 * If no multipart resolver is set, simply use the existing request.
	 * @param request current HTTP request
	 * @return the processed request (multipart wrapper if necessary)
	 * @see MultipartResolver#resolveMultipart
	 */
	protected HttpServletRequest checkMultipart(HttpServletRequest request) throws MultipartException {
		if (multipartResolver != null && multipartResolver.isMultipart(request)) {
			if (WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class) != null) {
				if (request.getDispatcherType().equals(DispatcherType.REQUEST)) logger.trace("Request already resolved to MultipartHttpServletRequest, e.g. by MultipartFilter");
			}else if (hasMultipartException(request)) {
				logger.debug("Multipart resolution previously failed for current request - skipping re-resolution for undisturbed error rendering");
			}else {
				try {
					return multipartResolver.resolveMultipart(request);
				}catch (MultipartException ex) {
					if (request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE) != null) {
						logger.debug("Multipart resolution failed for error dispatch", ex);
						// Keep processing error dispatch with regular request handle below
					}else {
						throw ex;
					}
				}
			}
		}
		// If not returned before: return original request.
		return request;
	}

	/**
	 * Check "javax.servlet.error.exception" attribute for a multipart exception.
	 */
	private boolean hasMultipartException(HttpServletRequest request) {
		Throwable error = (Throwable) request.getAttribute(WebUtils.ERROR_EXCEPTION_ATTRIBUTE);
		while (error != null) {
			if (error instanceof MultipartException) {
				return true;
			}
			error = error.getCause();
		}
		return false;
	}

	/**
	 * Clean up any resources used by the given multipart request (if any).
	 * @param request current HTTP request
	 * @see MultipartResolver#cleanupMultipart
	 */
	protected void cleanupMultipart(HttpServletRequest request) {
		if (multipartResolver != null) {
			MultipartHttpServletRequest multipartRequest = WebUtils.getNativeRequest(request, MultipartHttpServletRequest.class);
			if (multipartRequest != null) {
				multipartResolver.cleanupMultipart(multipartRequest);
			}
		}
	}

	/**
	 * Return the HandlerExecutionChain for this request. Tries all handler mappings in order.
	 * @param request current HTTP request
	 * @return the HandlerExecutionChain, or {@code null} if no handler could be found
	 */
	@Nullable
	protected HandlerExecutionChain getHandler(HttpServletRequest request) throws Exception {
		if (handlerMappings != null) {
			/**
			 * 遍历当前容器中所有的HandlerMapping对象，调用其getHandler()方法，如果其能够根据当前request获取一个handler，那么就直接返回。
			 *  handlerMappings 集合 有两个   requestMappingHandlerMapping  和 beanNameHandlerMapping
			 *  以 @Controller 方式注册的controller 会存放在 requestMappingHandlerMapping 集合里
			 *  以 @component + implement Controller 方式注册的controller 会存放在 beanNameHandlerMapping 集合里
			*/
			for (HandlerMapping mapping : handlerMappings) {
				HandlerExecutionChain handler = mapping.getHandler(request);
				if (handler != null) {
					return handler;
				}
			}
		}
		return null;
	}

	/**
	 * No handler found -> set appropriate HTTP response status.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @throws Exception if preparing the response failed
	 */
	protected void noHandlerFound(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (pageNotFoundLogger.isWarnEnabled()) {
			pageNotFoundLogger.warn("No mapping for " + request.getMethod() + " " + getRequestUri(request));
		}
		if (throwExceptionIfNoHandlerFound) {
			throw new NoHandlerFoundException(request.getMethod(), getRequestUri(request),new ServletServerHttpRequest(request).getHeaders());
		}else {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
		}
	}

	/**
	 * Return the HandlerAdapter for this handler object.
	 * @param handler the handler object to find an adapter for
	 * @throws ServletException if no HandlerAdapter can be found for the handler. This is a fatal error.
	 */
	protected HandlerAdapter getHandlerAdapter(Object handler) throws ServletException {
		if (handlerAdapters != null) {
			// 遍历当前容器中所有的HandlerAdapter，通过调用其supports()方法，判断当前HandlerAdapter
			// 能否用于适配当前的handler，如果可以，则直接使用该HandlerAdapter
			for (HandlerAdapter adapter : handlerAdapters) {
				if (adapter.supports(handler)) { // 遍历出 当前请求controller 对应的适配器
					return adapter;
				}
			}
		}
		// 如果找不到任何一个HandlerAdapter用于适配当前请求，则抛出异常
		throw new ServletException("No adapter for handler [" + handler + "]: The DispatcherServlet configuration needs to include a HandlerAdapter that supports this handler");
	}

	/**
	 * Determine an error ModelAndView via the registered HandlerExceptionResolvers.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler the executed handler, or {@code null} if none chosen at the time of the exception (for example, if multipart resolution failed)
	 * @param ex the exception that got thrown during handler execution
	 * @return a corresponding ModelAndView to forward to
	 * @throws Exception if no error ModelAndView found
	 */
	@Nullable
	protected ModelAndView processHandlerException(HttpServletRequest request, HttpServletResponse response,@Nullable Object handler, Exception ex) throws Exception {
		// Success and error responses may use different content types
		request.removeAttribute(HandlerMapping.PRODUCIBLE_MEDIA_TYPES_ATTRIBUTE);
		// Check registered HandlerExceptionResolvers...
		ModelAndView exMv = null;
		if (handlerExceptionResolvers != null) {
			for (HandlerExceptionResolver resolver : handlerExceptionResolvers) {
				exMv = resolver.resolveException(request, response, handler, ex);
				if (exMv != null) {
					break;
				}
			}
		}
		if (exMv != null) {
			if (exMv.isEmpty()) {
				request.setAttribute(EXCEPTION_ATTRIBUTE, ex);
				return null;
			}
			// We might still need view name translation for a plain error model...
			if (!exMv.hasView()) {
				String defaultViewName = getDefaultViewName(request);
				if (defaultViewName != null) {
					exMv.setViewName(defaultViewName);
				}
			}
			if (logger.isTraceEnabled()) logger.trace("Using resolved error view: " + exMv, ex);
			if (logger.isDebugEnabled()) logger.debug("Using resolved error view: " + exMv);
			WebUtils.exposeErrorRequestAttributes(request, ex, getServletName());
			return exMv;
		}
		throw ex;
	}

	/**
	 * Render the given ModelAndView.
	 * This is the last stage in handling a request. It may involve resolving the view by name.
	 * @param mv the ModelAndView to render
	 * @param request current HTTP servlet request
	 * @param response current HTTP servlet response
	 * @throws ServletException if view is missing or cannot be resolved
	 * @throws Exception if there's a problem rendering the view
	 */
	protected void render(ModelAndView mv, HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 获取当前请求的Locale信息，该信息在进行视图的国际化展示时将会非常有用
		// Determine locale for request and apply it to the response.
		Locale locale = (localeResolver != null ? localeResolver.resolveLocale(request) : request.getLocale());
		response.setLocale(locale);
		View view;
		String viewName = mv.getViewName();
		if (viewName != null) {
			// 解析视图
			// 如果视图名不为空，那么就会使用当前容器中配置的ViewResolver根据视图名获取一个View对象
			// We need to resolve the view name.
			view = resolveViewName(viewName, mv.getModelInternal(), locale, request);
			if (view == null) {
				throw new ServletException("Could not resolve view with name '" + mv.getViewName() + "' in servlet with name '" + getServletName() + "'");
			}
		}else {
			// 如果ModelAndView中没有视图名，而提供的View对象，则直接使用该View对象
			// No need to lookup: the ModelAndView object contains the actual View object.
			view = mv.getView();
			if (view == null) {
				throw new ServletException("ModelAndView [" + mv + "] neither contains a view name nor a " + "View object in servlet with name '" + getServletName() + "'");
			}
		}

		// Delegate to the View object for rendering.
		if (logger.isTraceEnabled()) logger.trace("Rendering view [" + view + "] ");
		try {
			// 设置响应的status属性
			if (mv.getStatus() != null) {
				response.setStatus(mv.getStatus().value());
			}
			// 调用View对象的render()方法来渲染具体的视图
			// 渲染视图，并将结果返回给用户。对应步骤⑥和⑦
			view.render(mv.getModelInternal(), request, response);
		}catch (Exception ex) {
			if (logger.isDebugEnabled()) logger.debug("Error rendering view [" + view + "]", ex);
			throw ex;
		}
	}

	/**
	 * Translate the supplied request into a default view name.
	 * @param request current HTTP servlet request
	 * @return the view name (or {@code null} if no default found)
	 * @throws Exception if view name translation failed
	 */
	@Nullable
	protected String getDefaultViewName(HttpServletRequest request) throws Exception {
		return (viewNameTranslator != null ? viewNameTranslator.getViewName(request) : null);
	}

	/**
	 * Resolve the given view name into a View object (to be rendered).
	 * The default implementations asks all ViewResolvers of this dispatcher.
	 * Can be overridden for custom resolution strategies, potentially based on specific model attributes or request parameters.
	 * @param viewName the name of the view to resolve
	 * @param model the model to be passed to the view
	 * @param locale the current locale
	 * @param request current HTTP servlet request
	 * @return the View object, or {@code null} if none found
	 * @throws Exception if the view cannot be resolved (typically in case of problems creating an actual View object)
	 * @see ViewResolver#resolveViewName
	 */
	@Nullable
	protected View resolveViewName(String viewName, @Nullable Map<String, Object> model,Locale locale, HttpServletRequest request) throws Exception {
		if (viewResolvers != null) {
			for (ViewResolver viewResolver : viewResolvers) {
				View view = viewResolver.resolveViewName(viewName, locale);
				if (view != null) {
					return view;
				}
			}
		}
		return null;
	}

	private void triggerAfterCompletion(HttpServletRequest request, HttpServletResponse response,@Nullable HandlerExecutionChain mappedHandler, Exception ex) throws Exception {
		if (mappedHandler != null) {
			mappedHandler.triggerAfterCompletion(request, response, ex);
		}
		throw ex;
	}

	/**
	 * Restore the request attributes after an include.
	 * @param request current HTTP request
	 * @param attributesSnapshot the snapshot of the request attributes before the include
	 */
	@SuppressWarnings("unchecked")
	private void restoreAttributesAfterInclude(HttpServletRequest request, Map<?, ?> attributesSnapshot) {
		// Need to copy into separate Collection here, to avoid side effects on the Enumeration when removing attributes.
		Set<String> attrsToCheck = new HashSet<>();
		Enumeration<?> attrNames = request.getAttributeNames();
		while (attrNames.hasMoreElements()) {
			String attrName = (String) attrNames.nextElement();
			if (cleanupAfterInclude || attrName.startsWith(DEFAULT_STRATEGIES_PREFIX)) {
				attrsToCheck.add(attrName);
			}
		}
		// Add attributes that may have been removed
		attrsToCheck.addAll((Set<String>) attributesSnapshot.keySet());
		// Iterate over the attributes to check, restoring the original value or removing the attribute, respectively, if appropriate.
		for (String attrName : attrsToCheck) {
			Object attrValue = attributesSnapshot.get(attrName);
			if (attrValue == null) {
				request.removeAttribute(attrName);
			}else if (attrValue != request.getAttribute(attrName)) {
				request.setAttribute(attrName, attrValue);
			}
		}
	}

	private static String getRequestUri(HttpServletRequest request) {
		String uri = (String) request.getAttribute(WebUtils.INCLUDE_REQUEST_URI_ATTRIBUTE);
		if (uri == null) {
			uri = request.getRequestURI();
		}
		return uri;
	}
}
