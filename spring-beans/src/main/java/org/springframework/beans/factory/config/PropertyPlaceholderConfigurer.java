

package org.springframework.beans.factory.config;

import java.util.Properties;

import org.springframework.beans.BeansException;
import org.springframework.core.Constants;
import org.springframework.core.SpringProperties;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.lang.Nullable;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.PropertyPlaceholderHelper.PlaceholderResolver;
import org.springframework.util.StringValueResolver;

/**
 * {@link PlaceholderConfigurerSupport} subclass that resolves ${...} placeholders against {@link #setLocation local} {@link #setProperties properties} and/or system properties and environment variables.
 * As of Spring 3.1, {@link org.springframework.context.support.PropertySourcesPlaceholderConfigurer  PropertySourcesPlaceholderConfigurer} should be used preferentially over this implementation;
 * it is more flexible through taking advantage of the {@link org.springframework.core.env.Environment} and {@link org.springframework.core.env.PropertySource} mechanisms also made available in Spring 3.1.
 * {@link PropertyPlaceholderConfigurer} is still appropriate for use when:
 * <li>the {@code spring-context} module is not available (i.e., one is using Spring's {@code BeanFactory} API as opposed to {@code ApplicationContext}).
 * <li>existing configuration makes use of the {@link #setSystemPropertiesMode(int) "systemPropertiesMode"} and/or {@link #setSystemPropertiesModeName(String) "systemPropertiesModeName"} properties.
 * Users are encouraged to move away from using these settings, and rather configure property source search order through the container's {@code Environment}; however, exact preservation
 * of functionality may be maintained by continuing to use {@code PropertyPlaceholderConfigurer}.
 * @since 02.10.2003
 * @see #setSystemPropertiesModeName
 * @see PlaceholderConfigurerSupport
 * @see PropertyOverrideConfigurer
 * @see org.springframework.context.support.PropertySourcesPlaceholderConfigurer
 * @see BeanFactoryPostProcessor 的实现类
 * PropertyPlaceholderConfigurer可以将上下文（配置文 件）中的属性值放在另一个单独的标准java Properties文件中去。
 * 在XML文件中用${…}替换指定的properties文件中的值。
 * 这样的话，只需要对properties文件进 行修改，而不用对xml配置文件进行修改。
 *
 *
 * PropertyPlaceholderConfigurer
 *
 * 功能 : 解析和处理bean定义中属性值,构造函数参数值,和@Value注解中的占位符${...}
 * 属性源 : 所设置的Properties属性对象,属性文件,系统属性(system properties)，环境变量(environment variables)
 * 工作模式 : “拉”，遍历每个bean定义中的属性占位符，从属性源中拉取对应的属性值替换属性占位符
 * 从Spring 3.1 开始，推荐使用PropertySourcesPlaceholderConfigurer而不是PropertyPlaceholderConfigurer。
 *
 * PropertyOverrideConfigurer
 *
 * 功能 : 基于属性文件定义的bean属性值设置指令执行相应的bean属性值设置
 * 属性源 : 指定路径的属性文件
 * 工作模式: “推”,根据属性文件中的bean属性设置指令将属性值推送设置到相应的bean属性
 */
public class PropertyPlaceholderConfigurer extends PlaceholderConfigurerSupport {

	/** Never check system properties. */
	public static final int SYSTEM_PROPERTIES_MODE_NEVER = 0;

	/**
	 * Check system properties if not resolvable in the specified properties.
	 * This is the default.
	 */
	public static final int SYSTEM_PROPERTIES_MODE_FALLBACK = 1;

	/**
	 * Check system properties first, before trying the specified properties.
	 * This allows system properties to override any other property source.
	 */
	public static final int SYSTEM_PROPERTIES_MODE_OVERRIDE = 2;

	private static final Constants constants = new Constants(PropertyPlaceholderConfigurer.class);

	private int systemPropertiesMode = SYSTEM_PROPERTIES_MODE_FALLBACK;

	private boolean searchSystemEnvironment = !SpringProperties.getFlag(AbstractEnvironment.IGNORE_GETENV_PROPERTY_NAME);

	/**
	 * Set the system property mode by the name of the corresponding constant,
	 * e.g. "SYSTEM_PROPERTIES_MODE_OVERRIDE".
	 * @param constantName name of the constant
	 * @see #setSystemPropertiesMode
	 */
	public void setSystemPropertiesModeName(String constantName) throws IllegalArgumentException {
		this.systemPropertiesMode = constants.asNumber(constantName).intValue();
	}

	/**
	 * Set how to check system properties: as fallback, as override, or never.
	 * For example, will resolve ${user.dir} to the "user.dir" system property.
	 * The default is "fallback": If not being able to resolve a placeholder with the specified properties, a system property will be tried.
	 * "override" will check for a system property first, before trying the specified properties. "never" will not check system properties at all.
	 * @see #SYSTEM_PROPERTIES_MODE_NEVER
	 * @see #SYSTEM_PROPERTIES_MODE_FALLBACK
	 * @see #SYSTEM_PROPERTIES_MODE_OVERRIDE
	 * @see #setSystemPropertiesModeName
	 */
	public void setSystemPropertiesMode(int systemPropertiesMode) {
		this.systemPropertiesMode = systemPropertiesMode;
	}

	/**
	 * Set whether to search for a matching system environment variable if no matching system property has been found. Only applied when
	 * "systemPropertyMode" is active (i.e. "fallback" or "override"), right after checking JVM system properties.
	 * Default is "true". Switch this setting off to never resolve placeholders against system environment variables.
	 * Note that it is generally recommended to pass external values in as JVM system properties: This can easily be
	 * achieved in a startup script, even for existing environment variables.
	 * @see #setSystemPropertiesMode
	 * @see System#getProperty(String)
	 * @see System#getenv(String)
	 */
	public void setSearchSystemEnvironment(boolean searchSystemEnvironment) {
		this.searchSystemEnvironment = searchSystemEnvironment;
	}

	/**
	 * Resolve the given placeholder using the given properties, performing a system properties check according to the given mode.
	 * The default implementation delegates to {@code resolvePlaceholder (placeholder, props)} before/after the system properties check.
	 * Subclasses can override this for custom resolution strategies,including customized points for the system properties check.
	 * @param placeholder the placeholder to resolve
	 * @param props the merged properties of this configurer
	 * @param systemPropertiesMode the system properties mode,according to the constants in this class
	 * @return the resolved value, of null if none
	 * @see #setSystemPropertiesMode
	 * @see System#getProperty
	 * @see #resolvePlaceholder(String, java.util.Properties)
	 */
	@Nullable
	protected String resolvePlaceholder(String placeholder, Properties props, int systemPropertiesMode) {
		String propVal = null;
		if (systemPropertiesMode == SYSTEM_PROPERTIES_MODE_OVERRIDE) {
			propVal = resolveSystemProperty(placeholder);
		}
		if (propVal == null) {
			propVal = resolvePlaceholder(placeholder, props);
		}
		if (propVal == null && systemPropertiesMode == SYSTEM_PROPERTIES_MODE_FALLBACK) {
			propVal = resolveSystemProperty(placeholder);
		}
		return propVal;
	}

	/**
	 * Resolve the given placeholder using the given properties.
	 * The default implementation simply checks for a corresponding property key.
	 * Subclasses can override this for customized placeholder-to-key mappings
	 * or custom resolution strategies, possibly just using the given properties as fallback.
	 * Note that system properties will still be checked before respectively
	 * after this method is invoked, according to the system properties mode.
	 * @param placeholder the placeholder to resolve
	 * @param props the merged properties of this configurer
	 * @return the resolved value, of {@code null} if none
	 * @see #setSystemPropertiesMode
	 */
	@Nullable
	protected String resolvePlaceholder(String placeholder, Properties props) {
		return props.getProperty(placeholder);
	}

	/**
	 * Resolve the given key as JVM system property, and optionally also as system environment variable if no matching system property has been found.
	 * @param key the placeholder to resolve as system property key
	 * @return the system property value, or {@code null} if not found
	 * @see #setSearchSystemEnvironment
	 * @see System#getProperty(String)
	 * @see System#getenv(String)
	 */
	@Nullable
	protected String resolveSystemProperty(String key) {
		try {
			String value = System.getProperty(key);
			if (value == null && this.searchSystemEnvironment) {
				value = System.getenv(key);
			}
			return value;
		}catch (Throwable ex) {
			if (logger.isDebugEnabled()) logger.debug("Could not access system property '" + key + "': " + ex);
			return null;
		}
	}

	/**
	 * Visit each bean definition in the given bean factory and attempt to replace ${...} property
	 * placeholders with values from the given properties.
	 */
	@Override
	protected void processProperties(ConfigurableListableBeanFactory beanFactoryToProcess, Properties props) throws BeansException {
		StringValueResolver valueResolver = new PlaceholderResolvingStringValueResolver(props);
		doProcessProperties(beanFactoryToProcess, valueResolver);
	}

	private class PlaceholderResolvingStringValueResolver implements StringValueResolver {
		private final PropertyPlaceholderHelper helper;
		private final PlaceholderResolver resolver;

		public PlaceholderResolvingStringValueResolver(Properties props) {
			this.helper = new PropertyPlaceholderHelper(placeholderPrefix, placeholderSuffix, valueSeparator, ignoreUnresolvablePlaceholders);
			this.resolver = new PropertyPlaceholderConfigurerResolver(props);
		}

		@Override
		@Nullable
		public String resolveStringValue(String strVal) throws BeansException {
			String resolved = this.helper.replacePlaceholders(strVal, this.resolver);
			if (trimValues) {
				resolved = resolved.trim();
			}
			return (resolved.equals(nullValue) ? null : resolved);
		}
	}

	private final class PropertyPlaceholderConfigurerResolver implements PlaceholderResolver {

		private final Properties props;

		private PropertyPlaceholderConfigurerResolver(Properties props) {
			this.props = props;
		}

		@Override
		@Nullable
		public String resolvePlaceholder(String placeholderName) {
			return PropertyPlaceholderConfigurer.this.resolvePlaceholder(placeholderName,this.props, systemPropertiesMode);
		}
	}
}
