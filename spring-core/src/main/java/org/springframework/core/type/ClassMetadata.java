

package org.springframework.core.type;

import org.springframework.lang.Nullable;

/**
 * Interface that defines abstract metadata of a specific class,in a form that does not require that class to be loaded yet.
 * @since 2.5
 * @see StandardClassMetadata
 * @see org.springframework.core.type.classreading.MetadataReader#getClassMetadata()
 * @see AnnotationMetadata
 * 此接口的所有方法，基本上都跟 Class 有关。
 */
public interface ClassMetadata {

	/**
	 * Return the name of the underlying class.  返回类名（注意返回的是最原始的那个className）
	 */
	String getClassName();

	/**
	 * Return whether the underlying class represents an interface. 返回基础类是否表示 接口
	 */
	boolean isInterface();

	/**
	 * Return whether the underlying class represents an annotation. 返回基础类是否表示 注解
	 * @since 4.1
	 */
	boolean isAnnotation();

	/**
	 * Return whether the underlying class is marked as abstract. 返回基础类是否表示 抽象类
	 */
	boolean isAbstract();

	/**
	 * Return whether the underlying class represents a concrete class, i.e. neither an interface nor an abstract class.
	 *  返回基础类是否表示具体类， // 是否允许创建  不是接口也不是抽象类  这里就返回true了
	 */
	boolean isConcrete();

	/**
	 * Return whether the underlying class is marked as 'final'.
	 */
	boolean isFinal();

	/**
	 * Determine whether the underlying class is independent, i.e. whether it is a top-level class or a nested class (static inner class) that can be constructed independently from an enclosing class.
	 *  是否是独立的(能够创建对象的)  比如是Class、或者内部类、静态内部类
	 */
	boolean isIndependent();

	/**
	 * Return whether the underlying class is declared within an enclosing class (i.e. the underlying class is an inner/nested class or a local class within a method).
	 * If this method returns {@code false}, then the underlying class is a top-level class. // 是否有内部类之类的东东
	 */
	boolean hasEnclosingClass();

	/**
	 * Return the name of the enclosing class of the underlying class, or {@code null} if the underlying class is a top-level class.
	 */
	@Nullable
	String getEnclosingClassName();

	/**
	 * Return whether the underlying class has a super class.
	 */
	boolean hasSuperClass();

	/**
	 * Return the name of the super class of the underlying class,or {@code null} if there is no super class defined.
	 */
	@Nullable
	String getSuperClassName();

	/**
	 * Return the names of all interfaces that the underlying class implements, or an empty array if there are none.
	 * 会把实现的所有接口名称都返回  具体依赖于Class#getSuperclass
	 */
	String[] getInterfaceNames();

	/**
	 * Return the names of all classes declared as members of the class represented by this ClassMetadata object.
	 * This includes public, protected, default (package) access,
	 * and private classes and interfaces declared by the class, but excludes inherited classes and interfaces.
	 * An empty array is returned if no member classes or interfaces exist.
	 * @since 3.1
	 * 基于：Class#getDeclaredClasses  返回类中定义的公共、私有、保护的内部类
	 */
	String[] getMemberClassNames();

}
