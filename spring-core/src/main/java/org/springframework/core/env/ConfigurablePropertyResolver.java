package org.springframework.core.env;
import org.springframework.core.convert.support.ConfigurableConversionService;
import org.springframework.lang.Nullable;

/**
 * Configuration interface to be implemented by most if not all {@link PropertyResolver} types.
 *  Provides facilities for accessing and customizing the {@link org.springframework.core.convert.ConversionService ConversionService}
 *  used when converting property values from one type to another.
 * @since 3.1
 */
public interface ConfigurablePropertyResolver extends PropertyResolver {

	/**
	 * Return the {@link ConfigurableConversionService} used when performing type conversions on properties.
	 * The configurable nature of the returned conversion service allows for the convenient addition and removal of individual {@code Converter} instances:
	 * ConfigurableConversionService cs = env.getConversionService();
	 * cs.addConverter(new FooConverter());
	 * @see PropertyResolver#getProperty(String, Class)
	 * @see org.springframework.core.convert.converter.ConverterRegistry#addConverter
	 * 返回在解析属性时使用的ConfigurableConversionService。此方法的返回值可被用户定制化set
	 * 例如可以移除或者添加Converter  cs.addConverter(new FooConverter());等等
	 * 默认值使用的DefaultConversionService
	 */
	ConfigurableConversionService getConversionService();

	/**
	 * Set the {@link ConfigurableConversionService} to be used when performing type conversions on properties.
	 * <strong>Note:</strong> as an alternative to fully replacing the {@code ConversionService},
	 * consider adding or removing individual {@code Converter} instances by drilling into {@link #getConversionService()}
	 * and calling methods such as {@code #addConverter}.
	 * @see PropertyResolver#getProperty(String, Class)
	 * @see #getConversionService()
	 * @see org.springframework.core.convert.converter.ConverterRegistry#addConverter
	 * 全部替换ConfigurableConversionService的操作(不常用)  一般还是get出来操作它内部的东东
	 */
	void setConversionService(ConfigurableConversionService conversionService);

	// Set the prefix that placeholders replaced by this resolver must begin with. 设置占位符的前缀  后缀    默认是${}
	void setPlaceholderPrefix(String placeholderPrefix);

	// Set the suffix that placeholders replaced by this resolver must end with.
	void setPlaceholderSuffix(String placeholderSuffix);

	/**
	 * Specify the separating character between the placeholders replaced by this  resolver and their associated default value,
	 * or {@code null} if no such special character should be processed as a value separator.
	 * 默认值的分隔符   默认为冒号:
	 */
	void setValueSeparator(@Nullable String valueSeparator);

	/**
	 * Set whether to throw an exception when encountering an unresolvable placeholder nested within the value of a given property.
	 * A {@code false} value indicates strict  resolution, i.e. that an exception will be thrown.
	 * A {@code true} value indicates that unresolvable nested placeholders should be passed through in their unresolved ${...} form.
	 * Implementations of {@link #getProperty(String)} and its variants must inspect the value set here to determine correct behavior when property values contain unresolvable placeholders.
	 * 是否忽略解析不了的占位符，默认是false  表示不忽略~~~（解析不了就抛出异常）
	 * @since 3.2
	 */
	void setIgnoreUnresolvableNestedPlaceholders(boolean ignoreUnresolvableNestedPlaceholders);

	/**
	 * Specify which properties must be present, to be verified by {@link #validateRequiredProperties()}.
	 */
	void setRequiredProperties(String... requiredProperties);

	/**
	 * 校检requiredProperties中是否有对应的值，如果没有则会抛出异常
	 * @see AbstractPropertyResolver#requiredProperties
	 * Validate that each of the properties specified by {@link #setRequiredProperties} is present and resolves to a non-{@code null} value.
	 * @throws MissingRequiredPropertiesException if any of the required properties are not resolvable.
	 * 检查环境变量的核心方法为，简单来说就是如果存在环境变量的value 为空的时候就抛异常，然后停止启动Spring
	 * 基于这个特性我们可以做一些扩展，提前在集合`requiredProperties`中放入我们这个项目必须存在的一些环境变量。
	 * 假说我们的生产环境数据库地址、用户名和密码都是使用环境变量的方式注入进去来代替测试环境的配置，
	 * 那么就可以在这里添加这个校验，在程序刚启动的时候就能发现问题
	 */
	void validateRequiredProperties() throws MissingRequiredPropertiesException;
}
