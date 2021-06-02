

package org.springframework.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.Stream;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * The default implementation of the {@link PropertyValues} interface.
 * Allows simple manipulation of properties, and provides constructors to support deep copy and construction from a Map.
 * @since 13 May 2001
 */
@SuppressWarnings("serial")
public class MutablePropertyValues implements PropertyValues, Serializable {

	private final List<PropertyValue> propertyValueList;

	@Nullable
	private Set<String> processedProperties;

	private volatile boolean converted = false;

	/**
	 * Creates a new empty MutablePropertyValues object. Property values can be added with the {@code add} method.
	 * @see #add(String, Object)
	 */
	public MutablePropertyValues() {
		propertyValueList = new ArrayList<>(0);
	}

	/**
	 * Deep copy constructor.
	 * Guarantees PropertyValue references  are independent, although it can't deep copy objects currently referenced by individual PropertyValue objects.
	 * @param original the PropertyValues to copy
	 * @see #addPropertyValues(PropertyValues)
	 */
	public MutablePropertyValues(@Nullable PropertyValues original) {
		// We can optimize this because it's all new: There is no replacement of existing property values.
		if (original != null) {
			PropertyValue[] pvs = original.getPropertyValues();
			propertyValueList = new ArrayList<>(pvs.length);
			for (PropertyValue pv : pvs) {
				propertyValueList.add(new PropertyValue(pv));
			}
		}else {
			propertyValueList = new ArrayList<>(0);
		}
	}

	/**
	 * Construct a new MutablePropertyValues object from a Map.
	 * @param original a Map with property values keyed by property name Strings
	 * @see #addPropertyValues(Map)
	 */
	public MutablePropertyValues(@Nullable Map<?, ?> original) {
		// We can optimize this because it's all new: There is no replacement of existing property values.
		if (original != null) {
			propertyValueList = new ArrayList<>(original.size());
			original.forEach((attrName, attrValue) -> propertyValueList.add(new PropertyValue(attrName.toString(), attrValue)));
		}else {
			propertyValueList = new ArrayList<>(0);
		}
	}

	/**
	 * Construct a new MutablePropertyValues object using the given List of  PropertyValue objects as-is.
	 * This is a constructor for advanced usage scenarios.It is not intended for typical programmatic use.
	 * @param propertyValueList a List of PropertyValue objects
	 */
	public MutablePropertyValues(@Nullable List<PropertyValue> propertyValueList) {
		this.propertyValueList = (propertyValueList != null ? propertyValueList : new ArrayList<>());
	}


	/**
	 * Return the underlying List of PropertyValue objects in its raw form. The returned List can be modified directly, although this is not recommended.
	 * This is an accessor for optimized access to all PropertyValue objects.It is not intended for typical programmatic use.
	 */
	public List<PropertyValue> getPropertyValueList() {
		return propertyValueList;
	}

	/**
	 * Return the number of PropertyValue entries in the list.
	 */
	public int size() {
		return propertyValueList.size();
	}

	/**
	 * Copy all given PropertyValues into this object. Guarantees PropertyValue references are independent,
	 * although it can't deep copy objects currently referenced by individual PropertyValue objects.
	 * @param other the PropertyValues to copy
	 * @return this in order to allow for adding multiple property values in a chain
	 */
	public MutablePropertyValues addPropertyValues(@Nullable PropertyValues other) {
		if (other != null) {
			PropertyValue[] pvs = other.getPropertyValues();
			for (PropertyValue pv : pvs) {
				addPropertyValue(new PropertyValue(pv));
			}
		}
		return this;
	}

	/**
	 * Add all property values from the given Map.
	 * @param other a Map with property values keyed by property name, which must be a String
	 * @return this in order to allow for adding multiple property values in a chain
	 */
	public MutablePropertyValues addPropertyValues(@Nullable Map<?, ?> other) {
		if (other != null) {
			other.forEach((attrName, attrValue) -> addPropertyValue(new PropertyValue(attrName.toString(), attrValue)));
		}
		return this;
	}

	/**
	 * Add a PropertyValue object, replacing any existing one for the corresponding property or getting merged with it (if applicable).
	 * @param pv the PropertyValue object to add
	 * @return this in order to allow for adding multiple property values in a chain
	 */
	public MutablePropertyValues addPropertyValue(PropertyValue pv) {
		for (int i = 0; i < propertyValueList.size(); i++) {
			PropertyValue currentPv = propertyValueList.get(i);
			if (currentPv.getName().equals(pv.getName())) {
				pv = mergeIfRequired(pv, currentPv);
				setPropertyValueAt(pv, i);
				return this;
			}
		}
		propertyValueList.add(pv);
		return this;
	}

	/**
	 * Overloaded version of {@code addPropertyValue} that takes a property name and a property value.
	 * Note: As of Spring 3.0, we recommend using the more concise and chaining-capable variant {@link #add}.
	 * @param propertyName name of the property
	 * @param propertyValue value of the property
	 * @see #addPropertyValue(PropertyValue)
	 */
	public void addPropertyValue(String propertyName, Object propertyValue) {
		addPropertyValue(new PropertyValue(propertyName, propertyValue));
	}

	/**
	 * Add a PropertyValue object, replacing any existing one for the corresponding property or getting merged with it (if applicable).
	 * @param propertyName name of the property
	 * @param propertyValue value of the property
	 * @return this in order to allow for adding multiple property values in a chain
	 */
	public MutablePropertyValues add(String propertyName, @Nullable Object propertyValue) {
		addPropertyValue(new PropertyValue(propertyName, propertyValue));
		return this;
	}

	/**
	 * Modify a PropertyValue object held in this object.Indexed from 0.
	 */
	public void setPropertyValueAt(PropertyValue pv, int i) {
		propertyValueList.set(i, pv);
	}

	/**
	 * Merges the value of the supplied 'new' {@link PropertyValue} with that of the current {@link PropertyValue} if merging is supported and enabled.
	 * @see Mergeable
	 */
	private PropertyValue mergeIfRequired(PropertyValue newPv, PropertyValue currentPv) {
		Object value = newPv.getValue();
		if (value instanceof Mergeable) {
			Mergeable mergeable = (Mergeable) value;
			if (mergeable.isMergeEnabled()) {
				Object merged = mergeable.merge(currentPv.getValue());
				return new PropertyValue(newPv.getName(), merged);
			}
		}
		return newPv;
	}

	/**
	 * Remove the given PropertyValue, if contained.
	 * @param pv the PropertyValue to remove
	 */
	public void removePropertyValue(PropertyValue pv) {
		propertyValueList.remove(pv);
	}

	/**
	 * Overloaded version of {@code removePropertyValue} that takes a property name.
	 * @param propertyName name of the property
	 * @see #removePropertyValue(PropertyValue)
	 */
	public void removePropertyValue(String propertyName) {
		propertyValueList.remove(getPropertyValue(propertyName));
	}


	@Override
	public Iterator<PropertyValue> iterator() {
		return Collections.unmodifiableList(propertyValueList).iterator();
	}

	@Override
	public Spliterator<PropertyValue> spliterator() {
		return Spliterators.spliterator(propertyValueList, 0);
	}

	@Override
	public Stream<PropertyValue> stream() {
		return propertyValueList.stream();
	}

	@Override
	public PropertyValue[] getPropertyValues() {
		return propertyValueList.toArray(new PropertyValue[0]);
	}

	@Override
	@Nullable
	public PropertyValue getPropertyValue(String propertyName) {
		for (PropertyValue pv : propertyValueList) {
			if (pv.getName().equals(propertyName)) {
				return pv;
			}
		}
		return null;
	}

	/**
	 * Get the raw property value, if any.
	 * @param propertyName the name to search for
	 * @return the raw property value, or {@code null} if none found
	 * @since 4.0
	 * @see #getPropertyValue(String)
	 * @see PropertyValue#getValue()
	 */
	@Nullable
	public Object get(String propertyName) {
		PropertyValue pv = getPropertyValue(propertyName);
		return (pv != null ? pv.getValue() : null);
	}

	@Override
	public PropertyValues changesSince(PropertyValues old) {
		MutablePropertyValues changes = new MutablePropertyValues();
		if (old == this) return changes;
		// for each property value in the new set
		for (PropertyValue newPv : propertyValueList) {
			// if there wasn't an old one, add it
			PropertyValue pvOld = old.getPropertyValue(newPv.getName());
			if (pvOld == null || !pvOld.equals(newPv)) {
				changes.addPropertyValue(newPv);
			}
		}
		return changes;
	}

	@Override
	public boolean contains(String propertyName) {
		return (getPropertyValue(propertyName) != null || (processedProperties != null && processedProperties.contains(propertyName)));
	}

	@Override
	public boolean isEmpty() {
		return propertyValueList.isEmpty();
	}

	/**
	 * Register the specified property as "processed" in the sense  of some processor calling the corresponding setter method outside of the PropertyValue(s) mechanism.
	 * This will lead to {@code true} being returned from a {@link #contains} call for the specified property.
	 * @param propertyName the name of the property.
	 */
	public void registerProcessedProperty(String propertyName) {
		if (processedProperties == null) {
			processedProperties = new HashSet<>(4);
		}
		processedProperties.add(propertyName);
	}

	/**
	 * Clear the "processed" registration of the given property, if any.
	 * @since 3.2.13
	 */
	public void clearProcessedProperty(String propertyName) {
		if (processedProperties != null) {
			processedProperties.remove(propertyName);
		}
	}

	/**
	 * Mark this holder as containing converted values only (i.e. no runtime resolution needed anymore).
	 */
	public void setConverted() {
		converted = true;
	}

	/**
	 * Return whether this holder contains converted values only ({@code true}),or whether the values still need to be converted ({@code false}).
	 */
	public boolean isConverted() {
		return converted;
	}

	@Override
	public boolean equals(Object other) {
		return (this == other || (other instanceof MutablePropertyValues && propertyValueList.equals(((MutablePropertyValues) other).propertyValueList)));
	}

	@Override
	public int hashCode() {
		return propertyValueList.hashCode();
	}

	@Override
	public String toString() {
		PropertyValue[] pvs = getPropertyValues();
		StringBuilder sb = new StringBuilder("PropertyValues: length=").append(pvs.length);
		if (pvs.length > 0) {
			sb.append("; ").append(StringUtils.arrayToDelimitedString(pvs, "; "));
		}
		return sb.toString();
	}
}
