

package org.springframework.core.type;

import java.util.Map;

import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

/**
 * Defines access to the annotations of a specific type ({@link AnnotationMetadata class} or {@link MethodMetadata method}), in a form that does not necessarily require the class-loading.
 * @since 4.0
 * @see AnnotationMetadata
 * @see MethodMetadata
 */
public interface AnnotatedTypeMetadata {

	/**
	 * Determine whether the underlying element has an annotation or meta-annotation of the given type defined.
	 * If this method returns {@code true}, then {@link #getAnnotationAttributes} will return a non-null Map.
	 * @param annotationName the fully qualified class name of the annotation type to look for
	 * @return whether a matching annotation is defined
	 *  // 此元素是否标注有此注解~~~~
	 *     // 注意：annotationName 是全类名 ，也就是 xx.xx.xx.Component
	 */
	boolean isAnnotated(String annotationName);

	/**
	 * Retrieve the attributes of the annotation of the given type, if any (i.e. if defined on the underlying element, as direct annotation or meta-annotation),
	 * also taking attribute overrides on composed annotations into account.
	 * @param annotationName the fully qualified class name of the annotation type to look for
	 * @return a Map of attributes, with the attribute name as key (e.g. "value") and the defined attribute value as Map value. This return value will be
	 * {@code null} if no matching annotation is defined.
	 * // 取得指定类型注解的所有的属性 - 值（k-v）
	 */
	@Nullable
	Map<String, Object> getAnnotationAttributes(String annotationName);

	/**
	 * Retrieve the attributes of the annotation of the given type, if any (i.e. if defined on the underlying element,as direct annotation or meta-annotation),
	 * also taking attribute overrides on composed annotations into account.
	 * @param annotationName the fully qualified class name of the annotation type to look for
	 * @param classValuesAsString whether to convert class references to String class names for exposure as values in the returned Map,
	 * instead of Class references which might potentially have to be loaded first
	 * @return a Map of attributes, with the attribute name as key (e.g. "value") and the defined attribute value as Map value. This return value will be
	 * {@code null} if no matching annotation is defined.
	 * // 取得指定类型注解的所有的属性 - 值（k-v）
	 *     // annotationName：注解全类名
	 *     // classValuesAsString：若是true表示 Class用它的字符串的全类名来表示。这样可以避免Class被提前加载
	 */
	@Nullable
	Map<String, Object> getAnnotationAttributes(String annotationName, boolean classValuesAsString);

	/**
	 * Retrieve all attributes of all annotations of the given type, if any (i.e. if defined on the underlying element, as direct annotation or meta-annotation).
	 * Note that this variant does <i>not</i> take attribute overrides into account.
	 * @param annotationName the fully qualified class name of the annotation type to look for
	 * @return a MultiMap of attributes, with the attribute name as key (e.g. "value") and a list of the defined attribute values as Map value.
	 * This return value will be {@code null} if no matching annotation is defined.
	 * @see #getAllAnnotationAttributes(String, boolean)
	 *  // 取得指定类型注解的所有的属性 - 值（k-v）
	 */
	@Nullable
	MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName);

	/**
	 * Retrieve all attributes of all annotations of the given type, if any (i.e. if defined on the underlying element, as direct annotation or meta-annotation).
	 * Note that this variant does <i>not</i> take attribute overrides into account.
	 * @param annotationName the fully qualified class name of the annotation type to look for
	 * @param classValuesAsString  whether to convert class references to String
	 * @return a MultiMap of attributes, with the attribute name as key (e.g. "value") and a list of the defined attribute values as Map value.
	 * This return value will  be {@code null} if no matching annotation is defined.
	 * @see #getAllAnnotationAttributes(String)
	 *   // 取得指定类型注解的所有的属性 - 值（k-v）
	 *     // annotationName：注解全类名
	 *     // classValuesAsString：若是true表示 Class用它的字符串的全类名来表示。这样可以避免Class被提前加载
	 */
	@Nullable
	MultiValueMap<String, Object> getAllAnnotationAttributes(String annotationName, boolean classValuesAsString);
}
