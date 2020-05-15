

package org.springframework.web.context.support;

import java.io.Serializable;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertySource.StubPropertySource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestScope;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.SessionScope;
import org.springframework.web.context.request.WebRequest;

/**
 * Convenience methods for retrieving the root {@link WebApplicationContext} for  a given {@link ServletContext}.
 * This is useful for programmatically accessing a Spring application context from within custom web views or MVC actions.
 * Note that there are more convenient ways of accessing the root context for many web frameworks,either part of Spring or available as an external library.
 * This helper class is just the most generic way to access the root context.
 * @see org.springframework.web.context.ContextLoader
 * @see org.springframework.web.servlet.FrameworkServlet
 * @see org.springframework.web.servlet.DispatcherServlet
 * @see org.springframework.web.jsf.FacesContextUtils
 * @see org.springframework.web.jsf.el.SpringBeanFacesELResolver
 *
 * 是访问一个ServletContext的根WebApplicationContext的便捷方法类。该工具类提供了如下工具方法 :
 * 在web容器启动过程中注册web相关作用域bean : request,session , globalSession , application
 * 在web容器启动过程中注册相应类型的工厂bean，开发人员依赖注入相应的bean时能访问到正确的请求/响应/会话对象 : ServletRequest,ServletResponse,HttpSession,WebRequest
 * 在web容器启动过程中注册web相关环境bean : contextParameters , contextAttributes
 * 在web容器启动过程中初始化servlet propertySources
 * 在客户化web视图(custom web view)或者MVC action中，使用该工具类可以很方便地在程序中访问Spring应用上下文(application context)。
 */
public abstract class WebApplicationContextUtils {

	private static final boolean jsfPresent = ClassUtils.isPresent("javax.faces.context.FacesContext", RequestContextHolder.class.getClassLoader());

	/**
	 * Find the root {@code WebApplicationContext} for this web app, typically loaded via {@link org.springframework.web.context.ContextLoaderListener}.
	 * Will rethrow an exception that happened on root context startup,to differentiate between a failed context startup and no context at all.
	 * @param sc the ServletContext to find the web application context for
	 * @return the root WebApplicationContext for this web app
	 * @throws IllegalStateException if the root WebApplicationContext could not be found
	 * @see org.springframework.web.context.WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE
	 */
	public static WebApplicationContext getRequiredWebApplicationContext(ServletContext sc) throws IllegalStateException {
		WebApplicationContext wac = getWebApplicationContext(sc);
		if (wac == null) throw new IllegalStateException("No WebApplicationContext found: no ContextLoaderListener registered?");
		return wac;
	}

	/**
	 * Find the root {@code WebApplicationContext} for this web app, typically loaded via {@link org.springframework.web.context.ContextLoaderListener}.
	 * Will rethrow an exception that happened on root context startup,to differentiate between a failed context startup and no context at all.
	 * @param sc the ServletContext to find the web application context for
	 * @return the root WebApplicationContext for this web app, or {@code null} if none
	 * @see org.springframework.web.context.WebApplicationContext#ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE
	 */
	@Nullable
	public static WebApplicationContext getWebApplicationContext(ServletContext sc) {
		return getWebApplicationContext(sc, WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
	}

	/**
	 * Find a custom {@code WebApplicationContext} for this web app.
	 * @param sc the ServletContext to find the web application context for
	 * @param attrName the name of the ServletContext attribute to look for
	 * @return the desired WebApplicationContext for this web app, or {@code null} if none
	 */
	@Nullable
	public static WebApplicationContext getWebApplicationContext(ServletContext sc, String attrName) {
		Assert.notNull(sc, "ServletContext must not be null");
		Object attr = sc.getAttribute(attrName);
		if (attr == null) return null;
		if (attr instanceof RuntimeException) throw (RuntimeException) attr;
		if (attr instanceof Error) throw (Error) attr;
		if (attr instanceof Exception) throw new IllegalStateException((Exception) attr);
		if (!(attr instanceof WebApplicationContext)) throw new IllegalStateException("Context attribute is not of type WebApplicationContext: " + attr);
		return (WebApplicationContext) attr;
	}

	/**
	 * Find a unique {@code WebApplicationContext} for this web app: either the
	 * root web app context (preferred) or a unique {@code WebApplicationContext} among the registered {@code ServletContext} attributes (typically coming
	 * from a single {@code DispatcherServlet} in the current web application).
	 * Note that {@code DispatcherServlet}'s exposure of its context can be controlled through its {@code publishContext} property,
	 * which is {@code true} by default but can be selectively switched to only publish a single context despite multiple {@code DispatcherServlet} registrations in the web app.
	 * @param sc the ServletContext to find the web application context for
	 * @return the desired WebApplicationContext for this web app, or {@code null} if none
	 * @since 4.2
	 * @see #getWebApplicationContext(ServletContext)
	 * @see ServletContext#getAttributeNames()
	 */
	@Nullable
	public static WebApplicationContext findWebApplicationContext(ServletContext sc) {
		// 尝试获取根web应用上下文，如果找得到则返回根web应用上下文
		WebApplicationContext wac = getWebApplicationContext(sc);
		if (wac == null) {
			// 如果没有找到根web应用上下文，尝试从ServletContext的属性中查找唯一的一个WebApplicationContext
			// 并返回，如果找到的WebApplicationContext不唯一，则抛出异常声明该情况
			Enumeration<String> attrNames = sc.getAttributeNames();
			while (attrNames.hasMoreElements()) {
				String attrName = attrNames.nextElement();
				Object attrValue = sc.getAttribute(attrName);
				if (attrValue instanceof WebApplicationContext) {
					if (wac != null) {
						throw new IllegalStateException("No unique WebApplicationContext found: more than one DispatcherServlet registered with publishContext=true?");
					}
					wac = (WebApplicationContext) attrValue;
				}
			}
		}
		return wac;
	}


	/**
	 * Register web-specific scopes ("request", "session", "globalSession")  with the given BeanFactory, as used by the WebApplicationContext.
	 * @param beanFactory the BeanFactory to configure
	 */
	public static void registerWebApplicationScopes(ConfigurableListableBeanFactory beanFactory) {
		registerWebApplicationScopes(beanFactory, null);
	}

	/**
	 * Register web-specific scopes ("request", "session", "globalSession", "application") with the given BeanFactory, as used by the WebApplicationContext.
	 * @param beanFactory the BeanFactory to configure
	 * @param sc the ServletContext that we're running within
	 */
	public static void registerWebApplicationScopes(ConfigurableListableBeanFactory beanFactory,@Nullable ServletContext sc) {
		// 注册web相关作用域bean
		beanFactory.registerScope(WebApplicationContext.SCOPE_REQUEST, new RequestScope());
		beanFactory.registerScope(WebApplicationContext.SCOPE_SESSION, new SessionScope());
		if (sc != null) {
			ServletContextScope appScope = new ServletContextScope(sc);
			beanFactory.registerScope(WebApplicationContext.SCOPE_APPLICATION, appScope);
			// Register as ServletContext attribute, for ContextCleanupListener to detect it.
			sc.setAttribute(ServletContextScope.class.getName(), appScope);
		}
		// 注册ServletRequest的工厂bean，当开发人员依赖注入ServletRequest对象时，注入的bean其实是这里的 RequestObjectFactory工厂bean
		beanFactory.registerResolvableDependency(ServletRequest.class, new RequestObjectFactory());
		// 注册ServletResponse的工厂bean，当开发人员依赖注入ServletResponse对象时，注入的bean其实是这里的 ResponseObjectFactory工厂bean
		beanFactory.registerResolvableDependency(ServletResponse.class, new ResponseObjectFactory());
		// 注册HttpSession的工厂bean，当开发人员依赖注入 HttpSession 对象时，注入的bean其实是这里的 SessionObjectFactory工厂bean
		beanFactory.registerResolvableDependency(HttpSession.class, new SessionObjectFactory());
		// 注册WebRequest的工厂bean，当开发人员依赖注入WebRequest对象时，注入的bean其实是这里的 WebRequestObjectFactory工厂bean
		beanFactory.registerResolvableDependency(WebRequest.class, new WebRequestObjectFactory());
		if (jsfPresent) {
			FacesDependencyRegistrar.registerFacesDependencies(beanFactory);
		}
	}

	/**
	 * Register web-specific environment beans ("contextParameters", "contextAttributes")
	 * with the given BeanFactory, as used by the WebApplicationContext.
	 * @param bf the BeanFactory to configure
	 * @param sc the ServletContext that we're running within
	 */
	public static void registerEnvironmentBeans(ConfigurableListableBeanFactory bf, @Nullable ServletContext sc) {
		registerEnvironmentBeans(bf, sc, null);
	}

	/**
	 * Register web-specific environment beans ("contextParameters", "contextAttributes") with the given BeanFactory, as used by the WebApplicationContext.
	 * @param bf the BeanFactory to configure
	 * @param servletContext the ServletContext that we're running within
	 * @param servletConfig the ServletConfig
	 */
	public static void registerEnvironmentBeans(ConfigurableListableBeanFactory bf,@Nullable ServletContext servletContext, @Nullable ServletConfig servletConfig) {
		if (servletContext != null && !bf.containsBean(WebApplicationContext.SERVLET_CONTEXT_BEAN_NAME)) {
			bf.registerSingleton(WebApplicationContext.SERVLET_CONTEXT_BEAN_NAME, servletContext);
		}
		if (servletConfig != null && !bf.containsBean(ConfigurableWebApplicationContext.SERVLET_CONFIG_BEAN_NAME)) {
			bf.registerSingleton(ConfigurableWebApplicationContext.SERVLET_CONFIG_BEAN_NAME, servletConfig);
		}
		if (!bf.containsBean(WebApplicationContext.CONTEXT_PARAMETERS_BEAN_NAME)) {
			Map<String, String> parameterMap = new HashMap<>();
			if (servletContext != null) {
				Enumeration<?> paramNameEnum = servletContext.getInitParameterNames();
				while (paramNameEnum.hasMoreElements()) {
					String paramName = (String) paramNameEnum.nextElement();
					parameterMap.put(paramName, servletContext.getInitParameter(paramName));
				}
			}
			if (servletConfig != null) {
				Enumeration<?> paramNameEnum = servletConfig.getInitParameterNames();
				while (paramNameEnum.hasMoreElements()) {
					String paramName = (String) paramNameEnum.nextElement();
					parameterMap.put(paramName, servletConfig.getInitParameter(paramName));
				}
			}
			bf.registerSingleton(WebApplicationContext.CONTEXT_PARAMETERS_BEAN_NAME,Collections.unmodifiableMap(parameterMap));
		}

		if (!bf.containsBean(WebApplicationContext.CONTEXT_ATTRIBUTES_BEAN_NAME)) {
			Map<String, Object> attributeMap = new HashMap<>();
			if (servletContext != null) {
				Enumeration<?> attrNameEnum = servletContext.getAttributeNames();
				while (attrNameEnum.hasMoreElements()) {
					String attrName = (String) attrNameEnum.nextElement();
					attributeMap.put(attrName, servletContext.getAttribute(attrName));
				}
			}
			bf.registerSingleton(WebApplicationContext.CONTEXT_ATTRIBUTES_BEAN_NAME,Collections.unmodifiableMap(attributeMap));
		}
	}

	/**
	 * Convenient variant of {@link #initServletPropertySources(MutablePropertySources,
	 * ServletContext, ServletConfig)} that always provides {@code null} for the {@link ServletConfig} parameter.
	 * @see #initServletPropertySources(MutablePropertySources, ServletContext, ServletConfig)
	 */
	public static void initServletPropertySources(MutablePropertySources propertySources, ServletContext servletContext) {
		initServletPropertySources(propertySources, servletContext, null);
	}

	/**
	 * Replace {@code Servlet}-based {@link StubPropertySource stub property sources} with
	 * actual instances populated with the given {@code servletContext} and {@code servletConfig} objects.
	 * This method is idempotent with respect to the fact it may be called any number
	 * of times but will perform replacement of stub property sources with their corresponding actual property sources once and only once.
	 * @param sources the {@link MutablePropertySources} to initialize (must not be {@code null})
	 * @param servletContext the current {@link ServletContext} (ignored if {@code null} or if the {@link StandardServletEnvironment#SERVLET_CONTEXT_PROPERTY_SOURCE_NAME
	 * servlet context property source} has already been initialized)
	 * @param servletConfig the current {@link ServletConfig} (ignored if {@code null}
	 * or if the {@link StandardServletEnvironment#SERVLET_CONFIG_PROPERTY_SOURCE_NAME servlet config property source} has already been initialized)
	 * @see org.springframework.core.env.PropertySource.StubPropertySource
	 * @see org.springframework.core.env.ConfigurableEnvironment#getPropertySources()
	 */
	public static void initServletPropertySources(MutablePropertySources sources,@Nullable ServletContext servletContext, @Nullable ServletConfig servletConfig) {
		Assert.notNull(sources, "'propertySources' must not be null");
		String name = StandardServletEnvironment.SERVLET_CONTEXT_PROPERTY_SOURCE_NAME;
		if (servletContext != null && sources.contains(name) && sources.get(name) instanceof StubPropertySource) {
			sources.replace(name, new ServletContextPropertySource(name, servletContext));
		}
		name = StandardServletEnvironment.SERVLET_CONFIG_PROPERTY_SOURCE_NAME;
		if (servletConfig != null && sources.contains(name) && sources.get(name) instanceof StubPropertySource) {
			sources.replace(name, new ServletConfigPropertySource(name, servletConfig));
		}
	}

	/**
	 * 获取当前RequestAttributes实例，返回类型为ServletRequestAttributes，
	 * 该方法使用RequestContextHolder获取和当前请求处理线程绑定的ServletRequestAttributes对象，
	 * 进而可以获取其中的HttpServletRequest/HttpServletResponse对象
	 * 该类定义该方法的目的是给该类的以下四个私有嵌套静态类使用 :
	 * Return the current RequestAttributes instance as ServletRequestAttributes.
	 * @see RequestContextHolder#currentRequestAttributes()
	 */
	private static ServletRequestAttributes currentRequestAttributes() {
		RequestAttributes requestAttr = RequestContextHolder.currentRequestAttributes();
		if (!(requestAttr instanceof ServletRequestAttributes)) {
			throw new IllegalStateException("Current request is not a servlet request");
		}
		return (ServletRequestAttributes) requestAttr;
	}

	/**
	 * Factory that exposes the current request object on demand.
	 * 	 在下面的代码中，该类定义了四个私有嵌套静态类 :
	 * 	 RequestObjectFactory,ResponseObjectFactory,SessionObjectFactory,WebRequestObjectFactory
	 * 	 这四个静态类是四个工厂类，分别用于生成ServletRequest,ServletResponse,HttpSession,WebRequest对象,
	 * 	 当开发人员使用@Autowired分别注入了以上四种类型的bean时，返回的其实是下面四个工厂类的对象，这四个工厂类对象
	 * 	 均使用了上面定义的currentRequestAttributes()方法，能从当前请求处理线程中获取正确的ServletRequestAttributes 对象，
	 * 	 进而获取正确的ServletRequest,ServletResponse,HttpSession,WebRequest对象
	 */
	@SuppressWarnings("serial")
	private static class RequestObjectFactory implements ObjectFactory<ServletRequest>, Serializable {
		@Override
		public ServletRequest getObject() {
			return currentRequestAttributes().getRequest();
		}
		@Override
		public String toString() {
			return "Current HttpServletRequest";
		}
	}

	/**
	 * Factory that exposes the current response object on demand.
	 */
	@SuppressWarnings("serial")
	private static class ResponseObjectFactory implements ObjectFactory<ServletResponse>, Serializable {
		@Override
		public ServletResponse getObject() {
			ServletResponse response = currentRequestAttributes().getResponse();
			if (response == null) throw new IllegalStateException("Current servlet response not available - consider using RequestContextFilter instead of RequestContextListener");
			return response;
		}
		@Override
		public String toString() {
			return "Current HttpServletResponse";
		}
	}

	// Factory that exposes the current session object on demand.
	@SuppressWarnings("serial")
	private static class SessionObjectFactory implements ObjectFactory<HttpSession>, Serializable {
		@Override
		public HttpSession getObject() {
			return currentRequestAttributes().getRequest().getSession();
		}
		@Override
		public String toString() {
			return "Current HttpSession";
		}
	}

	//  Factory that exposes the current WebRequest object on demand.
	@SuppressWarnings("serial")
	private static class WebRequestObjectFactory implements ObjectFactory<WebRequest>, Serializable {
		@Override
		public WebRequest getObject() {
			ServletRequestAttributes requestAttr = currentRequestAttributes();
			return new ServletWebRequest(requestAttr.getRequest(), requestAttr.getResponse());
		}
		@Override
		public String toString() {
			return "Current ServletWebRequest";
		}
	}

	// Inner class to avoid hard-coded JSF dependency.
	private static class FacesDependencyRegistrar {

		public static void registerFacesDependencies(ConfigurableListableBeanFactory beanFactory) {
			beanFactory.registerResolvableDependency(FacesContext.class, new ObjectFactory<FacesContext>() {
				@Override
				public FacesContext getObject() {
					return FacesContext.getCurrentInstance();
				}
				@Override
				public String toString() {
					return "Current JSF FacesContext";
				}
			});
			beanFactory.registerResolvableDependency(ExternalContext.class, new ObjectFactory<ExternalContext>() {
				@Override
				public ExternalContext getObject() {
					return FacesContext.getCurrentInstance().getExternalContext();
				}
				@Override
				public String toString() {
					return "Current JSF ExternalContext";
				}
			});
		}
	}

}
