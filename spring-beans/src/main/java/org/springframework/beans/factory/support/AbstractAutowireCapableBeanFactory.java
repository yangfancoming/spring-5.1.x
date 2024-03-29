

package org.springframework.beans.factory.support;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

import org.apache.commons.logging.Log;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorUtils;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.GenericTypeResolver;
import org.springframework.core.MethodParameter;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.ParameterNameDiscoverer;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.ResolvableType;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * Abstract bean factory superclass that implements default bean creation,with the full capabilities specified by the {@link RootBeanDefinition} class.
 * Implements the {@link org.springframework.beans.factory.config.AutowireCapableBeanFactory} interface in addition to AbstractBeanFactory's {@link #createBean} method.
 *
 * Provides bean creation (with constructor resolution), property population, wiring (including autowiring), and initialization.
 * Handles runtime bean references, resolves managed collections, calls initialization methods, etc.
 * Supports autowiring constructors, properties by name, and properties by type.
 *
 * The main template method to be implemented by subclasses is {@link #resolveDependency(DependencyDescriptor, String, Set, TypeConverter)},
 * used for autowiring by type. In case of a factory which is capable of searching its bean definitions,
 * matching beans will typically be implemented through such a search. For other factory styles, simplified matching algorithms can be implemented.
 *
 * Note that this class does <i>not</i> assume or implement bean definition registry capabilities.
 * See {@link DefaultListableBeanFactory} for an implementation of the {@link org.springframework.beans.factory.ListableBeanFactory} and
 * {@link BeanDefinitionRegistry} interfaces, which represent the API and SPI view of such a factory, respectively.
 * @since 13.02.2004
 * @see RootBeanDefinition
 * @see DefaultListableBeanFactory
 * @see BeanDefinitionRegistry
 * 综合了 AbstractBeanFactory 并对接口 AutowireCapableBeanFactory 进行实现。
 */
public abstract class AbstractAutowireCapableBeanFactory extends AbstractBeanFactory implements AutowireCapableBeanFactory {

	/** Strategy for creating bean instances. */
	private InstantiationStrategy instantiationStrategy = new CglibSubclassingInstantiationStrategy();

	/** Resolver strategy for method parameter names. */
	@Nullable
	private ParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();

	/** 默认为true  Whether to automatically try to resolve circular references between beans. */
	private boolean allowCircularReferences = true;

	// Whether to resort to injecting a raw bean instance in case of circular reference,even if the injected bean eventually got wrapped.
	private boolean allowRawInjectionDespiteWrapping = false;

	//  Dependency types to ignore on dependency check and autowire, as Set of  Class objects: for example, String. Default is none.
	private final Set<Class<?>> ignoredDependencyTypes = new HashSet<>();

	/**
	 * Dependency interfaces to ignore on dependency check and autowire, as Set of Class objects.
	 *  By default, only the BeanFactory interface is ignored.
	 *  存储不自动注入的Class集合
	 */
	private final Set<Class<?>> ignoredDependencyInterfaces = new HashSet<>();

	// The name of the currently created bean, for implicit dependency registration on getBean etc invocations triggered from a user-specified Supplier callback.
	private final NamedThreadLocal<String> currentlyCreatedBean = new NamedThreadLocal<>("Currently created bean");

	/** Cache of unfinished FactoryBean instances: FactoryBean name to BeanWrapper. */
	private final ConcurrentMap<String, BeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();

	/** Cache of candidate factory methods per factory class. */
	private final ConcurrentMap<Class<?>, Method[]> factoryMethodCandidateCache = new ConcurrentHashMap<>();

	/** Cache of filtered PropertyDescriptors: bean Class to PropertyDescriptor array. */
	private final ConcurrentMap<Class<?>, PropertyDescriptor[]> filteredPropertyDescriptorsCache = new ConcurrentHashMap<>();

	// Create a new AbstractAutowireCapableBeanFactory.
	public AbstractAutowireCapableBeanFactory() {
		super();
		logger.warn("进入 【AbstractAutowireCapableBeanFactory】 构造函数 {}");
		// 忽略指定接口的自动装配功能
		/**
		 * 忽略指定接口的自动装配功能：如ClassA引用了ClassB，那么当Spring在获取ClassA的实例时，如果发现ClassB还没有被初始化，那么Spring会自动初始化ClassB。
		 * 但是如果ClassB实现了BeanNameAware接口的话，则Spring不会自动初始化ClassB，这就是忽略指定接口的自动装配
		 * （这里面注意：@Autowired和它的关系，其实是有坑的，后续会专门讲解这个坑）
		*/
		ignoreDependencyInterface(BeanNameAware.class);
		ignoreDependencyInterface(BeanFactoryAware.class);
		ignoreDependencyInterface(BeanClassLoaderAware.class);
	}

	/**
	 * Create a new AbstractAutowireCapableBeanFactory with the given parent.
	 * @param parentBeanFactory parent bean factory, or {@code null} if none
	 */
	public AbstractAutowireCapableBeanFactory(@Nullable BeanFactory parentBeanFactory) {
		this();
		setParentBeanFactory(parentBeanFactory);
	}

	/**
	 * Set the instantiation strategy to use for creating bean instances.
	 * Default is CglibSubclassingInstantiationStrategy.
	 * @see CglibSubclassingInstantiationStrategy
	 */
	public void setInstantiationStrategy(InstantiationStrategy instantiationStrategy) {
		this.instantiationStrategy = instantiationStrategy;
	}

	// Return the instantiation strategy to use for creating bean instances.
	protected InstantiationStrategy getInstantiationStrategy() {
		return this.instantiationStrategy;
	}

	/**
	 * Set the ParameterNameDiscoverer to use for resolving method parameter names if needed (e.g. for constructor names).
	 * Default is a {@link DefaultParameterNameDiscoverer}.
	 */
	public void setParameterNameDiscoverer(@Nullable ParameterNameDiscoverer parameterNameDiscoverer) {
		this.parameterNameDiscoverer = parameterNameDiscoverer;
	}

	// Return the ParameterNameDiscoverer to use for resolving method parameter  names if needed.
	@Nullable
	protected ParameterNameDiscoverer getParameterNameDiscoverer() {
		return this.parameterNameDiscoverer;
	}

	/**
	 * Set whether to allow circular references between beans - and automatically try to resolve them.
	 * Note that circular reference resolution means that one of the involved beans
	 * will receive a reference to another bean that is not fully initialized yet.
	 * This can lead to subtle and not-so-subtle side effects on initialization; it does work fine for many scenarios, though.
	 * Default is "true". Turn this off to throw an exception when encountering a circular reference, disallowing them completely.
	 * <b>NOTE:</b> It is generally recommended to not rely on circular references between your beans. Refactor your application logic to have the two beans
	 * involved delegate to a third bean that encapsulates their common logic.
	 */
	public void setAllowCircularReferences(boolean allowCircularReferences) {
		this.allowCircularReferences = allowCircularReferences;
	}

	/**
	 * Set whether to allow the raw injection of a bean instance into some other
	 * bean's property, despite the injected bean eventually getting wrapped (for example, through AOP auto-proxying).
	 * This will only be used as a last resort in case of a circular reference
	 * that cannot be resolved otherwise: essentially, preferring a raw instance getting injected over a failure of the entire bean wiring process.
	 * Default is "false", as of Spring 2.0. Turn this on to allow for non-wrapped raw beans injected into some of your references, which was Spring 1.2's
	 * (arguably unclean) default behavior.
	 * <b>NOTE:</b> It is generally recommended to not rely on circular references between your beans, in particular with auto-proxying involved.
	 * @see #setAllowCircularReferences
	 */
	public void setAllowRawInjectionDespiteWrapping(boolean allowRawInjectionDespiteWrapping) {
		this.allowRawInjectionDespiteWrapping = allowRawInjectionDespiteWrapping;
	}

	// Ignore the given dependency type for autowiring: for example, String. Default is none.
	public void ignoreDependencyType(Class<?> type) {
		this.ignoredDependencyTypes.add(type);
	}

	/**
	 * Ignore the given dependency interface for autowiring.
	 * This will typically be used by application contexts to register dependencies that are resolved in other ways,
	 * like BeanFactory through BeanFactoryAware or ApplicationContext through ApplicationContextAware.
	 * By default, only the BeanFactoryAware interface is ignored.
	 * For further types to ignore, invoke this method for each type.
	 * @see org.springframework.beans.factory.BeanFactoryAware
	 * @see org.springframework.context.ApplicationContextAware
	 */
	public void ignoreDependencyInterface(Class<?> ifc) {
		ignoredDependencyInterfaces.add(ifc);
	}

	@Override
	public void copyConfigurationFrom(ConfigurableBeanFactory otherFactory) {
		super.copyConfigurationFrom(otherFactory);
		if (otherFactory instanceof AbstractAutowireCapableBeanFactory) {
			AbstractAutowireCapableBeanFactory otherAutowireFactory = (AbstractAutowireCapableBeanFactory) otherFactory;
			instantiationStrategy = otherAutowireFactory.instantiationStrategy;
			allowCircularReferences = otherAutowireFactory.allowCircularReferences;
			ignoredDependencyTypes.addAll(otherAutowireFactory.ignoredDependencyTypes);
			ignoredDependencyInterfaces.addAll(otherAutowireFactory.ignoredDependencyInterfaces);
		}
	}

	//------------------------------------------------------------------------------------------------------------------------------------------
	// Implementation of 【AutowireCapableBeanFactory】 interface ，  Typical methods for creating and populating external bean instances
	//------------------------------------------------------------------------------------------------------------------------------------------
	@Override
	@SuppressWarnings("unchecked")
	public <T> T createBean(Class<T> beanClass) throws BeansException {
		// Use prototype bean definition, to avoid registering bean as dependent bean.
		RootBeanDefinition bd = new RootBeanDefinition(beanClass);
		bd.setScope(SCOPE_PROTOTYPE);
		bd.allowCaching = ClassUtils.isCacheSafe(beanClass, getBeanClassLoader());
		return (T) createBean(beanClass.getName(), bd, null);
	}

	@Override
	public void autowireBean(Object existingBean) {
		// Use non-singleton bean definition, to avoid registering bean as dependent bean.
		RootBeanDefinition bd = new RootBeanDefinition(ClassUtils.getUserClass(existingBean));
		bd.setScope(BeanDefinition.SCOPE_PROTOTYPE);
		bd.allowCaching = ClassUtils.isCacheSafe(bd.getBeanClass(), getBeanClassLoader());
		BeanWrapper bw = new BeanWrapperImpl(existingBean);
		initBeanWrapper(bw);
		// 前面都是铺垫，重点在这里
		populateBean(bd.getBeanClass().getName(), bd, bw);
	}

	@Override
	public Object configureBean(Object existingBean, String beanName) throws BeansException {
		markBeanAsCreated(beanName);
		BeanDefinition mbd = getMergedBeanDefinition(beanName);
		RootBeanDefinition bd = null;
		if (mbd instanceof RootBeanDefinition) {
			RootBeanDefinition rbd = (RootBeanDefinition) mbd;
			bd = (rbd.isPrototype() ? rbd : rbd.cloneBeanDefinition());
		}
		if (bd == null) {
			bd = new RootBeanDefinition(mbd);
		}
		if (!bd.isPrototype()) {
			bd.setScope(BeanDefinition.SCOPE_PROTOTYPE);
			bd.allowCaching = ClassUtils.isCacheSafe(ClassUtils.getUserClass(existingBean), getBeanClassLoader());
		}
		BeanWrapper bw = new BeanWrapperImpl(existingBean);
		initBeanWrapper(bw);
		populateBean(beanName, bd, bw);
		return initializeBean(beanName, existingBean, bd);
	}

	//-------------------------------------------------------------------------
	// Specialized methods for fine-grained control over the bean lifecycle
	//-------------------------------------------------------------------------
	@Override
	public Object createBean(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException {
		// Use non-singleton bean definition, to avoid registering bean as dependent bean.
		RootBeanDefinition bd = new RootBeanDefinition(beanClass, autowireMode, dependencyCheck);
		// 这里看到了，采用的不是单例，而是prototype
		bd.setScope(BeanDefinition.SCOPE_PROTOTYPE);
		// For the nullability warning, see the elaboration in AbstractBeanFactory.doGetBean;
		// in short: This is never going to be null unless user-declared code enforces null.
		// doc说得很明白，这里返回值永远不可能为null。除非调用者强制return null
		// 注意的是:这里BeanName就是beanClass.getName()
		return createBean(beanClass.getName(), bd, null);
	}

	@Override
	public Object autowire(Class<?> beanClass, int autowireMode, boolean dependencyCheck) throws BeansException {
		// Use non-singleton bean definition, to avoid registering bean as dependent bean.
		final RootBeanDefinition bd = new RootBeanDefinition(beanClass, autowireMode, dependencyCheck);
		bd.setScope(BeanDefinition.SCOPE_PROTOTYPE);
		// 使用指定有参构造函数注入
		if (bd.getResolvedAutowireMode() == AUTOWIRE_CONSTRUCTOR) {
			return autowireConstructor(beanClass.getName(), bd, null, null).getWrappedInstance();
		}else {  // 使用无参构造函数注入
			Object bean;
			final BeanFactory parent = this;
			if (System.getSecurityManager() != null) {
				bean = AccessController.doPrivileged((PrivilegedAction<Object>) () -> getInstantiationStrategy().instantiate(bd, null, parent),getAccessControlContext());
			}else {
				bean = getInstantiationStrategy().instantiate(bd, null, parent);
			}
			populateBean(beanClass.getName(), bd, new BeanWrapperImpl(bean));
			return bean;
		}
	}

	@Override
	public void autowireBeanProperties(Object existingBean, int autowireMode, boolean dependencyCheck) throws BeansException {
		if (autowireMode == AUTOWIRE_CONSTRUCTOR) throw new IllegalArgumentException("AUTOWIRE_CONSTRUCTOR not supported for existing bean instance");
		// Use non-singleton bean definition, to avoid registering bean as dependent bean.
		RootBeanDefinition bd = new RootBeanDefinition(ClassUtils.getUserClass(existingBean), autowireMode, dependencyCheck);
		bd.setScope(BeanDefinition.SCOPE_PROTOTYPE);
		BeanWrapper bw = new BeanWrapperImpl(existingBean);
		initBeanWrapper(bw);
		populateBean(bd.getBeanClass().getName(), bd, bw);
	}

	@Override
	public void applyBeanPropertyValues(Object existingBean, String beanName) throws BeansException {
		markBeanAsCreated(beanName);
		BeanDefinition bd = getMergedBeanDefinition(beanName);
		BeanWrapper bw = new BeanWrapperImpl(existingBean);
		initBeanWrapper(bw);
		applyPropertyValues(beanName, bd, bw, bd.getPropertyValues());
	}

	@Override
	public Object initializeBean(Object existingBean, String beanName) {
		return initializeBean(beanName, existingBean, null);
	}

	@Override
	public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName) throws BeansException {
		Object result = existingBean;
		// 取出所有 像 MyBeanPostProcessor 那样实现了 BeanPostProcessor 接口的Bean  按个遍历
		List<BeanPostProcessor> beanPostProcessors = getBeanPostProcessors();
		for (BeanPostProcessor processor : beanPostProcessors) {
			Object current = processor.postProcessBeforeInitialization(result, beanName);
			// 该循环中 一旦返回null 则跳出循环 后面的 后置处理器不会再被执行
			if (current == null) return result;
			result = current;
		}
		return result;
	}

	@Override
	public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName) throws BeansException {
		Object result = existingBean;
		List<BeanPostProcessor> beanPostProcessors = getBeanPostProcessors();
		for (BeanPostProcessor processor : beanPostProcessors) {
			// bean 初始化后置处理
			Object current = processor.postProcessAfterInitialization(result, beanName);
			// 如果返回null；后面的所有 后置处理器的方法就不执行，直接返回(所以执行顺序很重要)
			if (current == null) return result;
			result = current;
		}
		logger.warn("【IOC容器 BeanPostProcessor  after 处理  成熟态 ---  】 beanName： " + beanName + "		实际对象：" + result);
		return result;
	}

	@Override
	public void destroyBean(Object existingBean) {
		new DisposableBeanAdapter(existingBean, getBeanPostProcessors(), getAccessControlContext()).destroy();
	}

	//-------------------------------------------------------------------------
	// Delegate methods for resolving injection points
	//-------------------------------------------------------------------------
	@Override
	public Object resolveBeanByName(String name, DependencyDescriptor descriptor) {
		InjectionPoint previousInjectionPoint = ConstructorResolver.setCurrentInjectionPoint(descriptor);
		try {
			return getBean(name, descriptor.getDependencyType());
		}finally {
			ConstructorResolver.setCurrentInjectionPoint(previousInjectionPoint);
		}
	}

	@Override
	@Nullable
	public Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName) throws BeansException {
		return resolveDependency(descriptor, requestingBeanName, null, null);
	}

	//---------------------------------------------------------------------
	// Implementation of relevant 【AbstractBeanFactory】 template methods
	//---------------------------------------------------------------------
	/**
	 * Central method of this class: creates a bean instance, populates the bean instance, applies post-processors, etc.
	 * @see #doCreateBean
	 * 最终都调用到了下面这个createBean方法。它也是AbstractBeanFactory提供的一个抽象方法
	 * 最终也由AbstractAutowireCapableBeanFactory去实现的。 我们熟悉的doGetBean()方法，最终也是调用它来创建实例对象  只是doGetBean()把单例对象都缓存起来了
	 * 这个方法很单纯：创建一个实例，然后初始化他（给属性们赋值），然后return出去即可
	 */
	@Override
	protected Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) throws BeanCreationException {
		if (logger.isTraceEnabled()) logger.trace("Creating instance of bean '" + beanName + "'");
		RootBeanDefinition mbdToUse = mbd;
		// Make sure bean class is actually resolved at this point, and clone the bean definition in case of a dynamically resolved Class which cannot be stored in the shared merged bean definition.
		// 确保对应BeanClass完成解析，具体表现是进行了ClassLoder.loadClass或Class.forName完成了类加载
		// 解析 bean 的类型  // 从bd中解析出bean的Class
		Class<?> resolvedClass = resolveBeanClass(mbd, beanName);
		if (resolvedClass != null && !mbd.hasBeanClass() && mbd.getBeanClassName() != null) {
			mbdToUse = new RootBeanDefinition(mbd);
			mbdToUse.setBeanClass(resolvedClass);
		}
		// Prepare method overrides.
		try {
			/**
			 * 准备方法覆盖，主要为lookup-method,replace-method等配置准备
			 * 处理 lookup-method 和 replace-method 配置，Spring 将这两个配置统称为 override method
			 * 当用户配置了 lookup-method 和 replace-method 时，Spring 需要对目标 bean 进行增强。在增强之前，需要做一些准备工作，也就是 prepareMethodOverrides 中的逻辑
			*/
			mbdToUse.prepareMethodOverrides();
		}catch (BeanDefinitionValidationException ex) {
			throw new BeanDefinitionStoreException(mbdToUse.getResourceDescription(),beanName, "Validation of method overrides failed", ex);
		}
		try {
			// Give BeanPostProcessors a chance to return a proxy instead of the target bean instance.
			/**
			 供特定后置处理器拓展，如果直接生成了一个Bean，就直接返回不走正常创建流程。
			 具体逻辑是判断当前Spring容器是否注册了实现了InstantiationAwareBeanPostProcessor 接口的后置处理器
			 如果有，则依次调用其中的 applyBeanPostProcessorsBeforeInstantiation 方法，如果中间任意一个方法返回不为null,直接结束调用。
			 然后依次所有注册的 BeanPostProcessor 的 postProcessAfterInitialization 方法（同样如果任意一次返回不为null,即终止调用。
			 */
			// 在 bean 初始化前应用后置处理，如果后置处理返回的 bean 不为空，则直接返回
			// 若BeanPostProcessors 产生了一个代理对象，就不需要我去创建了，就不继续往下走了（AOP都走这里）
			Object bean = resolveBeforeInstantiation(beanName, mbdToUse);
			if (bean != null) return bean;
		}catch (Throwable ex) {
			throw new BeanCreationException(mbdToUse.getResourceDescription(), beanName,"BeanPostProcessor before instantiation of bean failed", ex);
		}
		try {
			// 这里被代理了！  调用 doCreateBean 创建 bean
			// 重点来了。它是本类的一个protected方法，专门用于处理创建Bean的过程（包括属性赋值之类的）
			Object beanInstance = doCreateBean(beanName, mbdToUse, args);
			if (logger.isTraceEnabled()) logger.trace("Finished creating instance of bean '" + beanName + "'");
			return beanInstance;
		} catch (BeanCreationException | ImplicitlyAppearedSingletonException ex) {
			// A previously detected exception with proper bean creation context already,or illegal singleton state to be communicated up to DefaultSingletonBeanRegistry.
			throw ex;
		} catch (Throwable ex) {
			throw new BeanCreationException(mbdToUse.getResourceDescription(), beanName, "Unexpected exception during bean creation", ex);
		}
	}

	/**
	 * Actually create the specified bean. Pre-creation processing has already happened at this point,
	 * e.g. checking {@code postProcessBeforeInstantiation} callbacks.
	 * Differentiates between default bean instantiation, use of a factory method, and autowiring a constructor.
	 * @param beanName the name of the bean
	 * @param mbd the merged bean definition for the bean
	 * @param args explicit arguments to use for constructor or factory method invocation
	 * @return a new instance of the bean
	 * @throws BeanCreationException if the bean could not be created
	 * @see #instantiateBean
	 * @see #instantiateUsingFactoryMethod
	 * @see #autowireConstructor
	 * 最重要的三个步骤为doCreateBean里面的：createBeanInstance、populateBean、initializeBean，都在AbstractAutowireCapableBeanFactory这里
	 * doCreateBean是bean创建的核心方法。
	 * 它大体上可以分成两个步骤：1.bean实例化；2.bean初始化。
	 * bean实例化是调用 createBeanInstance 方法，最终通过反射创建出对象。
	 * bean的初始化是通过 populateBean(beanName, mbd, instanceWrapper)和initializeBean(beanName, exposedObject, mbd)实现的，对纯净态的bean进行属性填充及自动注入。
	 */
	protected Object doCreateBean(final String beanName, final RootBeanDefinition mbd, final @Nullable Object[] args) throws BeanCreationException {
		/**
		 *  BeanWrapper 是一个基础接口，由接口名可看出这个接口的实现类用于包裹 bean 实例。
		 *  BeanWrapper 的实现类可以通过调用getPropertyValue和setPropertyValue等方法 方便的反射读写Bean的具体属性
		 *  Instantiate the bean.
		*/
		BeanWrapper instanceWrapper = null;
		if (mbd.isSingleton()) {
			// 先尝试从缓存中取 BeanWrapper，并清理相关记录
			instanceWrapper = factoryBeanInstanceCache.remove(beanName);
		}
		if (instanceWrapper == null) {
			/*
			 * 创建 bean 实例，并将实例包裹在 BeanWrapper 实现类对象中返回。
			 * createBeanInstance 中包含三种创建 bean 实例的方式：
			 *   1. 通过工厂方法创建 bean 实例
			 *   2. 通过构造方法自动注入（autowire by constructor）的方式创建 bean 实例
			 *   3. 通过无参构造方法方法创建 bean 实例
			 * 若 bean 的配置信息中配置了 lookup-method 和 replace-method，则会使用 CGLIB  增强 bean 实例。
			 */
			// 调用构造方法创建一个空实例对象，并用BeanWrapper进行包装  内存态--->纯净态
			instanceWrapper = createBeanInstance(beanName, mbd, args);
		}
		// 从 BeanWrapper 中获取我们内存态对象
		// 此处的 bean 可以认为是一个原始的纯净态的 bean 实例，暂未填充属性
		final Object bean = instanceWrapper.getWrappedInstance();
		Class<?> beanType = instanceWrapper.getWrappedClass();
		if (beanType != NullBean.class) {
			mbd.resolvedTargetType = beanType;
		}
		// Allow post-processors to modify the merged bean definition.
		// 获取所有的后置处理器，如果后置处理器实现了MergedBeanDefinitionPostProcessor接口，则一次调用其postProcessMergedBeanDefinition方法
		// 这里又遇到后置处理了，此处的后置处理是用于处理已“合并的 BeanDefinition”。关于这种后置处理器具体的实现细节就不深入理解了，大家有兴趣可以自己去看
		// 调用实现了MergedBeanDefinitionPostProcessor的bean后置处理器的postProcessMergedBeanDefinition方法。
		synchronized (mbd.postProcessingLock) {
			if (!mbd.postProcessed) {
				try {
					applyMergedBeanDefinitionPostProcessors(mbd, beanType, beanName);
				}catch (Throwable ex) {
					throw new BeanCreationException(mbd.getResourceDescription(), beanName,"Post-processing of merged bean definition failed", ex);
				}
				mbd.postProcessed = true;
			}
		}
		// Eagerly cache singletons to be able to resolve circular references even when triggered by lifecycle interfaces like BeanFactoryAware.
		/**
		 * 如果满足循环引用缓存条件，先缓存具体对象
		 * earlySingletonExposure 是一个重要的变量，该变量用于表示是否提前暴露单例 bean，用于解决循环依赖。
		 * earlySingletonExposure 由三个条件综合而成，如下： 单例 && 是否允许循环依赖 && 是否存于创建状态中。
		*/
		boolean earlySingletonExposure = (mbd.isSingleton() && allowCircularReferences && isSingletonCurrentlyInCreation(beanName));
		if (earlySingletonExposure) {
			if (logger.isTraceEnabled()) logger.trace("Eagerly caching bean '" + beanName + "' to allow for resolving potential circular references");
			// 提前暴露一个单例工厂方法，确保其他Bean能引用到此bean
			// 具体内部会遍历后置处理器，判断是否有SmartInstantiationAwareBeanPostProcessor的实现类，然后调用里面getEarlyBeanReference覆盖当前Bean
			// 默认不做任何操作返回当前Bean，作为拓展，这里比如可以供AOP来创建代理类
			// 添加工厂对象到 singletonFactories 三级缓存中
			addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean));
		}
		// Initialize the bean instance.开始对Bean实例进行初始化
		Object exposedObject = bean;
		try {
			// 向 bean 实例中填充属性，在这里面完成依赖注入的相关内容  完成属性的注入  反射调用 getter setter方法  即由 纯净态---->成熟态
			// @Autowire-  和 循环依赖 均在该方法处理
			// 给Bean实例的各个属性进行赋值 比如调用 InstantiationAwareBeanPostProcessor#postProcessAfterInstantiation、给属性注入值（并不依赖于@Autowired注解）
			// 执行InstantiationAwareBeanPostProcessor#postProcessPropertyValues 等等
			populateBean(beanName, mbd, instanceWrapper);
			/**
			 * populateBean 完成属性依赖注入后，下面 initializeBean 进一步初始化Bean 可以将populateBean填充的属性 通过 initializeBean 修改掉
			 * 具体进行了以下操作：
			 * 1.若实现了BeanNameAware， BeanClassLoaderAware，BeanFactoryAwareAware等接口，则执行接口方法，并注入相关对象
			 * 2.遍历后置处理器，调用实现的postProcessBeforeInitialization方法，
			 * 3.如果实现了initialzingBean，调用实现的 afterPropertiesSet()
			 * 4.如果配置了init-mothod，调用相应的init方法
			 * 5.遍历后置处理器，调用实现的postProcessAfterInitialization
			 *  另外，AOP 相关逻辑也会在该方法中织入切面逻辑，此时的 exposedObject 就变成了 一个代理对象了
			*/
			// 初始化Bean 执行一些初始化方法init @PostContruct方法等等
			// BeanPostProcessor的postProcessBeforeInitialization和postProcessAfterInitialization等等
			exposedObject = initializeBean(beanName, exposedObject, mbd);
		}catch (Throwable ex) {
			if (ex instanceof BeanCreationException && beanName.equals(((BeanCreationException) ex).getBeanName())) {
				throw (BeanCreationException) ex;
			}else {
				throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Initialization of bean failed", ex);
			}
		}

		if (earlySingletonExposure) {
			Object earlySingletonReference = getSingleton(beanName, false);
			if (earlySingletonReference != null) {
				// 若 initializeBean 方法未改变 exposedObject 的引用，则此处的条件为 true
				if (exposedObject == bean) {
					exposedObject = earlySingletonReference;
				}
				// 下面的逻辑我也没完全搞懂，就不分析了。见谅。
				else if (!allowRawInjectionDespiteWrapping && hasDependentBean(beanName)) {
					String[] dependentBeans = getDependentBeans(beanName);
					Set<String> actualDependentBeans = new LinkedHashSet<>(dependentBeans.length);
					for (String dependentBean : dependentBeans) {
						if (!removeSingletonIfCreatedForTypeCheckOnly(dependentBean)) {
							actualDependentBeans.add(dependentBean);
						}
					}
					if (!actualDependentBeans.isEmpty()) {
						throw new BeanCurrentlyInCreationException(beanName,
								"Bean with name '" + beanName + "' has been injected into other beans [" + StringUtils.collectionToCommaDelimitedString(actualDependentBeans) +
										"] in its raw version as part of a circular reference, but has eventually been wrapped. This means that said other beans do not use the final version of the " +
										"bean. This is often the result of over-eager type matching - consider using 'getBeanNamesOfType' with the 'allowEagerInit' flag turned off, for example.");
					}
				}
			}
		}
		// Register bean as disposable.
		// 如果实现了 Disposable 接口，会在这里进行注册，最后在销毁的时候调用相应的destroy方法
		try {
			// 注册销毁逻辑
				registerDisposableBeanIfNecessary(beanName, bean, mbd);
		}catch (BeanDefinitionValidationException ex) {
			throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Invalid destruction signature", ex);
		}
		return exposedObject;
	}

	@Override
	@Nullable
	protected Class<?> predictBeanType(String beanName, RootBeanDefinition mbd, Class<?>... typesToMatch) {
		//判断beanName对应的类型
		Class<?> targetType = determineTargetType(beanName, mbd, typesToMatch);
		// Apply SmartInstantiationAwareBeanPostProcessors to predict the eventual type after a before-instantiation shortcut.
		//SmartInstantiationAwareBeanPostProcessor这个后置处理器可以决定bean的类型和用于实例化bean的构造函数
		if (targetType != null && !mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
			for (BeanPostProcessor bp : getBeanPostProcessors()) {
				if (bp instanceof SmartInstantiationAwareBeanPostProcessor) {
					SmartInstantiationAwareBeanPostProcessor ibp = (SmartInstantiationAwareBeanPostProcessor) bp;
					Class<?> predicted = ibp.predictBeanType(targetType, beanName);
					//如果返回的类型不为空，并且如果是要判断是否为FactoryBean，那么会再不符合条件的情况下继续调用下一个后置处理器
					//如果是其他类型，直接返回
					if (predicted != null && (typesToMatch.length != 1 || FactoryBean.class != typesToMatch[0] || FactoryBean.class.isAssignableFrom(predicted))) {
						return predicted;
					}
				}
			}
		}
		return targetType;
	}

	/**
	 * Determine the target type for the given bean definition.
	 * @param beanName the name of the bean (for error handling purposes)
	 * @param mbd the merged bean definition for the bean
	 * @param typesToMatch the types to match in case of internal type matching purposes (also signals that the returned {@code Class} will never be exposed to application code)
	 * @return the type for the bean if determinable, or {@code null} otherwise
	 */
	@Nullable
	protected Class<?> determineTargetType(String beanName, RootBeanDefinition mbd, Class<?>... typesToMatch) {
		//获取缓存的类型，如果有，直接返回，没有就需要继续解析
		Class<?> targetType = mbd.getTargetType();
		if (targetType == null) {
			//判断是否有指定工厂方法，没有就通过加载器加载beanClass，返回类型
			//这里面如果指定了loadTimeWeaver的临时的加载器，那么会判断是否需要进行增强，如果增强会返回增强后的类对象
			//这个临时加载器，我们在预备容器时判断是否存在loadTimeWeaver时，设置进来的
			//如果存在工厂方法，那么需要解析工厂方法的返回值类型
			targetType = (mbd.getFactoryMethodName() != null ? getTypeForFactoryMethod(beanName, mbd, typesToMatch) : resolveBeanClass(mbd, beanName, typesToMatch));
			if (ObjectUtils.isEmpty(typesToMatch) || getTempClassLoader() == null) {
				mbd.resolvedTargetType = targetType;
			}
		}
		return targetType;
	}

	/**
	 * Determine the target type for the given bean definition which is based on
	 * a factory method. Only called if there is no singleton instance registered  for the target bean already.
	 * This implementation determines the type matching {@link #createBean}'s
	 * different creation strategies. As far as possible, we'll perform static type checking to avoid creation of the target bean.
	 * @param beanName the name of the bean (for error handling purposes)
	 * @param mbd the merged bean definition for the bean
	 * @param typesToMatch the types to match in case of internal type matching purposes (also signals that the returned {@code Class} will never be exposed to application code)
	 * @return the type for the bean if determinable, or {@code null} otherwise
	 * @see #createBean
	 */
	@Nullable
	protected Class<?> getTypeForFactoryMethod(String beanName, RootBeanDefinition mbd, Class<?>... typesToMatch) {
		//从缓存中获取其属性
		ResolvableType cachedReturnType = mbd.factoryMethodReturnType;
		if (cachedReturnType != null) {
			return cachedReturnType.resolve();
		}
		Class<?> factoryClass;
		boolean isStatic = true;
		String factoryBeanName = mbd.getFactoryBeanName();
		if (factoryBeanName != null) {
			//如果指定的工厂beanName是自己，报错
			if (factoryBeanName.equals(beanName)) {
				throw new BeanDefinitionStoreException(mbd.getResourceDescription(), beanName,"factory-bean reference points back to the same bean definition");
			}
			// Check declared factory method return type on factory class.
			//获取factoryBeanName对应的bean的class
			factoryClass = getType(factoryBeanName);
			//如果存在factoryBeanName，那么对应的方法不为静态方法
			isStatic = false;
		}else {
			// Check declared factory method return type on bean class.
			//直接解析beanName对应bean的class
			factoryClass = resolveBeanClass(mbd, beanName, typesToMatch);
		}
		if (factoryClass == null) return null;
		factoryClass = ClassUtils.getUserClass(factoryClass);
		// If all factory methods have the same return type, return that type.
		// Can't clearly figure out exact method due to type converting / autowiring!
		Class<?> commonType = null;
		Method uniqueCandidate = null;
		//获取xml配置时指定的构造参数
		int minNrOfArgs = (mbd.hasConstructorArgumentValues() ? mbd.getConstructorArgumentValues().getArgumentCount() : 0);
		//获取当前工厂类的所有具体方法（当前类->父类->接口的default方法）
		//如果发生方法覆盖，那么取返回类型最具体的那个一个
		//在java中覆盖的方法的返回类型不能比被覆盖方法的返回类型更宽松
		//也就是这个覆盖方法的返回类型要么与父类的被覆盖返回值类型一样
		//要么是其返回值的子类
		Method[] candidates = factoryMethodCandidateCache.computeIfAbsent(	factoryClass, ReflectionUtils::getUniqueDeclaredMethods);

		for (Method candidate : candidates) {
			//找到符合当前bean指定的工厂方法的方法
			if (Modifier.isStatic(candidate.getModifiers()) == isStatic && mbd.isFactoryMethod(candidate) && candidate.getParameterCount() >= minNrOfArgs) {
				//这里可能有人会问为啥是factoryMethod.getParameterTypes().length >= minNrOfArgs
				//因为我们配置方法参数可能没有全部指定，特别是发生注解注入与xml相结合的，哈哈，这就是需求，习惯就好
				// Declared type variables to inspect?
				if (candidate.getTypeParameters().length > 0) {
					try {
						// Fully resolve parameter names and argument values.
						//获取方法的参数类型
						Class<?>[] paramTypes = candidate.getParameterTypes();
						String[] paramNames = null;
						//通过asm解析字节码获取参数名称
						ParameterNameDiscoverer pnd = getParameterNameDiscoverer();
						if (pnd != null) {
							paramNames = pnd.getParameterNames(candidate);
						}
						ConstructorArgumentValues cav = mbd.getConstructorArgumentValues();
						Set<ConstructorArgumentValues.ValueHolder> usedValueHolders = new HashSet<>(paramTypes.length);
						Object[] args = new Object[paramTypes.length];
						for (int i = 0; i < args.length; i++) {
							//获取符合条件的参数，首先根据下标，类型，名字匹配
							//如果没有匹配到，那么到通用参数集合中获取
							//根据类型，名称匹配
							ConstructorArgumentValues.ValueHolder valueHolder = cav.getArgumentValue(i, paramTypes[i], (paramNames != null ? paramNames[i] : null), usedValueHolders);
							if (valueHolder == null) {
								//通过参数值进行类型匹配
								valueHolder = cav.getGenericArgumentValue(null, null, usedValueHolders);
							}
							//如果匹配成功，设置对应的参数值，没有匹配到值就是默认的null
							if (valueHolder != null) {
								args[i] = valueHolder.getValue();
								//加入到已被使用集合中，下次不再被重复使用
								usedValueHolders.add(valueHolder);
							}
						}
						//解析返回值，如果存在泛型，那么会通过参数来解析返回类型
						//比如 public <T> T test(T arg1);
						//通过参数arg1参数判断返回的值类型，没有的直接使用反射method.getReturnType()
						//（*1*）
						Class<?> returnType = AutowireUtils.resolveReturnTypeForFactoryMethod(candidate, args, getBeanClassLoader());
						uniqueCandidate = (commonType == null && returnType == candidate.getReturnType() ? candidate : null);
						commonType = ClassUtils.determineCommonAncestor(returnType, commonType);
						if (commonType == null) {
							// Ambiguous return types found: return null to indicate "not determinable".
							return null;
						}
					}catch (Throwable ex) {
						if (logger.isDebugEnabled()) logger.debug("Failed to resolve generic return type for factory method: " + ex);
					}
				}else {
					uniqueCandidate = (commonType == null ? candidate : null);
					//如果方法参数为空的，走这，这个方法的逻辑很简单就是将当前方法的返回值和上一个方法的返回值进行isAssignableFrom，通俗点就是找爸爸，如果不是父子关系，那么向上追踪
					//找共同的父级
					commonType = ClassUtils.determineCommonAncestor(candidate.getReturnType(), commonType);
					if (commonType == null) {
						// Ambiguous return types found: return null to indicate "not determinable".
						return null;
					}
				}
			}
		}
		mbd.factoryMethodToIntrospect = uniqueCandidate;
		if (commonType == null) return null;
		// Common return type found: all factory methods return same type. For a non-parameterized
		// unique candidate, cache the full type declaration context of the target factory method.
		cachedReturnType = (uniqueCandidate != null ? ResolvableType.forMethodReturnType(uniqueCandidate) : ResolvableType.forClass(commonType));
		mbd.factoryMethodReturnType = cachedReturnType;
		return cachedReturnType.resolve();
	}

	/**
	 * This implementation attempts to query the FactoryBean's generic parameter metadata if present to determine the object type.
	 * If not present, i.e. the FactoryBean is declared as a raw type,
	 * checks the FactoryBean's {@code getObjectType} method on a plain instance of the FactoryBean, without bean properties applied yet.
	 * If this doesn't return a type yet, a full creation of the FactoryBean is used as fallback (through delegation to the superclass's implementation).
	 * The shortcut check for a FactoryBean is only applied in case of a singleton
	 * FactoryBean. If the FactoryBean instance itself is not kept as singleton,it will be fully created to check the type of its exposed object.
	 *
	 */
	@Override
	@Nullable
	protected Class<?> getTypeForFactoryBean(String beanName, RootBeanDefinition mbd) {
		if (mbd.getInstanceSupplier() != null) {
			ResolvableType targetType = mbd.targetType;
			if (targetType != null) {
				Class<?> result = targetType.as(FactoryBean.class).getGeneric().resolve();
				if (result != null) return result;
			}
			if (mbd.hasBeanClass()) {
				Class<?> result = GenericTypeResolver.resolveTypeArgument(mbd.getBeanClass(), FactoryBean.class);
				if (result != null) return result;
			}
		}
		String factoryBeanName = mbd.getFactoryBeanName();
		String factoryMethodName = mbd.getFactoryMethodName();
		if (factoryBeanName != null) {
			if (factoryMethodName != null) {
				// Try to obtain the FactoryBean's object type from its factory method declaration without instantiating the containing bean at all.
				BeanDefinition fbDef = getBeanDefinition(factoryBeanName);
				if (fbDef instanceof AbstractBeanDefinition) {
					AbstractBeanDefinition afbDef = (AbstractBeanDefinition) fbDef;
					if (afbDef.hasBeanClass()) {
						Class<?> result = getTypeForFactoryBeanFromMethod(afbDef.getBeanClass(), factoryMethodName);
						if (result != null) return result;
					}
				}
			}
			// If not resolvable above and the referenced factory bean doesn't exist yet,
			// exit here - we don't want to force the creation of another bean just to obtain a FactoryBean's object type...
			if (!isBeanEligibleForMetadataCaching(factoryBeanName)) {
				return null;
			}
		}
		// Let's obtain a shortcut instance for an early getObjectType() call...
		FactoryBean<?> fb = (mbd.isSingleton() ? getSingletonFactoryBeanForTypeCheck(beanName, mbd) : getNonSingletonFactoryBeanForTypeCheck(beanName, mbd));
		if (fb != null) {
			// Try to obtain the FactoryBean's object type from this early stage of the instance.
			Class<?> result = getTypeForFactoryBean(fb);
			if (result != null) {
				return result;
			}else {
				// No type found for shortcut FactoryBean instance:fall back to full creation of the FactoryBean instance.
				return super.getTypeForFactoryBean(beanName, mbd);
			}
		}
		if (factoryBeanName == null && mbd.hasBeanClass()) {
			// No early bean instantiation possible: determine FactoryBean's type from static factory method signature or from class inheritance hierarchy...
			if (factoryMethodName != null) {
				return getTypeForFactoryBeanFromMethod(mbd.getBeanClass(), factoryMethodName);
			}else {
				return GenericTypeResolver.resolveTypeArgument(mbd.getBeanClass(), FactoryBean.class);
			}
		}
		return null;
	}

	/**
	 * Introspect the factory method signatures on the given bean class, trying to find a common {@code FactoryBean} object type declared there.
	 * @param beanClass the bean class to find the factory method on
	 * @param factoryMethodName the name of the factory method
	 * @return the common {@code FactoryBean} object type, or {@code null} if none
	 */
	@Nullable
	private Class<?> getTypeForFactoryBeanFromMethod(Class<?> beanClass, final String factoryMethodName) {
		// Holder used to keep a reference to a {@code Class} value.
		class Holder {
			@Nullable
			Class<?> value = null;
		}
		final Holder objectType = new Holder();
		// CGLIB subclass methods hide generic parameters; look at the original user class.
		Class<?> fbClass = ClassUtils.getUserClass(beanClass);
		// Find the given factory method, taking into account that in the case of @Bean methods, there may be parameters present.
		ReflectionUtils.doWithMethods(fbClass, method -> {
			if (method.getName().equals(factoryMethodName) && FactoryBean.class.isAssignableFrom(method.getReturnType())) {
				Class<?> currentType = GenericTypeResolver.resolveReturnTypeArgument(method, FactoryBean.class);
				if (currentType != null) {
					objectType.value = ClassUtils.determineCommonAncestor(currentType, objectType.value);
				}
			}
		});
		return (objectType.value != null && Object.class != objectType.value ? objectType.value : null);
	}

	/**
	 * Obtain a reference for early access to the specified bean,typically for the purpose of resolving a circular reference.
	 * @param beanName the name of the bean (for error handling purposes)
	 * @param mbd the merged bean definition for the bean
	 * @param bean the raw bean instance
	 * @return the object to expose as bean reference
	 * @see DefaultSingletonBeanRegistry#getSingleton(java.lang.String, boolean) 中的singletonFactory.getObject() 为调用入口
	 * 获取早期 bean 的引用，如果 bean 中的方法被 AOP 切点所匹配到，此时 AOP 相关逻辑会介入
	 */
	protected Object getEarlyBeanReference(String beanName, RootBeanDefinition mbd, Object bean) {
		Object exposedObject = bean; // 这里是回调  来自 addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean));
		if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
			for (BeanPostProcessor bp : getBeanPostProcessors()) {
				if (bp instanceof SmartInstantiationAwareBeanPostProcessor) {
					SmartInstantiationAwareBeanPostProcessor ibp = (SmartInstantiationAwareBeanPostProcessor) bp;
					// 实现类是 AbstractAutoProxyCreator  就会去为这个对象创建的代理
					// 实现类是 InstantiationAwareBeanPostProcessorAdapter  就什么都不做原样返回
					exposedObject = ibp.getEarlyBeanReference(exposedObject, beanName);
				}
			}
		}
		return exposedObject;
	}

	//---------------------------------------------------------------------
	// Implementation methods
	//---------------------------------------------------------------------
	/**
	 * Obtain a "shortcut" singleton FactoryBean instance to use for a {@code getObjectType()} call, without full initialization of the FactoryBean.
	 * @param beanName the name of the bean
	 * @param mbd the bean definition for the bean
	 * @return the FactoryBean instance, or {@code null} to indicate  that we couldn't obtain a shortcut FactoryBean instance
	 */
	@Nullable
	private FactoryBean<?> getSingletonFactoryBeanForTypeCheck(String beanName, RootBeanDefinition mbd) {
		synchronized (getSingletonMutex()) {
			BeanWrapper bw = factoryBeanInstanceCache.get(beanName);
			if (bw != null) {
				return (FactoryBean<?>) bw.getWrappedInstance();
			}
			Object beanInstance = getSingleton(beanName, false);
			if (beanInstance instanceof FactoryBean) {
				return (FactoryBean<?>) beanInstance;
			}
			if (isSingletonCurrentlyInCreation(beanName) || (mbd.getFactoryBeanName() != null && isSingletonCurrentlyInCreation(mbd.getFactoryBeanName()))) {
				return null;
			}
			Object instance;
			try {
				// Mark this bean as currently in creation, even if just partially.
				beforeSingletonCreation(beanName);
				// Give BeanPostProcessors a chance to return a proxy instead of the target bean instance.
				instance = resolveBeforeInstantiation(beanName, mbd);
				if (instance == null) {
					bw = createBeanInstance(beanName, mbd, null);
					instance = bw.getWrappedInstance();
				}
			}catch (UnsatisfiedDependencyException ex) {
				// Don't swallow, probably misconfiguration...
				throw ex;
			}catch (BeanCreationException ex) {
				// Instantiation failure, maybe too early...
				if (logger.isDebugEnabled()) logger.debug("Bean creation exception on singleton FactoryBean type check: " + ex);
				onSuppressedException(ex);
				return null;
			}finally {
				// Finished partial creation of this bean.
				afterSingletonCreation(beanName);
			}
			FactoryBean<?> fb = getFactoryBean(beanName, instance);
			if (bw != null) {
				factoryBeanInstanceCache.put(beanName, bw);
			}
			return fb;
		}
	}

	/**
	 * Obtain a "shortcut" non-singleton FactoryBean instance to use for a  {@code getObjectType()} call, without full initialization of the FactoryBean.
	 * @param beanName the name of the bean
	 * @param mbd the bean definition for the bean
	 * @return the FactoryBean instance, or {@code null} to indicate that we couldn't obtain a shortcut FactoryBean instance
	 */
	@Nullable
	private FactoryBean<?> getNonSingletonFactoryBeanForTypeCheck(String beanName, RootBeanDefinition mbd) {
		if (isPrototypeCurrentlyInCreation(beanName)) return null;
		Object instance;
		try {
			// Mark this bean as currently in creation, even if just partially.
			beforePrototypeCreation(beanName);
			// Give BeanPostProcessors a chance to return a proxy instead of the target bean instance.
			instance = resolveBeforeInstantiation(beanName, mbd);
			if (instance == null) {
				BeanWrapper bw = createBeanInstance(beanName, mbd, null);
				instance = bw.getWrappedInstance();
			}
		}catch (UnsatisfiedDependencyException ex) {
			// Don't swallow, probably misconfiguration...
			throw ex;
		}catch (BeanCreationException ex) {
			// Instantiation failure, maybe too early...
			if (logger.isDebugEnabled()) logger.debug("Bean creation exception on non-singleton FactoryBean type check: " + ex);
			onSuppressedException(ex);
			return null;
		}finally {
			// Finished partial creation of this bean.
			afterPrototypeCreation(beanName);
		}
		return getFactoryBean(beanName, instance);
	}

	/**
	 * Apply MergedBeanDefinitionPostProcessors to the specified bean definition, invoking their {@code postProcessMergedBeanDefinition} methods.
	 * @param mbd the merged bean definition for the bean
	 * @param beanType the actual type of the managed bean instance
	 * @param beanName the name of the bean
	 * @see MergedBeanDefinitionPostProcessor#postProcessMergedBeanDefinition
	 */
	protected void applyMergedBeanDefinitionPostProcessors(RootBeanDefinition mbd, Class<?> beanType, String beanName) {
		for (BeanPostProcessor bp : getBeanPostProcessors()) {
			if (bp instanceof MergedBeanDefinitionPostProcessor) {
				MergedBeanDefinitionPostProcessor bdp = (MergedBeanDefinitionPostProcessor) bp;
				bdp.postProcessMergedBeanDefinition(mbd, beanType, beanName);
			}
		}
	}

	/**
	 * 有则调用的是InstantiationAwareBeanPostProcessor的postProcessBeforeInstantiation()实例化前置处理方法，
	 * 也就是在Bean没有生成之前执行。（注意：这里所说的是Bean未生成指的是Bean没有走spring定义创建Bean的流程，也就是doCreateBean()方法。）
	 * 如果postProcessBeforeInstantiation()返回的对象不为空, 那么对象的生成阶段直接完成了
	 * 会接着调用postProcessAfterInitialization() 处理这个对象. 如果为空则走流水线doCreateBean()创建对象, 对象初始化.
	 * Apply before-instantiation post-processors, resolving whether there is a before-instantiation shortcut for the specified bean.
	 * @param beanName the name of the bean
	 * @param mbd the bean definition for the bean
	 * @return the shortcut-determined bean instance, or {@code null} if none
	 */
	@Nullable
	protected Object resolveBeforeInstantiation(String beanName, RootBeanDefinition mbd) {
		Object bean = null;
		// 检测是否解析过，mbd.beforeInstantiationResolved 的值在下面的代码中会被设置
		if (!Boolean.FALSE.equals(mbd.beforeInstantiationResolved)) {
			// Make sure bean class is actually resolved at this point.
			if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
				Class<?> targetType = determineTargetType(beanName, mbd);
				if (targetType != null) {
					// 应用前置处理  // 调用before方法实现用户自定义的bean实例化方法
					bean = applyBeanPostProcessorsBeforeInstantiation(targetType, beanName);
					if (bean != null) {
						// 应用后置处理 // 调用after方法实现用户自定义的依赖注入和自动装配方法
						// 直接执行自定义初始化完成后的方法,跳过了其他几个方法
						bean = applyBeanPostProcessorsAfterInitialization(bean, beanName);
					}
				}
			}
			// 设置 mbd.beforeInstantiationResolved
			mbd.beforeInstantiationResolved = (bean != null);
		}
		return bean;
	}

	/**
	 * Apply InstantiationAwareBeanPostProcessors to the specified bean definition
	 * (by class and name), invoking their {@code postProcessBeforeInstantiation} methods.
	 * Any returned object will be used as the bean instead of actually instantiating
	 * the target bean. A {@code null} return value from the post-processor will result in the target bean being instantiated.
	 * @param beanClass the class of the bean to be instantiated
	 * @param beanName the name of the bean
	 * @return the bean object to use instead of a default instance of the target bean, or {@code null}
	 * @see InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation
	 */
	@Nullable
	protected Object applyBeanPostProcessorsBeforeInstantiation(Class<?> beanClass, String beanName) {
		for (BeanPostProcessor bp : getBeanPostProcessors()) {
			// InstantiationAwareBeanPostProcessor 一般在 Spring 框架内部使用，不建议用户直接使用
			if (bp instanceof InstantiationAwareBeanPostProcessor) {
				InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor) bp;
				// bean 初始化前置处理 // 只要有一个result不为null；后面的所有 后置处理器的方法就不执行了，直接返回(所以执行顺序很重要)
				Object result = ibp.postProcessBeforeInstantiation(beanClass, beanName);
				if (result != null) return result;
			}
		}
		return null;
	}

	/**
	 * Create a new instance for the specified bean, using an appropriate instantiation strategy:factory method, constructor autowiring, or simple instantiation.
	 * @param beanName the name of the bean
	 * @param mbd the bean definition for the bean
	 * @param args explicit arguments to use for constructor or factory method invocation
	 * @return a BeanWrapper for the new instance
	 * @see #obtainFromSupplier
	 * @see #instantiateUsingFactoryMethod
	 * @see #autowireConstructor
	 * @see #instantiateBean
	 */
	protected BeanWrapper createBeanInstance(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) {
		// Make sure bean class is actually resolved at this point.
		Class<?> beanClass = resolveBeanClass(mbd, beanName);
		// 如果不为null，并且还不是public的访问权限 并且nonPublicAccessAllowed为false 那就抛异常吧，题外话：nonPublicAccessAllowed为true的情况下（默认值），即使你不是public的也ok
		// 检测类的访问权限。默认情况下，对于非 public 的类，是允许访问的。若禁止访问，这里会抛出异常
		if (beanClass != null && !Modifier.isPublic(beanClass.getModifiers()) && !mbd.isNonPublicAccessAllowed()) {
			throw new BeanCreationException(mbd.getResourceDescription(), beanName,"Bean class isn't public, and non-public access not allowed: " + beanClass.getName());
		}
		// 你可以自己通过Supplier来创建Bean，最终交给obtainFromSupplier包装成BeanWrapper
		Supplier<?> instanceSupplier = mbd.getInstanceSupplier();
		// 如果工厂方法不为空，则通过工厂方法构建 bean 对象。这种构建 bean 的方式 就不深入分析了，有兴趣的朋友可以自己去看一下。
		if (instanceSupplier != null) {
			// 通过“工厂方法”的方式构建 bean 对象
			return obtainFromSupplier(instanceSupplier, beanName);
		}
		// 通过工厂方法创建Bean ， 另外@Bean注解的方法也走这里
		// 旧版本的Spring可以通过xml配置 <bean id='A' class='xxx' factory-method='getA'>从而获得静态的工厂方法获得bean A
		if (mbd.getFactoryMethodName() != null) {
			return instantiateUsingFactoryMethod(beanName, mbd, args);
		}
		/*
		 * 当多次构建同一个 bean 时，可以使用此处的快捷路径，即无需再次推断应该使用哪种方式构造实例，以提高效率。
		 * 比如在多次构建同一个 prototype 类型的 bean 时，就可以走此处的捷径。
		 * 这里的 resolved 和 mbd.constructorArgumentsResolved 将会在 bean 第一次实例化的过程中被设置，在后面的源码中会分析到，先继续往下看。
		 */
		// Shortcut when re-creating the same bean...
		// 一个类可能有多个构造器，所以Spring得根据参数个数、类型确定需要调用的构造器
		// 在使用构造器创建实例后，Spring会将解析过后确定下来的构造器或工厂方法保存在缓存中，避免再次创建相同bean时再次解析
		// 如果之前已经解析过，就不用再去解析一遍了。RootBeanDefinition.resolvedConstructorOrFactoryMethod专门用来缓存构造函数，constructorArgumentsResolved用来存放构造是有参还是无参。
		// 有参走autowireConstructor，无参走instantiateBean
		boolean resolved = false;
		boolean autowireNecessary = false;
		if (args == null) {
			synchronized (mbd.constructorArgumentLock) {
				if (mbd.resolvedConstructorOrFactoryMethod != null) {
					resolved = true;
					autowireNecessary = mbd.constructorArgumentsResolved;
				}
			}
		}
		// 首次进入，resolved为false。说一下：ConstructorResolver，就是找到合适的构造器给你去实例化一个Bean（会结合Spring容器进行一起解析）
		if (resolved) {
			if (autowireNecessary) {
				// 通过“构造方法自动注入”的方式构造 bean 对象
				return autowireConstructor(beanName, mbd, null, null);
			}else {
				// 通过“默认构造方法”的方式构造 bean 对象
				return instantiateBean(beanName, mbd);
			}
		}
		// Candidate constructors for autowiring?    Need to determine the constructor...
		// 执行BeanPostProcesso#determineCandidateConstructors 自己去决定使用哪个构造器，可能会返回一批构造器哟
		// 这里需要注意了，因为我们的Child的属性HelloService里并没有书写@Autowired属性，所以这里最终的返回结果是null
		// 需要注意的是：若我们自己写了一个构造    public Child(HelloService helloService) {  this.helloService = helloService; } 那么它就会拿到，然后走下面让Spring执行构造器的注入的
		// 旁白：如果你只有空构造，那就直接instantiateBean，否则会自动去走Spring的构造器注入的逻辑
		// 检查所有的SmartInstantiationAwareBeanPostProcessor，为指定bean确定一个构造函数。
		// 实际上就是org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor#determineCandidateConstructors来负责。
		// 如果找到了构造函数，或者该bean是通过“AUTOWIRE_CONSTRUCTOR”按构造器自动装配，或者如果此bean定义了构造函数参数值，或者传入的构造参数不为空。
		Constructor<?>[] ctors = determineConstructorsFromBeanPostProcessors(beanClass, beanName);
		/*
		 * 下面的条件分支条件用于判断使用什么方式构造 bean 实例，
		 * 有两种方式可选 - 构造方法自动注入和默认构造方法。
		 * 判断的条件由4部分综合而成，如下：
		 *    条件1：ctors != null -> 后置处理器返回构造方法数组是否为空
		 *    条件2：mbd.getResolvedAutowireMode() == RootBeanDefinition.AUTOWIRE_CONSTRUCTOR -> bean 配置中的 autowire 属性是否为 constructor
		 *    条件3：mbd.hasConstructorArgumentValues()  -> constructorArgumentValues 是否存在元素，即 bean 配置文件中是否配置了 <construct-arg/>
		 *    条件4：!ObjectUtils.isEmpty(args)-> args 数组是否存在元素，args 是由用户调用getBean(String name, Object... args) 传入的
		 * 上面4个条件，只要有一个为 true，就会通过构造方法自动注入的方式构造 bean 实例
		 */
		if (ctors != null || mbd.getResolvedAutowireMode() == AUTOWIRE_CONSTRUCTOR || mbd.hasConstructorArgumentValues() || !ObjectUtils.isEmpty(args)) {
			// 通过“构造方法自动注入”的方式构造 bean 对象 // 走有参构造函数
			return autowireConstructor(beanName, mbd, ctors, args);
		}
		// Preferred constructors for default construction? 默认构造的首选构造函数？ // 确定用于默认构造的首选构造函数(如果有)。如果需要，构造函数参数将自动连接。
		ctors = mbd.getPreferredConstructors();
		if (ctors != null) {
			return autowireConstructor(beanName, mbd, ctors, null);
		}
		// No special handling: simply use no-arg constructor. 无特殊处理：只需使用no arg构造函数
		// 通过“默认构造方法”的方式实例化bean 对象
		return instantiateBean(beanName, mbd);
	}

	/**
	 * Obtain a bean instance from the given supplier.
	 * @param instanceSupplier the configured supplier
	 * @param beanName the corresponding bean name
	 * @return a BeanWrapper for the new instance
	 * @since 5.0
	 * @see #getObjectForBeanInstance
	 */
	protected BeanWrapper obtainFromSupplier(Supplier<?> instanceSupplier, String beanName) {
		Object instance;
		String outerBean = currentlyCreatedBean.get();
		currentlyCreatedBean.set(beanName);
		try {
			instance = instanceSupplier.get();
		}finally {
			if (outerBean != null) {
				currentlyCreatedBean.set(outerBean);
			}else {
				currentlyCreatedBean.remove();
			}
		}
		if (instance == null) instance = new NullBean();
		BeanWrapper bw = new BeanWrapperImpl(instance);
		initBeanWrapper(bw);
		return bw;
	}

	/**
	 * Overridden in order to implicitly register the currently created bean as dependent on further beans getting programmatically retrieved during a {@link Supplier} callback.
	 * @since 5.0
	 * @see #obtainFromSupplier
	 */
	@Override
	protected Object getObjectForBeanInstance(Object beanInstance, String name, String beanName, @Nullable RootBeanDefinition mbd) {
		String currentlyCreatedBean = this.currentlyCreatedBean.get();
		if (currentlyCreatedBean != null) {
			registerDependentBean(beanName, currentlyCreatedBean);
		}
		return super.getObjectForBeanInstance(beanInstance, name, beanName, mbd);
	}

	/**
	 * Determine candidate constructors to use for the given bean, checking all registered {@link SmartInstantiationAwareBeanPostProcessor SmartInstantiationAwareBeanPostProcessors}.
	 * @param beanClass the raw class of the bean
	 * @param beanName the name of the bean
	 * @return the candidate constructors, or {@code null} if none specified
	 * @throws org.springframework.beans.BeansException in case of errors
	 * @see org.springframework.beans.factory.config.SmartInstantiationAwareBeanPostProcessor#determineCandidateConstructors
	 */
	@Nullable
	protected Constructor<?>[] determineConstructorsFromBeanPostProcessors(@Nullable Class<?> beanClass, String beanName) throws BeansException {
		if (beanClass != null && hasInstantiationAwareBeanPostProcessors()) {
			for (BeanPostProcessor bp : getBeanPostProcessors()) {
				if (bp instanceof SmartInstantiationAwareBeanPostProcessor) {
					SmartInstantiationAwareBeanPostProcessor ibp = (SmartInstantiationAwareBeanPostProcessor) bp;
					Constructor<?>[] ctors = ibp.determineCandidateConstructors(beanClass, beanName);
					if (ctors != null) {
						return ctors;
					}
				}
			}
		}
		return null;
	}

	/**
	 * Instantiate the given bean using its default constructor.
	 * @param beanName the name of the bean
	 * @param mbd the bean definition for the bean
	 * @return a BeanWrapper for the new instance
	 */
	protected BeanWrapper instantiateBean(final String beanName, final RootBeanDefinition mbd) {
		try {
			Object beanInstance;
			final BeanFactory parent = this;
			// if 条件分支里的一大坨是 Java 安全相关的代码，可以忽略，直接看 else 分支
			if (System.getSecurityManager() != null) {
				beanInstance = AccessController.doPrivileged((PrivilegedAction<Object>) () -> getInstantiationStrategy().instantiate(mbd, beanName, parent),getAccessControlContext());
			}else {
				//  调用实例化策略创建实例，默认情况下使用反射创建对象。如果 bean 的配置信息中 包含 lookup-method 和 replace-method，则通过 CGLIB 创建 bean 对象
				beanInstance = getInstantiationStrategy().instantiate(mbd, beanName, parent);
			}
			// 创建 BeanWrapperImpl 对象
			BeanWrapper bw = new BeanWrapperImpl(beanInstance);
			initBeanWrapper(bw);
			return bw;
		}catch (Throwable ex) {
			throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Instantiation of bean failed", ex);
		}
	}

	/**
	 * Instantiate the bean using a named factory method. The method may be static, if the mbd parameter specifies a class, rather than a factoryBean,
	 * or an instance variable on a factory object itself configured using Dependency Injection.
	 * @param beanName the name of the bean
	 * @param mbd the bean definition for the bean
	 * @param explicitArgs argument values passed in programmatically via the getBean method,or {@code null} if none (-> use constructor argument values from bean definition)
	 * @return a BeanWrapper for the new instance
	 * @see #getBean(String, Object[])
	 */
	protected BeanWrapper instantiateUsingFactoryMethod(String beanName, RootBeanDefinition mbd, @Nullable Object[] explicitArgs) {
		return new ConstructorResolver(this).instantiateUsingFactoryMethod(beanName, mbd, explicitArgs);
	}

	/**
	 * "autowire constructor" (with constructor arguments by type) behavior.
	 * Also applied if explicit constructor argument values are specified,matching all remaining arguments with beans from the bean factory.
	 * This corresponds to constructor injection: In this mode, a Spring bean factory is able to host components that expect constructor-based dependency resolution.
	 * @param beanName the name of the bean
	 * @param mbd the bean definition for the bean
	 * @param ctors the chosen candidate constructors
	 * @param explicitArgs argument values passed in programmatically via the getBean method,or {@code null} if none (-> use constructor argument values from bean definition)
	 * @return a BeanWrapper for the new instance
	 */
	protected BeanWrapper autowireConstructor(String beanName, RootBeanDefinition mbd, @Nullable Constructor<?>[] ctors, @Nullable Object[] explicitArgs) {
		return new ConstructorResolver(this).autowireConstructor(beanName, mbd, ctors, explicitArgs);
	}

	/**
	 * Populate the bean instance in the given BeanWrapper with the property values from the bean definition.
	 * @param beanName the name of the bean
	 * @param mbd the bean definition for the bean
	 * @param bw the BeanWrapper with bean instance
	 * 用来自 BeanDefinition 的属性值填充给定的BeanWrapper中的bean实例
	 */
	@SuppressWarnings("deprecation")  // for postProcessPropertyValues
	protected void populateBean(String beanName, RootBeanDefinition mbd, @Nullable BeanWrapper bw) {
		logger.warn("【IOC容器 进入  populateBean 方法  开始填充bean属性 --- 】 beanName： " + beanName);
		if (bw == null) {
			//如果bw为空，且mdb有需要设置的属性，则抛出异常
			if (mbd.hasPropertyValues()) {
				throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Cannot apply property values to null instance");
			}else {
				return;// Skip property population phase for null instance. // 跳过 null 实例的属性填充阶段
			}
		}
		// Give any InstantiationAwareBeanPostProcessors the opportunity to modify the state of the bean before properties are set. This can be used, for example, to support styles of field injection.
		// 给任何 InstantiationAwareBeanPostProcessors 会在属性设置之前修改bean的状态，这可以支持字段注入的样式
		//继续对bean对象属性赋值的标记
		boolean continueWithPropertyPopulation = true;
		/*
		 * 在属性被填充前，给 InstantiationAwareBeanPostProcessor 类型的后置处理器一个修改bean 状态的机会。
		 * 关于这段后置引用，官方的解释是：让用户可以自定义属性注入。
		 * 比如用户实现一个 InstantiationAwareBeanPostProcessor 类型的后置处理器，并通过 postProcessAfterInstantiation 方法向 bean 的成员变量注入自定义的信息。
		 * 当然，如果无特殊需求，直接使用配置中的信息注入即可。
		 * 另外，Spring 并不建议大家直接实现InstantiationAwareBeanPostProcessor 接口，如果想实现这种类型的后置处理器，更建议通过继承 InstantiationAwareBeanPostProcessorAdapter 抽象类实现自定义后置处理器。
		 */
		// 因为已经实例化了，对象已经创建了，所以这里立马执行了InstantiationAwareBeanPostProcessor#postProcessAfterInstantiation方法
		// 但凡只有其中一个返回了false，相当于告诉容器我都处理好了，那么后面的赋值操作就Spring容器就不再处理了
		//否是"synthetic"。一般是指只有AOP相关的prointCut配置或者Advice配置才会将 synthetic设置为true
		//如果mdb是不是'syntheic' 且 工厂拥有InstiationAwareBeanPostProcessor
		if (!mbd.isSynthetic() && hasInstantiationAwareBeanPostProcessors()) {
			//遍历工厂中的BeanPostProcessor对象
			for (BeanPostProcessor bp : getBeanPostProcessors()) {
				//如果 bp 是 InstantiationAwareBeanPostProcessor 实例
				if (bp instanceof InstantiationAwareBeanPostProcessor) {
					InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor) bp;
					//postProcessAfterInstantiation：一般用于设置属性
					if (!ibp.postProcessAfterInstantiation(bw.getWrappedInstance(), beanName)) {
						//如果postProcessAfterInstantiation返回false，则不再调用任何后续的InstantiationAwareBeanPostProcessor实例， 也不再调用 populateBean 的后续逻辑
						continueWithPropertyPopulation = false;
						break;
					}
				}
			}
		}
		// 如果上面设置 continueWithPropertyPopulation = false，表明用户可能已经自己填充了bean的属性，不需要Spring帮忙填充了。此时直接返回即可
		//如果不需要调用 populateBean 的后续逻辑，就结束改方法
		if (!continueWithPropertyPopulation) return;
		// 准备pvs属性源，最终使用其【给bean填充属性】。
		PropertyValues pvs = mbd.hasPropertyValues() ? mbd.getPropertyValues() : null;
		// Spring扫描后管理的beans，如果你想要给他字段注入属性值，必须使用@Autowired 注解，从而交给后置处理器AutowiredAnnotationBeanPostProcessor#postProcessPropertyValues这个方法去处理
		// 2. 自动属性注入。根据自动装配模式 分为按 beanName 或 byType 查找可注入的属性值(依赖)。
		if (mbd.getResolvedAutowireMode() == AUTOWIRE_BY_NAME || mbd.getResolvedAutowireMode() == AUTOWIRE_BY_TYPE) {
			MutablePropertyValues newPvs = new MutablePropertyValues(pvs);
			// Add property values based on autowire by name if applicable.
			// 通过属性名称自动注入
			if (mbd.getResolvedAutowireMode() == AUTOWIRE_BY_NAME) {
				logger.warn("【IOC容器 使用 autowireMode 类型为 ByName 进行属性注入  --- 】 beanName： " + beanName);
				// 通过bw中的PropertyDescriptor属性名，查找出对应的Bean对象，将其添加到newPvs中
				autowireByName(beanName, mbd, bw, newPvs);
			}
			// Add property values based on autowire by type if applicable.
			// 通过属性类型注入依赖 // 按类型自动装配。一般都会走这里，通过类型的匹配，来给属性赋值，实现注入
			// 根据自动装配的类型(如果适用)添加属性值
			if (mbd.getResolvedAutowireMode() == AUTOWIRE_BY_TYPE) {
				logger.warn("【IOC容器 使用 autowireMode 类型为 ByType 进行属性注入  --- 】 beanName： " + beanName);
				//通过bw的PropertyDescriptor属性类型，查找出对应的Bean对象，将其添加到newPvs中
				autowireByType(beanName, mbd, bw, newPvs);
			}
			// 结合注入后的配置，覆盖当前配置
			//让pvs重新引用newPvs,newPvs此时已经包含了pvs的属性值以及通过AUTOWIRE_BY_NAME，AUTOWIRE_BY_TYPE自动装配所得到的属性值
			pvs = newPvs;
		}
		// 下面的代码是判断是否需要执行postProcessPropertyValues；改变bean的属性
		//工厂是否拥有InstiationAwareBeanPostProcessor
		boolean hasInstAwareBpps = hasInstantiationAwareBeanPostProcessors();
		// 是否进行依赖检查
		//mbd.getDependencyCheck()，默认返回 DEPENDENCY_CHECK_NONE，表示 不检查
		//是否需要依赖检查
		boolean needsDepCheck = (mbd.getDependencyCheck() != AbstractBeanDefinition.DEPENDENCY_CHECK_NONE);
		//经过筛选的PropertyDesciptor数组,存放着排除忽略的依赖项或忽略项上的定义的属性
		PropertyDescriptor[] filteredPds = null;
		// 这里又是一种后置处理，用于在 Spring 填充属性到 bean 对象前，对属性的值进行相应的处理，比如可以修改某些属性的值。这时注入到 bean 中的值就不是配置文件中的内容了，而是经过后置处理器修改后的内容
		//如果工厂拥有InstiationAwareBeanPostProcessor
		if (hasInstAwareBpps) {
			//尝试获取mbd的PropertyValues
			if (pvs == null) pvs = mbd.getPropertyValues();
			// 这里主要施工的又是我们大名鼎鼎的 AutowiredAnnotationBeanPostProcessor#postProcessPropertyValues 它了，他会根据所有标注有@Autpwired注解地方，执行注入（非常重要）
			//遍历工厂内的所有后置处理器
			for (BeanPostProcessor bp : getBeanPostProcessors()) {
				//如果 bp 是 InstantiationAwareBeanPostProcessor 的实例
				if (bp instanceof InstantiationAwareBeanPostProcessor) {
					//将bp 强转成 InstantiationAwareBeanPostProcessor 对象
					InstantiationAwareBeanPostProcessor ibp = (InstantiationAwareBeanPostProcessor) bp;
					// sos 这里解析@Autowire注解  依赖的bean注入在这里实现  AutowiredAnnotationBeanPostProcessor
					//postProcessProperties:在工厂将给定的属性值应用到给定Bean之前，对它们进行后处理，不需要任何属性扫描符。该回调方法在未来的版本会被删掉。
					// -- 取而代之的是 postProcessPropertyValues 回调方法。
					// 让ibp对pvs增加对bw的Bean对象的propertyValue，或编辑pvs的proertyValue
					PropertyValues pvsToUse = ibp.postProcessProperties(pvs, bw.getWrappedInstance(), beanName);
					if (pvsToUse == null) {
						if (filteredPds == null) {
							//mbd.allowCaching:是否允许缓存，默认时允许的。缓存除了可以提高效率以外，还可以保证在并发的情况下，返回的PropertyDesciptor[]永远都是同一份
							//从bw提取一组经过筛选的PropertyDesciptor,排除忽略的依赖项或忽略项上的定义的属性
							filteredPds = filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
						}
						//postProcessPropertyValues:一般进行检查是否所有依赖项都满足，例如基于"Require"注释在 bean属性 setter，
						// 	-- 替换要应用的属性值，通常是通过基于原始的PropertyValues创建一个新的MutablePropertyValue实例， 添加或删除特定的值
						// 	-- 返回的PropertyValues 将应用于bw包装的bean实例 的实际属性值（添加PropertyValues实例到pvs 或者 设置为null以跳过属性填充）
						//回到ipd的postProcessPropertyValues方法
						pvsToUse = ibp.postProcessPropertyValues(pvs, filteredPds, bw.getWrappedInstance(), beanName);
						//如果pvsToUse为null，将终止该方法精致，以跳过属性填充
						if (pvsToUse == null) return;
					}
					//让pvs引用pvsToUse
					pvs = pvsToUse;
				}
			}
		}
		// 4. 依赖校验。是否所有的字段已经全部匹配上了，根据需要是否要抛出异常 //如果需要依赖检查
		if (needsDepCheck) {
			if (filteredPds == null) {
				// 过滤不需要进行属性注入的字段，如 String、BeanFactory...
				//从bw提取一组经过筛选的PropertyDesciptor,排除忽略的依赖项或忽略项上的定义的属性
				filteredPds = filterPropertyDescriptorsForDependencyCheck(bw, mbd.allowCaching);
			}
			//检查依赖项：主要检查pd的setter方法需要赋值时,pvs中有没有满足其pd的需求的属性值可供其赋值
			checkDependencies(beanName, mbd, filteredPds, pvs);
		}
		// 作用：Apply the given property values, resolving any runtime references
		// 5. 依赖注入。至些属性已经全部准备好了，可以进行属性注入。
		if (pvs != null) {
			// 应用属性值到 bean 对象中
			// 这里才是真正的设置到我们的实例对象里面（通过 PropertyValues）；之前postProcessPropertyValues这个还只是单纯的改变 PropertyValues
			// 应用给定的属性值，解决任何在这个bean工厂运行时其他bean的引用。必须使用深拷贝，所以我们 不会永久地修改这个属性
			applyPropertyValues(beanName, mbd, bw, pvs);
		}
	}

	/**
	 * Fill in any missing property values with references to other beans in this factory if autowire is set to "byName".
	 * @param beanName the name of the bean we're wiring up. Useful for debugging messages; not used functionally.  -- 我们要连接的bean的名称。用于调试消息；未使用的功能
	 * @param mbd bean definition to update through autowiring -- 通过自动装配来更新BeanDefinition
	 * @param bw the BeanWrapper from which we can obtain information about the bean  -- 我们可以从中获取关于bean的信息的BeanWrapper
	 * @param pvs the PropertyValues to register wired objects with -- 要向其注册连接对象的 PropertyValues
	 */
	protected void autowireByName(String beanName, AbstractBeanDefinition mbd, BeanWrapper bw, MutablePropertyValues pvs) {
		// 获取bw中有setter方法 && 非简单类型属性 && mbd的PropertyValues中没有该pd的属性名的
		String[] propertyNames = unsatisfiedNonSimpleProperties(mbd, bw);
		for (String propertyName : propertyNames) {
			// 检测是否存在与 propertyName 相关的 bean 或 BeanDefinition。若存在，则调用 BeanFactory.getBean 方法获取 bean 实例
			//如果该bean工厂有propertyName的beanDefinition或外部注册的singleton实例
			if (containsBean(propertyName)) {
				// 从容器中获取相应的 bean 实例，如果没有，则会去创建
				//获取该工厂中propertyName的bean对象
				Object bean = getBean(propertyName);
				// 将解析出的 bean 存入到属性值列表（pvs）中
				//将propertyName,bean添加到pvs中
				pvs.add(propertyName, bean);
				// 这里的注册是为了在销毁的时候用的
				//注册propertyName与beanName的依赖关系
				registerDependentBean(propertyName, beanName);
				// 从 bean名称中添加名称自动装配 'beanName' 通过 property 'propertyName' 到 bean 名为 'propertyName'
				if (logger.isTraceEnabled()) logger.trace("Added autowiring by name from bean name '" + beanName + "' via property '" + propertyName + "' to bean named '" + propertyName + "'");
			}else {
				// 不能通过bean名方式自动装配属性 'propertyName':没有找到匹配的bean
				if (logger.isTraceEnabled()) logger.trace("Not autowiring property '" + propertyName + "' of bean '" + beanName + "' by name: no matching bean found");
			}
		}
	}


	/**
	 * Abstract method defining "autowire by type" (bean properties by type) behavior.
	 * This is like PicoContainer default, in which there must be exactly one bean of the property type in the bean factory.
	 * This makes bean factories simple to configure for small namespaces, but doesn't work as well as standard Spring  behavior for bigger applications.
	 * @param beanName the name of the bean to autowire by type
	 * @param mbd the merged bean definition to update through autowiring
	 * @param bw the BeanWrapper from which we can obtain information about the bean
	 * @param pvs the PropertyValues to register wired objects with
	 *  它的步骤相对简单：显示BeanUtils.getWriteMethodParameter(pd)拿到set方法（所以，这里需要注意，若没有set方法，这里是注入不进去的，这个没@Autowired强大）
	 *  然后去容器里面找对应的依赖，也是resolveDependency方法（最终由DefaultListableBeanFactory去实现的）
	 *  这里需要注意：注入的时候isSimpleProperty不会被注入的（包括基本数据类型、Integer、Long。。。
	 *  甚至还包括Enum、CharSequence(显然就包含了Spring)、Number、Date、URI、URL、Locale、Class等等）
	 *  但是标注@Autowired是能够注入的哦，哪怕是String、Integer等等。标注了@Autowired，没有找到反倒为报错 No qualifying bean of type 'java.lang.String' 。。。注意这些区别
	 */
	protected void autowireByType(String beanName, AbstractBeanDefinition mbd, BeanWrapper bw, MutablePropertyValues pvs) {
		TypeConverter converter = getCustomTypeConverter();
		if (converter == null) converter = bw;
		Set<String> autowiredBeanNames = new LinkedHashSet<>(4);
		// 获取bw中有setter方法 && 非简单类型属性 && mbd的PropertyValues中没有该pd的属性名
		String[] propertyNames = unsatisfiedNonSimpleProperties(mbd, bw);
		for (String propertyName : propertyNames) {
			try {
				PropertyDescriptor pd = bw.getPropertyDescriptor(propertyName);
				// Don't try autowiring by type for type Object: never makes sense,even if it technically is a unsatisfied, non-simple property.
				// 如果属性类型为 Object，则忽略，不做解析
				if (Object.class != pd.getPropertyType()) {
					// 获取 setter 方法（write method）的参数信息，比如参数在参数列表中的 位置，参数类型，以及该参数所归属的方法等信息
					MethodParameter methodParam = BeanUtils.getWriteMethodParameter(pd);
					// Do not allow eager init for type matching in case of a prioritized post-processor.
					boolean eager = !PriorityOrdered.class.isInstance(bw.getWrappedInstance());
					// 创建依赖描述对象
					DependencyDescriptor desc = new AutowireByTypeDependencyDescriptor(methodParam, eager);
					// 核心代码就这一句，用于解析依赖。 解析指定 beanName 的属性所匹配的值，并把解析到的属性名称存储在 autowiredBeanNames 中
					Object autowiredArgument = resolveDependency(desc, beanName, autowiredBeanNames, converter);
					if (autowiredArgument != null) {
						// 将解析出的 bean 存入到属性值列表（pvs）中
						pvs.add(propertyName, autowiredArgument);
					}
					for (String autowiredBeanName : autowiredBeanNames) {
						registerDependentBean(autowiredBeanName, beanName);
						if (logger.isTraceEnabled()) logger.trace("Autowiring by type from bean name '" + beanName + "' via property '" +  propertyName + "' to bean named '" + autowiredBeanName + "'");
					}
					autowiredBeanNames.clear();
				}
			}catch (BeansException ex) {
				throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, propertyName, ex);
			}
		}
	}

	/**
	 * 获取bw中有setter方法 && 非简单类型属性 && mbd的PropertyValues中没有该pd的属性名
	 * Return an array of non-simple bean properties that are unsatisfied.
	 * These are probably unsatisfied references to other beans in the factory.
	 * Does not include simple properties like primitives or Strings.
	 * @param mbd the merged bean definition the bean was created with   创建bean时适用的合并BeanDefinition
	 * @param bw the BeanWrapper the bean was created with
	 * @return an array of bean property names
	 * @see org.springframework.beans.BeanUtils#isSimpleProperty
	 * 返回不满足的非简单bean属性数组。
	 * 这个方法会去推断所有需要注入的属性，由于set方法是spring定义的所谓writeMethod，所以能找到userDao
	 *
	 * 获取非简单类型属性的名称，且该属性未被配置在配置文件中。这里从反面解释一下什么是"非简单类型"
	 * 属性，我们先来看看 Spring 认为的"简单类型"属性有哪些，如下：
	 *   1. CharSequence 接口的实现类，比如 String
	 *   2. Enum
	 *   3. Date
	 *   4. URI/URL
	 *   5. Number 的继承类，比如 Integer/Long
	 *   6. 8种基本类型 和8中包装类型
	 *   7. Locale
	 *   8. 以上所有类型的数组形式，比如 String[]、Date[]、int[] 等等
	 * 除了要求非简单类型的属性外，还要求属性未在配置文件中配置过，也就是 pvs.contains(pd.getName()) = false。
	 *
	 *由此我们可以推断出，能自动注入的属性必须具备以下几点
	 * （1）有对应的set方法
	 * （2）非cglib生成的类的内部属性
	 * （3）非通过spring中aware接口设置的属性，也就是说你可以注入容器内部属性，但是不能实现这个属性的aware接口
	 * （4）非配置文件中手动注入的属性（property标签）
	 * （5）非简单属性
	 */
	protected String[] unsatisfiedNonSimpleProperties(AbstractBeanDefinition mbd, BeanWrapper bw) {
		// TreeSet:TreeSet底层是二叉树，可以对对象元素进行排序，但是自定义类需要实现comparable接口，重写comparaTo()方法。
		Set<String> result = new TreeSet<>();
		// 获取mdbd的所有属性值
		PropertyValues pvs = mbd.getPropertyValues();
		// PropertyDescriptor:表示JavaBean类通过存储器导出一个属性获取bw的所有属性描述对象
		PropertyDescriptor[] pds = bw.getPropertyDescriptors();
		// 遍历属性描述对象
		for (PropertyDescriptor pd : pds) {
			// 如果 pd有写入属性方法 && 该pd不是被排除在依赖项检查之外 && pvs没有该pd的属性名 && pd的属性类型不是"简单值类型"
			// 有可写方法、依赖检测中没有被忽略、不是简单属性类型
			if (pd.getWriteMethod() != null
					&& !isExcludedFromDependencyCheck(pd)
					&& !pvs.contains(pd.getName())
					&& !BeanUtils.isSimpleProperty(pd.getPropertyType())) {
				// 将pdd的属性名添加到result中
				result.add(pd.getName());
			}
		}
		return StringUtils.toStringArray(result);
	}

	/**
	 * Extract a filtered set of PropertyDescriptors from the given BeanWrapper, excluding ignored dependency types or properties defined on ignored dependency interfaces.
	 * @param bw the BeanWrapper the bean was created with
	 * @param cache whether to cache filtered PropertyDescriptors for the given bean Class
	 * @return the filtered PropertyDescriptors
	 * @see #isExcludedFromDependencyCheck
	 * @see #filterPropertyDescriptorsForDependencyCheck(org.springframework.beans.BeanWrapper)
	 */
	protected PropertyDescriptor[] filterPropertyDescriptorsForDependencyCheck(BeanWrapper bw, boolean cache) {
		PropertyDescriptor[] filtered = filteredPropertyDescriptorsCache.get(bw.getWrappedClass());
		if (filtered == null) {
			filtered = filterPropertyDescriptorsForDependencyCheck(bw);
			if (cache) {
				PropertyDescriptor[] existing = filteredPropertyDescriptorsCache.putIfAbsent(bw.getWrappedClass(), filtered);
				if (existing != null) {
					filtered = existing;
				}
			}
		}
		return filtered;
	}

	/**
	 * Extract a filtered set of PropertyDescriptors from the given BeanWrapper,excluding ignored dependency types or properties defined on ignored dependency interfaces.
	 * @param bw the BeanWrapper the bean was created with
	 * @return the filtered PropertyDescriptors
	 * @see #isExcludedFromDependencyCheck
	 */
	protected PropertyDescriptor[] filterPropertyDescriptorsForDependencyCheck(BeanWrapper bw) {
		List<PropertyDescriptor> pds = new ArrayList<>(Arrays.asList(bw.getPropertyDescriptors()));
		pds.removeIf(this::isExcludedFromDependencyCheck);
		return pds.toArray(new PropertyDescriptor[0]);
	}

	/**
	 * Determine whether the given bean property is excluded from dependency checks.
	 * This implementation excludes properties defined by CGLIB and  properties whose type matches an ignored dependency type or which are defined by an ignored dependency interface.
	 * @param pd the PropertyDescriptor of the bean property
	 * @return whether the bean property is excluded
	 * @see #ignoreDependencyType(Class)
	 * @see #ignoreDependencyInterface(Class)
	 */
	protected boolean isExcludedFromDependencyCheck(PropertyDescriptor pd) {
		//pd的属性是CGLIB定义的属性 || 该工厂的忽略依赖类型列表中包含该pd的属性类型 || pd的属性是ignoredDependencyInterfaces里面的接口定义的方法
		return (AutowireUtils.isExcludedFromDependencyCheck(pd) || ignoredDependencyTypes.contains(pd.getPropertyType()) || AutowireUtils.isSetterDefinedInInterface(pd, ignoredDependencyInterfaces));
	}

	/**
	 * Perform a dependency check that all properties exposed have been set,if desired.
	 * Dependency checks can be objects (collaborating beans),simple (primitives and String), or all (both).
	 * @param beanName the name of the bean
	 * @param mbd the merged bean definition the bean was created with
	 * @param pds the relevant property descriptors for the target bean
	 * @param pvs the property values to be applied to the bean
	 * @see #isExcludedFromDependencyCheck(java.beans.PropertyDescriptor)
	 */
	protected void checkDependencies(String beanName, AbstractBeanDefinition mbd, PropertyDescriptor[] pds, @Nullable PropertyValues pvs) throws UnsatisfiedDependencyException {
		int dependencyCheck = mbd.getDependencyCheck();
		for (PropertyDescriptor pd : pds) {
			if (pd.getWriteMethod() != null && (pvs == null || !pvs.contains(pd.getName()))) {
				boolean isSimple = BeanUtils.isSimpleProperty(pd.getPropertyType());
				boolean unsatisfied = (dependencyCheck == AbstractBeanDefinition.DEPENDENCY_CHECK_ALL) || (isSimple && dependencyCheck == AbstractBeanDefinition.DEPENDENCY_CHECK_SIMPLE) || (!isSimple && dependencyCheck == AbstractBeanDefinition.DEPENDENCY_CHECK_OBJECTS);
				if (unsatisfied) {
					throw new UnsatisfiedDependencyException(mbd.getResourceDescription(), beanName, pd.getName(),"Set this property value or disable dependency checking for this bean.");
				}
			}
		}
	}

	/**
	 * Apply the given property values, resolving any runtime references to other beans in this bean factory.
	 * Must use deep copy, so we don't permanently modify this property.
	 * @param beanName the bean name passed for better exception information
	 * @param mbd the merged bean definition
	 * @param bw the BeanWrapper wrapping the target object
	 * @param pvs the new property values
	 */
	protected void applyPropertyValues(String beanName, BeanDefinition mbd, BeanWrapper bw, PropertyValues pvs) {
		if (pvs.isEmpty()) return;
		if (System.getSecurityManager() != null && bw instanceof BeanWrapperImpl) {
			((BeanWrapperImpl) bw).setSecurityContext(getAccessControlContext());
		}
		MutablePropertyValues mpvs = null;
		List<PropertyValue> original;
		if (pvs instanceof MutablePropertyValues) {
			mpvs = (MutablePropertyValues) pvs;
			// 如果属性列表 pvs 被转换过，则直接返回即可
			if (mpvs.isConverted()) {
				// Shortcut: use the pre-converted values as-is.
				try {
					// 如果已被设置转换完成，直接完成配置
					bw.setPropertyValues(mpvs);
					return;
				}catch (BeansException ex) {
					throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Error setting property values", ex);
				}
			}
			original = mpvs.getPropertyValueList();
		}else {
			original = Arrays.asList(pvs.getPropertyValues());
		}

		TypeConverter converter = getCustomTypeConverter();
		if (converter == null) {
			converter = bw;
		}
		// 创建BeanDefinitionValueResolver解析器，用来解析未被解析的PropertyValue。
		// 开始遍历检查original中的属性，对未被解析的先解析/已解析的直接加入deepCopy中，最后再填充到具体的Bean实例中
		BeanDefinitionValueResolver valueResolver = new BeanDefinitionValueResolver(this, beanName, mbd, converter);
		// Create a deep copy, resolving any references for values.
		List<PropertyValue> deepCopy = new ArrayList<>(original.size());
		boolean resolveNecessary = false;
		// 遍历属性列表
		for (PropertyValue pv : original) {
			// 如果属性值被转换过，则就不需要再次转换
			if (pv.isConverted()) {
				// 如果属性已经转化，直接添加
				deepCopy.add(pv);
			}else {
				String propertyName = pv.getName();
				Object originalValue = pv.getValue();
				/**
				 * 解析属性值。举例说明，先看下面的配置：
				 *   <bean id="macbook" class="MacBookPro">
				 *       <property name="manufacturer" value="Apple"/>
				 *       <property name="width" value="280"/>
				 *       <property name="cpu" ref="cpu"/>
				 *       <property name="interface">
				 *           <list>
				 *               <value>USB</value>
				 *               <value>HDMI</value>
				 *               <value>Thunderbolt</value>
				 *           </list>
				 *       </property>
				 *   </bean>
				 *
				 * 上面是一款电脑的配置信息，每个 property 配置经过下面的方法解析后，返回如下结果：
				 *   propertyName = "manufacturer", resolvedValue = "Apple"
				 *   propertyName = "width", resolvedValue = "280"
				 *   propertyName = "cpu", resolvedValue = "CPU@1234"  注：resolvedValue 是一个对象
				 *   propertyName = "interface", resolvedValue = ["USB", "HDMI", "Thunderbolt"]
				 *
				 * 如上所示，resolveValueIfNecessary 会将 ref 解析为具体的对象，将 <list> 标签转换为 List 对象等。
				 * 对于 int 类型的配置，这里并未做转换，所以width = "280"，还是字符串。
				 * 除了解析上面几种类型，该方法还会解析 <set/>、 <map/>、<array/> 等集合配置
				 */
				// 核心逻辑，解析获取实际的值
				// 对于RuntimeReference，会解析拿到具体的beanName,最终通过getBean(beanName)拿到具体的对象
				Object resolvedValue = valueResolver.resolveValueIfNecessary(pv, originalValue);
				Object convertedValue = resolvedValue;
				/*
				 * convertible 表示属性值是否可转换，由两个条件合成而来。第一个条件不难理解，解释
				 * 一下第二个条件。第二个条件用于检测 propertyName 是否是 nested 或者 indexed，
				 * 直接举例说明吧：
				 *
				 *   public class Room {
				 *       private Door door = new Door();
				 *   }
				 *
				 * room 对象里面包含了 door 对象，如果我们想向 door 对象中注入属性值，则可以这样配置：
				 *
				 *   <bean id="room" class="xyz.coolblog.Room">
				 *      <property name="door.width" value="123"/>
				 *   </bean>
				 *
				 * isNestedOrIndexedProperty 会根据 propertyName 中是否包含 . 或 [  返回
				 * true 和 false。包含则返回 true，否则返回 false。关于 nested 类型的属性，我
				 * 没在实践中用过，所以不知道上面举的例子是不是合理。若不合理，欢迎指正，也请多多指教。
				 * 关于 nested 类型的属性，大家还可以参考 Spring 的官方文档：
				 *     https://docs.spring.io/spring/docs/4.3.17.RELEASE/spring-framework-reference/htmlsingle/#beans-beans-conventions
				 */
				// 判断是否可以转换
				boolean convertible = bw.isWritableProperty(propertyName) && !PropertyAccessorUtils.isNestedOrIndexedProperty(propertyName);
				// 对于一般的属性，convertible 通常为 true
				if (convertible) {
					// 对属性值的类型进行转换，比如将 String 类型的属性值 "123" 转为 Integer 类型的 123   Resourse 转换为 UrlResource
					// 尝试进行转换
					convertedValue = convertForProperty(resolvedValue, propertyName, bw, converter);
				}
				// Possibly store converted value in merged bean definition,
				// in order to avoid re-conversion for every created bean instance.
				/*
				 * 如果 originalValue 是通过 autowireByType 或 autowireByName 解析而来，
				 * 那么此处条件成立，即 (resolvedValue == originalValue) = true
				 */
				// 避免需要重复转换，设定已转换
				if (resolvedValue == originalValue) {
					if (convertible) {
						// 将 convertedValue 设置到 pv 中，后续再次创建同一个 bean 时，就无需再次进行转换了
						pv.setConvertedValue(convertedValue);
					}
					deepCopy.add(pv);
				}else if (convertible && originalValue instanceof TypedStringValue && !((TypedStringValue) originalValue).isDynamic() && !(convertedValue instanceof Collection || ObjectUtils.isArray(convertedValue))) {
				/*
				 * 如果原始值 originalValue 是 TypedStringValue，且转换后的值
				 * convertedValue 不是 Collection 或数组类型，则将转换后的值存入到 pv 中。
				 */
					pv.setConvertedValue(convertedValue);
					deepCopy.add(pv);
				}else {
					resolveNecessary = true;
					deepCopy.add(new PropertyValue(pv, convertedValue));
				}
			}
		}
		if (mpvs != null && !resolveNecessary) {
			mpvs.setConverted();
		}
		// Set our (possibly massaged) deep copy.
		try {
			// 将所有的属性值设置到 bean 实例中
			bw.setPropertyValues(new MutablePropertyValues(deepCopy));
		}catch (BeansException ex) {
			throw new BeanCreationException(mbd.getResourceDescription(), beanName, "Error setting property values", ex);
		}
	}

	/**
	 * Convert the given value for the specified target property.
	 */
	@Nullable
	private Object convertForProperty(@Nullable Object value, String propertyName, BeanWrapper bw, TypeConverter converter) {
		if (converter instanceof BeanWrapperImpl) {
			return ((BeanWrapperImpl) converter).convertForProperty(value, propertyName);
		}else {
			PropertyDescriptor pd = bw.getPropertyDescriptor(propertyName);
			MethodParameter methodParam = BeanUtils.getWriteMethodParameter(pd);
			return converter.convertIfNecessary(value, pd.getPropertyType(), methodParam);
		}
	}

	/**
	 * Initialize the given bean instance, applying factory callbacks as well as init methods and bean post processors.
	 * 初始化给定的bean实例，应用工厂回调以及init方法和bean post处理器。
	 * Called from {@link #createBean} for traditionally defined beans,and from {@link #initializeBean} for existing bean instances.
	 * @param beanName the bean name in the factory (for debugging purposes)
	 * @param bean the new bean instance we may need to initialize
	 * @param mbd the bean definition that the bean was created with (can also be {@code null}, if given an existing bean instance)
	 * @return the initialized bean instance (potentially wrapped)
	 * @see BeanNameAware
	 * @see BeanClassLoaderAware
	 * @see BeanFactoryAware
	 * @see #applyBeanPostProcessorsBeforeInitialization
	 * @see #invokeInitMethods
	 * @see #applyBeanPostProcessorsAfterInitialization
	 */
	protected Object initializeBean(final String beanName, final Object bean, @Nullable RootBeanDefinition mbd) {
		// 就是要调用：invokeAwareMethods(beanName, bean); （若 bean 实现了 BeanNameAware、BeanFactoryAware、BeanClassLoaderAware 等接口，则向 bean 中注入相关对象）
		if (System.getSecurityManager() != null) {
			AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
				invokeAwareMethods(beanName, bean);
				return null;
			}, getAccessControlContext());
		}else {
			invokeAwareMethods(beanName, bean);
		}
		/**
		 *  ① ② ③ 步骤 常用于 aop的前置/后置/异常/环绕 增强 、 生命周期回调 @PostConstruct @PreDestroy
		*/
		// ② 执行初始化方法之前 执行 postProcessBeforeInitialization   处理 @PostConstruct
		Object wrappedBean = bean;
		if (mbd == null || !mbd.isSynthetic()) {
			wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
		}
		try {
			/*
			 *  ① 调用初始化方法： 总结的四种方法  参考 MyBeanPostProcessor 对应 @Bean(initMethod = "init", destroyMethod = "destroy") 中的 init 方法
			 * 1. 若 bean 实现了 InitializingBean 接口，则调用 afterPropertiesSet 方法
			 * 2. 若用户配置了 bean 的 init-method 属性，则调用用户在配置中指定的方法
			 */
			invokeInitMethods(beanName, wrappedBean, mbd);
		}catch (Throwable ex) {
			throw new BeanCreationException((mbd != null ? mbd.getResourceDescription() : null),beanName, "Invocation of init method failed", ex);
		}
		// ③ 执行初始化方法之前 执行 postProcessAfterInitialization
		if (mbd == null || !mbd.isSynthetic()) {
			// 执行 bean 初始化后置操作，AOP 会在此处向目标对象中织入切面逻辑
			wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
		}
		return wrappedBean;
	}

	/**
	 * <bean id="gaga9" class="com.goat.chapter185.item03.InitializingBeanTest"/>
	 * @param beanName   对应 id="gaga9"
	 * @param bean  对应 class="com.goat.chapter185.item03.InitializingBeanTest"
	 * @see com.goat.chapter185.item03.InitializingBeanTestApp#test2()
	 */
	private void invokeAwareMethods(final String beanName, final Object bean) {
		if (!(bean instanceof Aware)) return; // -modify
		// 注入 beanName 字符串
		if (bean instanceof BeanNameAware) {
			((BeanNameAware) bean).setBeanName(beanName);
		}
		// 注入 ClassLoader 对象 -moidfy
		if (bean instanceof BeanClassLoaderAware) {
			Optional.ofNullable(getBeanClassLoader()).ifPresent(((BeanClassLoaderAware) bean)::setBeanClassLoader);
		}
		// 注入 BeanFactory 对象
		if (bean instanceof BeanFactoryAware) {
			((BeanFactoryAware) bean).setBeanFactory(AbstractAutowireCapableBeanFactory.this);
		}
	}

	/**
	 * Give a bean a chance to react now all its properties are set,and a chance to know about its owning bean factory (this object).
	 * 现在给bean一个反应的机会，它的所有属性都设置好了，并且有机会知道它拥有的bean工厂（这个对象）。
	 * This means checking whether the bean implements InitializingBean or defines a custom init method, and invoking the necessary callback(s) if it does.
	 * 这意味着检查bean是否实现了initialingbean或定义了自定义init方法，如果实现了，则调用必要的回调。
	 * @param beanName the bean name in the factory (for debugging purposes)
	 * @param bean the new bean instance we may need to initialize
	 * @param mbd the merged bean definition that the bean was created with
	 * (can also be {@code null}, if given an existing bean instance)
	 * @throws Throwable if thrown by init methods or by the invocation process
	 * @see #invokeCustomInitMethod
	 */
	protected void invokeInitMethods(String beanName, final Object bean, @Nullable RootBeanDefinition mbd) throws Throwable {
		// 检测 bean 是否是 InitializingBean 类型的
		boolean isInitializingBean = (bean instanceof InitializingBean);
		if (isInitializingBean && (mbd == null || !mbd.isExternallyManagedInitMethod("afterPropertiesSet"))) {
			if (logger.isTraceEnabled()) logger.trace("Invoking afterPropertiesSet() on bean with name '" + beanName + "'");
			if (System.getSecurityManager() != null) {
				try {
					PrivilegedExceptionAction<Object> objectPrivilegedExceptionAction = ()->{
						((InitializingBean) bean).afterPropertiesSet();
						return null;
					};
					AccessController.doPrivileged(objectPrivilegedExceptionAction, getAccessControlContext());
				}catch (PrivilegedActionException pae) {
					throw pae.getException();
				}
			}else {
				// 如果 bean 实现了 InitializingBean，则调用 afterPropertiesSet 方法执行初始化逻辑
				((InitializingBean) bean).afterPropertiesSet();
			}
		}
		if (mbd != null && bean.getClass() != NullBean.class) {
			String initMethodName = mbd.getInitMethodName();
			if (StringUtils.hasLength(initMethodName) && !(isInitializingBean && "afterPropertiesSet".equals(initMethodName)) && !mbd.isExternallyManagedInitMethod(initMethodName)) {
				// 调用用户自定义的初始化方法  InitMethod  参考 @Bean(name = "eventListener", initMethod = "initialize")
				invokeCustomInitMethod(beanName, bean, mbd);
			}
		}
	}

	/**
	 * Invoke the specified custom init method on the given bean. Called by invokeInitMethods.
	 * Can be overridden in subclasses for custom resolution of init methods with arguments.
	 * @see #invokeInitMethods
	 */
	protected void invokeCustomInitMethod(String beanName, final Object bean, RootBeanDefinition mbd) throws Throwable {
		String initMethodName = mbd.getInitMethodName();
		Assert.state(initMethodName != null, "No init method set");
		Method initMethod = (mbd.isNonPublicAccessAllowed() ? BeanUtils.findMethod(bean.getClass(), initMethodName) : ClassUtils.getMethodIfAvailable(bean.getClass(), initMethodName));
		if (initMethod == null) {
			if (mbd.isEnforceInitMethod()) {
				throw new BeanDefinitionValidationException("Could not find an init method named '" + initMethodName + "' on bean with name '" + beanName + "'");
			}else {
				if (logger.isTraceEnabled()) logger.trace("No default init method named '" + initMethodName + "' found on bean with name '" + beanName + "'");
				// Ignore non-existent default lifecycle methods.
				return;
			}
		}
		if (logger.isTraceEnabled()) logger.trace("Invoking init method  '" + initMethodName + "' on bean with name '" + beanName + "'");
		Method methodToInvoke = ClassUtils.getInterfaceMethodIfPossible(initMethod);
		if (System.getSecurityManager() != null) {
			AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
				ReflectionUtils.makeAccessible(methodToInvoke);
				return null;
			});
			try {
				AccessController.doPrivileged((PrivilegedExceptionAction<Object>) () -> methodToInvoke.invoke(bean), getAccessControlContext());
			}catch (PrivilegedActionException pae) {
				InvocationTargetException ex = (InvocationTargetException) pae.getException();
				throw ex.getTargetException();
			}
		}else {
			try {
				ReflectionUtils.makeAccessible(initMethod);
				initMethod.invoke(bean);
			}catch (InvocationTargetException ex) {
				throw ex.getTargetException();
			}
		}
	}

	/**
	 * Expose the logger to collaborating delegates.
	 * @since 5.0.7
	 */
	Log getLogger() {
		return logger;
	}

	/**
	 * Special DependencyDescriptor variant for Spring's good old autowire="byType" mode.
	 * Always optional; never considering the parameter name for choosing a primary candidate.
	 */
	@SuppressWarnings("serial")
	private static class AutowireByTypeDependencyDescriptor extends DependencyDescriptor {
		public AutowireByTypeDependencyDescriptor(MethodParameter methodParameter, boolean eager) {
			super(methodParameter, false, eager);
		}
		@Override
		public String getDependencyName() {
			return null;
		}
	}

	//---------------------------------------------------------------------
	// Implementation of 【FactoryBeanRegistrySupport】 class
	//---------------------------------------------------------------------
	/**
	 * Applies the {@code postProcessAfterInitialization} callback of all registered BeanPostProcessors,
	 * giving them a chance to post-process the object obtained from FactoryBeans (for example, to auto-proxy them).
	 * @see #applyBeanPostProcessorsAfterInitialization
	 */
	@Override
	protected Object postProcessObjectFromFactoryBean(Object object, String beanName) {
		return applyBeanPostProcessorsAfterInitialization(object, beanName);
	}

	//  Overridden to clear FactoryBean instance cache as well.
	@Override
	protected void removeSingleton(String beanName) {
		synchronized (getSingletonMutex()) {
			super.removeSingleton(beanName);
			factoryBeanInstanceCache.remove(beanName);
		}
	}

	// Overridden to clear FactoryBean instance cache as well.
	@Override
	protected void clearSingletonCache() {
		synchronized (getSingletonMutex()) {
			super.clearSingletonCache();
			factoryBeanInstanceCache.clear();
		}
	}
}
