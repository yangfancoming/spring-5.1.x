

package org.springframework.beans.factory.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.TypeConverter;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;

/**
 * Simple factory for shared List instances. Allows for central setup of Lists via the "list" element in XML bean definitions.
 * @since 09.12.2003
 * @see SetFactoryBean
 * @see MapFactoryBean
 */
public class ListFactoryBean extends AbstractFactoryBean<List<Object>> {

	@Nullable
	private List<?> sourceList;

	@SuppressWarnings("rawtypes")
	@Nullable
	private Class<? extends List> targetListClass;

	/**
	 * Set the source List, typically populated via XML "list" elements.
	 */
	public void setSourceList(List<?> sourceList) {
		this.sourceList = sourceList;
	}

	/**
	 * Set the class to use for the target List. Can be populated with a fully
	 * qualified class name when defined in a Spring application context.
	 * Default is a {@code java.util.ArrayList}.
	 * @see java.util.ArrayList
	 */
	@SuppressWarnings("rawtypes")
	public void setTargetListClass(@Nullable Class<? extends List> targetListClass) {
		if (targetListClass == null) {
			throw new IllegalArgumentException("'targetListClass' must not be null");
		}
		if (!List.class.isAssignableFrom(targetListClass)) {
			throw new IllegalArgumentException("'targetListClass' must implement [java.util.List]");
		}
		this.targetListClass = targetListClass;
	}


	@Override
	@SuppressWarnings("rawtypes")
	public Class<List> getObjectType() {
		return List.class;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected List<Object> createInstance() {
		if (sourceList == null) throw new IllegalArgumentException("'sourceList' is required");
		List<Object> result;
		if (targetListClass != null) {
			result = BeanUtils.instantiateClass(targetListClass);
		}else {
			result = new ArrayList<>(sourceList.size());
		}
		Class<?> valueType = null;
		if (targetListClass != null) {
			valueType = ResolvableType.forClass(targetListClass).asCollection().resolveGeneric();
		}
		if (valueType != null) {
			TypeConverter converter = getBeanTypeConverter();
			for (Object elem : sourceList) {
				result.add(converter.convertIfNecessary(elem, valueType));
			}
		}else {
			result.addAll(sourceList);
		}
		return result;
	}

}
