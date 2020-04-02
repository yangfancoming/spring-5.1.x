

package org.springframework.web.servlet.handler;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.util.ObjectUtils;

/**
 * Abstract implementation of the {@link org.springframework.web.servlet.HandlerMapping}
 * interface, detecting URL mappings for handler beans through introspection of all
 * defined beans in the application context.
 * @since 2.5
 * @see #determineUrlsForHandler
 */
public abstract class AbstractDetectingUrlHandlerMapping extends AbstractUrlHandlerMapping {

	private boolean detectHandlersInAncestorContexts = false;

	/**
	 * Set whether to detect handler beans in ancestor ApplicationContexts.
	 * Default is "false": Only handler beans in the current ApplicationContext
	 * will be detected, i.e. only in the context that this HandlerMapping itself
	 * is defined in (typically the current DispatcherServlet's context).
	 * Switch this flag on to detect handler beans in ancestor contexts
	 * (typically the Spring root WebApplicationContext) as well.
	 */
	public void setDetectHandlersInAncestorContexts(boolean detectHandlersInAncestorContexts) {
		this.detectHandlersInAncestorContexts = detectHandlersInAncestorContexts;
	}


	/**
	 * Calls the {@link #detectHandlers()} method in addition to the superclass's initialization.
	 */
	@Override
	public void initApplicationContext() throws ApplicationContextException {
		// 调用父类AbstractHandlerMapping初始化拦截器，与SimpleUrlHandlerMapping一样
		super.initApplicationContext();
		// 处理url和bean name，具体注册调用父类AbstractUrlHandlerMapping类完成
		detectHandlers();
	}

	/**
	 * Register all handlers found in the current ApplicationContext.
	 * The actual URL determination for a handler is up to the concrete {@link #determineUrlsForHandler(String)} implementation.
	 *  A bean for which no such URLs could be determined is simply not considered a handler.
	 *  对于无法确定此类URL的bean，不将其视为处理程序。
	 * @throws org.springframework.beans.BeansException if the handler couldn't be registered
	 * @see #determineUrlsForHandler(String)
	 *  建立当前ApplicationContext中的所有controller和url的对应关系
	 *
	 * 0 = "sampleController"
	 * 1 = "org.springframework.context.annotation.internalConfigurationAnnotationProcessor"
	 * 2 = "org.springframework.context.annotation.internalAutowiredAnnotationProcessor"
	 * 3 = "org.springframework.context.annotation.internalCommonAnnotationProcessor"
	 * 4 = "org.springframework.context.event.internalEventListenerProcessor"
	 * 5 = "org.springframework.context.event.internalEventListenerFactory"
	 * 6 = "org.springframework.web.servlet.view.InternalResourceViewResolver#0"
	 * 7 = "environment"
	 * 8 = "systemProperties"
	 * 9 = "systemEnvironment"
	 * 10 = "servletContext"
	 * 11 = "servletConfig"
	 * 12 = "contextParameters"
	 * 13 = "contextAttributes"
	 * 14 = "org.springframework.context.annotation.ConfigurationClassPostProcessor.importRegistry"
	 * 15 = "messageSource"
	 * 16 = "applicationEventMulticaster"
	 * 17 = "lifecycleProcessor"
	 */
	protected void detectHandlers() throws BeansException {
		// 获取应用上下文
		ApplicationContext applicationContext = obtainApplicationContext();
		// 获取ApplicationContext容器中所有bean的Name
		String[] beanNames = (this.detectHandlersInAncestorContexts ? BeanFactoryUtils.beanNamesForTypeIncludingAncestors(applicationContext, Object.class) : applicationContext.getBeanNamesForType(Object.class));
		// Take any bean name that we can determine URLs for.  遍历beanNames,并找到这些bean对应的url
		for (String beanName : beanNames) {
			// 找bean上的所有url(controller上的url+方法上的url),该方法由对应的子类实现  // 通过模板方法模式调用BeanNameUrlHandlerMapping子类处理
			String[] urls = determineUrlsForHandler(beanName);
			if (!ObjectUtils.isEmpty(urls)) {
				// URL paths found: Let's consider it a handler. // 保存urls和beanName的对应关系,put it to Map,该方法在父类AbstractUrlHandlerMapping中实现
				// 调用父类AbstractUrlHandlerMapping将url与handler存入map
				registerHandler(urls, beanName);
			}
		}
		if ((logger.isDebugEnabled() && !getHandlerMap().isEmpty()) || logger.isTraceEnabled()) {
			logger.debug("Detected " + getHandlerMap().size() + " mappings in " + formatMappingName());
		}
	}


	/**
	 * Determine the URLs for the given handler bean.
	 * @param beanName the name of the candidate bean
	 * @return the URLs determined for the bean, or an empty array if none
	 * 获取controller中所有方法的url,由子类实现,典型的模板模式
	 */
	protected abstract String[] determineUrlsForHandler(String beanName);

}
