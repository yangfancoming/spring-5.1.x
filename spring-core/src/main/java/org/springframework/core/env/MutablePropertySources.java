package org.springframework.core.env;
import java.util.Iterator;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;
import org.springframework.lang.Nullable;

/**
 * The default implementation of the {@link PropertySources} interface.
 * Allows manipulation of contained property sources and provides a constructor for copying an existing {@code PropertySources} instance.
 * Where <em>precedence</em> is mentioned in methods such as {@link #addFirst} and {@link #addLast},
 * this is with regard to the order in which property sources will be searched when resolving a given property with a {@link PropertyResolver}.
 * @since 3.1
 * @see PropertySourcesPropertyResolver
 * 可变数据源 PropertySources 接口的唯一实现类，持有一个数据源列表 propertySourceList（使用List集合 有顺序优先级之分）。
 */
public class MutablePropertySources implements PropertySources {

	private final List<PropertySource<?>> propertySourceList = new CopyOnWriteArrayList<>();

	// Create a new {@link MutablePropertySources} object.
	public MutablePropertySources() {}

	//  Create a new {@code MutablePropertySources} from the given propertySources object, preserving the original order of contained {@code PropertySource} objects.
	public MutablePropertySources(PropertySources propertySources) {
		this();
		for (PropertySource<?> propertySource : propertySources) {
			addLast(propertySource);
		}
	}

	@Override
	public Iterator<PropertySource<?>> iterator() {
		return propertySourceList.iterator();
	}

	@Override
	public Spliterator<PropertySource<?>> spliterator() {
		return Spliterators.spliterator(propertySourceList, 0);
	}

	@Override
	public Stream<PropertySource<?>> stream() {
		return propertySourceList.stream();
	}

	@Override
	public boolean contains(String name) {
		return propertySourceList.contains(PropertySource.named(name));
	}

	@Override
	@Nullable
	public PropertySource<?> get(String name) {
		int index = propertySourceList.indexOf(PropertySource.named(name));
		return (index != -1 ? propertySourceList.get(index) : null);
	}

	// Add the given property source object with highest precedence.
	public void addFirst(PropertySource<?> propertySource) {
		removeIfPresent(propertySource);
		propertySourceList.add(0, propertySource);
	}

	// Add the given property source object with lowest precedence.
	public void addLast(PropertySource<?> propertySource) {
		removeIfPresent(propertySource);
		propertySourceList.add(propertySource);
	}

	// Add the given property source object with precedence immediately higher than the named relative property source.
	public void addBefore(String relativePropertySourceName, PropertySource<?> propertySource) {
		assertLegalRelativeAddition(relativePropertySourceName, propertySource);
		removeIfPresent(propertySource);
		int index = assertPresentAndGetIndex(relativePropertySourceName);
		addAtIndex(index, propertySource);
	}

	// Add the given property source object with precedence immediately lower than the named relative property source.
	public void addAfter(String relativePropertySourceName, PropertySource<?> propertySource) {
		assertLegalRelativeAddition(relativePropertySourceName, propertySource);
		removeIfPresent(propertySource);
		int index = assertPresentAndGetIndex(relativePropertySourceName);
		addAtIndex(index + 1, propertySource);
	}

	/**
	 * 获取指定属性源在集合中的优先级
	 * Return the precedence of the given property source, {@code -1} if not found.
	 * @param propertySource
	 * @return  返回-1  未找到指定数据源
	*/
	public int precedenceOf(PropertySource<?> propertySource) {
		return propertySourceList.indexOf(propertySource);
	}

	/**
	 * Remove and return the property source with the given name, {@code null} if not found.
	 * @param name the name of the property source to find and remove
	 */
	@Nullable
	public PropertySource<?> remove(String name) {
		int index = propertySourceList.indexOf(PropertySource.named(name));
		return (index != -1 ? propertySourceList.remove(index) : null);
	}

	/**
	 * Replace the property source with the given name with the given property source object.
	 * @param name the name of the property source to find and replace
	 * @param propertySource the replacement property source
	 * @throws IllegalArgumentException if no property source with the given name is present
	 * @see #contains
	 */
	public void replace(String name, PropertySource<?> propertySource) {
		int index = assertPresentAndGetIndex(name);
		propertySourceList.set(index, propertySource);
	}

	// Return the number of {@link PropertySource} objects contained.
	public int size() {
		return propertySourceList.size();
	}

	@Override
	public String toString() {
		return propertySourceList.toString();
	}

	//  Ensure that the given property source is not being added relative to itself.
	protected void assertLegalRelativeAddition(String relativePropertySourceName, PropertySource<?> propertySource) {
		String newPropertySourceName = propertySource.getName();
		if (relativePropertySourceName.equals(newPropertySourceName)) {
			throw new IllegalArgumentException("PropertySource named '" + newPropertySourceName + "' cannot be added relative to itself");
		}
	}

	// Remove the given property source if it is present.
	protected void removeIfPresent(PropertySource<?> propertySource) {
		propertySourceList.remove(propertySource);
	}

	//  Add the given property source at a particular index in the list.
	private void addAtIndex(int index, PropertySource<?> propertySource) {
		removeIfPresent(propertySource);
		propertySourceList.add(index, propertySource);
	}

	/**
	 * Assert that the named property source is present and return its index.
	 * @param name {@linkplain PropertySource#getName() name of the property source} to find
	 * @throws IllegalArgumentException if the named property source is not present
	 */
	private int assertPresentAndGetIndex(String name) {
		int index = propertySourceList.indexOf(PropertySource.named(name));
		if (index == -1) throw new IllegalArgumentException("PropertySource named '" + name + "' does not exist");
		return index;
	}
}
