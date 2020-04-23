

package org.springframework.context.support;

import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * {@link AbstractRefreshableApplicationContext} subclass that adds common handling f specified config locations.
 * Serves as base class for XML-based application context implementations such as {@link ClassPathXmlApplicationContext} and {@link FileSystemXmlApplicationContext},
 * as well as {@link org.springframework.web.context.support.XmlWebApplicationContext}.
 * @since 2.5.2
 * @see #setConfigLocation
 * @see #setConfigLocations
 * @see #getDefaultConfigLocations
 */
public abstract class AbstractRefreshableConfigApplicationContext extends AbstractRefreshableApplicationContext implements BeanNameAware, InitializingBean {

	// 设置配置文件存放位置： ApplicationContext ac = new ClassPathXmlApplicationContext("classpath:beans.xml"); 中的配置字符串数组："classpath:beans.xml"
	@Nullable
	private String[] configLocations;

	private boolean setIdCalled = false;

	/**
	 * Create a new AbstractRefreshableConfigApplicationContext with no parent.
	 */
	public AbstractRefreshableConfigApplicationContext() {}

	/**
	 * Create a new AbstractRefreshableConfigApplicationContext with the given parent context.
	 * @param parent the parent context
	 */
	public AbstractRefreshableConfigApplicationContext(@Nullable ApplicationContext parent) {
		super(parent);
	}

	/**
	 * 处理单个资源文件路径为一个字符串的情况
	 * Set the config locations for this application context in init-param style,
	 * i.e. with distinct locations separated by commas, semicolons or whitespace.
	 * If not set, the implementation may use a default as appropriate.
	 */
	public void setConfigLocation(String location) {
		//即多个资源文件路径之间用” ,; /t/n”分隔，解析成数组形式
		setConfigLocations(StringUtils.tokenizeToStringArray(location, CONFIG_LOCATION_DELIMITERS));
	}

	//  Set the config locations for this application context.If not set, the implementation may use a default as appropriate.
	public void setConfigLocations(@Nullable String... locations) {
		if (locations != null) {
			Assert.noNullElements(locations, "Config locations must not be null");
			this.configLocations = new String[locations.length];
			for (int i = 0; i < locations.length; i++) {
				this.configLocations[i] = resolvePath(locations[i]).trim();
			}
		}else {
			this.configLocations = null;
		}
	}

	/**
	 * Return an array of resource locations, referring to the XML bean definition
	 * files that this context should be built with. Can also include location
	 * patterns, which will get resolved via a ResourcePatternResolver.
	 * The default implementation returns {@code null}. Subclasses can override
	 * this to provide a set of resource locations to load bean definitions from.
	 * @return an array of resource locations, or {@code null} if none
	 * @see #getResources
	 * @see #getResourcePatternResolver
	 */
	@Nullable
	protected String[] getConfigLocations() {
		return (configLocations != null ? configLocations : getDefaultConfigLocations());
	}

	/**
	 * Return the default config locations to use, for the case where no explicit config locations have been specified.
	 * 对于没有指定显式配置位置的情况，返回要使用的默认配置位置
	 * The default implementation returns {@code null},  requiring explicit config locations.
	 * @return an array of default config locations, if any
	 * @see #setConfigLocations
	 */
	@Nullable
	protected String[] getDefaultConfigLocations() {
		return null;
	}

	/**
	 * Resolve the given path, replacing placeholders with corresponding environment property values if necessary.Applied to config locations.
	 * @param path the original file path
	 * @return the resolved file path
	 * @see org.springframework.core.env.Environment#resolveRequiredPlaceholders(String)
	 * 此方法的目的在于将占位符(placeholder)解析成实际的地址。
	 * 比如可以这么写: new ClassPathXmlApplicationContext("classpath:config.xml");那么classpath:就是需要被解析的。
	 * 我们有不同的运行环境，dev，test 或者 prod，这个时候加载的配置文件和属性应该有所不同，这个时候就需要使用到 Environment 来进行区分。
	 */
	protected String resolvePath(String path) {
		return getEnvironment().resolveRequiredPlaceholders(path);
	}

	@Override
	public void setId(String id) {
		super.setId(id);
		this.setIdCalled = true;
	}

	//---------------------------------------------------------------------
	// Implementation of 【BeanNameAware】 interface
	//---------------------------------------------------------------------
	/**
	 * Sets the id of this context to the bean name by default,for cases where the context instance is itself defined as a bean.
	 */
	@Override
	public void setBeanName(String name) {
		if (!this.setIdCalled) {
			super.setId(name);
			setDisplayName("ApplicationContext '" + name + "'");
		}
	}

	//---------------------------------------------------------------------
	// Implementation of 【InitializingBean】 interface
	//---------------------------------------------------------------------
	/**
	 * Triggers {@link #refresh()} if not refreshed in the concrete context's constructor already.
	 */
	@Override
	public void afterPropertiesSet() {
		if (!isActive()) {
			refresh();
		}
	}

}
