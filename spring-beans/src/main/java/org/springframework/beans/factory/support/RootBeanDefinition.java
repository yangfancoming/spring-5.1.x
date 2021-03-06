

package org.springframework.beans.factory.support;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * A root bean definition represents the merged bean definition that backs a specific bean in a Spring BeanFactory at runtime. 
 * It might have been created from multiple original bean definitions that inherit from each other,typically registered as {@link GenericBeanDefinition GenericBeanDefinitions}.
 * A root bean definition is essentially the 'unified' bean definition view at runtime.
 * Root bean definitions may also be used for registering individual bean definitions in the configuration phase.
 * However, since Spring 2.5, the preferred way to register bean definitions programmatically is the {@link GenericBeanDefinition} class.
 * GenericBeanDefinition has the advantage that it allows to dynamically define parent dependencies, not 'hard-coding' the role as a root bean definition.
 * @see GenericBeanDefinition
 * @see ChildBeanDefinition
 * *根bean定义表示在运行时支持springbeanfactory中特定bean的合并bean定义。
 * *它可能是从相互继承的多个原始bean定义创建的，通常注册为{@link GenericBeanDefinition GenericBeanDefinitions}。
 * *根bean定义本质上是运行时的“统一”bean定义视图。
 * *根bean定义也可以用于在配置阶段注册单个bean定义。
 * *但是，自Spring2.5以来，以编程方式注册bean定义的首选方法是{@link GenericBeanDefinition}类。
 * *GenericBeanDefinition的优点是它允许动态定义父依赖项，而不是将角色“硬编码”为根bean定义。
 *
 * 简单的说：在多继承体系中，RootBeanDefinition代表的是当前初始化类的父类的BeanDefinition 若没有父类，那就是它自己嘛
 * 总结一下，RootBeanDefiniiton保存了以下信息：
 * 1.定义了id、别名与Bean的对应关系（BeanDefinitionHolder）
 * 2.Bean的注解（AnnotatedElement）
 * 3.具体的工厂方法（Class类型），包括工厂方法的返回类型，工厂方法的Method对象
 * 4.构造函数、构造函数形参类型
 * 5.Bean的class对象
 * 可以看到，RootBeanDefinition与AbstractBeanDefinition是互补关系，RootBeanDefinition在AbstractBeanDefinition的基础上定义了更多属性，初始化Bean需要的信息基本完善
 */
@SuppressWarnings("serial")
public class RootBeanDefinition extends AbstractBeanDefinition {

	// BeanDefinitionHolder存储有Bean的名称、别名、BeanDefinition
	@Nullable
	private BeanDefinitionHolder decoratedDefinition;
	// AnnotatedElement 是java反射包的接口，通过它可以查看Bean的注解信息
	@Nullable
	private AnnotatedElement qualifiedElement;
	//允许缓存
	boolean allowCaching = true;
	//从字面上理解：工厂方法是否唯一
	boolean isFactoryMethodUnique = false;
	//封装了java.lang.reflect.Type,提供了泛型相关的操作，具体请查看：ResolvableType 可以专题去了解一下子，虽然比较简单 但常见
	@Nullable
	volatile ResolvableType targetType;
	//缓存class，表明RootBeanDefinition存储哪个类的信息
	/** Package-visible field for caching the determined Class of a given bean definition. */
	@Nullable
	volatile Class<?> resolvedTargetType;
	//缓存工厂方法的返回类型
	/** Package-visible field for caching the return type of a generically typed factory method. */
	@Nullable
	volatile ResolvableType factoryMethodReturnType;

	/** Package-visible field for caching a unique factory method candidate for introspection. */
	@Nullable
	volatile Method factoryMethodToIntrospect;

	/** Common lock for the four constructor fields below. */
	final Object constructorArgumentLock = new Object();

	/** Package-visible field for caching the resolved constructor or factory method. */
	//缓存已经解析的构造函数或是工厂方法，Executable是Method、Constructor类型的父类
	@Nullable
	Executable resolvedConstructorOrFactoryMethod;

	/** Package-visible field that marks the constructor arguments as resolved. */
	//表明构造函数参数是否解析完毕
	boolean constructorArgumentsResolved = false;

	/** Package-visible field for caching fully resolved constructor arguments. */
	//缓存完全解析的构造函数参数
	@Nullable
	Object[] resolvedConstructorArguments;

	/** Package-visible field for caching partly prepared constructor arguments. */
	//缓存待解析的构造函数参数，即还没有找到对应的实例，可以理解为还没有注入依赖的形参
	@Nullable
	Object[] preparedConstructorArguments;

	/** Common lock for the two post-processing fields below. */
	final Object postProcessingLock = new Object();

	/** Package-visible field that indicates MergedBeanDefinitionPostProcessor having been applied. */
	//表明是否被MergedBeanDefinitionPostProcessor处理过
	boolean postProcessed = false;

	/** Package-visible field that indicates a before-instantiation post-processor having kicked in. */
	//在生成代理的时候会使用，表明是否已经生成代理
	@Nullable
	volatile Boolean beforeInstantiationResolved;
	//实际缓存的类型是Constructor、Field、Method类型
	@Nullable
	private Set<Member> externallyManagedConfigMembers;
	//InitializingBean中的init回调函数名——afterPropertiesSet会在这里记录，以便进行生命周期回调
	@Nullable
	private Set<String> externallyManagedInitMethods;
	//DisposableBean的destroy回调函数名——destroy会在这里记录，以便进行生命周期回调
	@Nullable
	private Set<String> externallyManagedDestroyMethods;

	/**
	 * Create a new RootBeanDefinition, to be configured through its bean properties and configuration methods.
	 * @see #setBeanClass
	 * @see #setScope
	 * @see #setConstructorArgumentValues
	 * @see #setPropertyValues
	 */
	public RootBeanDefinition() {
		super();
	}

	/**
	 * Create a new RootBeanDefinition for a singleton.
	 * @param beanClass the class of the bean to instantiate
	 * @see #setBeanClass
	 */
	public RootBeanDefinition(@Nullable Class<?> beanClass) {
		super();
		setBeanClass(beanClass);
	}

	/**
	 * Create a new RootBeanDefinition for a singleton bean, constructing each instance through calling the given supplier (possibly a lambda or method reference).
	 * @param beanClass the class of the bean to instantiate
	 * @param instanceSupplier the supplier to construct a bean instance,as an alternative to a declaratively specified factory method
	 * @since 5.0
	 * @see #setInstanceSupplier
	 */
	public <T> RootBeanDefinition(@Nullable Class<T> beanClass, @Nullable Supplier<T> instanceSupplier) {
		super();
		setBeanClass(beanClass);
		setInstanceSupplier(instanceSupplier);
	}

	/**
	 * Create a new RootBeanDefinition for a scoped bean, constructing each instance through calling the given supplier (possibly a lambda or method reference).
	 * @param beanClass the class of the bean to instantiate
	 * @param scope the name of the corresponding scope
	 * @param instanceSupplier the supplier to construct a bean instance,as an alternative to a declaratively specified factory method
	 * @since 5.0
	 * @see #setInstanceSupplier
	 */
	public <T> RootBeanDefinition(@Nullable Class<T> beanClass, String scope, @Nullable Supplier<T> instanceSupplier) {
		super();
		setBeanClass(beanClass);
		setScope(scope);
		setInstanceSupplier(instanceSupplier);
	}

	/**
	 * Create a new RootBeanDefinition for a singleton,using the given autowire mode.
	 * @param beanClass the class of the bean to instantiate
	 * @param autowireMode by name or type, using the constants in this interface
	 * @param dependencyCheck whether to perform a dependency check for objects (not applicable to autowiring a constructor, thus ignored there)
	 */
	public RootBeanDefinition(@Nullable Class<?> beanClass, int autowireMode, boolean dependencyCheck) {
		super();
		setBeanClass(beanClass);
		setAutowireMode(autowireMode);
		if (dependencyCheck && getResolvedAutowireMode() != AUTOWIRE_CONSTRUCTOR) {
			setDependencyCheck(DEPENDENCY_CHECK_OBJECTS);
		}
	}

	/**
	 * Create a new RootBeanDefinition for a singleton,providing constructor arguments and property values.
	 * @param beanClass the class of the bean to instantiate
	 * @param cargs the constructor argument values to apply
	 * @param pvs the property values to apply
	 */
	public RootBeanDefinition(@Nullable Class<?> beanClass, @Nullable ConstructorArgumentValues cargs,@Nullable MutablePropertyValues pvs) {
		super(cargs, pvs);
		setBeanClass(beanClass);
	}

	/**
	 * Create a new RootBeanDefinition for a singleton,providing constructor arguments and property values.
	 * Takes a bean class name to avoid eager loading of the bean class.
	 * @param beanClassName the name of the class to instantiate
	 */
	public RootBeanDefinition(String beanClassName) {
		setBeanClassName(beanClassName);
	}

	/**
	 * Create a new RootBeanDefinition for a singleton, providing constructor arguments and property values.
	 * Takes a bean class name to avoid eager loading of the bean class.
	 * @param beanClassName the name of the class to instantiate
	 * @param cargs the constructor argument values to apply
	 * @param pvs the property values to apply
	 */
	public RootBeanDefinition(String beanClassName, ConstructorArgumentValues cargs, MutablePropertyValues pvs) {
		super(cargs, pvs);
		setBeanClassName(beanClassName);
	}

	/**
	 * Create a new RootBeanDefinition as deep copy of the given bean definition.
	 * @param original the original bean definition to copy from
	 */
	public RootBeanDefinition(RootBeanDefinition original) {
		super(original);
		this.decoratedDefinition = original.decoratedDefinition;
		this.qualifiedElement = original.qualifiedElement;
		this.allowCaching = original.allowCaching;
		this.isFactoryMethodUnique = original.isFactoryMethodUnique;
		this.targetType = original.targetType;
	}

	/**
	 * Create a new RootBeanDefinition as deep copy of the given bean definition.
	 * @param original the original bean definition to copy from
	 */
	RootBeanDefinition(BeanDefinition original) {
		super(original);
	}

	// 由此看出，RootBeanDefiniiton 是木有父的
	@Override
	public String getParentName() {
		return null;
	}

	@Override
	public void setParentName(@Nullable String parentName) {
		if (parentName != null) throw new IllegalArgumentException("Root bean cannot be changed into a child bean with parent reference");
	}

	// Register a target definition that is being decorated by this bean definition.
	public void setDecoratedDefinition(@Nullable BeanDefinitionHolder decoratedDefinition) {
		this.decoratedDefinition = decoratedDefinition;
	}

	// Return the target definition that is being decorated by this bean definition, if any.
	@Nullable
	public BeanDefinitionHolder getDecoratedDefinition() {
		return decoratedDefinition;
	}

	/**
	 * Specify the {@link AnnotatedElement} defining qualifiers,to be used instead of the target class or factory method.
	 * @since 4.3.3
	 * @see #setTargetType(ResolvableType)
	 * @see #getResolvedFactoryMethod()
	 */
	public void setQualifiedElement(@Nullable AnnotatedElement qualifiedElement) {
		this.qualifiedElement = qualifiedElement;
	}

	/**
	 * Return the {@link AnnotatedElement} defining qualifiers, if any. Otherwise, the factory method and target class will be checked.
	 * @since 4.3.3
	 */
	@Nullable
	public AnnotatedElement getQualifiedElement() {
		return qualifiedElement;
	}

	// Specify a generics-containing target type of this bean definition, if known in advance. @since 4.3.3
	public void setTargetType(ResolvableType targetType) {
		this.targetType = targetType;
	}

	// Specify the target type of this bean definition, if known in advance. @since 3.2.2
	public void setTargetType(@Nullable Class<?> targetType) {
		this.targetType = (targetType != null ? ResolvableType.forClass(targetType) : null);
	}

	// Return the target type of this bean definition, if known (either specified in advance or resolved on first instantiation). @since 3.2.2
	@Nullable
	public Class<?> getTargetType() {
		if (resolvedTargetType != null) {
			return resolvedTargetType;
		}
		ResolvableType targetType = this.targetType;
		return (targetType != null ? targetType.resolve() : null);
	}

	/**
	 * Return a {@link ResolvableType} for this bean definition,either from runtime-cached type information or from configuration-time
	 * {@link #setTargetType(ResolvableType)} or {@link #setBeanClass(Class)}.
	 * @since 5.1
	 * @see #getTargetType()
	 * @see #getBeanClass()
	 */
	public ResolvableType getResolvableType() {
		ResolvableType targetType = this.targetType;
		return (targetType != null ? targetType : ResolvableType.forClass(getBeanClass()));
	}

	/**
	 * Determine preferred constructors to use for default construction, if any.Constructor arguments will be autowired if necessary.
	 * @return one or more preferred constructors, or {@code null} if none (in which case the regular no-arg default constructor will be called)
	 * @since 5.1
	 */
	@Nullable
	public Constructor<?>[] getPreferredConstructors() {
		return null;
	}

	// Specify a factory method name that refers to a non-overloaded method.
	public void setUniqueFactoryMethodName(String name) {
		Assert.hasText(name, "Factory method name must not be empty");
		setFactoryMethodName(name);
		isFactoryMethodUnique = true;
	}

	// Check whether the given candidate qualifies as a factory method.
	public boolean isFactoryMethod(Method candidate) {
		return candidate.getName().equals(getFactoryMethodName());
	}

	/**
	 * Return the resolved factory method as a Java Method object, if available.
	 * @return the factory method, or {@code null} if not found or not resolved yet
	 */
	@Nullable
	public Method getResolvedFactoryMethod() {
		return factoryMethodToIntrospect;
	}

	public void registerExternallyManagedConfigMember(Member configMember) {
		synchronized (postProcessingLock) {
			if (externallyManagedConfigMembers == null) externallyManagedConfigMembers = new HashSet<>(1);
			externallyManagedConfigMembers.add(configMember);
		}
	}

	public boolean isExternallyManagedConfigMember(Member configMember) {
		synchronized (postProcessingLock) {
			return (externallyManagedConfigMembers != null && externallyManagedConfigMembers.contains(configMember));
		}
	}

	public void registerExternallyManagedInitMethod(String initMethod) {
		synchronized (postProcessingLock) {
			if (externallyManagedInitMethods == null) externallyManagedInitMethods = new HashSet<>(1);
			externallyManagedInitMethods.add(initMethod);
		}
	}

	public boolean isExternallyManagedInitMethod(String initMethod) {
		synchronized (postProcessingLock) {
			return (externallyManagedInitMethods != null && externallyManagedInitMethods.contains(initMethod));
		}
	}

	public void registerExternallyManagedDestroyMethod(String destroyMethod) {
		synchronized (postProcessingLock) {
			if (externallyManagedDestroyMethods == null) externallyManagedDestroyMethods = new HashSet<>(1);
			externallyManagedDestroyMethods.add(destroyMethod);
		}
	}

	public boolean isExternallyManagedDestroyMethod(String destroyMethod) {
		synchronized (postProcessingLock) {
			return (externallyManagedDestroyMethods != null && externallyManagedDestroyMethods.contains(destroyMethod));
		}
	}

	@Override
	public RootBeanDefinition cloneBeanDefinition() {
		return new RootBeanDefinition(this);
	}

	@Override
	public boolean equals(Object other) {
		return (this == other || (other instanceof RootBeanDefinition && super.equals(other)));
	}

	@Override
	public String toString() {
		return "Root bean: " + super.toString();
	}
}
