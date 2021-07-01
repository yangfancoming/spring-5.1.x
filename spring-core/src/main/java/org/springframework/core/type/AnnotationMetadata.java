

package org.springframework.core.type;

import java.util.Set;

/**
 * Interface that defines abstract access to the annotations of a specific class, in a form that does not require that class to be loaded yet.
 * 定义对特定类的注解的抽象访问的接口，其形式不需要加载该类。
 * @since 2.5
 * @see StandardAnnotationMetadata
 * @see org.springframework.core.type.classreading.MetadataReader#getAnnotationMetadata()
 * @see AnnotatedTypeMetadata
 * 对Class相关的多个注解进行获取和判断
 */
public interface AnnotationMetadata extends ClassMetadata, AnnotatedTypeMetadata {

	/**
	 * 获取当前类上所有的注解的全类名  eg: 类上有@Component注解  获取的值为：org.springframework.stereotype.Component
	 * Get the fully qualified class names of all annotation types that are <em>present</em> on the underlying class.
	 * @return the annotation type names
	 */
	Set<String> getAnnotationTypes();

	/**
	 * Get the fully qualified class names of all meta-annotation types that are <em>present</em> on the given annotation type on the underlying class.
	 * @param annotationName the fully qualified class name of the meta-annotation type to look for  注解类型的全类名
	 * @return the meta-annotation type names, or an empty set if none found
	 * 拿到所有的元注解信息AnnotatedElementUtils#getMetaAnnotationTypes
	 * // 获取当前类上指定注解的注解类型
	 *     // 比如：@Component 的注解类型就是 @Indexed
	 *     // 比如：@Service 的注解类型就是 @Component和@Indexed
	 *     // 注意：annotationName 是全类名 ，也就是 xx.xx.xx.Component
	 */
	Set<String> getMetaAnnotationTypes(String annotationName);

	/**
	 * Determine whether an annotation of the given type is <em>present</em> on the underlying class.
	 * @param annotationName the fully qualified class name of the annotation type to look for
	 * @return {@code true} if a matching annotation is present
	 * 是否包含指定注解 （annotationName：全类名）
	 * 注意：annotationName 是全类名 ，也就是 xx.xx.xx.Component
	 */
	boolean hasAnnotation(String annotationName);

	/**
	 * Determine whether the underlying class has an annotation that is itself annotated with the meta-annotation of the given type.
	 * @param metaAnnotationName the fully qualified class name of the meta-annotation type to look for
	 * @return {@code true} if a matching meta-annotation is present
	 * 这个厉害了，依赖于AnnotatedElementUtils#hasMetaAnnotationTypes
	 *  // 判断注解类型自己是否被某个元注解类型所标注
	 *     // 比如：@Service 的注解类型就是 @Component，如果 metaAnnotationName 是 xx.xx.xx.Component 就会返回 true
	 *     // 比如：比如我只需要传递 xx.xx.xx.@Component 注解 如果返回 ture 就代表了是一个标准的 Bean
	 *     // 注意：metaAnnotationName 是全类名 ，也就是 xx.xx.xx.Component
	 */
	boolean hasMetaAnnotation(String metaAnnotationName);

	/**
	 * Determine whether the underlying class has any methods that are annotated (or meta-annotated) with the given annotation type.
	 * @param annotationName the fully qualified class name of the annotation type to look for
	 *  类里面只要有一个方法标注有指定注解，就返回true
	 *   // 类中只要有方法标注有指定注解，就返回true
	 *     // 注意：annotationName 是全类名 ，也就是 xx.xx.xx.Bean
	 */
	boolean hasAnnotatedMethods(String annotationName);

	/**
	 * 返回当前类中，所有标注了指定注解的方法
	 * Retrieve the method metadata for all methods that are annotated (or meta-annotated) with the given annotation type.
	 * For any returned method, {@link MethodMetadata#isAnnotated} will return {@code true} for the given annotation type.
	 * @param annotationName the fully qualified class name of the annotation type to look for
	 * @return a set of {@link MethodMetadata} for methods that have a matching annotation.
	 * The return value will be an empty set if no methods match the annotation type.
	 */
	Set<MethodMetadata> getAnnotatedMethods(String annotationName);
}
