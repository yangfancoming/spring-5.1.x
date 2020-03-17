

package org.springframework.context.annotation;

/**
 * Enumeration of the type filters that may be used in conjunction with {@link ComponentScan @ComponentScan}.
 * @since 2.5
 * @see ComponentScan
 * @see ComponentScan#includeFilters()
 * @see ComponentScan#excludeFilters()
 * @see org.springframework.core.type.filter.TypeFilter
 */
public enum FilterType {

	/**
	 * Filter candidates marked with a given annotation.  根据指定注解进行过滤
	 * @see org.springframework.core.type.filter.AnnotationTypeFilter
	 */
	ANNOTATION,

	/**
	 * Filter candidates assignable to a given type. 根据给定类型进行过滤
	 * @see org.springframework.core.type.filter.AssignableTypeFilter
	 */
	ASSIGNABLE_TYPE,

	/**
	 * Filter candidates matching a given AspectJ type pattern expression. 根据AspectJ表达式进行过滤
	 * @see org.springframework.core.type.filter.AspectJTypeFilter
	 */
	ASPECTJ,

	/**
	 * Filter candidates matching a given regex pattern. 根据正则表达式进行过滤
	 * @see org.springframework.core.type.filter.RegexPatternTypeFilter
	 */
	REGEX,

	/** Filter candidates using a given custom   自定义规则过滤
	 * {@link org.springframework.core.type.filter.TypeFilter} implementation.
	 */
	CUSTOM

}
