

package org.springframework.beans.factory.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanMetadataAttributeAccessor;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.core.io.DescriptiveResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.function.Supplier;

/**
 * Base class for concrete, full-fledged {@link BeanDefinition} classes,
 * factoring out common properties of {@link GenericBeanDefinition},{@link RootBeanDefinition}, and {@link ChildBeanDefinition}.
 * The autowire constants match the ones defined in the {@link org.springframework.beans.factory.config.AutowireCapableBeanFactory} interface.
 * @see GenericBeanDefinition
 * @see RootBeanDefinition
 * @see ChildBeanDefinition
 * AbstractBeanDefinition 实现了 BeanDefinition 接口，在 BeanDefinition 接口中只是定义了<bean>标签对应属性的 setter/getter 方法，
 * 而没有定义对应的属性，而在 AbstractBeanDefinition 类中就定义了对应的各种属性，并重写了接口的 setter/getter 方法
 * XML 配置文件中所有的配置都可以在该类中找到对应的位置。
 *
 * *具体的、成熟的{@link BeanDefinition}类的基类，
 * *分解出{@link GenericBeanDefinition}、{@link RootBeanDefinition}和{@link ChildBeanDefinition}的公共属性。
 * *autowire常量与{@link org.springframework.beans.factory.config.AutowireCapableBeanFactory}接口中定义的常量匹配。
 */
@SuppressWarnings("serial")
public abstract class AbstractBeanDefinition extends BeanMetadataAttributeAccessor implements BeanDefinition, Cloneable {

	protected final Log logger = LogFactory.getLog(getClass());

	//=====================定义众多常量。这一些常量会直接影响到spring实例化Bean时的策略
	// 个人觉得这些常量的定义不是必须的，在代码里判断即可。Spring定义这些常量的原因很简单，便于维护，让读代码的人知道每个值的意义

	/**
	 * Constant for the default scope name: {@code ""}, equivalent to singleton status unless overridden from a parent bean definition (if applicable).
	 * 默认作用域名称的常量：等于singleton状态，除非从父bean定义中重写（如果适用）。
	 * 默认的SCOPE，默认是单例
	 */
	public static final String SCOPE_DEFAULT = "";

	/**
	 * Constant that indicates no external autowiring at all. 不进行自动装配
	 * @see #setAutowireMode
	 */
	public static final int AUTOWIRE_NO = AutowireCapableBeanFactory.AUTOWIRE_NO;

	/**
	 * Constant that indicates autowiring bean properties by name.根据Bean的名字进行自动装配，即autowired属性的值为byname
	 * @see #setAutowireMode
	 */
	public static final int AUTOWIRE_BY_NAME = AutowireCapableBeanFactory.AUTOWIRE_BY_NAME;

	/**
	 * Constant that indicates autowiring bean properties by type. 根据Bean的类型进行自动装配，调用setter函数装配属性，即autowired属性的值为byType
	 * @see #setAutowireMode
	 */
	public static final int AUTOWIRE_BY_TYPE = AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE;

	/**
	 * Constant that indicates autowiring a constructor.自动装配构造函数的形参，完成对应属性的自动装配，即autowired属性的值为byConstructor
	 * @see #setAutowireMode
	 */
	public static final int AUTOWIRE_CONSTRUCTOR = AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR;

	/**
	 * Constant that indicates determining an appropriate autowire strategy through introspection of the bean class.
	 * @see #setAutowireMode
	 * @deprecated as of Spring 3.0: If you are using mixed autowiring strategies,
	 * use annotation-based autowiring for clearer demarcation of autowiring needs.
	 * 通过Bean的class推断适当的自动装配策略（autowired=autodetect），如果Bean定义有有参构造函数，则通过自动装配构造函数形参，完成对应属性的自动装配（AUTOWIRE_CONSTRUCTOR），否则，使用setter函数（AUTOWIRE_BY_TYPE）
	 * 原文链接：https://blog.csdn.net/dhaiuda/article/details/83210577
	 */
	@Deprecated
	public static final int AUTOWIRE_AUTODETECT = AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT;

	// 检查依赖是否合法，在本类中，默认不进行依赖检查
	/**
	 * Constant that indicates no dependency check at all.  // 不进行检查
	 * @see #setDependencyCheck
	 */
	public static final int DEPENDENCY_CHECK_NONE = 0;

	/**
	 * Constant that indicates dependency checking for object references. //如果依赖类型为对象引用，则需要检查
	 * @see #setDependencyCheck
	 */
	public static final int DEPENDENCY_CHECK_OBJECTS = 1;

	/**
	 * Constant that indicates dependency checking for "simple" properties. //对简单属性的依赖进行检查
	 * @see #setDependencyCheck
	 * @see org.springframework.beans.BeanUtils#isSimpleProperty
	 */
	public static final int DEPENDENCY_CHECK_SIMPLE = 2;

	/**
	 * Constant that indicates dependency checking for all properties (object references as well as "simple" properties).对所有属性的依赖进行检查
	 * @see #setDependencyCheck
	 */
	public static final int DEPENDENCY_CHECK_ALL = 3;

	/**
	 * Constant that indicates the container should attempt to infer the {@link #setDestroyMethodName destroy method name} for a bean as opposed to
	 * explicit specification of a method name. The value {@value} is specifically designed to include characters otherwise illegal in a method name,
	 * ensuring no possibility of collisions with legitimately named methods having the same name.
	 * Currently, the method names detected during destroy method inference are "close" and "shutdown", if present on the specific bean class.
	 * 若Bean未指定销毁方法，容器应该尝试推断Bean的销毁方法的名字，
	 * 目前来说，推断的销毁方法的名字一般为close或是shutdown ( 即未指定Bean的销毁方法，但是内部定义了名为close或是shutdown的方法，则容器推断其为销毁方法)
	 */
	public static final String INFER_METHOD = "(inferred)";

	//---------------------------------------------------------------------
	// 以下属性：基本囊括了Bean实例化需要的所有信息
	//---------------------------------------------------------------------

	/** Bean的class对象或是类的全限定名 */
	@Nullable
	private volatile Object beanClass;

	/** bean的作用范围，对应bean属性scope */
	/** 默认的scope是单例 */
	@Nullable
	private String scope = SCOPE_DEFAULT;

	/** 是否是抽象，对应bean属性abstract */
	private boolean abstractFlag = false;

	/** 是否延迟加载，对应bean属性lazy-init */
	private boolean lazyInit = false;

	/** 自动注入模式，对应bean属性autowire */
	private int autowireMode = AUTOWIRE_NO;

	/** 依赖检查，Spring 3.0后弃用这个属性  默认不进行检查*/
	private int dependencyCheck = DEPENDENCY_CHECK_NONE;

	/**
	 * 用来表示一个bean的实例化依靠另一个bean先实例化，对应bean属性depend-on 这里只会存放<bean/>标签的depends-on属性或是@DependsOn注解的值
	 */
	@Nullable
	private String[] dependsOn;

	/**
	 * autowire-candidate属性设置为false，这样容器在查找自动装配对象时，将不考虑该bean，即它不会被考虑作为其他bean自动装配的候选者，
	 * 但是该bean本身还是可以使用自动装配来注入其他bean的 是自动装配的候选者，意味着可以自动装配到其他Bean的某个属性中
	 */
	private boolean autowireCandidate = true;

	/**
	 * 自动装配时出现多个bean候选者时，将作为首选者，对应bean属性primary
	 * 当某个Bean的某个属性自动装配有多个候选者（包括自己）时，是否优先注入，即@Primary注解
	  */
	private boolean primary = false;

	/**  这个不是很清楚，查看了这个类的定义，AutowireCandidateQualifier用于解析自动装配的候选者 */
	/**
	 * 用于记录Qualifier，对应子元素qualifier=======这个字段有必要解释一下
	 *  唯一向这个字段放值的方法为本类的：public void addQualifier(AutowireCandidateQualifier qualifier)    copyQualifiersFrom这个不算，那属于拷贝
	 *  调用处：AnnotatedBeanDefinitionReader#doRegisterBean  但是Spring所有调用处，qualifiers字段传的都是null~~~~~~~~~尴尬
	 *  通过我多方跟踪发现，此处这个字段目前【永远】不会被赋值（除非我们手动调用对应方法为其赋值）   但是有可能我才疏学浅，若有知道的  请告知，非常非常感谢  我考虑到它可能是预留字段~~~~
	 *  我起初以为这样可以赋值：
	 * @Qualifier("aaa")
	 * @Service
	 * public class HelloServiceImpl   没想到，也是不好使的，Bean定义里面也不会有值
	 *  因此对应的方法getQualifier和getQualifiers 目前应该基本上都返回null或者[]
	*/
	private final Map<String, AutowireCandidateQualifier> qualifiers = new LinkedHashMap<>();

	/**
	 * 用于初始化Bean的回调函数，一旦指定，这个方法会覆盖工厂方法以及构造函数中的元数据
	 *  我理解为通过这个函数的逻辑初始化Bean，而不是构造函数或是工厂方法（相当于自己去实例化，而不是交给Bean工厂）
	*/
	@Nullable
	private Supplier<?> instanceSupplier;

	/**  允许访问非公开的构造器和方法，程序设置 */
	/**  是否允许访问非public方法和属性，应用于构造函数、工厂方法、init、destroy方法的解析， 默认是true，表示啥都可以访问 */
	private boolean nonPublicAccessAllowed = true;

	/**  指定解析构造函数的模式，是宽松还是严格（什么是宽松、什么是严格，我没有找到解释）
	 * 是否以一种宽松的模式解析构造函数，默认为true，
	 * 如果为false，则在以下情况
	 * interface ITest{}
	 * class ITestImpl implements ITest{};
	 * class Main {
	 *     Main(ITest i) {}
	 *     Main(ITestImpl i) {}
	 * }
	 * 抛出异常，因为Spring无法准确定位哪个构造函数程序设置
	 */
	private boolean lenientConstructorResolution = true;

	/**
	 * 对应bean属性factory-bean，用法：
	 * <bean id = "instanceFactoryBean" class = "example.chapter3.InstanceFactoryBean" />
	 * <bean id = "currentTime" factory-bean = "instanceFactoryBean" factory-method = "createTime" />
	 * 工厂类名（注意是String类型，不是Class类型）对应bean属性factory-method
	 */
	@Nullable
	private String factoryBeanName;

	/**  对应bean属性factory-method */
	/**  工厂方法名（注意是String类型，不是Method类型） */
	@Nullable
	private String factoryMethodName;

	/**  记录构造函数注入属性，对应bean属性constructor-arg */
	/**  存储构造函数形参的值 */
	@Nullable
	private ConstructorArgumentValues constructorArgumentValues;

	/**  普通属性集合 */
	/**  Bean属性的名称以及对应的值，这里不会存放构造函数相关的参数值，只会存放通过setter注入的依赖 */
	@Nullable
	private MutablePropertyValues propertyValues;

	/**  方法重写的持有者，记录lookup-method、replaced-method 元素 */
	/**  存储被IOC容器覆盖的方法的相关信息（例如replace-method属性指定的函数） */
	@Nullable
	private MethodOverrides methodOverrides;

	/**  初始化方法，对应bean属性init-method */
	@Nullable
	private String initMethodName;

	/**  销毁方法，对应bean属性destroy-method */
	@Nullable
	private String destroyMethodName;

	/**  是否执行init-method，程序设置 */
	private boolean enforceInitMethod = true;

	/**  是否执行destroy-method，程序设置 */
	private boolean enforceDestroyMethod = true;

	/**  是否是用户定义的而不是应用程序本身定义的，创建AOP时候为true，程序设置 */
	/**  是否是合成类（是不是应用自定义的，例如生成AOP代理时，会用到某些辅助类，这些辅助类不是应用自定义的，这个就是合成类） */
	// 是否是一个合成 BeanDefinition,
	// 合成 在这里的意思表示这不是一个应用开发人员自己定义的 BeanDefinition, 而是程序
	// 自己组装而成的一个 BeanDefinition, 例子 :
	// 1. 自动代理的helper bean，一个基础设施bean，因为使用<aop:config> 被自动合成创建;
	// 2. bean errorPageRegistrarBeanPostProcessor , Spring boot 自动配置针对Web错误页面的
	// 一个bean，这个bean不需要应用开发人员定义，而是框架根据上下文自动合成组装而成；
	private boolean synthetic = false;

	/**
	 * 定义这个bean的应用，APPLICATION：用户，INFRASTRUCTURE：完全内部使用，与用户无关，
	 * SUPPORT：某些复杂配置的一部分
	 * 程序设置
	 */
	// 当前bean 定义的角色，初始化为 ROLE_APPLICATION ， 提示这是一个应用bean
	// 另外还有基础设施bean（仅供框架内部工作使用），和 支持bean
	private int role = BeanDefinition.ROLE_APPLICATION;

	/**  bean的描述信息 */
	@Nullable
	private String description;

	/**  这个bean定义的资源 */
	@Nullable
	private Resource resource;

	// Create a new AbstractBeanDefinition with default settings.
	protected AbstractBeanDefinition() {
		this(null, null);
	}

	// Create a new AbstractBeanDefinition with the given constructor argument values and property values.
	protected AbstractBeanDefinition(@Nullable ConstructorArgumentValues cargs, @Nullable MutablePropertyValues pvs) {
		logger.warn("进入 【AbstractBeanDefinition】 构造函数 {}");
		this.constructorArgumentValues = cargs;
		this.propertyValues = pvs;
	}

	/**
	 * Create a new AbstractBeanDefinition as a deep copy of the given bean definition.
	 * @param original the original bean definition to copy from
	 */
	protected AbstractBeanDefinition(BeanDefinition original) {
		setParentName(original.getParentName());
		setBeanClassName(original.getBeanClassName());
		setScope(original.getScope());
		setAbstract(original.isAbstract());
		setLazyInit(original.isLazyInit());
		setFactoryBeanName(original.getFactoryBeanName());
		setFactoryMethodName(original.getFactoryMethodName());
		setRole(original.getRole());
		setSource(original.getSource());
		copyAttributesFrom(original);

		if (original instanceof AbstractBeanDefinition) {
			AbstractBeanDefinition originalAbd = (AbstractBeanDefinition) original;
			if (originalAbd.hasBeanClass()) {
				setBeanClass(originalAbd.getBeanClass());
			}
			if (originalAbd.hasConstructorArgumentValues()) {
				setConstructorArgumentValues(new ConstructorArgumentValues(original.getConstructorArgumentValues()));
			}
			if (originalAbd.hasPropertyValues()) {
				setPropertyValues(new MutablePropertyValues(original.getPropertyValues()));
			}
			if (originalAbd.hasMethodOverrides()) {
				setMethodOverrides(new MethodOverrides(originalAbd.getMethodOverrides()));
			}
			setAutowireMode(originalAbd.getAutowireMode());
			setDependencyCheck(originalAbd.getDependencyCheck());
			setDependsOn(originalAbd.getDependsOn());
			setAutowireCandidate(originalAbd.isAutowireCandidate());
			setPrimary(originalAbd.isPrimary());
			copyQualifiersFrom(originalAbd);
			setInstanceSupplier(originalAbd.getInstanceSupplier());
			setNonPublicAccessAllowed(originalAbd.isNonPublicAccessAllowed());
			setLenientConstructorResolution(originalAbd.isLenientConstructorResolution());
			setInitMethodName(originalAbd.getInitMethodName());
			setEnforceInitMethod(originalAbd.isEnforceInitMethod());
			setDestroyMethodName(originalAbd.getDestroyMethodName());
			setEnforceDestroyMethod(originalAbd.isEnforceDestroyMethod());
			setSynthetic(originalAbd.isSynthetic());
			setResource(originalAbd.getResource());
		}else {
			setConstructorArgumentValues(new ConstructorArgumentValues(original.getConstructorArgumentValues()));
			setPropertyValues(new MutablePropertyValues(original.getPropertyValues()));
			setResourceDescription(original.getResourceDescription());
		}
	}

	/**
	 * Override settings in this bean definition (presumably a copied parent  from a parent-child inheritance relationship) from the given bean definition (presumably the child).
	 * <li>Will override beanClass if specified in the given bean definition.
	 * <li>Will always take {@code abstract}, {@code scope},{@code lazyInit}, {@code autowireMode}, {@code dependencyCheck},and {@code dependsOn} from the given bean definition.
	 * <li>Will add {@code constructorArgumentValues}, {@code propertyValues},{@code methodOverrides} from the given bean definition to existing ones.
	 * <li>Will override {@code factoryBeanName}, {@code factoryMethodName},
	 * {@code initMethodName}, and {@code destroyMethodName} if specified in the given bean definition.
	 */
	public void overrideFrom(BeanDefinition other) {
		//如有直接覆盖BeanClassName
		if (StringUtils.hasLength(other.getBeanClassName())) {
			setBeanClassName(other.getBeanClassName());
		}
		//如有作用域直接覆盖作用域
		if (StringUtils.hasLength(other.getScope())) {
			setScope(other.getScope());
		}
		//覆盖是否抽象
		setAbstract(other.isAbstract());
		setLazyInit(other.isLazyInit());
		//如有直接覆盖工厂Bean的姓名
		if (StringUtils.hasLength(other.getFactoryBeanName())) {
			setFactoryBeanName(other.getFactoryBeanName());
		}
		//如有直接覆盖工厂方法名
		if (StringUtils.hasLength(other.getFactoryMethodName())) {
			setFactoryMethodName(other.getFactoryMethodName());
		}
		setRole(other.getRole());
		setSource(other.getSource());
		copyAttributesFrom(other);
		//如果不是自己实现的BeanDefinition的话，都是继承这个BeanDefinition的
		if (other instanceof AbstractBeanDefinition) {
			AbstractBeanDefinition otherAbd = (AbstractBeanDefinition) other;
			//如有BeanClass直接覆盖
			if (otherAbd.hasBeanClass()) {
				setBeanClass(otherAbd.getBeanClass());
			}
			//如有构造函数的参数的直接覆盖
			if (otherAbd.hasConstructorArgumentValues()) {
				getConstructorArgumentValues().addArgumentValues(other.getConstructorArgumentValues());
			}
			//如有属性的参数直接覆盖
			if (otherAbd.hasPropertyValues()) {
				getPropertyValues().addPropertyValues(other.getPropertyValues());
			}
			//如果有方法重写直接覆盖
			if (otherAbd.hasMethodOverrides()) {
				getMethodOverrides().addOverrides(otherAbd.getMethodOverrides());
			}
			//设置自动装配的模型
			setAutowireMode(otherAbd.getAutowireMode());
			setDependencyCheck(otherAbd.getDependencyCheck());
			setDependsOn(otherAbd.getDependsOn());
			setAutowireCandidate(otherAbd.isAutowireCandidate());
			setPrimary(otherAbd.isPrimary());
			copyQualifiersFrom(otherAbd);
			setInstanceSupplier(otherAbd.getInstanceSupplier());
			setNonPublicAccessAllowed(otherAbd.isNonPublicAccessAllowed());
			setLenientConstructorResolution(otherAbd.isLenientConstructorResolution());
			if (otherAbd.getInitMethodName() != null) {
				setInitMethodName(otherAbd.getInitMethodName());
				setEnforceInitMethod(otherAbd.isEnforceInitMethod());
			}
			if (otherAbd.getDestroyMethodName() != null) {
				setDestroyMethodName(otherAbd.getDestroyMethodName());
				setEnforceDestroyMethod(otherAbd.isEnforceDestroyMethod());
			}
			//设置是否是合成的
			setSynthetic(otherAbd.isSynthetic());
			setResource(otherAbd.getResource());
		}else {
			getConstructorArgumentValues().addArgumentValues(other.getConstructorArgumentValues());
			getPropertyValues().addPropertyValues(other.getPropertyValues());
			setResourceDescription(other.getResourceDescription());
		}
	}

	/**
	 * Apply the provided default values to this bean.
	 * @param defaults the default settings to apply
	 * @since 2.5
	 */
	public void applyDefaults(BeanDefinitionDefaults defaults) {
		setLazyInit(defaults.isLazyInit());
		setAutowireMode(defaults.getAutowireMode());
		setDependencyCheck(defaults.getDependencyCheck());
		setInitMethodName(defaults.getInitMethodName());
		setEnforceInitMethod(false);
		setDestroyMethodName(defaults.getDestroyMethodName());
		setEnforceDestroyMethod(false);
	}

	// Specify the class for this bean.
	public void setBeanClass(@Nullable Class<?> beanClass) {
		this.beanClass = beanClass;
	}

	/**
	 * Return the class of the wrapped bean (assuming it is resolved already).
	 * @return the bean class (never {@code null})
	 * @throws IllegalStateException if the bean definition does not define a bean class,or a specified bean class name has not been resolved into an actual Class yet
	 * @see #hasBeanClass()
	 * @see #setBeanClass(Class)
	 * @see #resolveBeanClass(ClassLoader)
	 */
	public Class<?> getBeanClass() throws IllegalStateException {
		Object beanClassObject = beanClass;
		if (beanClassObject == null) throw new IllegalStateException("No bean class specified on bean definition");
		if (!(beanClassObject instanceof Class)) {
			throw new IllegalStateException("Bean class name [" + beanClassObject + "] has not been resolved into an actual Class");
		}
		return (Class<?>) beanClassObject;
	}

	/**
	 * Return whether this definition specifies a bean class.
	 * @see #getBeanClass()
	 * @see #setBeanClass(Class)
	 * @see #resolveBeanClass(ClassLoader)
	 */
	public boolean hasBeanClass() {
		return (beanClass instanceof Class);
	}

	/**
	 * Set if this bean is "abstract", i.e. not meant to be instantiated itself but  rather just serving as parent for concrete child bean definitions.
	 * Default is "false". Specify true to tell the bean factory to not try to instantiate that particular bean in any case.
	 */
	public void setAbstract(boolean abstractFlag) {
		this.abstractFlag = abstractFlag;
	}

	/**
	 * Determine the class of the wrapped bean, resolving it from a specified class name if necessary.
	 * Will also reload a specified  Class from its name when called with the bean class already resolved.
	 * @param classLoader the ClassLoader to use for resolving a (potential) class name
	 * @return the resolved bean class
	 * @throws ClassNotFoundException if the class name could be resolved
	 */
	@Nullable
	public Class<?> resolveBeanClass(@Nullable ClassLoader classLoader) throws ClassNotFoundException {
		String className = getBeanClassName();
		if (className == null) return null;
		Class<?> resolvedClass = ClassUtils.forName(className, classLoader);
		this.beanClass = resolvedClass;
		return resolvedClass;
	}
	/**
	 * Set the autowire mode. This determines whether any automagical detection and setting of bean references will happen. Default is AUTOWIRE_NO
	 * which means there won't be convention-based autowiring by name or type (however, there may still be explicit annotation-driven autowiring).
	 * @param autowireMode the autowire mode to set.  Must be one of the constants defined in this class.
	 * @see #AUTOWIRE_NO
	 * @see #AUTOWIRE_BY_NAME
	 * @see #AUTOWIRE_BY_TYPE
	 * @see #AUTOWIRE_CONSTRUCTOR
	 * @see #AUTOWIRE_AUTODETECT
	 */
	public void setAutowireMode(int autowireMode) {
		this.autowireMode = autowireMode;
	}

	// Return the autowire mode as specified in the bean definition.
	public int getAutowireMode() {
		return autowireMode;
	}

	/**
	 * Return the resolved autowire code,(resolving AUTOWIRE_AUTODETECT to AUTOWIRE_CONSTRUCTOR or AUTOWIRE_BY_TYPE).
	 * @see #AUTOWIRE_AUTODETECT
	 * @see #AUTOWIRE_CONSTRUCTOR
	 * @see #AUTOWIRE_BY_TYPE
	 */
	public int getResolvedAutowireMode() {
		if (autowireMode == AUTOWIRE_AUTODETECT) {
			// Work out whether to apply setter autowiring or constructor autowiring.
			// If it has a no-arg constructor it's deemed to be setter autowiring, otherwise we'll try constructor autowiring.
			Constructor<?>[] constructors = getBeanClass().getConstructors();
			for (Constructor<?> constructor : constructors) {
				// 一旦发现有 无参构造函数 则使用 按类型注入
				if (constructor.getParameterCount() == 0) return AUTOWIRE_BY_TYPE;
			}
			// 如果当前类中没有无参构造函数，则 使用指定有参构造函数注入
			return AUTOWIRE_CONSTRUCTOR;
		}else {
			return autowireMode;
		}
	}

	/**
	 * Set the dependency check code.
	 * @param dependencyCheck the code to set.Must be one of the four constants defined in this class.
	 * @see #DEPENDENCY_CHECK_NONE
	 * @see #DEPENDENCY_CHECK_OBJECTS
	 * @see #DEPENDENCY_CHECK_SIMPLE
	 * @see #DEPENDENCY_CHECK_ALL
	 */
	public void setDependencyCheck(int dependencyCheck) {
		this.dependencyCheck = dependencyCheck;
	}

	// Return the dependency check code.
	public int getDependencyCheck() {
		return dependencyCheck;
	}


	/**
	 * Register a qualifier to be used for autowire candidate resolution,keyed by the qualifier's type name.
	 * @see AutowireCandidateQualifier#getTypeName()
	 */
	public void addQualifier(AutowireCandidateQualifier qualifier) {
		qualifiers.put(qualifier.getTypeName(), qualifier);
	}

	// Return whether this bean has the specified qualifier.
	public boolean hasQualifier(String typeName) {
		return qualifiers.containsKey(typeName);
	}

	//  Return the qualifier mapped to the provided type name.
	@Nullable
	public AutowireCandidateQualifier getQualifier(String typeName) {
		return qualifiers.get(typeName);
	}

	/**
	 * Return all registered qualifiers.
	 * @return the Set of {@link AutowireCandidateQualifier} objects.
	 */
	public Set<AutowireCandidateQualifier> getQualifiers() {
		return new LinkedHashSet<>(qualifiers.values());
	}

	/**
	 * Copy the qualifiers from the supplied AbstractBeanDefinition to this bean definition.
	 * @param source the AbstractBeanDefinition to copy from
	 */
	public void copyQualifiersFrom(AbstractBeanDefinition source) {
		Assert.notNull(source, "Source must not be null");
		qualifiers.putAll(source.qualifiers);
	}

	/**
	 * Specify a callback for creating an instance of the bean,as an alternative to a declaratively specified factory method.
	 * If such a callback is set, it will override any other constructor or factory method metadata.
	 * However, bean property population and potential annotation-driven injection will still apply as usual.
	 * @since 5.0
	 * @see #setConstructorArgumentValues(ConstructorArgumentValues)
	 * @see #setPropertyValues(MutablePropertyValues)
	 */
	public void setInstanceSupplier(@Nullable Supplier<?> instanceSupplier) {
		this.instanceSupplier = instanceSupplier;
	}

	// Return a callback for creating an instance of the bean, if any. @since 5.0
	@Nullable
	public Supplier<?> getInstanceSupplier() {
		return instanceSupplier;
	}

	/**
	 * Specify whether to allow access to non-public constructors and methods,for the case of externalized metadata pointing to those.
	 * The default is {@code true}; switch this to {@code false} for public access only.
	 * This applies to constructor resolution, factory method resolution, and also init/destroy methods.
	 * Bean property accessors have to be public in any case and are not affected by this setting.
	 * Note that annotation-driven configuration will still access non-public
	 * members as far as they have been annotated. This setting applies to externalized metadata in this bean definition only.
	 */
	public void setNonPublicAccessAllowed(boolean nonPublicAccessAllowed) {
		this.nonPublicAccessAllowed = nonPublicAccessAllowed;
	}

	// Return whether to allow access to non-public constructors and methods.
	public boolean isNonPublicAccessAllowed() {
		return nonPublicAccessAllowed;
	}

	/**
	 * Specify whether to resolve constructors in lenient mode ({@code true},
	 * which is the default) or to switch to strict resolution (throwing an exception
	 * in case of ambiguous constructors that all match when converting the arguments,
	 * whereas lenient mode would use the one with the 'closest' type matches).
	 */
	public void setLenientConstructorResolution(boolean lenientConstructorResolution) {
		this.lenientConstructorResolution = lenientConstructorResolution;
	}

	//  Return whether to resolve constructors in lenient mode or in strict mode.
	public boolean isLenientConstructorResolution() {
		return lenientConstructorResolution;
	}

	// Specify property values for this bean, if any.
	public void setPropertyValues(MutablePropertyValues propertyValues) {
		this.propertyValues = propertyValues;
	}

	// Specify method overrides for the bean, if any.
	public void setMethodOverrides(MethodOverrides methodOverrides) {
		this.methodOverrides = methodOverrides;
	}

	/**
	 * Return information about methods to be overridden by the IoC container. This will be empty if there are no method overrides.
	 * Never returns {@code null}.
	 */
	public MethodOverrides getMethodOverrides() {
		if (methodOverrides == null) {
			methodOverrides = new MethodOverrides();
		}
		return methodOverrides;
	}

	// Return if there are method overrides defined for this bean. @since 5.0.2
	public boolean hasMethodOverrides() {
		return (methodOverrides != null && !methodOverrides.isEmpty());
	}

	/**
	 * Specify whether or not the configured init method is the default.The default value is {@code false}.
	 * @see #setInitMethodName
	 */
	public void setEnforceInitMethod(boolean enforceInitMethod) {
		this.enforceInitMethod = enforceInitMethod;
	}

	/**
	 * Indicate whether the configured init method is the default.
	 * @see #getInitMethodName()
	 */
	public boolean isEnforceInitMethod() {
		return enforceInitMethod;
	}

	/**
	 * Specify whether or not the configured destroy method is the default. The default value is {@code false}.
	 * @see #setDestroyMethodName
	 */
	public void setEnforceDestroyMethod(boolean enforceDestroyMethod) {
		this.enforceDestroyMethod = enforceDestroyMethod;
	}

	/**
	 * Indicate whether the configured destroy method is the default.
	 * @see #getDestroyMethodName
	 */
	public boolean isEnforceDestroyMethod() {
		return enforceDestroyMethod;
	}

	/**
	 * Set whether this bean definition is 'synthetic', that is,
	 * not defined by the application itself (for example, an infrastructure bean such as a helper for auto-proxying, created through {@code <aop:config>}).
	 */
	public void setSynthetic(boolean synthetic) {
		this.synthetic = synthetic;
	}

	//  Return whether this bean definition is 'synthetic', that is, not defined by the application itself.
	public boolean isSynthetic() {
		return synthetic;
	}

	// Set the resource that this bean definition came from (for the purpose of showing context in case of errors).
	public void setResource(@Nullable Resource resource) {
		this.resource = resource;
	}

	//  Set a description of the resource that this bean definition came from (for the purpose of showing context in case of errors).
	public void setResourceDescription(@Nullable String resourceDescription) {
		this.resource = (resourceDescription != null ? new DescriptiveResource(resourceDescription) : null);
	}

	/** Set the originating (e.g. decorated) BeanDefinition, if any.  */
	public void setOriginatingBeanDefinition(BeanDefinition originatingBd) {
		resource = new BeanDefinitionResource(originatingBd);
	}

	/**
	 * Validate this bean definition.
	 * @throws BeanDefinitionValidationException in case of validation failure
	 */
	public void validate() throws BeanDefinitionValidationException {
		if (hasMethodOverrides() && getFactoryMethodName() != null) {
			throw new BeanDefinitionValidationException("Cannot combine static factory method with method overrides: the static factory method must create the instance");
		}
		if (hasBeanClass()) {
			prepareMethodOverrides();
		}
	}

	/**
	 * Validate and prepare the method overrides defined for this bean.Checks for existence of a method with the specified name.
	 * @throws BeanDefinitionValidationException in case of validation failure
	 */
	public void prepareMethodOverrides() throws BeanDefinitionValidationException {
		// Check that lookup methods exists.
		if (hasMethodOverrides()) {
			Set<MethodOverride> overrides = getMethodOverrides().getOverrides();
			synchronized (overrides) {
				// 循环处理每个 MethodOverride 对象
				for (MethodOverride mo : overrides) {
					prepareMethodOverride(mo);
				}
			}
		}
	}

	/**
	 * Validate and prepare the given method override.
	 * Checks for existence of a method with the specified name,marking it as not overloaded if none found.
	 * @param mo the MethodOverride object to validate
	 * @throws BeanDefinitionValidationException in case of validation failure
	 */
	protected void prepareMethodOverride(MethodOverride mo) throws BeanDefinitionValidationException {
		// 获取方法名为 mo.getMethodName() 的方法数量，当方法重载时，count 的值就会大于1
		int count = ClassUtils.getMethodCountForName(getBeanClass(), mo.getMethodName());
		// count = 0，表明根据方法名未找到相应的方法，此时抛出异常
		if (count == 0) {
			throw new BeanDefinitionValidationException("Invalid method override: no method with name '" + mo.getMethodName() + "' on class [" + getBeanClassName() + "]");
		}else if (count == 1) {
			// 若 count = 1，表明仅存在已方法名为 mo.getMethodName()，这意味着方法不存在重载
			// Mark override as not overloaded, to avoid the overhead of arg type checking.
			// 方法不存在重载，则将 overloaded 成员变量设为 false
			mo.setOverloaded(false);
		}
	}

	//---------------------------------------------------------------------
	// Implementation of 【BeanDefinition】 interface
	//---------------------------------------------------------------------
	/** Specify the bean class name of this bean definition.*/
	@Override
	public void setBeanClassName(@Nullable String beanClassName) {
		this.beanClass = beanClassName;
	}

	/**  Return the current bean class name of this bean definition. */
	@Override
	@Nullable
	public String getBeanClassName() {
		Object beanClassObject = beanClass;
		if (beanClassObject instanceof Class) {
			return ((Class<?>) beanClassObject).getName();
		}else {
			return (String) beanClassObject;
		}
	}

	/**
	 * Set the name of the target scope for the bean.
	 * The default is singleton status, although this is only applied once a bean definition becomes active in the containing factory.
	 * A bean definition may eventually inherit its scope from a parent bean definition.
	 * For this reason, the default scope name is an empty string (i.e., {@code ""}),with singleton status being assumed until a resolved scope is set.
	 * @see #SCOPE_SINGLETON
	 * @see #SCOPE_PROTOTYPE
	 */
	@Override
	public void setScope(@Nullable String scope) {
		this.scope = scope;
	}

	// Return the name of the target scope for the bean.
	@Override
	@Nullable
	public String getScope() {
		return scope;
	}

	/**
	 * 只有为 "singleton" 或者 ""  的情况下，该bean才是单例！
	 * Return whether this a <b>Singleton</b>, with a single shared instance returned from all calls.
	 * @see #SCOPE_SINGLETON
	 */
	@Override
	public boolean isSingleton() {
		return SCOPE_SINGLETON.equals(scope) || SCOPE_DEFAULT.equals(scope);
	}

	/**
	 * Return whether this a <b>Prototype</b>, with an independent instance returned for each call.
	 * @see #SCOPE_PROTOTYPE
	 */
	@Override
	public boolean isPrototype() {
		return SCOPE_PROTOTYPE.equals(scope);
	}

	/**
	 * Return whether this bean is "abstract", i.e. not meant to be instantiated itself but rather just serving as parent for concrete child bean definitions.
	 * 返回此bean是否是“抽象的”
	 */
	@Override
	public boolean isAbstract() {
		return abstractFlag;
	}

	/**
	 * Set whether this bean should be lazily initialized.
	 * If {@code false}, the bean will get instantiated on startup by bean factories that perform eager initialization of singletons.
	 * 如果是false，bean将在启动时由执行单例初始化的bean工厂实例化。
	 */
	@Override
	public void setLazyInit(boolean lazyInit) {
		this.lazyInit = lazyInit;
	}

	/**
	 * Return whether this bean should be lazily initialized, i.e. not eagerly instantiated on startup.  Only applicable to a singleton bean.
	 * @return whether to apply lazy-init semantics ({@code false} by default)
	 */
	@Override
	public boolean isLazyInit() {
		return lazyInit;
	}

	/**
	 * Set the names of the beans that this bean depends on being initialized.
	 * The bean factory will guarantee that these beans get initialized first.
	 * Note that dependencies are normally expressed through bean properties or constructor arguments.
	 * This property should just be necessary for other kinds  of dependencies like statics (*ugh*) or database preparation on startup.
	 */
	@Override
	public void setDependsOn(@Nullable String... dependsOn) {
		this.dependsOn = dependsOn;
	}

	// Return the bean names that this bean depends on.
	@Override
	@Nullable
	public String[] getDependsOn() {
		return dependsOn;
	}

	/**
	 * Set whether this bean is a candidate for getting autowired into some other bean.
	 * Note that this flag is designed to only affect type-based autowiring.
	 * It does not affect explicit references by name, which will get resolved even if the specified bean is not marked as an autowire candidate.
	 * As a consequence,autowiring by name will nevertheless inject a bean if the name matches.
	 * @see #AUTOWIRE_BY_TYPE
	 * @see #AUTOWIRE_BY_NAME
	 */
	@Override
	public void setAutowireCandidate(boolean autowireCandidate) {
		this.autowireCandidate = autowireCandidate;
	}

	//  Return whether this bean is a candidate for getting autowired into some other bean.
	@Override
	public boolean isAutowireCandidate() {
		return autowireCandidate;
	}

	/**
	 * Set whether this bean is a primary autowire candidate.
	 * If this value is {@code true} for exactly one bean among multiple matching candidates, it will serve as a tie-breaker.
	 */
	@Override
	public void setPrimary(boolean primary) {
		this.primary = primary;
	}

	//  Return whether this bean is a primary autowire candidate.
	@Override
	public boolean isPrimary() {
		return primary;
	}

	/**
	 * Specify the factory bean to use, if any.
	 * This the name of the bean to call the specified factory method on.
	 * @see #setFactoryMethodName
	 */
	@Override
	public void setFactoryBeanName(@Nullable String factoryBeanName) {
		this.factoryBeanName = factoryBeanName;
	}

	//  Return the factory bean name, if any.
	@Override
	@Nullable
	public String getFactoryBeanName() {
		return factoryBeanName;
	}

	/**
	 * Specify a factory method, if any. This method will be invoked with
	 * constructor arguments, or with no arguments if none are specified.
	 * The method will be invoked on the specified factory bean, if any,
	 * or otherwise as a static method on the local bean class.
	 * @see #setFactoryBeanName
	 * @see #setBeanClassName
	 */
	@Override
	public void setFactoryMethodName(@Nullable String factoryMethodName) {
		this.factoryMethodName = factoryMethodName;
	}

	// Return a factory method, if any.
	@Override
	@Nullable
	public String getFactoryMethodName() {
		return factoryMethodName;
	}

	// Specify constructor argument values for this bean.
	public void setConstructorArgumentValues(ConstructorArgumentValues constructorArgumentValues) {
		this.constructorArgumentValues = constructorArgumentValues;
	}

	// Return constructor argument values for this bean (never {@code null}).
	@Override
	public ConstructorArgumentValues getConstructorArgumentValues() {
		if (constructorArgumentValues == null) constructorArgumentValues = new ConstructorArgumentValues();
		return constructorArgumentValues;
	}

	// Return if there are constructor argument values defined for this bean.
	@Override
	public boolean hasConstructorArgumentValues() {
		return (constructorArgumentValues != null && !constructorArgumentValues.isEmpty());
	}

	//  Return property values for this bean (never {@code null}).
	@Override
	public MutablePropertyValues getPropertyValues() {
		if (propertyValues == null) {
			propertyValues = new MutablePropertyValues();
		}
		return propertyValues;
	}

	/**
	 * Return if there are property values values defined for this bean.
	 * @since 5.0.2
	 */
	@Override
	public boolean hasPropertyValues() {
		return (propertyValues != null && !propertyValues.isEmpty());
	}

	//  Set the name of the initializer method.The default is {@code null} in which case there is no initializer method.
	@Override
	public void setInitMethodName(@Nullable String initMethodName) {
		this.initMethodName = initMethodName;
	}

	//  Return the name of the initializer method.
	@Override
	@Nullable
	public String getInitMethodName() {
		return initMethodName;
	}

	//  Set the name of the destroy method. The default is {@code null} in which case there is no destroy method.
	@Override
	public void setDestroyMethodName(@Nullable String destroyMethodName) {
		this.destroyMethodName = destroyMethodName;
	}

	// Return the name of the destroy method.
	@Override
	@Nullable
	public String getDestroyMethodName() {
		return destroyMethodName;
	}

	// Set the role hint for this {@code BeanDefinition}.
	@Override
	public void setRole(int role) {
		this.role = role;
	}

	//  Return the role hint for this {@code BeanDefinition}.
	@Override
	public int getRole() {
		return role;
	}

	// Set a human-readable description of this bean definition.
	@Override
	public void setDescription(@Nullable String description) {
		this.description = description;
	}

	// Return a human-readable description of this bean definition.
	@Override
	@Nullable
	public String getDescription() {
		return description;
	}

	// Return the resource that this bean definition came from.
	@Nullable
	public Resource getResource() {
		return resource;
	}

	// Return a description of the resource that this bean definition came from (for the purpose of showing context in case of errors).
	@Override
	@Nullable
	public String getResourceDescription() {
		return (resource != null ? resource.getDescription() : null);
	}

	/**
	 * Return the originating BeanDefinition, or {@code null} if none.
	 * Allows for retrieving the decorated bean definition, if any.
	 * Note that this method returns the immediate originator. Iterate through the originator chain to find the original BeanDefinition as defined by the user.
	 */
	@Override
	@Nullable
	public BeanDefinition getOriginatingBeanDefinition() {
		return (resource instanceof BeanDefinitionResource ? ((BeanDefinitionResource) resource).getBeanDefinition() : null);
	}

	//---------------------------------------------------------------------
	// Implementation of 【JDK】 interface
	//---------------------------------------------------------------------
	/**
	 * Public declaration of Object's {@code clone()} method. Delegates to {@link #cloneBeanDefinition()}.
	 * @see Object#clone()
	 */
	@Override
	public Object clone() {
		return cloneBeanDefinition();
	}

	/**
	 * Clone this bean definition.To be implemented by concrete subclasses.
	 * @return the cloned bean definition object
	 */
	public abstract AbstractBeanDefinition cloneBeanDefinition();

	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof AbstractBeanDefinition)) {
			return false;
		}
		AbstractBeanDefinition that = (AbstractBeanDefinition) other;
		boolean rtn = ObjectUtils.nullSafeEquals(getBeanClassName(), that.getBeanClassName());
		rtn = rtn &= ObjectUtils.nullSafeEquals(this.scope, that.scope);
		rtn = rtn &= this.abstractFlag == that.abstractFlag;
		rtn = rtn &= this.lazyInit == that.lazyInit;
		rtn = rtn &= this.autowireMode == that.autowireMode;
		rtn = rtn &= this.dependencyCheck == that.dependencyCheck;
		rtn = rtn &= Arrays.equals(this.dependsOn, that.dependsOn);
		rtn = rtn &= this.autowireCandidate == that.autowireCandidate;
		rtn = rtn &= ObjectUtils.nullSafeEquals(this.qualifiers, that.qualifiers);
		rtn = rtn &= this.primary == that.primary;
		rtn = rtn &= this.nonPublicAccessAllowed == that.nonPublicAccessAllowed;
		rtn = rtn &= this.lenientConstructorResolution == that.lenientConstructorResolution;
		rtn = rtn &= ObjectUtils.nullSafeEquals(this.constructorArgumentValues, that.constructorArgumentValues);
		rtn = rtn &= ObjectUtils.nullSafeEquals(this.propertyValues, that.propertyValues);
		rtn = rtn &= ObjectUtils.nullSafeEquals(this.methodOverrides, that.methodOverrides);
		rtn = rtn &= ObjectUtils.nullSafeEquals(this.factoryBeanName, that.factoryBeanName);
		rtn = rtn &= ObjectUtils.nullSafeEquals(this.factoryMethodName, that.factoryMethodName);
		rtn = rtn &= ObjectUtils.nullSafeEquals(this.initMethodName, that.initMethodName);
		rtn = rtn &= this.enforceInitMethod == that.enforceInitMethod;
		rtn = rtn &= ObjectUtils.nullSafeEquals(this.destroyMethodName, that.destroyMethodName);
		rtn = rtn &= this.enforceDestroyMethod == that.enforceDestroyMethod;
		rtn = rtn &= this.synthetic == that.synthetic;
		rtn = rtn &= this.role == that.role;
		return rtn && super.equals(other);
	}

	@Override
	public int hashCode() {
		int hashCode = ObjectUtils.nullSafeHashCode(getBeanClassName());
		hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.scope);
		hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.constructorArgumentValues);
		hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.propertyValues);
		hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.factoryBeanName);
		hashCode = 29 * hashCode + ObjectUtils.nullSafeHashCode(this.factoryMethodName);
		hashCode = 29 * hashCode + super.hashCode();
		return hashCode;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("class [");
		sb.append(getBeanClassName()).append("]");
		sb.append("; scope=").append(this.scope);
		sb.append("; abstract=").append(this.abstractFlag);
		sb.append("; lazyInit=").append(this.lazyInit);
		sb.append("; autowireMode=").append(this.autowireMode);
		sb.append("; dependencyCheck=").append(this.dependencyCheck);
		sb.append("; autowireCandidate=").append(this.autowireCandidate);
		sb.append("; primary=").append(this.primary);
		sb.append("; factoryBeanName=").append(this.factoryBeanName);
		sb.append("; factoryMethodName=").append(this.factoryMethodName);
		sb.append("; initMethodName=").append(this.initMethodName);
		sb.append("; destroyMethodName=").append(this.destroyMethodName);
		if (this.resource != null) {
			sb.append("; defined in ").append(this.resource.getDescription());
		}
		return sb.toString();
	}
}
