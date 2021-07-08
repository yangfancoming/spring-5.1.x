package org.springframework.core.env;
import org.springframework.lang.Nullable;

/**
 * {@link PropertyResolver} implementation that resolves property values against an underlying set of {@link PropertySources}.
 * @since 3.1
 * @see PropertySource
 * @see PropertySources
 * @see AbstractEnvironment
 * 从上面知道AbstractPropertyResolver封装了解析占位符的具体实现。PropertySourcesPropertyResolver作为它的子类它只需要提供数据源，所以它主要是负责提供数据源。
 *  PropertySource：就是我们所说的数据源，它是Spring一个非常重要的概念，比如可以来自Map，来自命令行、来自自定义等等~~~
 */
public class PropertySourcesPropertyResolver extends AbstractPropertyResolver {

	// 待解析的属性源集合
	@Nullable
	private final PropertySources propertySources;

	/**
	 * Create a new resolver against the given property sources.
	 * @param propertySources the set of {@link PropertySource} objects to use
	 * 唯一构造函数：必须指定数据源~
	 */
	public PropertySourcesPropertyResolver(@Nullable PropertySources propertySources) {
		this.propertySources = propertySources;
	}

	//---------------------------------------------------------------------
	// Implementation of 【AbstractPropertyResolver】 class
	//---------------------------------------------------------------------
	@Override
	public boolean containsProperty(String key) {
		if (this.propertySources != null) {
			for (PropertySource<?> propertySource : this.propertySources) {
				if (propertySource.containsProperty(key)) {
					return true;
				}
			}
		}
		return false;
	}

	@Override
	@Nullable
	public String getProperty(String key) {
		return getProperty(key, String.class, true);
	}

	@Override
	@Nullable
	protected String getPropertyAsRawString(String key) {
		return getProperty(key, String.class, false);
	}

	//---------------------------------------------------------------------
	// Implementation of 【PropertyResolver】 interface
	//---------------------------------------------------------------------
	@Override
	@Nullable
	public <T> T getProperty(String key, Class<T> targetValueType) {
		return getProperty(key, targetValueType, true);
	}


	/**
	 * 根据指定key获取对应value
	 * @param key
	 * @param targetValueType  指定value返回值的类型
	 * @param resolveNestedPlaceholders  指定value是否为嵌套占位符
	*/
	@Nullable
	protected <T> T getProperty(String key, Class<T> targetValueType, boolean resolveNestedPlaceholders) {
		if (this.propertySources != null) {
			for (PropertySource<?> propertySource : this.propertySources) {
				if (logger.isTraceEnabled()) logger.trace("Searching for key '" + key + "' in PropertySource '" + propertySource.getName() + "'");
				Object value = propertySource.getProperty(key);
				if (value != null) {
					// 如果目标值为嵌套占位符，且是字符串的话 就继续处理
					if (resolveNestedPlaceholders && value instanceof String) {
						value = resolveNestedPlaceholders((String) value);
					}
					logKeyFound(key, propertySource, value);
					return convertValueIfNecessary(value, targetValueType);
				}
			}
		}
		if (logger.isTraceEnabled()) logger.trace("Could not find key '" + key + "' in any property source");
		return null;
	}

	/**
	 * Log the given key as found in the given {@link PropertySource}, resulting in the given value.
	 * The default implementation writes a debug log message with key and source.
	 * As of 4.3.3, this does not log the value anymore in order to avoid accidental logging of sensitive settings.
	 * Subclasses may override this method to change the log level and/or log message, including the property's value if desired.
	 * @param key the key found
	 * @param propertySource the {@code PropertySource} that the key has been found in
	 * @param value the corresponding value
	 * @since 4.3.1
	 */
	protected void logKeyFound(String key, PropertySource<?> propertySource, Object value) {
		if (logger.isDebugEnabled()) {
			logger.debug("Found key '" + key + "' in PropertySource '" + propertySource.getName() + "' with value of type " + value.getClass().getSimpleName());
		}
	}
}
