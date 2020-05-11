

package org.springframework.core.type;

/**
 * Interface that defines abstract access to the annotations of a specific class, in a form that does not require that class to be loaded yet.
 * @since 3.0
 * @see StandardMethodMetadata
 * @see AnnotationMetadata#getAnnotatedMethods
 * @see AnnotatedTypeMetadata
 * 基本上是代理了Method introspectedMethod;
 */
public interface MethodMetadata extends AnnotatedTypeMetadata {

	/**
	 * Return the name of the method. 方法名称
	 */
	String getMethodName();

	/**
	 * Return the fully-qualified name of the class that declares this method. 此方法所属类的全类名
	 */
	String getDeclaringClassName();

	/**
	 * Return the fully-qualified name of this method's declared return type. 方法返回值的全类名
	 * @since 4.2
	 */
	String getReturnTypeName();

	/**
	 * Return whether the underlying method is effectively abstract:i.e. marked as abstract on a class or declared as a regular, non-default method in an interface.
	 * @since 4.2
	 *  是否是抽象方法
	 */
	boolean isAbstract();

	/**
	 * Return whether the underlying method is declared as 'static'.
	 * 是否是静态方法
	 */
	boolean isStatic();

	/**
	 * Return whether the underlying method is marked as 'final'.
	 * 是否是final方法
	 */
	boolean isFinal();

	/**
	 * Return whether the underlying method is overridable,
	 * i.e. not marked as static, final or private.
	 * 是否可以被复写（不是静态、不是final、不是private的  就表示可以被复写）
	 */
	boolean isOverridable();
}
