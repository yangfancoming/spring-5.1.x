package org.springframework.beans.factory.support;
import java.beans.PropertyEditor;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyEditorRegistrar;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.PropertyEditorRegistrySupport;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.TypeMismatchException;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanIsAbstractException;
import org.springframework.beans.factory.BeanIsNotAFactoryException;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.SmartFactoryBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanExpressionContext;
import org.springframework.beans.factory.config.BeanExpressionResolver;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.config.Scope;
import org.springframework.core.DecoratingClassLoader;
import org.springframework.core.NamedThreadLocal;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionService;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

/**
 * Abstract base class for {@link org.springframework.beans.factory.BeanFactory} implementations,
 * providing the full capabilities of the {@link org.springframework.beans.factory.config.ConfigurableBeanFactory} SPI.
 * Does <i>not</i> assume a listable bean factory: can therefore also be used as base class for bean factory implementations which obtain bean definitions
 * from some backend resource (where bean definition access is an expensive operation).
 *
 * This class provides a singleton cache (through its base class {@link org.springframework.beans.factory.support.DefaultSingletonBeanRegistry},
 * singleton/prototype determination, {@link org.springframework.beans.factory.FactoryBean} handling, aliases, bean definition merging for child bean definitions,
 * and bean destruction ({@link org.springframework.beans.factory.DisposableBean} interface, custom destroy methods).
 * Furthermore, it can manage a bean factory hierarchy (delegating to the parent in case of an unknown bean),
 * through implementing the {@link org.springframework.beans.factory.HierarchicalBeanFactory} interface.
 *
 * The main template methods to be implemented by subclasses are {@link #getBeanDefinition} and {@link #createBean},
 * retrieving a bean definition for a given bean name and creating a bean instance for a given bean definition,respectively.
 * Default implementations of those operations can be found in {@link DefaultListableBeanFactory} and {@link AbstractAutowireCapableBeanFactory}.
 * @since 15 April 2001
 * @see #getBeanDefinition
 * @see #createBean
 * @see AbstractAutowireCapableBeanFactory#createBean
 * @see DefaultListableBeanFactory#getBeanDefinition
 * 综合了 FactoryBeanRegistrySupport 和 ConfigurableBeanFactory 的功能
 */
public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport implements ConfigurableBeanFactory {

	/** Parent bean factory, for bean inheritance support. */
	@Nullable
	private BeanFactory parentBeanFactory;

	/** ClassLoader to resolve bean class names with, if necessary. */
	@Nullable
	private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

	/** ClassLoader to temporarily resolve bean class names with, if necessary. */
	@Nullable
	private ClassLoader tempClassLoader;

	/** Whether to cache bean metadata or rather reobtain it for every access. */
	private boolean cacheBeanMetadata = true;

	/** Resolution strategy for expressions in bean definition values. */
	@Nullable
	private BeanExpressionResolver beanExpressionResolver;

	/** Spring ConversionService to use instead of PropertyEditors. */
	@Nullable
	private ConversionService conversionService;

	/** Custom PropertyEditorRegistrars to apply to the beans of this factory. 存储PropertyEditorRegistrar接口实现类的集合*/
	private final Set<PropertyEditorRegistrar> propertyEditorRegistrars = new LinkedHashSet<>(4);

	/** Custom PropertyEditors to apply to the beans of this factory. */
	private final Map<Class<?>, Class<? extends PropertyEditor>> customEditors = new HashMap<>(4);

	/** A custom TypeConverter to use, overriding the default PropertyEditor mechanism. */
	@Nullable
	private TypeConverter typeConverter;

	/** String resolvers to apply e.g. to annotation attribute values. 存储StringValueResolver接口实现类的集合 */
	private final List<StringValueResolver> embeddedValueResolvers = new CopyOnWriteArrayList<>();

	/** BeanPostProcessors to apply in createBean. 存储BeanPostProcessor接口实现类的集合 */
	private final List<BeanPostProcessor> beanPostProcessors = new CopyOnWriteArrayList<>();

	/** Indicates whether any InstantiationAwareBeanPostProcessors have been registered. */
	private volatile boolean hasInstantiationAwareBeanPostProcessors;

	/** Indicates whether any DestructionAwareBeanPostProcessors have been registered. */
	private volatile boolean hasDestructionAwareBeanPostProcessors;

	/** Map from scope identifier String to corresponding Scope. */
	private final Map<String, Scope> scopes = new LinkedHashMap<>(8);

	/** Security context used when running with a SecurityManager. */
	@Nullable
	private SecurityContextProvider securityContextProvider;

	/** Map from bean name to merged RootBeanDefinition. 存储bean名称和合并过的根bean定义映射关系*/
	private final Map<String, RootBeanDefinition> mergedBeanDefinitions = new ConcurrentHashMap<>(256);

	/** Names of beans that have already been created at least once. 存储至少被创建过一次的bean名称集合*/
	private final Set<String> alreadyCreated = Collections.newSetFromMap(new ConcurrentHashMap<>(256));

	/** Names of beans that are currently in creation. 当前正在创建的bean的名称集合 */
	private final ThreadLocal<Object> prototypesCurrentlyInCreation = new NamedThreadLocal<>("Prototype beans currently in creation");

	// Create a new AbstractBeanFactory.
	public AbstractBeanFactory() {
		logger.warn("进入 【AbstractBeanFactory】 构造函数 {}");
	}

	/**
	 * Create a new AbstractBeanFactory with the given parent.
	 * @param parentBeanFactory parent bean factory, or {@code null} if none
	 * @see #getBean
	 */
	public AbstractBeanFactory(@Nullable BeanFactory parentBeanFactory) {
		logger.warn("进入 【AbstractBeanFactory】 构造函数 {}");
		this.parentBeanFactory = parentBeanFactory;
	}

	/**
	 * Return an instance, which may be shared or independent, of the specified bean.
	 * @param name the name of the bean to retrieve
	 * @param requiredType the required type of the bean to retrieve
	 * @param args arguments to use when creating a bean instance using explicit arguments (only applied when creating a new instance as opposed to retrieving an existing one)
	 * @return an instance of the bean
	 * @throws BeansException if the bean could not be created
	 */
	//获取IOC容器中指定名称、类型和参数的Bean
	public <T> T getBean(String name, @Nullable Class<T> requiredType, @Nullable Object... args) throws BeansException {
		return doGetBean(name, requiredType, args, false);
	}

	/**
	 * Return an instance, which may be shared or independent, of the specified bean.
	 * @param name the name of the bean to retrieve
	 * @param requiredType the required type of the bean to retrieve
	 * @param args arguments to use when creating a bean instance using explicit arguments (only applied when creating a new instance as opposed to retrieving an existing one)
	 * @param typeCheckOnly whether the instance is obtained for a type check,not for actual use
	 * @return an instance of the bean
	 * @throws BeansException if the bean could not be created
	 * 	进一步调用了如下方法，其中有参数：
	 * 	requiredType=null： 一般情况用不到，如果获取到的字符串，但requiredType是Integer，会在最后进行类型转换。
	 * 	args=null： 在获取prototype对象时传入，用来初始化原型对象
	 * 	typeCheckOnly=false： 如果为false，会将Bean标志为已创建,记录在alreadyCreated变量中。
	 * 	真正实现向IOC容器获取Bean的功能，也是触发依赖注入功能的地方
	 *
	 * 	Spring理念：
	 * 	这种以空间换时间的思想在整个Spring框架中展现的淋漓尽致，小到属性，大到bd，乃至bean实例都无一例外的先查缓存，
	 * 	尽量做到一次解析，多次使用。这和Java的“一次编译，处处运行”的理念交相辉映，相得益彰。
	 */
	@SuppressWarnings("unchecked")
	protected <T> T doGetBean(final String name, @Nullable final Class<T> requiredType,@Nullable final Object[] args, boolean typeCheckOnly) throws BeansException {
		// 先去掉beanName中的所有&前缀，然后再获取其正名
		final String beanName = transformedBeanName(name);
		// 最后的返回值
		Object bean;
		// Eagerly check singleton cache for manually registered singletons.急切地检查singleton缓存中手动注册的singleton
		//  2、这里先尝试从缓存中获取，获取不到再走后面的创建流程
		// 检查是否已初始化
		// 首先尝试去一级缓冲池singletonObjects中获取bean对象。
		Object sharedInstance = getSingleton(beanName);
		if (sharedInstance != null && args == null) {
			if (logger.isTraceEnabled()) {
				if (isSingletonCurrentlyInCreation(beanName)) {
					logger.trace("Returning eagerly cached instance of singleton bean '" + beanName + "' that is not fully initialized yet - a consequence of a circular reference");
				}else {
					logger.trace("Returning cached instance of singleton bean '" + beanName + "'");
				}
			}
			// 从FactoryBean获取bean。这里主要处理实现了FactoryBean的情况，需要调用重写的getObject()方法来获取实际的Bean实例。
			// 这里如果是普通Bean 的话，直接返回，如果是 FactoryBean 的话，返回它创建的那个实例对象
			bean = getObjectForBeanInstance(sharedInstance, name, beanName, null);
		}else { // 如果缓存中没有，尝试从父容器中查找
			// Fail if we're already creating this bean instance: We're assumably within a circular reference.
			// 原型对象不允许循环创建，如果是原型对象则抛异常 // BeanFactory 不缓存 Prototype 类型的 bean，无法处理该类型 bean 的循环依赖问题
			// 判断指定的原型模式的bean是否当前正在创建(在当前线程内),如果是->则抛出异常(Spring不会解决原型模式bean的循环依赖)
			// Spring无法处理循环依赖对象是prototype类型的问题。
			if (isPrototypeCurrentlyInCreation(beanName)) {
				throw new BeanCurrentlyInCreationException(beanName);
			}
			// Check if bean definition exists in this factory.
			//  如果存在父容器，且Bean在父容器中有定义，则通过父容器返回。 因为与springMVC整合后 会涉及到子父级容器间的关系，其他情况下是没有父容器的
			// 检测bean definition是否存在beanFactory中
			BeanFactory parentBeanFactory = getParentBeanFactory();
			// 如果当前BeanFactory中不包含给定beanName的beanDefinition定义,且父beanFactory不为空,则去父beanFactory中再次查找
			if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
				// Not found -> check parent. // 获取 name 对应的 beanName，如果 name 是以 & 字符开头，则返回 & + beanName
				// 将name转换为原始beanName
				// 因为这里的name已经经过beanName的规范处理,例如:&myBean-->规范-->myBean
				// 所以当我们再次去父beanFactory查找时,要将beanName再次转换为原始的beanName,myBean-->回转-->&myBean
				String nameToLookup = originalBeanName(name);
				// 下面会递归调用各种getBean的方法重载,从当前bean的父factoryBean中加载bean
				if (parentBeanFactory instanceof AbstractBeanFactory) {
					return ((AbstractBeanFactory) parentBeanFactory).doGetBean(nameToLookup, requiredType, args, typeCheckOnly);
				}else if (args != null) {
				// 根据 args 是否为空，以决定调用父容器哪个方法获取 bean
					// 参数不为空,则委托parentBeanFactory使用显式参数调动
					// Delegation to parent with explicit args.
					return (T) parentBeanFactory.getBean(nameToLookup, args);
				}else if (requiredType != null) {
					// 参数为空,则委托parentBeanFactory使用标准的getBean方法获取bean
					// No args -> delegate to standard getBean method.
					return parentBeanFactory.getBean(nameToLookup, requiredType);
				}else {
					// 否则委托parentBeanFactory使用默认的getBean方法
					return (T) parentBeanFactory.getBean(nameToLookup);
				}
			}
			// 如果当前bean不是用于类型检查,则将该bean标记为已经被创建或者即将被创建，保证不会重复创建该bean
			if (!typeCheckOnly) {
				markBeanAsCreated(beanName);
			}
			try {
				// 合并beanDefinition,如果指定的bean是一个子bean的话,则遍历其所有的父bean
				// 根据名字获取合并过的对应的RootBeanDefinition  // 合并父 BeanDefinition 与子 BeanDefinition
				// 检测当前bean对象 是否有 dependsOn 属性。
				final RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
				// 校验合并的beanDefinition,如果验证失败,则抛出异常
				// 检查mbd是否为抽象的或mbd为单例，但存在args的情况（args只有初始化原型对象才允许存在)
				checkMergedBeanDefinition(mbd, beanName, args);
				// Guarantee initialization of beans that the current bean depends on. 保证当前bean所依赖的bean的先行初始化
				// 注解@DependsOn。 A->B->C，则先注册和实例化C,然后是B，最后是A
				String[] dependsOn = mbd.getDependsOn();
				if (dependsOn != null) {
					// 循环实例化所有的依赖bean。 三步走： 1.检测条件 2.更新检测条件 3.实例化Bean
					for (String dep : dependsOn) {
						// 1.检测 是否有循环依赖，如果有则抛出异常
						if (isDependent(beanName, dep)) {
							throw new BeanCreationException(mbd.getResourceDescription(), beanName,"Circular depends-on relationship between '" + beanName + "' and '" + dep + "'");
						}
						// 2.注册依赖记录2个Map（dependentBeanMap dependenciesForBeanMap）
						registerDependentBean(dep, beanName);
						try {
							// 3.实例化依赖的bean （走正常流水线）
							getBean(dep);
						}catch (NoSuchBeanDefinitionException ex) {
							throw new BeanCreationException(mbd.getResourceDescription(), beanName,	"'" + beanName + "' depends on missing bean '" + dep + "'", ex);
						}
					}
				}
				// Create bean instance. 重点方法 ！！！  创建单例bean对象 （会走缓存）
				if (mbd.isSingleton()) {
					sharedInstance = getSingleton(beanName, () -> {
						try {
							return createBean(beanName, mbd, args); // 创建单例对象
						}catch (BeansException ex) {
							// Explicitly remove instance from singleton cache: It might have been put there eagerly by the creation process, to allow for circular reference resolution.
							// Also remove any beans that received a temporary reference to the bean.
							destroySingleton(beanName);
							throw ex;
						}
					});
					// 这里主要处理实现了FactoryBean的情况，需要调用重写的getObject()方法来获取实际的Bean实例。
					// 如果 bean 是 FactoryBean 类型，则调用工厂方法获取真正的 bean 实例。否则直接返回 bean 实例
					// 从容器中获取刚刚创建好的bean
					bean = getObjectForBeanInstance(sharedInstance, name, beanName, mbd);
				}else if (mbd.isPrototype()) {
					// It's a prototype -> create a new instance. 创建原型（prototype）bean 实例 （没有缓存 每次都是直接创建）
					Object prototypeInstance;
					try {
						beforePrototypeCreation(beanName);  // 将正在创建的原型对象进行记录
						prototypeInstance = createBean(beanName, mbd, args); // 创建原型对象
					}finally {
						afterPrototypeCreation(beanName); // 移除正在创建的原型对象记录
					}
					bean = getObjectForBeanInstance(prototypeInstance, name, beanName, mbd); // 同上
				}else {
				// 创建其他类型的 bean 实例
					String scopeName = mbd.getScope();
					final Scope scope = scopes.get(scopeName);
					if (scope == null) throw new IllegalStateException("No Scope registered for scope name '" + scopeName + "'");
					try {
						Object scopedInstance = scope.get(beanName, () -> {
							beforePrototypeCreation(beanName);
							try {
								return createBean(beanName, mbd, args);
							}finally {
								afterPrototypeCreation(beanName);
							}
						});
						bean = getObjectForBeanInstance(scopedInstance, name, beanName, mbd);// 同上
					} catch (IllegalStateException ex) {
						throw new BeanCreationException(beanName,"Scope '" + scopeName + "' is not active for the current thread; consider defining a scoped proxy for this bean if you intend to refer to it from a singleton",ex);
					}
				}
			}catch (BeansException ex) {
				cleanupAfterBeanCreationFailure(beanName);
				throw ex;
			}
		}
		// Check if required type matches the type of the actual bean instance.
		// 如果需要进行类型转换，则在此处进行转换。类型转换这一块我没细看，就不多说了。 // 返回bean前做最后的类型检查和转换
		if (requiredType != null && !requiredType.isInstance(bean)) {
			try {
				T convertedBean = getTypeConverter().convertIfNecessary(bean, requiredType);
				if (convertedBean == null) {
					throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
				}
				return convertedBean;
			}catch (TypeMismatchException ex) {
				if (logger.isTraceEnabled()) logger.trace("Failed to convert bean '" + name + "' to required type '" + ClassUtils.getQualifiedName(requiredType) + "'", ex);
				throw new BeanNotOfRequiredTypeException(name, requiredType, bean.getClass());
			}
		}
		return (T) bean;
	}

	//---------------------------------------------------------------------
	// Implementation of 【HierarchicalBeanFactory】 interface
	//---------------------------------------------------------------------
	@Override
	@Nullable
	public BeanFactory getParentBeanFactory() {
		return parentBeanFactory;
	}

	@Override
	public boolean containsLocalBean(String name) {
		String beanName = transformedBeanName(name);
		return ((containsSingleton(beanName) || containsBeanDefinition(beanName)) && (!BeanFactoryUtils.isFactoryDereference(name) || isFactoryBean(beanName)));
	}

	//---------------------------------------------------------------------
	// Implementation of 【ConfigurableBeanFactory】 interface
	//---------------------------------------------------------------------
	@Override
	public void setParentBeanFactory(@Nullable BeanFactory parentBeanFactory) {
		if (this.parentBeanFactory != null && this.parentBeanFactory != parentBeanFactory) {
			throw new IllegalStateException("Already associated with parent BeanFactory: " + this.parentBeanFactory);
		}
		this.parentBeanFactory = parentBeanFactory;
	}

	@Override
	public void setBeanClassLoader(@Nullable ClassLoader beanClassLoader) {
		this.beanClassLoader = (beanClassLoader != null ? beanClassLoader : ClassUtils.getDefaultClassLoader());
	}

	@Override
	@Nullable
	public ClassLoader getBeanClassLoader() {
		return this.beanClassLoader;
	}

	@Override
	public void setTempClassLoader(@Nullable ClassLoader tempClassLoader) {
		this.tempClassLoader = tempClassLoader;
	}

	@Override
	@Nullable
	public ClassLoader getTempClassLoader() {
		return this.tempClassLoader;
	}

	@Override
	public void setCacheBeanMetadata(boolean cacheBeanMetadata) {
		this.cacheBeanMetadata = cacheBeanMetadata;
	}

	@Override
	public boolean isCacheBeanMetadata() {
		return this.cacheBeanMetadata;
	}

	@Override
	public void setBeanExpressionResolver(@Nullable BeanExpressionResolver resolver) {
		this.beanExpressionResolver = resolver;
	}

	@Override
	@Nullable
	public BeanExpressionResolver getBeanExpressionResolver() {
		return this.beanExpressionResolver;
	}

	@Override
	public void setConversionService(@Nullable ConversionService conversionService) {
		this.conversionService = conversionService;
	}

	@Override
	@Nullable
	public ConversionService getConversionService() {
		return this.conversionService;
	}

	@Override
	public void addPropertyEditorRegistrar(PropertyEditorRegistrar registrar) {
		Assert.notNull(registrar, "PropertyEditorRegistrar must not be null");
		this.propertyEditorRegistrars.add(registrar);
	}

	@Override
	public void registerCustomEditor(Class<?> requiredType, Class<? extends PropertyEditor> propertyEditorClass) {
		Assert.notNull(requiredType, "Required type must not be null");
		Assert.notNull(propertyEditorClass, "PropertyEditor class must not be null");
		customEditors.put(requiredType, propertyEditorClass);
	}

	@Override
	public void copyRegisteredEditorsTo(PropertyEditorRegistry registry) {
		registerCustomEditors(registry);
	}

	@Override
	public void setTypeConverter(TypeConverter typeConverter) {
		this.typeConverter = typeConverter;
	}

	/**
	 * Return the custom TypeConverter to use, if any.
	 * @return the custom TypeConverter, or {@code null} if none specified
	 */
	@Nullable
	protected TypeConverter getCustomTypeConverter() {
		return this.typeConverter;
	}

	@Override
	public TypeConverter getTypeConverter() {
		TypeConverter customConverter = getCustomTypeConverter();
		if (customConverter != null) {
			return customConverter;
		}else {
			// Build default TypeConverter, registering custom editors.
			SimpleTypeConverter typeConverter = new SimpleTypeConverter();
			typeConverter.setConversionService(getConversionService());
			registerCustomEditors(typeConverter);
			return typeConverter;
		}
	}


	/**
	 * Delegate the creation of the access control context to the {@link #setSecurityContextProvider SecurityContextProvider}.
	 */
	@Override
	public AccessControlContext getAccessControlContext() {
		return (this.securityContextProvider != null ? this.securityContextProvider.getAccessControlContext() : AccessController.getContext());
	}

	@Override
	public void addEmbeddedValueResolver(StringValueResolver valueResolver) {
		Assert.notNull(valueResolver, "StringValueResolver must not be null");
		embeddedValueResolvers.add(valueResolver);
	}

	@Override
	public boolean hasEmbeddedValueResolver() {
		return !embeddedValueResolvers.isEmpty();
	}

	@Override
	@Nullable
	public String resolveEmbeddedValue(@Nullable String value) {
		if (value == null) return null;
		String result = value;
		for (StringValueResolver resolver : embeddedValueResolvers) {
			result = resolver.resolveStringValue(result);
			if (result == null) return null;
		}
		return result;
	}

	@Override
	public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
		Assert.notNull(beanPostProcessor, "BeanPostProcessor must not be null");
		// Remove from old position, if any
		beanPostProcessors.remove(beanPostProcessor);
		// Track whether it is instantiation/destruction aware
		if (beanPostProcessor instanceof InstantiationAwareBeanPostProcessor) {
			hasInstantiationAwareBeanPostProcessors = true;
		}
		if (beanPostProcessor instanceof DestructionAwareBeanPostProcessor) {
			hasDestructionAwareBeanPostProcessors = true;
		}
		// Add to end of list
		logger.warn("【IOC容器 添加 beanPostProcessors  --- 】 实现类： " + beanPostProcessor);
		beanPostProcessors.add(beanPostProcessor);
	}

	@Override
	public int getBeanPostProcessorCount() {
		return beanPostProcessors.size();
	}

	@Override
	public void copyConfigurationFrom(ConfigurableBeanFactory otherFactory) {
		Assert.notNull(otherFactory, "BeanFactory must not be null");
		setBeanClassLoader(otherFactory.getBeanClassLoader());
		setCacheBeanMetadata(otherFactory.isCacheBeanMetadata());
		setBeanExpressionResolver(otherFactory.getBeanExpressionResolver());
		setConversionService(otherFactory.getConversionService());
		if (otherFactory instanceof AbstractBeanFactory) {
			AbstractBeanFactory otherAbstractFactory = (AbstractBeanFactory) otherFactory;
			propertyEditorRegistrars.addAll(otherAbstractFactory.propertyEditorRegistrars);
			customEditors.putAll(otherAbstractFactory.customEditors);
			typeConverter = otherAbstractFactory.typeConverter;
			beanPostProcessors.addAll(otherAbstractFactory.beanPostProcessors);
			hasInstantiationAwareBeanPostProcessors = hasInstantiationAwareBeanPostProcessors || otherAbstractFactory.hasInstantiationAwareBeanPostProcessors;
			hasDestructionAwareBeanPostProcessors = hasDestructionAwareBeanPostProcessors || otherAbstractFactory.hasDestructionAwareBeanPostProcessors;
			scopes.putAll(otherAbstractFactory.scopes);
			securityContextProvider = otherAbstractFactory.securityContextProvider;
		}else {
			setTypeConverter(otherFactory.getTypeConverter());
			String[] otherScopeNames = otherFactory.getRegisteredScopeNames();
			for (String scopeName : otherScopeNames) {
				scopes.put(scopeName, otherFactory.getRegisteredScope(scopeName));
			}
		}
	}

	@Override
	public void registerScope(String scopeName, Scope scope) {
		Assert.notNull(scopeName, "Scope identifier must not be null");
		Assert.notNull(scope, "Scope must not be null");
		if (SCOPE_SINGLETON.equals(scopeName) || SCOPE_PROTOTYPE.equals(scopeName)) {
			throw new IllegalArgumentException("Cannot replace existing scopes 'singleton' and 'prototype'");
		}
		Scope previous = scopes.put(scopeName, scope);
		if (previous != null && previous != scope) {
			if (logger.isDebugEnabled()) logger.debug("Replacing scope '" + scopeName + "' from [" + previous + "] to [" + scope + "]");
		}else {
			if (logger.isTraceEnabled()) logger.trace("Registering scope '" + scopeName + "' with implementation [" + scope + "]");
		}
	}

	@Override
	public String[] getRegisteredScopeNames() {
		return StringUtils.toStringArray(scopes.keySet());
	}

	@Override
	@Nullable
	public Scope getRegisteredScope(String scopeName) {
		Assert.notNull(scopeName, "Scope identifier must not be null");
		return scopes.get(scopeName);
	}

	/**
	 * Return a 'merged' BeanDefinition for the given bean name,merging a child bean definition with its parent if necessary.
	 * This {@code getMergedBeanDefinition} considers bean definition in ancestors as well.
	 * @param name the name of the bean to retrieve the merged definition for (may be an alias)
	 * @return a (potentially merged) RootBeanDefinition for the given bean
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @throws BeanDefinitionStoreException in case of an invalid bean definition
	 */
	@Override
	public BeanDefinition getMergedBeanDefinition(String name) throws BeansException {
		String beanName = transformedBeanName(name);
		// Efficiently check whether bean definition exists in this factory.
		if (!containsBeanDefinition(beanName) && getParentBeanFactory() instanceof ConfigurableBeanFactory) {
			return ((ConfigurableBeanFactory) getParentBeanFactory()).getMergedBeanDefinition(beanName);
		}
		// Resolve merged bean definition locally.
		return getMergedLocalBeanDefinition(beanName);
	}

	@Override
	public boolean isFactoryBean(String name) throws NoSuchBeanDefinitionException {
		String beanName = transformedBeanName(name);
		//从单例集合singletonObjects中获取缓存的单例对象，第二参数表示不允许早期引用
		//所谓早期引用就是只是创建了一个对象，但填充属性和初始化的bean
		Object beanInstance = getSingleton(beanName, false);
		//如果存在，并且它是个FactoryBean，返回true
		if (beanInstance != null) {
			return (beanInstance instanceof FactoryBean);
		}
		// No singleton instance found -> check bean definition.
		//如果当前BeanFactory中没有注册对应的bean，那么到父BeanFactory中寻找
		if (!containsBeanDefinition(beanName) && getParentBeanFactory() instanceof ConfigurableBeanFactory) {
			// No bean definition found in this factory -> delegate to parent.
			return ((ConfigurableBeanFactory) getParentBeanFactory()).isFactoryBean(name);
		}
		//获取合并BeanDefinition进行判断
		return isFactoryBean(beanName, getMergedLocalBeanDefinition(beanName));
	}

	@Override
	public void destroyScopedBean(String beanName) {
		RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
		if (mbd.isSingleton() || mbd.isPrototype()) {
			throw new IllegalArgumentException("Bean name '" + beanName + "' does not correspond to an object in a mutable scope");
		}
		String scopeName = mbd.getScope();
		Scope scope = scopes.get(scopeName);
		if (scope == null) throw new IllegalStateException("No Scope SPI registered for scope name '" + scopeName + "'");
		Object bean = scope.remove(beanName);
		if (bean != null) {
			destroyBean(beanName, bean, mbd);
		}
	}

	@Override
	public boolean isActuallyInCreation(String beanName) {
		return (isSingletonCurrentlyInCreation(beanName) || isPrototypeCurrentlyInCreation(beanName));
	}

	@Override
	public void destroyBean(String beanName, Object beanInstance) {
		destroyBean(beanName, beanInstance, getMergedLocalBeanDefinition(beanName));
	}

	// Return the set of PropertyEditorRegistrars.
	public Set<PropertyEditorRegistrar> getPropertyEditorRegistrars() {
		return propertyEditorRegistrars;
	}

	// Return the map of custom editors, with Classes as keys and PropertyEditor classes as values.
	public Map<Class<?>, Class<? extends PropertyEditor>> getCustomEditors() {
		return customEditors;
	}

	// Return the list of BeanPostProcessors that will get applied to beans created with this factory.
	public List<BeanPostProcessor> getBeanPostProcessors() {
		return beanPostProcessors;
	}

	/**
	 * Return whether this factory holds a InstantiationAwareBeanPostProcessor that will get applied to singleton beans on shutdown.
	 * @see #addBeanPostProcessor
	 * @see org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor
	 */
	protected boolean hasInstantiationAwareBeanPostProcessors() {
		return hasInstantiationAwareBeanPostProcessors;
	}

	/**
	 * Return whether this factory holds a DestructionAwareBeanPostProcessor that will get applied to singleton beans on shutdown.
	 * @see #addBeanPostProcessor
	 * @see org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor
	 */
	protected boolean hasDestructionAwareBeanPostProcessors() {
		return hasDestructionAwareBeanPostProcessors;
	}

	/**
	 * Set the security context provider for this bean factory. If a security manager is set,interaction with the user code will be executed using the privileged  of the provided security context.
	 */
	public void setSecurityContextProvider(SecurityContextProvider securityProvider) {
		this.securityContextProvider = securityProvider;
	}

	/**
	 * Return whether the specified prototype bean is currently in creation (within the current thread).
	 * @param beanName the name of the bean
	 */
	protected boolean isPrototypeCurrentlyInCreation(String beanName) {
		Object curVal = prototypesCurrentlyInCreation.get();
		return (curVal != null && (curVal.equals(beanName) || (curVal instanceof Set && ((Set<?>) curVal).contains(beanName))));
	}

	/**
	 * Callback before prototype creation.
	 * The default implementation register the prototype as currently in creation.
	 * @param beanName the name of the prototype about to be created
	 * @see #isPrototypeCurrentlyInCreation
	 */
	@SuppressWarnings("unchecked")
	protected void beforePrototypeCreation(String beanName) {
		Object curVal = prototypesCurrentlyInCreation.get();
		if (curVal == null) {
			prototypesCurrentlyInCreation.set(beanName);
		}else if (curVal instanceof String) {
			Set<String> beanNameSet = new HashSet<>(2);
			beanNameSet.add((String) curVal);
			beanNameSet.add(beanName);
			prototypesCurrentlyInCreation.set(beanNameSet);
		}else {
			Set<String> beanNameSet = (Set<String>) curVal;
			beanNameSet.add(beanName);
		}
	}

	/**
	 * Callback after prototype creation.
	 * The default implementation marks the prototype as not in creation anymore.
	 * @param beanName the name of the prototype that has been created
	 * @see #isPrototypeCurrentlyInCreation
	 */
	@SuppressWarnings("unchecked")
	protected void afterPrototypeCreation(String beanName) {
		Object curVal = prototypesCurrentlyInCreation.get();
		if (curVal instanceof String) {
			prototypesCurrentlyInCreation.remove();
		}else if (curVal instanceof Set) {
			Set<String> beanNameSet = (Set<String>) curVal;
			beanNameSet.remove(beanName);
			if (beanNameSet.isEmpty()) {
				prototypesCurrentlyInCreation.remove();
			}
		}
	}

	/**
	 * Destroy the given bean instance (usually a prototype instance obtained from this factory) according to the given bean definition.
	 * @param beanName the name of the bean definition
	 * @param bean the bean instance to destroy
	 * @param mbd the merged bean definition
	 */
	protected void destroyBean(String beanName, Object bean, RootBeanDefinition mbd) {
		new DisposableBeanAdapter(bean, beanName, mbd, getBeanPostProcessors(), getAccessControlContext()).destroy();
	}



	//---------------------------------------------------------------------
	// Implementation methods
	//---------------------------------------------------------------------

	/**
	 * 先去掉beanName中的所有&前缀，然后再获取其正名
	 * Return the bean name, stripping out the factory dereference prefix if necessary,and resolving aliases to canonical names.
	 * 需要转换的2个原因：
	 * 	1.如果是FactoryBean,会去掉Bean开头的&符号
	 * 	2.可能存在传入别名且别名存在多重映射的情况，这里会返回最终的名字，如存在多层别名映射A->B->C->D，传入D,最终会返回A
	 * @param name the user-specified name
	 * @return the transformed bean name
	 */
	protected String transformedBeanName(String name) {
		return canonicalName(BeanFactoryUtils.transformedBeanName(name));
	}

	/**
	 * Determine the original bean name, resolving locally defined aliases to canonical names.
	 * @param name the user-specified name
	 * @return the original bean name
	 */
	protected String originalBeanName(String name) {
		String beanName = transformedBeanName(name);
		if (name.startsWith(FACTORY_BEAN_PREFIX)) {
			beanName = FACTORY_BEAN_PREFIX + beanName;
		}
		return beanName;
	}

	/**
	 * Initialize the given BeanWrapper with the custom editors registered with this factory.
	 * To be called for BeanWrappers that will create and populate bean instances.
	 * The default implementation delegates to {@link #registerCustomEditors}.Can be overridden in subclasses.
	 * @param bw the BeanWrapper to initialize
	 */
	protected void initBeanWrapper(BeanWrapper bw) {
		bw.setConversionService(getConversionService());
		registerCustomEditors(bw);
	}

	/**
	 * Initialize the given PropertyEditorRegistry with the custom editors that have been registered with this BeanFactory.
	 * To be called for BeanWrappers that will create and populate bean instances, and for SimpleTypeConverter used for constructor argument and factory method type conversion.
	 * @param registry the PropertyEditorRegistry to initialize
	 */
	protected void registerCustomEditors(PropertyEditorRegistry registry) {
		PropertyEditorRegistrySupport registrySupport = (registry instanceof PropertyEditorRegistrySupport ? (PropertyEditorRegistrySupport) registry : null);
		if (registrySupport != null) {
			registrySupport.useConfigValueEditors();
		}
		if (!propertyEditorRegistrars.isEmpty()) {
			for (PropertyEditorRegistrar registrar : propertyEditorRegistrars) {
				try {
					registrar.registerCustomEditors(registry);
				}catch (BeanCreationException ex) {
					Throwable rootCause = ex.getMostSpecificCause();
					if (rootCause instanceof BeanCurrentlyInCreationException) {
						BeanCreationException bce = (BeanCreationException) rootCause;
						String bceBeanName = bce.getBeanName();
						if (bceBeanName != null && isCurrentlyInCreation(bceBeanName)) {
							if (logger.isDebugEnabled()) {
								logger.debug("PropertyEditorRegistrar [" + registrar.getClass().getName() + "] failed because it tried to obtain currently created bean '" + ex.getBeanName() + "': " + ex.getMessage());
							}
							onSuppressedException(ex);
							continue;
						}
					}
					throw ex;
				}
			}
		}
		if (!customEditors.isEmpty()) {
			customEditors.forEach((requiredType, editorClass) -> registry.registerCustomEditor(requiredType, BeanUtils.instantiateClass(editorClass)));
		}
	}


	/**
	 * Return a merged RootBeanDefinition, traversing the parent bean definition if the specified bean corresponds to a child bean definition.
	 * @param beanName the name of the bean to retrieve the merged definition for
	 * @return a (potentially merged) RootBeanDefinition for the given bean
	 * @throws NoSuchBeanDefinitionException if there is no bean with the given name
	 * @throws BeanDefinitionStoreException in case of an invalid bean definition
	 * 合并父 BeanDefinition 与子 BeanDefinition
	 * 也就是说BeanFactoryPostProcessor处理器完之后，bean定义可能会有变化，所以我需要重新合并，以保证合并的是最新的。
	 */
	protected RootBeanDefinition getMergedLocalBeanDefinition(String beanName) throws BeansException {
		// Quick check on the concurrent map first, with minimal locking. 首先快速检查并发map，以最小化锁定
		// 检查缓存中是否存在“已合并的 BeanDefinition”，若有直接返回即可
		RootBeanDefinition mbd = mergedBeanDefinitions.get(beanName);
		if (mbd != null) return mbd;
		// 调用重载方法进行bean定义合并 //缓存中未找到，就到BeanFactory中寻找
		RootBeanDefinition mergedBeanDefinition = getMergedBeanDefinition(beanName, getBeanDefinition(beanName));
		return mergedBeanDefinition;
	}

	/**
	 * Return a RootBeanDefinition for the given top-level bean, by merging with the parent if the given bean's definition is a child bean definition.
	 * @param beanName the name of the bean definition
	 * @param bd the original bean definition (Root/ChildBeanDefinition)
	 * @return a (potentially merged) RootBeanDefinition for the given bean
	 * @throws BeanDefinitionStoreException in case of an invalid bean definition
	 */
	protected RootBeanDefinition getMergedBeanDefinition(String beanName, BeanDefinition bd) throws BeanDefinitionStoreException {
		RootBeanDefinition mergedBeanDefinition = getMergedBeanDefinition(beanName, bd, null);
		return mergedBeanDefinition;
	}

	/**
	 * Return a RootBeanDefinition for the given bean, by merging with the parent if the given bean's definition is a child bean definition.
	 * @param beanName the name of the bean definition
	 * @param bd the original bean definition (Root/ChildBeanDefinition)
	 * @param containingBd the containing bean definition in case of inner bean, or {@code null} in case of a top-level bean
	 * @return a (potentially merged) RootBeanDefinition for the given bean
	 * @throws BeanDefinitionStoreException in case of an invalid bean definition
	 */
	protected RootBeanDefinition getMergedBeanDefinition(String beanName, BeanDefinition bd, @Nullable BeanDefinition containingBd) throws BeanDefinitionStoreException {
		synchronized (mergedBeanDefinitions) {
			// 准备一个RootBeanDefinition变量引用，用于记录要构建和最终要返回的BeanDefinition，这里根据上下文不难猜测 mbd 应该就是 mergedBeanDefinition 的缩写。
			RootBeanDefinition mbd = null;
			// Check with full lock now in order to enforce the same merged instance.
			//如果包含BeanDefinition为空，从缓存中获取合并BeanDefinition，应为被包含的BeanDefinition不会被缓存
			if (containingBd == null) {
				mbd = mergedBeanDefinitions.get(beanName);
			}
			//如果缓存中没有
			if (mbd == null) {
				// bd.getParentName() == null，表明无父配置，这时直接将当前的 BeanDefinition 升级为 RootBeanDefinition。 不需要bean合并
				//如果父BeanDefinition为空
				if (bd.getParentName() == null) {
					// bd不是一个ChildBeanDefinition的情况,换句话讲，这 bd应该是 :
					// 1. 一个独立的 GenericBeanDefinition 实例，parentName 属性为null
					// 2. 或者是一个 RootBeanDefinition 实例，parentName 属性为null，此时mbd直接使用一个bd的复制品
					// Use copy of given root bean definition.
					if (bd instanceof RootBeanDefinition) {
						mbd = ((RootBeanDefinition) bd).cloneBeanDefinition();
					}else {
						mbd = new RootBeanDefinition(bd);
					}
				}else {
					// bd是 非RootBeanDefinition 的情况（ChildBeanDefinition/GenericBeanDefinition）, 需要将bd和其parent bean definition 合并到一起，形成最终的 mbd
					// 下面是获取bd的 parent bean definition 的过程，最终结果记录到 pbd，
					// 并且可以看到该过程中递归使用了getMergedBeanDefinition(), 为什么呢? 因为 bd 的 parent bd 可能也是个ChildBeanDefinition，所以该过程需要递归处理
					// Child bean definition: needs to be merged with parent.
					BeanDefinition pbd;
					try {
						String parentBeanName = transformedBeanName(bd.getParentName());
						/**
						 * 判断父类 beanName 与子类 beanName 名称是否相同。若相同，则父类 bean 一定在父容器中。
						 * 原因也很简单，容器底层是用 Map 缓存 <beanName, bean> 键值对的。
						 * 同一个容器下，使用同一个 beanName 映射两个 bean 实例显然是不合适的。
						 * 有的朋友可能会觉得可以这样存储：<beanName, [bean1, bean2]> ，似乎解决了一对多的问题。
						 * 但是也有问题，调用 getName(beanName) 时，到底返回哪个 bean 实例好呢？
						 */
						//如果当前beanName与父beanName不相同，那么递归调用合并方法
						if (!beanName.equals(parentBeanName)) {
							/**
							 * 又调用了一次合并，等于是递归的调用，因为这个parentName的BeanDefinition的parentName的值也不等于空，
							 * 直到找到等于null的，就不合并了,由于我们的parentName的值是root，所有不需要合并，这儿返回的就是root表示的BeanDefinition
							 */
							pbd = getMergedBeanDefinition(parentBeanName);
						}else {
							//如果相同的beanName，那么认为它来自父容器
							BeanFactory parent = getParentBeanFactory();
							if (parent instanceof ConfigurableBeanFactory) {
								pbd = ((ConfigurableBeanFactory) parent).getMergedBeanDefinition(parentBeanName);
							}else {
								// 获取父容器，并判断，父容器的类型，若不是 ConfigurableBeanFactory 则判抛出异常
								throw new NoSuchBeanDefinitionException(parentBeanName,"Parent name '" + parentBeanName + "' is equal to bean name '" + beanName + "': cannot be resolved without an AbstractBeanFactory parent");
							}
						}
					}catch (NoSuchBeanDefinitionException ex) {
						throw new BeanDefinitionStoreException(bd.getResourceDescription(), beanName,"Could not resolve parent bean definition '" + bd.getParentName() + "'", ex);
					}
					// Deep copy with overridden values.
					// 以父 BeanDefinition 的配置信息为蓝本创建 RootBeanDefinition，也就是“已合并的 BeanDefinition”
					// 现在已经获取 bd 的parent bd到pbd，从上面的过程可以看出，这个pbd也是已经"合并"过的。
					// 这里根据pbd创建最终的mbd，然后再使用子bd覆盖一次，这样就相当于mbd来自两个BeanDefinition:
					//复制父BeanDefinition的属性构建新的RootBeanDefinition
					mbd = new RootBeanDefinition(pbd);
					// 将子bean中的特有值属性 覆盖父中的属性（bd覆盖mbd）。
					//将子BeanDefinition的属性覆盖地设置
					mbd.overrideFrom(bd);
				}
				// Set default singleton scope, if not configured before.
				// 如果用户未配置 scope 属性，则默认将该属性配置为 singleton
				if (!StringUtils.hasLength(mbd.getScope())) {
					mbd.setScope(RootBeanDefinition.SCOPE_SINGLETON);
				}
				// A bean contained in a non-singleton bean cannot be a singleton itself.
				// Let's correct this on the fly here, since this might be the result of  parent-child merging for the outer bean, in which case the original inner bean
				// definition will not have inherited the merged outer bean's singleton status.
				// 非单例bean中包含的bean本身不能是单例。
				// 让我们在这里即时纠正 因为这可能是外层bean的父子合并的结果
				// 在这种情况下，原始的内部bean定义将不会继承合并的外部bean的单例状态。
				if (containingBd != null && !containingBd.isSingleton() && mbd.isSingleton()) {
					mbd.setScope(containingBd.getScope());
				}
				// Cache the merged bean definition for the time being (it might still get re-merged later on in order to pick up metadata changes)
				//如果当前bean为被包含bean，它的scope是单例的而其包含bean的scope不是单例的，那么继承包含bean的scope
				//如果当前bean不是被包含的bean，那么进行缓存
				//为什么不缓存被包含bean呢？被包含bean换句话，就是别人的属性
				//属性可能会发生改变
				if (containingBd == null && isCacheBeanMetadata()) {
					// 暂时缓存合并的bean定义（稍后可能仍会重新合并以获取元数据更改）存到合并的bd的map中去。
					logger.warn("【IOC容器 mergedBeanDefinitions 合并bean定义  全局唯一入口】 beanName： " + beanName);
					mergedBeanDefinitions.put(beanName, mbd);
				}
			}
			return mbd;
		}
	}

	/**
	 * Check the given merged bean definition,potentially throwing validation exceptions.
	 * 检查给定的合并bean定义，可能会引发验证异常
	 * @param mbd the merged bean definition to check
	 * @param beanName the name of the bean
	 * @param args the arguments for bean creation, if any
	 * @throws BeanDefinitionStoreException in case of validation failure
	 */
	protected void checkMergedBeanDefinition(RootBeanDefinition mbd, String beanName, @Nullable Object[] args) throws BeanDefinitionStoreException {
		// 由于前面缓存没有命中，父容器中也没有找到，就开始尝试创建，在创建之前需要检测要创建bean是否为抽象类 如果是则抛出异常
		if (mbd.isAbstract()) {
			throw new BeanIsAbstractException(beanName);
		}
	}

	/**
	 * Remove the merged bean definition for the specified bean,recreating it on next access.
	 * @param beanName the bean name to clear the merged definition for
	 */
	protected void clearMergedBeanDefinition(String beanName) {
		mergedBeanDefinitions.remove(beanName);
	}

	/**
	 * Clear the merged bean definition cache, removing entries for beans  which are not considered eligible for full metadata caching yet.
	 * Typically triggered after changes to the original bean definitions,e.g. after applying a {@code BeanFactoryPostProcessor}.
	 * Note that metadata for beans which have already been created at this point will be kept around.
	 * @since 4.2
	 */
	public void clearMetadataCache() {
		mergedBeanDefinitions.keySet().removeIf(bean -> !isBeanEligibleForMetadataCaching(bean));
	}

	/**
	 * Resolve the bean class for the specified bean definition, resolving a bean class name into a Class reference (if necessary) and storing the resolved Class in the bean definition for further use.
	 * @param mbd the merged bean definition to determine the class for
	 * @param beanName the name of the bean (for error handling purposes)
	 * @param typesToMatch the types to match in case of internal type matching purposes (also signals that the returned {@code Class} will never be exposed to application code)
	 * @return the resolved bean class (or {@code null} if none)
	 * @throws CannotLoadBeanClassException if we failed to load the class
	 */
	@Nullable
	protected Class<?> resolveBeanClass(final RootBeanDefinition mbd, String beanName, final Class<?>... typesToMatch) throws CannotLoadBeanClassException {
		try {
			if (mbd.hasBeanClass()) {
				return mbd.getBeanClass();
			}
			if (System.getSecurityManager() != null) {
				return AccessController.doPrivileged((PrivilegedExceptionAction<Class<?>>) () -> doResolveBeanClass(mbd, typesToMatch), getAccessControlContext());
			}else {
				return doResolveBeanClass(mbd, typesToMatch);
			}
		}catch (PrivilegedActionException pae) {
			ClassNotFoundException ex = (ClassNotFoundException) pae.getException();
			throw new CannotLoadBeanClassException(mbd.getResourceDescription(), beanName, mbd.getBeanClassName(), ex);
		}catch (ClassNotFoundException ex) {
			throw new CannotLoadBeanClassException(mbd.getResourceDescription(), beanName, mbd.getBeanClassName(), ex);
		}catch (LinkageError err) {
			throw new CannotLoadBeanClassException(mbd.getResourceDescription(), beanName, mbd.getBeanClassName(), err);
		}
	}

	@Nullable
	private Class<?> doResolveBeanClass(RootBeanDefinition mbd, Class<?>... typesToMatch) throws ClassNotFoundException {
		// 1. Spring 自定义的类型器 DecoratingClassLoader 修改了 JDK 的类加载规则，自己先加载一把，没有再特派给父加载器
		//    这就产生了一个问题，每个临时的类加载器可能加载同一个类可能出现多个，所以可以将其加入到 excludeClass 仍采用双亲委派
		ClassLoader beanClassLoader = getBeanClassLoader();
		ClassLoader dynamicLoader = beanClassLoader;
		boolean freshResolve = false;
		if (!ObjectUtils.isEmpty(typesToMatch)) {
			// When just doing type checks (i.e. not creating an actual instance yet),
			// use the specified temporary class loader (e.g. in a weaving scenario).
			ClassLoader tempClassLoader = getTempClassLoader();
			if (tempClassLoader != null) {
				dynamicLoader = tempClassLoader;
				freshResolve = true;
				if (tempClassLoader instanceof DecoratingClassLoader) {
					DecoratingClassLoader dcl = (DecoratingClassLoader) tempClassLoader;
					for (Class<?> typeToMatch : typesToMatch) {
						dcl.excludeClass(typeToMatch.getName());
					}
				}
			}
		}
		// 2. 如果 className 含有点位符，解析后发生了变化，则不用缓存，以后每次都解析一次
		String className = mbd.getBeanClassName();
		if (className != null) {
			Object evaluated = evaluateBeanDefinitionString(className, mbd);
			if (!className.equals(evaluated)) {
				// A dynamically resolved expression, supported as of 4.2...
				if (evaluated instanceof Class) {
					return (Class<?>) evaluated;
				}else if (evaluated instanceof String) {
					className = (String) evaluated;
					freshResolve = true;
				}else {
					throw new IllegalStateException("Invalid class name expression result: " + evaluated);
				}
			}
			if (freshResolve) {
				// When resolving against a temporary class loader, exit early in order
				// to avoid storing the resolved Class in the bean definition.
				if (dynamicLoader != null) {
					try {
						return dynamicLoader.loadClass(className);
					}catch (ClassNotFoundException ex) {
						if (logger.isTraceEnabled()) logger.trace("Could not load class [" + className + "] from " + dynamicLoader + ": " + ex);
					}
				}
				return ClassUtils.forName(className, dynamicLoader);
			}
		}
		// Resolve regularly, caching the result in the BeanDefinition...
		// 3. 解析 className 后缓存起来
		return mbd.resolveBeanClass(beanClassLoader);
	}

	/**
	 * Evaluate the given String as contained in a bean definition,potentially resolving it as an expression.
	 * @param value the value to check
	 * @param beanDefinition the bean definition that the value comes from
	 * @return the resolved value
	 * @see #setBeanExpressionResolver
	 */
	@Nullable
	protected Object evaluateBeanDefinitionString(@Nullable String value, @Nullable BeanDefinition beanDefinition) {
		if (beanExpressionResolver == null) return value;
		Scope scope = null;
		if (beanDefinition != null) {
			String scopeName = beanDefinition.getScope();
			if (scopeName != null) {
				scope = getRegisteredScope(scopeName);
			}
		}
		return beanExpressionResolver.evaluate(value, new BeanExpressionContext(this, scope));
	}

	/**
	 * Predict the eventual bean type (of the processed bean instance) for the specified bean.
	 * Called by {@link #getType} and {@link #isTypeMatch}.
	 * Does not need to handle FactoryBeans specifically, since it is only supposed to operate on the raw bean type.
	 * This implementation is simplistic in that it is not able to handle factory methods and InstantiationAwareBeanPostProcessors.
	 * It only predicts the bean type correctly for a standard bean.
	 * To be overridden in subclasses, applying more sophisticated type detection.
	 * @param beanName the name of the bean
	 * @param mbd the merged bean definition to determine the type for
	 * @param typesToMatch the types to match in case of internal type matching purposes (also signals that the returned {@code Class} will never be exposed to application code)
	 * @return the type of the bean, or {@code null} if not predictable
	 */
	@Nullable
	protected Class<?> predictBeanType(String beanName, RootBeanDefinition mbd, Class<?>... typesToMatch) {
		// 1. 直接从缓存中获取
		Class<?> targetType = mbd.getTargetType();
		if (targetType != null) return targetType;
		// 2. 工厂方法一律返回 null，子类会重载后解析对应的 getFactoryMethodName
		if (mbd.getFactoryMethodName() != null) {
			return null;
		}
		// 3. 解析 BeanDefinition 的 className，如果已经加载则直接从缓存中获取
		return resolveBeanClass(mbd, beanName, typesToMatch);
	}

	/**
	 * Check whether the given bean is defined as a {@link FactoryBean}.
	 * @param beanName the name of the bean
	 * @param mbd the corresponding bean definition
	 */
	protected boolean isFactoryBean(String beanName, RootBeanDefinition mbd) {
		//判断当前bean类型
		Class<?> beanType = predictBeanType(beanName, mbd, FactoryBean.class);
		return (beanType != null && FactoryBean.class.isAssignableFrom(beanType));
	}

	/**
	 * Determine the bean type for the given FactoryBean definition, as far as possible.
	 * Only called if there is no singleton instance registered for the target bean already.
	 * The default implementation creates the FactoryBean via {@code getBean} to call its {@code getObjectType} method.
	 * Subclasses are encouraged to optimize this, typically by just instantiating the FactoryBean but not populating it yet, trying whether its {@code getObjectType} method already returns a type.
	 * If no type found, a full FactoryBean creation as performed by this implementation should be used as fallback.
	 * @param beanName the name of the bean
	 * @param mbd the merged bean definition for the bean
	 * @return the type for the bean if determinable, or {@code null} otherwise
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 * @see #getBean(String)
	 */
	@Nullable
	protected Class<?> getTypeForFactoryBean(String beanName, RootBeanDefinition mbd) {
		if (!mbd.isSingleton()) return null;
		try {
			FactoryBean<?> factoryBean = doGetBean(FACTORY_BEAN_PREFIX + beanName, FactoryBean.class, null, true);
			return getTypeForFactoryBean(factoryBean);
		}catch (BeanCreationException ex) {
			if (ex.contains(BeanCurrentlyInCreationException.class)) {
				if (logger.isTraceEnabled()) logger.trace("Bean currently in creation on FactoryBean type check: " + ex);
			}else if (mbd.isLazyInit()) {
				if (logger.isTraceEnabled()) logger.trace("Bean creation exception on lazy FactoryBean type check: " + ex);
			}else {
				if (logger.isDebugEnabled()) logger.debug("Bean creation exception on non-lazy FactoryBean type check: " + ex);
			}
			onSuppressedException(ex);
			return null;
		}
	}

	/**
	 * Mark the specified bean as already created (or about to be created).
	 * This allows the bean factory to optimize its caching for repeated creation of the specified bean.
	 * @param beanName the name of the bean
	 */
	protected void markBeanAsCreated(String beanName) {
		// 判断这个这个Bean是否已经创建，很明显这儿没有创建
		if (!alreadyCreated.contains(beanName)) {
			synchronized (mergedBeanDefinitions) {
				// 双重检查
				if (!alreadyCreated.contains(beanName)) {
					// Let the bean definition get re-merged now that we're actually creating the bean... just in case some of its metadata changed in the meantime.
					clearMergedBeanDefinition(beanName);
					alreadyCreated.add(beanName);
					logger.warn("【IOC容器 标记 alreadyCreated 当前 bean 已在创建中 】 beanName： " + beanName);
				}
			}
		}
	}

	/**
	 * Perform appropriate cleanup of cached metadata after bean creation failed.
	 * @param beanName the name of the bean
	 */
	protected void cleanupAfterBeanCreationFailure(String beanName) {
		synchronized (mergedBeanDefinitions) {
			alreadyCreated.remove(beanName);
		}
	}

	/**
	 * Determine whether the specified bean is eligible for having its bean definition metadata cached.
	 * @param beanName the name of the bean
	 * @return {@code true} if the bean's metadata may be cached at this point already
	 */
	protected boolean isBeanEligibleForMetadataCaching(String beanName) {
		return alreadyCreated.contains(beanName);
	}

	/**
	 * Remove the singleton instance (if any) for the given bean name, but only if it hasn't been used for other purposes than type checking.
	 * @param beanName the name of the bean
	 * @return {@code true} if actually removed, {@code false} otherwise
	 */
	protected boolean removeSingletonIfCreatedForTypeCheckOnly(String beanName) {
		if (!alreadyCreated.contains(beanName)) {
			removeSingleton(beanName);
			return true;
		}else {
			return false;
		}
	}

	/**
	 * Check whether this factory's bean creation phase already started,i.e. whether any bean has been marked as created in the meantime.
	 * @since 4.2.2
	 * @see #markBeanAsCreated
	 */
	protected boolean hasBeanCreationStarted() {
		return !alreadyCreated.isEmpty();
	}

	/**
	 * Get the object for the given bean instance, either the bean instance itself or its created object in case of a FactoryBean.
	 * 获取给定bean实例的对象，如果是factorybean，则为bean实例本身或其创建的对象。
	 * @param beanInstance the shared bean instance
	 * @param name name that may include factory dereference prefix
	 * @param beanName the canonical bean name
	 * @param mbd the merged bean definition
	 * @return the object to expose for the bean
	 */
	protected Object getObjectForBeanInstance(Object beanInstance, String name, String beanName, @Nullable RootBeanDefinition mbd) {
		// Don't let calling code try to dereference the factory if the bean isn't a factory.
		// 如果 name 以 & 开头，但 beanInstance 却不是 FactoryBean，则认为有问题。
		// 1、判断bean是否factoryBean
		if (BeanFactoryUtils.isFactoryDereference(name)) {
			// 当前bean是factoryBean,且beanInstance是NullBean的实例,则返回beanInstance
			if (beanInstance instanceof NullBean) {
				return beanInstance;
			}
			// 当前bean是factoryBean,但不是FactoryBean的实例,则抛出异常
			// 因BeanFactoryUtils.isFactoryDereference(name)-->只是从bean名称上进行了判断,我们通过getBean("&myBean")可以人为将一个非factoryBean当做factoryBean
			// 所以这里必须要判断beanInstance是否为FactoryBean的实例
			if (!(beanInstance instanceof FactoryBean)) {
				throw new BeanIsNotAFactoryException(beanName, beanInstance.getClass());
			}
		}
		// 现在我们有了bean实例，它可能是一个普通的bean，也可能是一个FactoryBean。
		// 如果它是FactoryBean，我们使用它创建一个bean实例，除非调用者实际上需要工厂的引用。
		// Now we have the bean instance, which may be a normal bean or a FactoryBean.
		// If it's a FactoryBean, we use it to create a bean instance, unless the
		// caller actually wants a reference to the factory.
		/*
		 * 如果上面的判断通过了，表明 beanInstance 可能是一个普通的 bean，也可能是一个FactoryBean
		 * 如果是一个普通的 bean，这里直接返回 beanInstance 即可。
		 * 如果是FactoryBean，则要调用工厂方法生成一个 bean 实例。
		 */
		/**
		 * 下面这句话稍微有些绕,首先判断beanInstance是FactoryBean的实例,然后又加了一个非的条件,将判断结果反置
		 * 再加一个或条件,判断该bean的name是否有&引用,这样一来就可以判断是返回bean的实例还是返回FactoryBean对象
		 * 例1:我们通过getBean("&myBean"),假设myBean实现了BeanFactory接口,那么myBean肯定是FactoryBean的实例
		 * 此时将第一个判断条件置否,再去判断bean的name是否包含了&符,如果是的话,那么就返回FactoryBean对象本身
		 *
		 * 例2:我们通过getBean("myBean"),假设myBean是一个普通的bean,那么它肯定不是FactoryBean的实例,
		 * 那么该bean跟FactoryBean无任何关系,直接返回其实例即可
		 */
		if (!(beanInstance instanceof FactoryBean) || BeanFactoryUtils.isFactoryDereference(name)) {
			return beanInstance;
		}
		Object object = null;
		// 如果beanDefinition为null,则尝试从缓存中获取给定的FactoryBean公开的对象
		if (mbd == null) {
			//  如果 mbd 为空，则从缓存中加载 bean。FactoryBean 生成的单例 bean 会被缓存在 factoryBeanObjectCache 集合中，不用每次都创建
			object = getCachedObjectForFactoryBean(beanName);
		}
		// 未能从缓存中获得FactoryBean公开的对象,则说明该bean是一个新创建的bean
		if (object == null) {
			// 经过前面的判断，到这里可以保证 beanInstance 是 FactoryBean 类型的，所以可以进行类型转换
			// Return bean instance from factory.
			FactoryBean<?> factory = (FactoryBean<?>) beanInstance;
			// Caches object obtained from FactoryBean if it is a singleton.
			// 如果 mbd 为空，则判断是否存在名字为 beanName 的 BeanDefinition
			// rootBeanDefinition为null,但是在beanDefinitionMap中缓存了对应的beanName
			if (mbd == null && containsBeanDefinition(beanName)) {
				// 合并 BeanDefinition  //合并beanDefinition(包括父类bean)
				mbd = getMergedLocalBeanDefinition(beanName);
			}
			// synthetic 字面意思是"合成的"。通过全局查找，我发现在 AOP 相关的类中会将该属性设为 true。
			// 所以我觉得该字段可能表示某个 bean 是不是被 AOP 增强过，也就是 AOP 基于原始类合成了一个新的代理类。
			// 不过目前只是猜测，没有深究。如果有朋友知道这个字段的具体意义，还望不吝赐教
			// 如果beanDefinition不为null,则要判断该beanDefinition对象是否通过合成获得,
			// 如果不是,则说明该beanDefinition不由有程序本身定义的
			boolean synthetic = (mbd != null && mbd.isSynthetic());
			// 调用 getObjectFromFactoryBean 方法继续获取实例
			// 从给定的FactoryBean中获取指定的beanName对象
			object = getObjectFromFactoryBean(factory, beanName, !synthetic);
		}
		return object;
	}

	/**
	 * Determine whether the given bean name is already in use within this factory,
	 * i.e. whether there is a local bean or alias registered under this name or an inner bean created with this name.
	 * @param beanName the name to check
	 */
	public boolean isBeanNameInUse(String beanName) {
		return isAlias(beanName) || containsLocalBean(beanName) || hasDependentBean(beanName);
	}

	/**
	 * Determine whether the given bean requires destruction on shutdown.
	 * The default implementation checks the DisposableBean interface as well as a specified destroy method and registered DestructionAwareBeanPostProcessors.
	 * @param bean the bean instance to check
	 * @param mbd the corresponding bean definition
	 * @see org.springframework.beans.factory.DisposableBean
	 * @see AbstractBeanDefinition#getDestroyMethodName()
	 * @see org.springframework.beans.factory.config.DestructionAwareBeanPostProcessor
	 */
	protected boolean requiresDestruction(Object bean, RootBeanDefinition mbd) {
		return (bean.getClass() != NullBean.class && (DisposableBeanAdapter.hasDestroyMethod(bean, mbd)
				|| (hasDestructionAwareBeanPostProcessors() && DisposableBeanAdapter.hasApplicableProcessors(bean, getBeanPostProcessors()))));
	}

	/**
	 * Add the given bean to the list of disposable beans in this factory,
	 * registering its DisposableBean interface and/or the given destroy method to be called on factory shutdown (if applicable).
	 * Only applies to singletons.
	 * @param beanName the name of the bean
	 * @param bean the bean instance
	 * @param mbd the bean definition for the bean
	 * @see RootBeanDefinition#isSingleton
	 * @see RootBeanDefinition#getDependsOn
	 * @see #registerDisposableBean
	 * @see #registerDependentBean
	 */
	protected void registerDisposableBeanIfNecessary(String beanName, Object bean, RootBeanDefinition mbd) {
		AccessControlContext acc = (System.getSecurityManager() != null ? getAccessControlContext() : null);
		if (!mbd.isPrototype() && requiresDestruction(bean, mbd)) {
			if (mbd.isSingleton()) {
				// Register a DisposableBean implementation that performs all destruction
				// work for the given bean: DestructionAwareBeanPostProcessors,DisposableBean interface, custom destroy method.
				registerDisposableBean(beanName,new DisposableBeanAdapter(bean, beanName, mbd, getBeanPostProcessors(), acc));
			}else {
				// A bean with a custom scope...
				Scope scope = scopes.get(mbd.getScope());
				if (scope == null) throw new IllegalStateException("No Scope registered for scope name '" + mbd.getScope() + "'");
				scope.registerDestructionCallback(beanName,new DisposableBeanAdapter(bean, beanName, mbd, getBeanPostProcessors(), acc));
			}
		}
	}

	//---------------------------------------------------------------------
	// Implementation of BeanFactory interface
	//---------------------------------------------------------------------
	// 通过bean名称 获取容器中的bean
	@Override
	public Object getBean(String name) throws BeansException {
		return doGetBean(name, null, null, false);
	}

	// 通过bean名称和类型 获取容器中的bean
	@Override
	public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
		return doGetBean(name, requiredType, null, false);
	}

	// 通过bean名称和参数 获取容器中的bean
	@Override
	public Object getBean(String name, Object... args) throws BeansException {
		return doGetBean(name, null, args, false);
	}

	@Override
	public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
		return isTypeMatch(name, ResolvableType.forRawClass(typeToMatch));
	}

	@Override
	@Nullable
	public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
		//如果是&+beanName，那么首先去掉&前缀，然后处理别名
		String beanName = transformedBeanName(name);
		// Check manually registered singletons.
		Object beanInstance = getSingleton(beanName, false);
		if (beanInstance != null && beanInstance.getClass() != NullBean.class) {
			//如果是工厂bean，并且不是想要获取FactoryBean对象，那么调用getObjectType方法返回对象类型
			if (beanInstance instanceof FactoryBean && !BeanFactoryUtils.isFactoryDereference(name)) {
				return getTypeForFactoryBean((FactoryBean<?>) beanInstance);
			}else {
				return beanInstance.getClass();
			}
		}
		// No singleton instance found -> check bean definition.
		//如果本地不包含这个BeanDefinition，那么从父工厂中获取
		BeanFactory parentBeanFactory = getParentBeanFactory();
		if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
			// No bean definition found in this factory -> delegate to parent.
			return parentBeanFactory.getType(originalBeanName(name));
		}
		//获取合并的BeanDefinition
		RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
		// Check decorated bean definition, if any: We assume it'll be easier
		// to determine the decorated bean's type than the proxy's type.
		//获取被装饰的BeanDefinition
		BeanDefinitionHolder dbd = mbd.getDecoratedDefinition();
		//先排除它是FactoryBean的情况，FactoryBean的情况统一到后面去处理
		if (dbd != null && !BeanFactoryUtils.isFactoryDereference(name)) {
			//这个方法我们已经见过了，递归调用
			RootBeanDefinition tbd = getMergedBeanDefinition(dbd.getBeanName(), dbd.getBeanDefinition(), mbd);
			Class<?> targetClass = predictBeanType(dbd.getBeanName(), tbd);
			//如果获取到了类型，并且不是工厂bean，那么直接返回
			if (targetClass != null && !FactoryBean.class.isAssignableFrom(targetClass)) {
				return targetClass;
			}
		}
		//判断类型，值得注意的是这个方法只会判断到&beanName级别
		//如果不是工厂bean，那么自然就是最终的getBean类型
		//FactoryBean<T> beanType指FactoryBean， 最终类型为T
		Class<?> beanClass = predictBeanType(beanName, mbd);
		// Check bean class whether we're dealing with a FactoryBean.
		//判断当前beanType是否为工厂bean类型，如果是工厂bean类型，那么需要继续处理，获取其最终类型
		if (beanClass != null && FactoryBean.class.isAssignableFrom(beanClass)) {
			if (!BeanFactoryUtils.isFactoryDereference(name)) {
				// If it's a FactoryBean, we want to look at what it creates, not at the factory class.
				return getTypeForFactoryBean(beanName, mbd);
			}else {
				return beanClass;
			}
		}else {
			return (!BeanFactoryUtils.isFactoryDereference(name) ? beanClass : null);
		}
	}

	//---------------------------------------------------------------------
	// Implementation of 【BeanFactory】 class
	//---------------------------------------------------------------------
	@Override
	public String[] getAliases(String name) { // 此处调用的是 实现BeanFactory接口的getAliases()
		String beanName = transformedBeanName(name);
		List<String> aliases = new ArrayList<>();
		boolean factoryPrefix = name.startsWith(FACTORY_BEAN_PREFIX);
		String fullBeanName = beanName;
		if (factoryPrefix) {
			fullBeanName = FACTORY_BEAN_PREFIX + beanName;
		}
		if (!fullBeanName.equals(name)) {
			aliases.add(fullBeanName);
		}
		// 此处调用的是父类 SimpleAliasRegistry#getAliases()  必须要 super 关键字显示指定。 否则，递归调用自己
		String[] retrievedAliases = super.getAliases(beanName);
		for (String retrievedAlias : retrievedAliases) {
			String alias = (factoryPrefix ? FACTORY_BEAN_PREFIX : "") + retrievedAlias;
			if (!alias.equals(name)) {
				aliases.add(alias);
			}
		}
		if (!containsSingleton(beanName) && !containsBeanDefinition(beanName)) {
			BeanFactory parentBeanFactory = getParentBeanFactory();
			if (parentBeanFactory != null) {
				aliases.addAll(Arrays.asList(parentBeanFactory.getAliases(fullBeanName)));// 此处调用的是 实现BeanFactory接口的getAliases()
			}
		}
		return StringUtils.toStringArray(aliases);
	}

	//---------------------------------------------------------------------
	// Implementation of 【BeanFactory】 interface
	//---------------------------------------------------------------------
	@Override
	public boolean containsBean(String name) {
		String beanName = transformedBeanName(name);
		if (containsSingleton(beanName) || containsBeanDefinition(beanName)) {
			return (!BeanFactoryUtils.isFactoryDereference(name) || isFactoryBean(name));
		}
		// Not found -> check parent.
		BeanFactory parentBeanFactory = getParentBeanFactory();
		return (parentBeanFactory != null && parentBeanFactory.containsBean(originalBeanName(name)));
	}

	@Override
	public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
		String beanName = transformedBeanName(name);
		Object beanInstance = getSingleton(beanName, false);
		if (beanInstance != null) {
			if (beanInstance instanceof FactoryBean) {
				return (BeanFactoryUtils.isFactoryDereference(name) || ((FactoryBean<?>) beanInstance).isSingleton());
			}else {
				return !BeanFactoryUtils.isFactoryDereference(name);
			}
		}
		// No singleton instance found -> check bean definition.
		BeanFactory parentBeanFactory = getParentBeanFactory();
		if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
			// No bean definition found in this factory -> delegate to parent.
			return parentBeanFactory.isSingleton(originalBeanName(name));
		}
		RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
		// In case of FactoryBean, return singleton status of created object if not a dereference.
		if (mbd.isSingleton()) {
			if (isFactoryBean(beanName, mbd)) {
				if (BeanFactoryUtils.isFactoryDereference(name)) {
					return true;
				}
				FactoryBean<?> factoryBean = (FactoryBean<?>) getBean(FACTORY_BEAN_PREFIX + beanName);
				return factoryBean.isSingleton();
			}else {
				return !BeanFactoryUtils.isFactoryDereference(name);
			}
		}else {
			return false;
		}
	}


	@Override
	public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
		String beanName = transformedBeanName(name);
		BeanFactory parentBeanFactory = getParentBeanFactory();
		if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
			// No bean definition found in this factory -> delegate to parent.
			return parentBeanFactory.isPrototype(originalBeanName(name));
		}
		RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
		if (mbd.isPrototype()) {
			// In case of FactoryBean, return singleton status of created object if not a dereference.
			return (!BeanFactoryUtils.isFactoryDereference(name) || isFactoryBean(beanName, mbd));
		}
		// Singleton or scoped - not a prototype.
		// However, FactoryBean may still produce a prototype object...
		if (BeanFactoryUtils.isFactoryDereference(name)) {
			return false;
		}
		if (isFactoryBean(beanName, mbd)) {
			final FactoryBean<?> fb = (FactoryBean<?>) getBean(FACTORY_BEAN_PREFIX + beanName);
			if (System.getSecurityManager() != null) {
				return AccessController.doPrivileged((PrivilegedAction<Boolean>) () -> ((fb instanceof SmartFactoryBean && ((SmartFactoryBean<?>) fb).isPrototype()) || !fb.isSingleton()),getAccessControlContext());
			}else {
				return ((fb instanceof SmartFactoryBean && ((SmartFactoryBean<?>) fb).isPrototype()) || !fb.isSingleton());
			}
		}else {
			return false;
		}
	}

	@Override
	public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
		// 转换beanName   这里我们可以知道我们的beanName为factoryBeanLearn 因为上面是循环了Spring容器中的所有的Bean
		// 1. 根据实例化后的对象获取 bean 的类型，注意 FactoryBean 的处理即可
		String beanName = transformedBeanName(name);
		// Check manually registered singletons.
		// 因为我们这里是用的AbstractApplicationContext的子类来从Spring容器中获取Bean
		// 获取beanName为factoryBeanLearn的Bean实例 这里是可以获取到Bean实例的
		// 这里有一个问题：使用AbstractApplicationContext的子类从Spring容器中获取Bean和
		// 使用BeanFactory的子类从容器中获取Bean有什么区别？这个可以思考一下
		Object beanInstance = getSingleton(beanName, false);
		// 1.1 如果是 FactoryBean 类型，此时需要判断是否要查找的就是这个工厂对象，判断 beanName 是否是以 & 开头
		//     如果是其创建的类型，则需要调用 getTypeForFactoryBean 从这个 FactoryBean 实例中获取真实类型
		if (beanInstance != null && beanInstance.getClass() != NullBean.class) {
			// factoryBeanLearn是FactoryBean的一个实现类
			if (beanInstance instanceof FactoryBean) {
				// 这里判断beanName是不是以&开头  这里明显不是 这里可以想一下什么情况下会有&开头的Bean
				if (!BeanFactoryUtils.isFactoryDereference(name)) {
					// 这里就是从factoryBeanLearn中获type类型 我们在下面会分析一下这个类
					Class<?> type = getTypeForFactoryBean((FactoryBean<?>) beanInstance);
					// 从factoryBeanLearn中获取到的type类型和我们传入的类型是不是同一种类型 是的话直接返回
					return (type != null && typeToMatch.isAssignableFrom(type));
				}else {
					return typeToMatch.isInstance(beanInstance);
				}
			}else if (!BeanFactoryUtils.isFactoryDereference(name)) {
				// 1.2 如果是普通 bean 则可直接判断类型，当然 Spring 还考虑的泛型的情况
				if (typeToMatch.isInstance(beanInstance)) {
					// Direct match for exposed instance?
					return true;
				}else if (typeToMatch.hasGenerics() && containsBeanDefinition(beanName)) {
					// Generics potentially only match on the target class, not on the proxy...
					RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
					Class<?> targetType = mbd.getTargetType();
					if (targetType != null && targetType != ClassUtils.getUserClass(beanInstance)) {
						// Check raw class match as well, making sure it's exposed on the proxy.
						Class<?> classToMatch = typeToMatch.resolve();
						if (classToMatch != null && !classToMatch.isInstance(beanInstance)) return false;
						if (typeToMatch.isAssignableFrom(targetType)) return true;
					}
					ResolvableType resolvableType = mbd.targetType;
					if (resolvableType == null) resolvableType = mbd.factoryMethodReturnType;
					return (resolvableType != null && typeToMatch.isAssignableFrom(resolvableType));
				}
			}
			return false;
		} else if (containsSingleton(beanName) && !containsBeanDefinition(beanName)) {
			// null instance registered
			return false;
		}
		// 2. 父工厂，没什么好说的
		// No singleton instance found -> check bean definition.
		BeanFactory parentBeanFactory = getParentBeanFactory();
		if (parentBeanFactory != null && !containsBeanDefinition(beanName)) {
			// No bean definition found in this factory -> delegate to parent.
			return parentBeanFactory.isTypeMatch(originalBeanName(name), typeToMatch);
		}
		// 3. 下面就要从 bean 的定义中获取该 bean 的类型了
		// Retrieve corresponding bean definition.
		RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
		Class<?> classToMatch = typeToMatch.resolve();
		if (classToMatch == null) classToMatch = FactoryBean.class;
		Class<?>[] typesToMatch = (FactoryBean.class == classToMatch ? new Class<?>[] {classToMatch} : new Class<?>[] {FactoryBean.class, classToMatch});
		// Check decorated bean definition, if any: We assume it'll be easier  to determine the decorated bean's type than the proxy's type.
		// 检查装饰bean的定义，如果有的话：我们假设确定装饰bean的类型比代理的类型更容易。
		// 3.1 AOP 代理时会将原始的 BeanDefinition 存放到 decoratedDefinition 属性中，可以行忽略这部分
		BeanDefinitionHolder dbd = mbd.getDecoratedDefinition();
		if (dbd != null && !BeanFactoryUtils.isFactoryDereference(name)) {
			RootBeanDefinition tbd = getMergedBeanDefinition(dbd.getBeanName(), dbd.getBeanDefinition(), mbd);
			Class<?> targetClass = predictBeanType(dbd.getBeanName(), tbd, typesToMatch);
			if (targetClass != null && !FactoryBean.class.isAssignableFrom(targetClass)) {
				return typeToMatch.isAssignableFrom(targetClass);
			}
		}
		// 3.2 predictBeanType 推断 beanName 的类型，主要的逻辑
		Class<?> beanType = predictBeanType(beanName, mbd, typesToMatch);
		if (beanType == null) return false;
		// Check bean class whether we're dealing with a FactoryBean.
		// 3.3 处理 FactoryBean 类型
		if (FactoryBean.class.isAssignableFrom(beanType)) {
			if (!BeanFactoryUtils.isFactoryDereference(name) && beanInstance == null) {
				// If it's a FactoryBean, we want to look at what it creates, not the factory class.
				// 此时需要从 FactoryBean 中推断出真实类型
				beanType = getTypeForFactoryBean(beanName, mbd);
				if (beanType == null) return false;
			}
		}else if (BeanFactoryUtils.isFactoryDereference(name)) {
			// 3.4 beanType 不是 FactoryBean 类型，但是又要获取 FactoryBean 的类型？？？
			// Special case: A SmartInstantiationAwareBeanPostProcessor returned a non-FactoryBean
			// type but we nevertheless are being asked to dereference a FactoryBean...
			// Let's check the original bean class and proceed with it if it is a FactoryBean.
			beanType = predictBeanType(beanName, mbd, FactoryBean.class);
			if (beanType == null || !FactoryBean.class.isAssignableFrom(beanType)) {
				return false;
			}
		}
		// 3.5 优先从缓存中判断，可以比较泛型
		ResolvableType resolvableType = mbd.targetType;
		if (resolvableType == null) resolvableType = mbd.factoryMethodReturnType;
		if (resolvableType != null && resolvableType.resolve() == beanType) {
			return typeToMatch.isAssignableFrom(resolvableType);
		}
		return typeToMatch.isAssignableFrom(beanType);
	}

	//---------------------------------------------------------------------
	// Abstract methods to be implemented by subclasses
	//---------------------------------------------------------------------
	/**
	 * Check if this bean factory contains a bean definition with the given name.
	 * Does not consider any hierarchy this factory may participate in.
	 * Invoked by {@code containsBean} when no cached singleton instance is found.
	 * Depending on the nature of the concrete bean factory implementation,
	 * this operation might be expensive (for example, because of directory lookups in external registries).
	 * However, for listable bean factories, this usually just amounts to a local hash lookup: The operation is therefore part of the public interface there.
	 * The same implementation can serve for both this template method and the public interface method in that case.
	 * @param beanName the name of the bean to look for
	 * @return if this bean factory contains a bean definition with the given name
	 * @see #containsBean
	 * @see org.springframework.beans.factory.ListableBeanFactory#containsBeanDefinition
	 */
	protected abstract boolean containsBeanDefinition(String beanName);

	/**
	 * Return the bean definition for the given bean name.
	 * Subclasses should normally implement caching, as this method is invoked by this class every time bean definition metadata is needed.
	 * Depending on the nature of the concrete bean factory implementation,this operation might be expensive (for example, because of directory lookups in external registries).
	 * However, for listable bean factories, this usually  just amounts to a local hash lookup:
	 * The operation is therefore part of the public interface there.
	 * The same implementation can serve for both this template method and the public interface method in that case.
	 * @param beanName the name of the bean to find a definition for
	 * @return the BeanDefinition for this prototype name (never {@code null})
	 * @throws org.springframework.beans.factory.NoSuchBeanDefinitionException if the bean definition cannot be resolved
	 * @throws BeansException in case of errors
	 * @see RootBeanDefinition
	 * @see ChildBeanDefinition
	 * @see org.springframework.beans.factory.config.ConfigurableListableBeanFactory#getBeanDefinition
	 */
	protected abstract BeanDefinition getBeanDefinition(String beanName) throws BeansException;

	/**
	 * Create a bean instance for the given merged bean definition (and arguments).
	 * The bean definition will already have been merged with the parent definition in case of a child definition.
	 * All bean retrieval methods delegate to this method for actual bean creation.
	 * @param beanName the name of the bean
	 * @param mbd the merged bean definition for the bean
	 * @param args explicit arguments to use for constructor or factory method invocation
	 * @return a new instance of the bean
	 * @throws BeanCreationException if the bean could not be created
	 */
	protected abstract Object createBean(String beanName, RootBeanDefinition mbd, @Nullable Object[] args) throws BeanCreationException;
}
