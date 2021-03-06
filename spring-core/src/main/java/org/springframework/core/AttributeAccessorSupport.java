

package org.springframework.core;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Support class for {@link AttributeAccessor AttributeAccessors}, providing a base implementation of all methods.
 * To be extended by subclasses.{@link Serializable} if subclasses and all attribute values are {@link Serializable}.
 * @since 2.0
 * AttributeAccessorSupport 是AttributeAccessor唯一抽象实现，内部基于LinkedHashMap实现了所有的接口，供其他子类继承使用  主要针对属性CRUD操作
 */
@SuppressWarnings("serial")
public abstract class AttributeAccessorSupport implements AttributeAccessor, Serializable {

	/** Map with String keys and Object values. */
	private final Map<String, Object> attributes = new LinkedHashMap<>();

	/**
	 * Copy the attributes from the supplied AttributeAccessor to this accessor.
	 * @param source the AttributeAccessor to copy from
	 */
	protected void copyAttributesFrom(AttributeAccessor source) {
		Assert.notNull(source, "Source must not be null");
		String[] attributeNames = source.attributeNames();
		for (String attributeName : attributeNames) {
			setAttribute(attributeName, source.getAttribute(attributeName));
		}
	}

	//---------------------------------------------------------------------
	// Implementation of 【AttributeAccessor】 interface
	//---------------------------------------------------------------------
	@Override
	public void setAttribute(String name, @Nullable Object value) {
		Assert.notNull(name, "Name must not be null");
		if (value != null) {
			attributes.put(name, value);
		}else {
			removeAttribute(name);
		}
	}

	@Override
	@Nullable
	public Object getAttribute(String name) {
		Assert.notNull(name, "Name must not be null");
		return attributes.get(name);
	}

	@Override
	@Nullable
	public Object removeAttribute(String name) {
		Assert.notNull(name, "Name must not be null");
		return attributes.remove(name);
	}

	@Override
	public boolean hasAttribute(String name) {
		Assert.notNull(name, "Name must not be null");
		return attributes.containsKey(name);
	}

	@Override
	public String[] attributeNames() {
		return StringUtils.toStringArray(attributes.keySet());
	}

	//---------------------------------------------------------------------
	// Implementation of 【JDK】 interface
	//---------------------------------------------------------------------
	@Override
	public boolean equals(Object other) {
		return (this == other || (other instanceof AttributeAccessorSupport && attributes.equals(((AttributeAccessorSupport) other).attributes)));
	}

	@Override
	public int hashCode() {
		return attributes.hashCode();
	}
}
