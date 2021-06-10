

package org.springframework.web.context.support;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.support.StaticApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.Nullable;
import org.springframework.ui.context.Theme;
import org.springframework.ui.context.ThemeSource;
import org.springframework.ui.context.support.UiApplicationContextUtils;
import org.springframework.util.Assert;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ServletConfigAware;
import org.springframework.web.context.ServletContextAware;

/**
 * Static {@link org.springframework.web.context.WebApplicationContext}
 * implementation for testing. Not intended for use in production applications.
 *
 * Implements the {@link org.springframework.web.context.ConfigurableWebApplicationContext}
 * interface to allow for direct replacement of an {@link XmlWebApplicationContext},despite not actually supporting external configuration files.
 *
 * Interprets resource paths as servlet context resources, i.e. as paths beneath
 * the web application root. Absolute paths, e.g. for files outside the web app root,
 * can be accessed via "file:" URLs, as implemented by {@link org.springframework.core.io.DefaultResourceLoader}.
 *
 * In addition to the special beans detected by {@link org.springframework.context.support.AbstractApplicationContext},
 * this class detects a bean of type {@link org.springframework.ui.context.ThemeSource} in the context, under the special bean name "themeSource".
 * @see org.springframework.ui.context.ThemeSource
 * 一般用于测试，几乎不使用
 */
public class StaticWebApplicationContext extends StaticApplicationContext implements ConfigurableWebApplicationContext, ThemeSource {

	@Nullable
	private ServletContext servletContext;

	@Nullable
	private ServletConfig servletConfig;

	@Nullable
	private String namespace;

	@Nullable
	private ThemeSource themeSource;

	public StaticWebApplicationContext() {
		setDisplayName("Root WebApplicationContext");
	}

	// Set the ServletContext that this WebApplicationContext runs in.
	@Override
	public void setServletContext(@Nullable ServletContext servletContext) {
		this.servletContext = servletContext;
	}

	@Override
	@Nullable
	public ServletContext getServletContext() {
		return servletContext;
	}

	@Override
	public void setServletConfig(@Nullable ServletConfig servletConfig) {
		this.servletConfig = servletConfig;
		if (servletConfig != null && this.servletContext == null) {
			this.servletContext = servletConfig.getServletContext();
		}
	}

	@Override
	@Nullable
	public ServletConfig getServletConfig() {
		return servletConfig;
	}

	@Override
	public void setNamespace(@Nullable String namespace) {
		this.namespace = namespace;
		if (namespace != null) {
			setDisplayName("WebApplicationContext for namespace '" + namespace + "'");
		}
	}

	@Override
	@Nullable
	public String getNamespace() {
		return namespace;
	}

	/**
	 * The {@link StaticWebApplicationContext} class does not support this method.
	 * @throws UnsupportedOperationException <b>always</b>
	 */
	@Override
	public void setConfigLocation(String configLocation) {
		throw new UnsupportedOperationException("StaticWebApplicationContext does not support config locations");
	}

	/**
	 * The {@link StaticWebApplicationContext} class does not support this method.
	 * @throws UnsupportedOperationException <b>always</b>
	 */
	@Override
	public void setConfigLocations(String... configLocations) {
		throw new UnsupportedOperationException("StaticWebApplicationContext does not support config locations");
	}

	@Override
	public String[] getConfigLocations() {
		return null;
	}

	/**
	 * Register request/session scopes, a {@link ServletContextAwareProcessor}, etc.
	 */
	@Override
	protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		beanFactory.addBeanPostProcessor(new ServletContextAwareProcessor(servletContext, servletConfig));
		beanFactory.ignoreDependencyInterface(ServletContextAware.class);
		beanFactory.ignoreDependencyInterface(ServletConfigAware.class);
		WebApplicationContextUtils.registerWebApplicationScopes(beanFactory, servletContext);
		WebApplicationContextUtils.registerEnvironmentBeans(beanFactory, servletContext, servletConfig);
	}

	/**
	 * This implementation supports file paths beneath the root of the ServletContext.
	 * @see ServletContextResource
	 */
	@Override
	protected Resource getResourceByPath(String path) {
		Assert.state(servletContext != null, "No ServletContext available");
		return new ServletContextResource(servletContext, path);
	}

	/**
	 * This implementation supports pattern matching in unexpanded WARs too.
	 * @see ServletContextResourcePatternResolver
	 */
	@Override
	protected ResourcePatternResolver getResourcePatternResolver() {
		return new ServletContextResourcePatternResolver(this);
	}

	/**
	 * Create and return a new {@link StandardServletEnvironment}.
	 */
	@Override
	protected ConfigurableEnvironment createEnvironment() {
		return new StandardServletEnvironment();
	}

	// Initialize the theme capability.
	@Override
	protected void onRefresh() {
		themeSource = UiApplicationContextUtils.initThemeSource(this);
	}

	@Override
	protected void initPropertySources() {
		WebApplicationContextUtils.initServletPropertySources(getEnvironment().getPropertySources(),servletContext, servletConfig);
	}

	@Override
	@Nullable
	public Theme getTheme(String themeName) {
		Assert.state(themeSource != null, "No ThemeSource available");
		return themeSource.getTheme(themeName);
	}
}
