package org.springframework.core.env;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.PropertyPlaceholderHelper;
import org.springframework.util.SystemPropertyUtils;

/**
 * Abstract base class for resolving properties against any underlying source.
 * 用于针对任何基础源解析属性的抽象基类
 * 它是对ConfigurablePropertyResolver的一个抽象实现，实现了了所有的接口方法，并且只提供一个抽象方法给子类去实现~~~
 * @since 3.1
 */
public abstract class AbstractPropertyResolver implements ConfigurablePropertyResolver {

	protected final Log logger = LogFactory.getLog(getClass());

	@Nullable
	private volatile ConfigurableConversionService conversionService;

	@Nullable
	private PropertyPlaceholderHelper nonStrictHelper;

	@Nullable
	private PropertyPlaceholderHelper strictHelper;

	private boolean ignoreUnresolvableNestedPlaceholders = false;

	private String placeholderPrefix = SystemPropertyUtils.PLACEHOLDER_PREFIX;

	private String placeholderSuffix = SystemPropertyUtils.PLACEHOLDER_SUFFIX;

	@Nullable
	private String valueSeparator = SystemPropertyUtils.VALUE_SEPARATOR;

	// 存储需要严格校检的属性，是否有对应的值，如果没有则会抛出异常
	private final Set<String> requiredProperties = new LinkedHashSet<>();

	//---------------------------------------------------------------------
	// Implementation of 【ConfigurablePropertyResolver】 interface
	//---------------------------------------------------------------------
	@Override
	public ConfigurableConversionService getConversionService() {
		// Need to provide an independent DefaultConversionService, not the shared DefaultConversionService used by PropertySourcesPropertyResolver.
		ConfigurableConversionService cs = conversionService;
		if (cs == null) {
			synchronized (this) {
				cs = conversionService;
				if (cs == null) {
					cs = new DefaultConversionService(); // 默认值使用的DefaultConversionService
					conversionService = cs;
				}
			}
		}
		return cs;
	}

	@Override
	public void setConversionService(ConfigurableConversionService conversionService) {
		Assert.notNull(conversionService, "ConversionService must not be null");
		this.conversionService = conversionService;
	}

	/**
	 * Set the prefix that placeholders replaced by this resolver must begin with.The default is "${".
	 * @see org.springframework.util.SystemPropertyUtils#PLACEHOLDER_PREFIX
	 */
	@Override
	public void setPlaceholderPrefix(String placeholderPrefix) {
		Assert.notNull(placeholderPrefix, "'placeholderPrefix' must not be null");
		this.placeholderPrefix = placeholderPrefix;
	}

	/**
	 * Set the suffix that placeholders replaced by this resolver must end with.The default is "}".
	 * @see org.springframework.util.SystemPropertyUtils#PLACEHOLDER_SUFFIX
	 */
	@Override
	public void setPlaceholderSuffix(String placeholderSuffix) {
		Assert.notNull(placeholderSuffix, "'placeholderSuffix' must not be null");
		this.placeholderSuffix = placeholderSuffix;
	}

	/**
	 * Specify the separating character between the placeholders replaced by this
	 * resolver and their associated default value, or {@code null} if no such
	 * special character should be processed as a value separator.The default is ":".
	 * @see org.springframework.util.SystemPropertyUtils#VALUE_SEPARATOR
	 */
	@Override
	public void setValueSeparator(@Nullable String valueSeparator) {
		this.valueSeparator = valueSeparator;
	}

	/**
	 * Set whether to throw an exception when encountering an unresolvable placeholder
	 * nested within the value of a given property. A {@code false} value indicates strict
	 * resolution, i.e. that an exception will be thrown. A {@code true} value indicates
	 * that unresolvable nested placeholders should be passed through in their unresolved ${...} form.
	 * The default is {@code false}.
	 * @since 3.2
	 */
	@Override
	public void setIgnoreUnresolvableNestedPlaceholders(boolean ignoreUnresolvableNestedPlaceholders) {
		this.ignoreUnresolvableNestedPlaceholders = ignoreUnresolvableNestedPlaceholders;
	}

	/**
	 *  添加应用所必须的属性！   全局唯一入口
	 * @see com.goat.chapter201.extend.MyApplicationContext#initPropertySources() 【测试用例】
	*/
	@Override
	public void setRequiredProperties(String... requiredProperties) {
		for (String key : requiredProperties) {
			this.requiredProperties.add(key);
		}
	}

	@Override
	public void validateRequiredProperties() {
		MissingRequiredPropertiesException ex = new MissingRequiredPropertiesException();
		for (String key : requiredProperties) {
			// 如果存在属性缺失，记录下来
			if (this.getProperty(key) == null) {
				ex.addMissingRequiredProperty(key);
			}
		}
		// 存在缺失属性则抛出异常
		if (!ex.getMissingRequiredProperties().isEmpty()) {
			throw ex;
		}
	}

	//---------------------------------------------------------------------
	// Implementation of 【PropertyResolver】 interface
	//---------------------------------------------------------------------
	@Override
	public boolean containsProperty(String key) {
		return (getProperty(key) != null);
	}

	@Override
	@Nullable
	public String getProperty(String key) {
		return getProperty(key, String.class);
	}

	@Override
	public String getProperty(String key, String defaultValue) {
		String value = getProperty(key);
		return (value != null ? value : defaultValue);
	}

	@Override
	public <T> T getProperty(String key, Class<T> targetType, T defaultValue) {
		T value = getProperty(key, targetType);
		return (value != null ? value : defaultValue);
	}

	@Override
	public String getRequiredProperty(String key) throws IllegalStateException {
		String value = getProperty(key);
		if (value == null) throw new IllegalStateException("Required key '" + key + "' not found");
		return value;
	}

	@Override
	public <T> T getRequiredProperty(String key, Class<T> valueType) throws IllegalStateException {
		T value = getProperty(key, valueType);
		if (value == null) throw new IllegalStateException("Required key '" + key + "' not found");
		return value;
	}

	// 非严格解析${...}这种类型的占位符，把他们替换为使用getProperty方法返回的结果，解析不了并且没有默认值的占位符会被忽略（原样输出）
	@Override
	public String resolvePlaceholders(String text) {
		if (nonStrictHelper == null) nonStrictHelper = createPlaceholderHelper(true);
		return doResolvePlaceholders(text, nonStrictHelper);
	}

	// 严格解析${...}这种类型的占位符，把他们替换为使用getProperty方法返回的结果，解析不了则抛异常！
	@Override
	public String resolveRequiredPlaceholders(String text) throws IllegalArgumentException {
		if (strictHelper == null) strictHelper = createPlaceholderHelper(false);
		return doResolvePlaceholders(text, strictHelper);
	}

	/**
	 * 解析嵌套占位符
	 * Resolve placeholders within the given string, deferring to the value of {@link #setIgnoreUnresolvableNestedPlaceholders}
	 * to determine whether any unresolvable placeholders should raise an exception or be ignored.
	 * Invoked from {@link #getProperty} and its variants, implicitly resolving nested placeholders.
	 * In contrast, {@link #resolvePlaceholders} and {@link #resolveRequiredPlaceholders} do <i>not</i>
	 * delegate to this method but rather perform their own handling of unresolvable placeholders, as specified by each of those methods.
	 * @param value 一般情况下都是字符串类型
	 * @since 3.2
	 * @see #setIgnoreUnresolvableNestedPlaceholders
	 */
	protected String resolveNestedPlaceholders(String value) {
		return (ignoreUnresolvableNestedPlaceholders ? resolvePlaceholders(value) : resolveRequiredPlaceholders(value));
	}

	/**
	 * 创建占位符解析辅助器  默认配置为： "${ }"   ":"
	*/
	private PropertyPlaceholderHelper createPlaceholderHelper(boolean ignoreUnresolvablePlaceholders) {
		return new PropertyPlaceholderHelper(placeholderPrefix, placeholderSuffix,valueSeparator, ignoreUnresolvablePlaceholders);
	}

	/**
	 * 使用指定的占位符解析辅助器，解析指定占位符字符串。
	 * @param text  待解析的占位符字符串
	 * @param helper  使用指定的占位符解析辅助器
	*/
	private String doResolvePlaceholders(String text, PropertyPlaceholderHelper helper) {
		return helper.replacePlaceholders(text, this::getPropertyAsRawString);
	}

	/**
	 * Convert the given value to the specified target type, if necessary.
	 * @param value the original property value
	 * @param targetType the specified target type for property retrieval
	 * @return the converted value, or the original value if no conversion is necessary
	 * @since 4.3.5
	 * 抽象类提供这个类型转换的方法~ 需要类型转换的会调用它
	 * 显然它是委托给了ConversionService，而这个类在前面文章已经都重点分析过了~
	 */
	@SuppressWarnings("unchecked")
	@Nullable
	protected <T> T convertValueIfNecessary(Object value, @Nullable Class<T> targetType) {
		if (targetType == null) return (T) value;
		ConversionService conversionServiceToUse = conversionService;
		if (conversionServiceToUse == null) {
			// Avoid initialization of shared DefaultConversionService if  no standard type conversion is needed in the first place...
			if (ClassUtils.isAssignableValue(targetType, value)) {
				return (T) value;
			}
			conversionServiceToUse = DefaultConversionService.getSharedInstance();
		}
		return conversionServiceToUse.convert(value, targetType);
	}

	/**
	 * Retrieve the specified property as a raw String, i.e. without resolution of nested placeholders.
	 * @param key the property name to resolve
	 * @return the property value or {@code null} if none found
	 */
	@Nullable
	protected abstract String getPropertyAsRawString(String key);
}
