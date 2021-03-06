package org.springframework.core.env;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;

/**
 * Abstract base class representing a source of name/value property pairs.
 * The underlying {@linkplain #getSource() source object} may be of any type {@code T} that encapsulates properties.
 * Examples include {@link java.util.Properties} objects, {@link java.util.Map} objects, {@code ServletContext} and {@code ServletConfig} objects (for access to init parameters).
 * Explore the {@code PropertySource} type hierarchy to see provided implementations.
 * {@code PropertySource} objects are not typically used in isolation, but rather through a {@link PropertySources} object,
 * which aggregates property sources and in conjunction with a {@link PropertyResolver} implementation that can perform precedence-based searches across the set of {@code PropertySources}.
 * {@code PropertySource} identity is determined not based on the content of encapsulated properties, but rather based on the {@link #getName() name} of the {@code PropertySource} alone.
 * This is useful for manipulating {@code PropertySource} objects when in collection contexts.
 * See operations in {@link MutablePropertySources} as well as the {@link #named(String)} and {@link #toString()} methods for details.
 * Note that when working with @{@link org.springframework.context.annotation.Configuration Configuration} classes that the @{@link org.springframework.context.annotation.PropertySource PropertySource}
 * annotation provides a convenient and declarative way of adding property sources to the enclosing {@code Environment}.
 * @since 3.1
 * @param <T> the source type
 * @see PropertySources
 * @see PropertyResolver
 * @see PropertySourcesPropertyResolver
 * @see MutablePropertySources
 * @see org.springframework.context.annotation.PropertySource
 */
public abstract class PropertySource<T> {

	protected final Log logger = LogFactory.getLog(getClass());

	// 该属性源的名字
	protected final String name;

	// 待解析的属性源对象
	protected final T source;

	// Create a new {@code PropertySource} with the given name and source object.
	public PropertySource(String name, T source) {
		Assert.hasText(name, "Property source name must contain at least one character");
		Assert.notNull(source, "Property source must not be null");
		this.name = name;
		this.source = source;
	}

	/**
	 * Create a new {@code PropertySource} with the given name and with a new {@code Object} instance as the underlying source.
	 * Often useful in testing scenarios when creating anonymous implementations that never query an actual source but rather return hard-coded values.
	 * 若没有指定source 默认就是object  而不是null
	 */
	@SuppressWarnings("unchecked")
	public PropertySource(String name) {
		this(name, (T) new Object());
	}

	// Return the name of this {@code PropertySource}.
	public String getName() {
		return this.name;
	}

	// Return the underlying source object for this {@code PropertySource}.
	public T getSource() {
		return this.source;
	}

	/**
	 * Return whether this {@code PropertySource} contains the given name.
	 * This implementation simply checks for a {@code null} return value from {@link #getProperty(String)}.
	 * Subclasses may wish to implement a more efficient algorithm if possible.
	 * @param name the property name to find
	 *  小细节：若对应的key存在但是值为null，此处也是返回false的  表示不包含~
	 */
	public boolean containsProperty(String name) {
		return (getProperty(name) != null);
	}

	/**
	 * Return the value associated with the given name, or {@code null} if not found.
	 * @param name the property to find
	 * @see PropertyResolver#getRequiredProperty(String)
	 * 抽象方法  子类去实现~~~
	 *
	 */
	@Nullable
	public abstract Object getProperty(String name);

	//  该类重写了equals()和hashCode()方法，所以对于List的remove、indexOf方法都是有影响的~~~后续会看到
	/**
	 * This {@code PropertySource} object is equal to the given object if:
	 * <li>they are the same instance
	 * <li>the {@code name} properties for both objects are equal
	 * No properties other than {@code name} are evaluated.
	 * 此处特别特别注意重写的这两个方法，我们发现它只和name有关，只要name相等  就代表着是同一个对象~~~~ 这点特别重要~
	 */
	@Override
	public boolean equals(Object other) {
		return (this == other || (other instanceof PropertySource && ObjectUtils.nullSafeEquals(this.name, ((PropertySource<?>) other).name)));
	}

	/**
	 * Return a hash code derived from the {@code name} property of this {@code PropertySource} object.
	 */
	@Override
	public int hashCode() {
		return ObjectUtils.nullSafeHashCode(this.name);
	}

	/**
	 * Produce concise output (type and name) if the current log level does not include debug.
	 * If debug is enabled, produce verbose output including the hash code of the PropertySource instance and every name/value property pair.
	 * This variable verbosity is useful as a property source such as system properties or environment variables may contain an arbitrary number of property pairs,
	 * potentially leading to difficult to read exception and log messages.
	 * @see Log#isDebugEnabled()
	 */
	@Override
	public String toString() {
		if (logger.isDebugEnabled()) {
			return getClass().getSimpleName() + "@" + System.identityHashCode(this) + " {name='" + this.name + "', properties=" + this.source + "}";
		} else {
			return getClass().getSimpleName() + " {name='" + this.name + "'}";
		}
	}

	/**
	 * Return a {@code PropertySource} implementation intended for collection comparison purposes only.
	 * Primarily for internal use, but given a collection of {@code PropertySource} objects, may be used as follows:
	 * {@code List<PropertySource<?>> sources = new ArrayList<PropertySource<?>>();
	 * sources.add(new MapPropertySource("sourceA", mapA));
	 * sources.add(new MapPropertySource("sourceB", mapB));
	 * assert sources.contains(PropertySource.named("sourceA"));
	 * assert sources.contains(PropertySource.named("sourceB"));
	 * assert !sources.contains(PropertySource.named("sourceC"));
	 * The returned {@code PropertySource} will throw {@code UnsupportedOperationException}
	 * if any methods other than {@code equals(Object)}, {@code hashCode()}, and {@code toString()} are called.
	 * @param name the name of the comparison {@code PropertySource} to be created and returned.
	 * 静态方法：根据name就创建一个属性源~  ComparisonPropertySource是StubPropertySource的子类~
	 */
	public static PropertySource<?> named(String name) {
		return new ComparisonPropertySource(name);
	}

	/**
	 * {@code PropertySource} to be used as a placeholder in cases where an actual  property source cannot be eagerly initialized at application context creation time.
	 * For example, a {@code ServletContext}-based property source must wait until the {@code ServletContext} object is available to its enclosing {@code ApplicationContext}.
	 * In such cases, a stub should be used to hold the intended default position/order of the property source, then be replaced during context refresh.
	 * @see org.springframework.context.support.AbstractApplicationContext#initPropertySources()
	 * @see org.springframework.web.context.support.StandardServletEnvironment
	 * @see org.springframework.web.context.support.ServletContextPropertySource
	 */
	public static class StubPropertySource extends PropertySource<Object> {
		public StubPropertySource(String name) {
			super(name, new Object());
		}
		//  Always returns {@code null}.
		@Override
		@Nullable
		public String getProperty(String name) {
			return null;
		}
	}

	/**
	 * A {@code PropertySource} implementation intended for collection comparison purposes.
	 * @see PropertySource#named(String)
	 */
	static class ComparisonPropertySource extends StubPropertySource {

		private static final String USAGE_ERROR = "ComparisonPropertySource instances are for use with collection comparison only";

		public ComparisonPropertySource(String name) {
			super(name);
		}

		@Override
		public Object getSource() {
			throw new UnsupportedOperationException(USAGE_ERROR);
		}

		@Override
		public boolean containsProperty(String name) {
			throw new UnsupportedOperationException(USAGE_ERROR);
		}

		@Override
		@Nullable
		public String getProperty(String name) {
			throw new UnsupportedOperationException(USAGE_ERROR);
		}
	}
}
