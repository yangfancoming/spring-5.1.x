

package org.springframework.beans.factory.support;

import java.io.IOException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.inject.Provider;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.BeanNotOfRequiredTypeException;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.NoUniqueBeanDefinitionException;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.SmartFactoryBean;
import org.springframework.beans.factory.SmartInitializingSingleton;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.DependencyDescriptor;
import org.springframework.beans.factory.config.NamedBeanHolder;
import org.springframework.core.OrderComparator;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.CompositeIterator;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Spring's default implementation of the {@link ConfigurableListableBeanFactory}  and {@link BeanDefinitionRegistry} interfaces:
 * a full-fledged bean factory based on bean definition metadata, extensible through post-processors.
 * Typical usage is registering all bean definitions first (possibly read from a bean definition file), before accessing beans.
 * Bean lookup by name is therefore an inexpensive operation in a local bean definition table,operating on pre-resolved bean definition metadata objects.
 * Note that readers for specific bean definition formats are typically implemented separately rather than as bean factory subclasses:
 * see for example {@link PropertiesBeanDefinitionReader} and {@link org.springframework.beans.factory.xml.XmlBeanDefinitionReader}.
 * For an alternative implementation of the {@link org.springframework.beans.factory.ListableBeanFactory} interface,
 * have a look at {@link StaticListableBeanFactory}, which manages existing bean instances rather than creating new ones based on bean definitions.
 * @see #registerBeanDefinition
 * @see #addBeanPostProcessor
 * @see #getBean
 * @see #resolveDependency
 * 综合uml中的上述所有的功能，主要是对 bean 注册后的处理。
 * 该类存储bean定义  bean的定义信息  bean定义存储 beanDefinitionMap
 */
@SuppressWarnings("serial")
public class DefaultListableBeanFactory extends AbstractAutowireCapableBeanFactory implements ConfigurableListableBeanFactory, BeanDefinitionRegistry, Serializable {

	@Nullable
	private static Class<?> javaxInjectProviderClass;

	static {
		try {
			javaxInjectProviderClass = ClassUtils.forName("javax.inject.Provider", DefaultListableBeanFactory.class.getClassLoader());
		}catch (ClassNotFoundException ex) {
			// JSR-330 API not available - Provider interface simply not supported then.
			javaxInjectProviderClass = null;
		}
	}

	/** Map from serialized id to factory instance. 序列化相关 */
	private static final Map<String, Reference<DefaultListableBeanFactory>> serializableFactories = new ConcurrentHashMap<>(8);

	/** Optional id for this factory, for serialization purposes.  此工厂的可选ID，用于序列化 */
	@Nullable
	private String serializationId;

	/**
	 * 是否允许相同beanName的覆盖，默认为true。
	 * Whether to allow re-registration of a different definition with the same name.
	 */
	private boolean allowBeanDefinitionOverriding = true;

	/** Whether to allow eager class loading even for lazy-init beans. 对于赖加载的bean，是否允许立即加载*/
	private boolean allowEagerClassLoading = true;

	/** Optional OrderComparator for dependency Lists and arrays. */
	@Nullable
	private Comparator<Object> dependencyComparator;

	/** Resolver to use for checking if a bean definition is an autowire candidate. */
	private AutowireCandidateResolver autowireCandidateResolver = new SimpleAutowireCandidateResolver();

	/** Map from dependency type to corresponding autowired value. 存储修正过的依赖映射关系*/
	private final Map<Class<?>, Object> resolvableDependencies = new ConcurrentHashMap<>(16);

	/** Map of bean definition objects, keyed by bean name. bean 注册的缓存，注册的bean就放在该集合中。存储bean名称和bean定义映射关系*/
	private final Map<String, BeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>(256);

	/** Map of singleton and non-singleton bean names, keyed by dependency type. 根据类型来返回所有bean的name，包含单例和原型两种模式的bean */
	private final Map<Class<?>, String[]> allBeanNamesByType = new ConcurrentHashMap<>(64);

	/** Map of singleton-only bean names, keyed by dependency type. 根据类型返回bean的name，只是返回单例的bean */
	private final Map<Class<?>, String[]> singletonBeanNamesByType = new ConcurrentHashMap<>(64);

	/** List of bean definition names, in registration order. 注册的bean的name的集合。存储bean定义名称列表*/
	private volatile List<String> beanDefinitionNames = new ArrayList<>(256);

	/** List of names of manually registered singletons, in registration order. 手动注册的单例的 bean name 集合 */
	private volatile Set<String> manualSingletonNames = new LinkedHashSet<>(16);

	/** Cached array of bean definition names in case of frozen configuration.  在冻结配置的情况下缓存bean定义名称数组 */
	@Nullable
	private volatile String[] frozenBeanDefinitionNames;

	/** Whether bean definition metadata may be cached for all beans. 是否可以为所有bean，缓存bean 的元数据 */
	private volatile boolean configurationFrozen = false;

	// Create a new DefaultListableBeanFactory.
	public DefaultListableBeanFactory() {
		 super(); // 注释掉super(); -modify
		logger.warn("进入 【DefaultListableBeanFactory】 构造函数 {}");
	}

	/**
	 * Create a new DefaultListableBeanFactory with the given parent.
	 * @param parentBeanFactory the parent BeanFactory
	 */
	public DefaultListableBeanFactory(@Nullable BeanFactory parentBeanFactory) {
		super(parentBeanFactory);
		logger.warn("进入 【DefaultListableBeanFactory】 构造函数 {}");
	}

	// Specify an id for serialization purposes, allowing this BeanFactory to be deserialized from this id back into the BeanFactory object, if needed.
	public void setSerializationId(@Nullable String serializationId) {
		if (serializationId != null) {
			serializableFactories.put(serializationId, new WeakReference<>(this));
		}else if (this.serializationId != null) {
			serializableFactories.remove(this.serializationId);
		}
		this.serializationId = serializationId;
	}

	/**
	 * Return an id for serialization purposes, if specified, allowing this BeanFactory to be deserialized from this id back into the BeanFactory object, if needed.
	 * @since 4.1.2
	 */
	@Nullable
	public String getSerializationId() {
		return serializationId;
	}

	/**
	 * Set whether it should be allowed to override bean definitions by registering a different definition with the same name, automatically replacing the former.
	 * If not, an exception will be thrown. This also applies to overriding aliases. Default is "true".
	 * @see #registerBeanDefinition
	 */
	public void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
		this.allowBeanDefinitionOverriding = allowBeanDefinitionOverriding;
	}

	/**
	 * Return whether it should be allowed to override bean definitions by registering a different definition with the same name, automatically replacing the former.
	 * @since 4.1.2
	 */
	public boolean isAllowBeanDefinitionOverriding() {
		return allowBeanDefinitionOverriding;
	}

	/**
	 * Set whether the factory is allowed to eagerly load bean classes even for bean definitions that are marked as "lazy-init".
	 * Default is "true". Turn this flag off to suppress class loading for lazy-init beans unless such a bean is explicitly requested.
	 * In particular, by-type lookups will then simply ignore bean definitions without resolved class name, instead of loading the bean classes on
	 * demand just to perform a type check.
	 * @see AbstractBeanDefinition#setLazyInit
	 */
	public void setAllowEagerClassLoading(boolean allowEagerClassLoading) {
		this.allowEagerClassLoading = allowEagerClassLoading;
	}

	// Return whether the factory is allowed to eagerly load bean classes even for bean definitions that are marked as "lazy-init".@since 4.1.2
	public boolean isAllowEagerClassLoading() {
		return allowEagerClassLoading;
	}

	/**
	 * Set a {@link java.util.Comparator} for dependency Lists and arrays.
	 * @since 4.0
	 * @see org.springframework.core.OrderComparator
	 * @see org.springframework.core.annotation.AnnotationAwareOrderComparator
	 */
	public void setDependencyComparator(@Nullable Comparator<Object> dependencyComparator) {
		this.dependencyComparator = dependencyComparator;
	}

	// Return the dependency comparator for this BeanFactory (may be {@code null}.@since 4.0
	@Nullable
	public Comparator<Object> getDependencyComparator() {
		return dependencyComparator;
	}

	//  Set a custom autowire candidate resolver for this BeanFactory to use  when deciding whether a bean definition should be considered as a candidate for autowiring.
	public void setAutowireCandidateResolver(final AutowireCandidateResolver autowireCandidateResolver) {
		Assert.notNull(autowireCandidateResolver, "AutowireCandidateResolver must not be null");
		if (autowireCandidateResolver instanceof BeanFactoryAware) {
			if (System.getSecurityManager() != null) {
				AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
					((BeanFactoryAware) autowireCandidateResolver).setBeanFactory(DefaultListableBeanFactory.this);
					return null;
				}, getAccessControlContext());
			}else {
				((BeanFactoryAware) autowireCandidateResolver).setBeanFactory(this);
			}
		}
		this.autowireCandidateResolver = autowireCandidateResolver;
	}

	// Return the autowire candidate resolver for this BeanFactory (never {@code null}).
	public AutowireCandidateResolver getAutowireCandidateResolver() {
		return autowireCandidateResolver;
	}

	@Override
	public void copyConfigurationFrom(ConfigurableBeanFactory otherFactory) {
		super.copyConfigurationFrom(otherFactory);
		if (otherFactory instanceof DefaultListableBeanFactory) {
			DefaultListableBeanFactory otherListableFactory = (DefaultListableBeanFactory) otherFactory;
			this.allowBeanDefinitionOverriding = otherListableFactory.allowBeanDefinitionOverriding;
			this.allowEagerClassLoading = otherListableFactory.allowEagerClassLoading;
			this.dependencyComparator = otherListableFactory.dependencyComparator;
			// A clone of the AutowireCandidateResolver since it is potentially BeanFactoryAware...
			setAutowireCandidateResolver(BeanUtils.instantiateClass(getAutowireCandidateResolver().getClass()));
			// Make resolvable dependencies (e.g. ResourceLoader) available here as well...
			resolvableDependencies.putAll(otherListableFactory.resolvableDependencies);
		}
	}


	//---------------------------------------------------------------------
	// Implementation of remaining BeanFactory methods
	//---------------------------------------------------------------------
	@Override
	public <T> T getBean(Class<T> requiredType) throws BeansException {
		return getBean(requiredType, (Object[]) null);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getBean(Class<T> requiredType, @Nullable Object... args) throws BeansException {
		Assert.notNull(requiredType, "Required type must not be null");
		Object resolved = resolveBean(ResolvableType.forRawClass(requiredType), args, false);
		if (resolved == null) throw new NoSuchBeanDefinitionException(requiredType);
		return (T) resolved;
	}

	@Override
	public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) throws BeansException {
		Assert.notNull(requiredType, "Required type must not be null");
		return getBeanProvider(ResolvableType.forRawClass(requiredType));
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
		return new BeanObjectProvider<T>() {
			@Override
			public T getObject() throws BeansException {
				T resolved = resolveBean(requiredType, null, false);
				if (resolved == null) throw new NoSuchBeanDefinitionException(requiredType);
				return resolved;
			}
			@Override
			public T getObject(Object... args) throws BeansException {
				T resolved = resolveBean(requiredType, args, false);
				if (resolved == null) throw new NoSuchBeanDefinitionException(requiredType);
				return resolved;
			}
			@Override
			@Nullable
			public T getIfAvailable() throws BeansException {
				return resolveBean(requiredType, null, false);
			}
			@Override
			@Nullable
			public T getIfUnique() throws BeansException {
				return resolveBean(requiredType, null, true);
			}
			@Override
			public Stream<T> stream() {
				return Arrays.stream(getBeanNamesForTypedStream(requiredType)).map(name -> (T) getBean(name)).filter(bean -> !(bean instanceof NullBean));
			}

			@Override
			public Stream<T> orderedStream() {
				String[] beanNames = getBeanNamesForTypedStream(requiredType);
				Map<String, T> matchingBeans = new LinkedHashMap<>(beanNames.length);
				for (String beanName : beanNames) {
					Object beanInstance = getBean(beanName);
					if (!(beanInstance instanceof NullBean)) {
						matchingBeans.put(beanName, (T) beanInstance);
					}
				}
				Stream<T> stream = matchingBeans.values().stream();
				return stream.sorted(adaptOrderComparator(matchingBeans));
			}
		};
	}

	@Nullable
	private <T> T resolveBean(ResolvableType requiredType, @Nullable Object[] args, boolean nonUniqueAsNull) {
		NamedBeanHolder<T> namedBean = resolveNamedBean(requiredType, args, nonUniqueAsNull);
		if (namedBean != null) {
			return namedBean.getBeanInstance();
		}
		BeanFactory parent = getParentBeanFactory();
		if (parent instanceof DefaultListableBeanFactory) {
			// 这里相当于递归
			return ((DefaultListableBeanFactory) parent).resolveBean(requiredType, args, nonUniqueAsNull);
		} else if (parent != null) {
			ObjectProvider<T> parentProvider = parent.getBeanProvider(requiredType);
			if (args != null) {
				return parentProvider.getObject(args);
			}else {
				return (nonUniqueAsNull ? parentProvider.getIfUnique() : parentProvider.getIfAvailable());
			}
		}
		return null;
	}

	private String[] getBeanNamesForTypedStream(ResolvableType requiredType) {
		return BeanFactoryUtils.beanNamesForTypeIncludingAncestors(this, requiredType);
	}

	//---------------------------------------------------------------------
	// Implementation of 【ListableBeanFactory】 interface
	//---------------------------------------------------------------------
	@Override
	public boolean containsBeanDefinition(String beanName) {
		Assert.notNull(beanName, "Bean name must not be null");
		return beanDefinitionMap.containsKey(beanName);
	}

	@Override
	public int getBeanDefinitionCount() {
		return beanDefinitionMap.size();
	}

	@Override
	public String[] getBeanDefinitionNames() {
		String[] frozenNames = frozenBeanDefinitionNames;
		if (frozenNames != null) {
			return frozenNames.clone();
		}else {
			return StringUtils.toStringArray(beanDefinitionNames);
		}
	}

	@Override
	public String[] getBeanNamesForType(ResolvableType type) {
		Class<?> resolved = type.resolve();
		if (resolved != null && !type.hasGenerics()) {
			return getBeanNamesForType(resolved, true, true);
		}else {
			return doGetBeanNamesForType(type, true, true);
		}
	}

	@Override
	public String[] getBeanNamesForType(@Nullable Class<?> type) {
		return getBeanNamesForType(type, true, true);
	}

	@Override
	public String[] getBeanNamesForType(@Nullable Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
		// 配置还未被冻结或者类型为null或者不允许早期初始化
		if (!isConfigurationFrozen() || type == null || !allowEagerInit) {
			return doGetBeanNamesForType(ResolvableType.forRawClass(type), includeNonSingletons, allowEagerInit);
		}
		// 先从缓存中获取
		//值得注意的是，不管type是否不为空，allowEagerInit是否为true。只要isConfigurationFrozen()为false就一定不会走这里
		//因为isConfigurationFrozen()为false的时候表示BeanDefinition，可能还会发生更改和添加，所以不能进行缓存
		//如果允许非单例的bean，那么从保存所有bean的集合中获取，否则从单例bean中获取
		Map<Class<?>, String[]> cache = (includeNonSingletons ? allBeanNamesByType : singletonBeanNamesByType);
		String[] resolvedBeanNames = cache.get(type);
		if (resolvedBeanNames != null) {
			return resolvedBeanNames;
		}
		//如果缓存中没有获取到，那么只能重新获取，获取到之后就存入缓存
		// 	调用doGetBeanNamesForType方法获取beanName
		resolvedBeanNames = doGetBeanNamesForType(ResolvableType.forRawClass(type), includeNonSingletons, true);
		// 所传入的类能不能被当前类加载加载
		if (ClassUtils.isCacheSafe(type, getBeanClassLoader())) {
			// 放入到缓存中，解析一次以后从缓存中获取
			// 这里对应到我们这里 key是FactoryBeanService Value是beanFactoryLearn
			cache.put(type, resolvedBeanNames);
		}
		return resolvedBeanNames;
	}

	private String[] doGetBeanNamesForType(ResolvableType type, boolean includeNonSingletons, boolean allowEagerInit) {
		List<String> result = new ArrayList<>();
		// 1.首先在 beanDefinitionNames 中去查找，再去手动注册bean集合（manualSingletonNames）中去查找
		// Check all bean definitions.
		for (String beanName : beanDefinitionNames) {
			// Only consider bean as eligible if the bean name is not defined as alias for some other bean.
			// 只考虑没有别名的bean， 有别名的bean直接忽略
			// 如果是别名，跳过（这个集合会保存所有的主beanName，并且不会保存别名，别名由BeanFactory中别名map维护，这里个人认为是一种防御性编程）
			if (!isAlias(beanName)) {// 不是别名
				try {
					// 根据beanName获取RootBeanDefinition
					// 获取合并的BeanDefinition，合并的BeanDefinition是指spring整合了父BeanDefinition的属性，将其他类型的BeanDefinition变成了RootBeanDefintion
					RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
					// Only check bean definition if it is complete.
					// RootBeanDefinition中的Bean不是抽象类、非延迟初始化
					//抽象的BeanDefinition是不做考虑，抽象的就是拿来继承的
					//如果允许早期初始化，那么直接短路，进入方法体
					//如果不允许早期初始化，那么需要进一步判断,如果是不允许早期初始化的，
					//并且beanClass已经被加载或者它是可以早期初始化的，那么如果当前bean是工厂bean，并且指定的bean又是工厂
					//那么这个bean就必须被早期初始化，也就是说就不符合我们制定的allowEagerInit为false的情况，直接跳过
					if (!mbd.isAbstract() &&
							(allowEagerInit || (mbd.hasBeanClass() || !mbd.isLazyInit() || isAllowEagerClassLoading()) && !requiresEagerInitForType(mbd.getFactoryBeanName()))) {
						// In case of FactoryBean, match object created by FactoryBean.  // 是不是FactoryBean的子类
						//如果当前bean是工厂bean
						boolean isFactoryBean = isFactoryBean(beanName, mbd);
						BeanDefinitionHolder dbd = mbd.getDecoratedDefinition();
						// 这里我们其他的几个变量的意思都差不多  我们需要重点关注的是isTypeMatch这个方法
						// 如果isTypeMatch这个方法返回true的话，我们会把这个beanName即factoryBeanLearn 放入到result中返回
						//如果允许早期初始化，那么基本上会调用最后的isTypeMatch方法
						//这个方法会导致工厂的实例化，但是当前不允许进行早期实例化
						//在不允许早期实例化的情况下，如果当前bean是工厂bean，那么它只能在已经被创建的情况下调用isTypeMatch进行匹配判断
						//否则只能宣告匹配失败，返回false
						boolean matchFound = (allowEagerInit || !isFactoryBean || (dbd != null && !mbd.isLazyInit()) || containsSingleton(beanName))
								&& (includeNonSingletons || (dbd != null ? mbd.isSingleton() : isSingleton(beanName)))
								&& isTypeMatch(beanName, type);
						//如果没有匹配到并且它是个工厂bean，那么加上&前缀，表示我要获取FactoryBean类型的bean
						if (!matchFound && isFactoryBean) {
							// In case of FactoryBean, try to match FactoryBean instance itself next.
							//如果不匹配，还是FactoryBean的子类 这里会把beanName变为 &beanName
							beanName = FACTORY_BEAN_PREFIX + beanName;
							//判断类型匹配不匹配
							matchFound = (includeNonSingletons || mbd.isSingleton()) && isTypeMatch(beanName, type);
						}
						//找到便记录到result集合中，等待返回
						if (matchFound) {
							result.add(beanName);
						}
					}
				}catch (CannotLoadBeanClassException ex) {
					if (allowEagerInit) throw ex;
					// Probably a class name with a placeholder: let's ignore it for type matching purposes.
					if (logger.isTraceEnabled()) logger.trace("Ignoring bean class loading failure for bean '" + beanName + "'", ex);
					onSuppressedException(ex);
				}catch (BeanDefinitionStoreException ex) {
					if (allowEagerInit) throw ex;
					// Probably some metadata with a placeholder: let's ignore it for type matching purposes.
					if (logger.isTraceEnabled()) logger.trace("Ignoring unresolvable metadata in bean definition '" + beanName + "'", ex);
					onSuppressedException(ex);
				}
			}
		}
		// Check manually registered singletons too.
		// 2.在手动注册bean集合（manualSingletonNames）中去查找
		//从单例注册集合中获取，这个单例集合是保存spring内部注入的单例对象。它们有特点就是没有BeanDefinition
		for (String beanName : manualSingletonNames) {
			try {
				// In case of FactoryBean, match object created by FactoryBean.
				//如果是工厂bean，那么调用其getObjectType去匹配是否符合指定类型
				if (isFactoryBean(beanName)) {
					if ((includeNonSingletons || isSingleton(beanName)) && isTypeMatch(beanName, type)) {
						result.add(beanName);
						// Match found for this bean: do not match FactoryBean itself anymore.
						continue;
					}
					// In case of FactoryBean, try to match FactoryBean itself next.
					beanName = FACTORY_BEAN_PREFIX + beanName;
				}
				// Match raw bean instance (might be raw FactoryBean).
				//如果没有匹配成功，那么匹配工厂类
				// 这里实际上就是，用 beanName 去一级单例缓冲池中获取bean，然后拿着该bean与type进行比对 如果是同一个实例类型 则完成匹配，添加到 result结果集中
				if (isTypeMatch(beanName, type)) {
					result.add(beanName);
				}
			}catch (NoSuchBeanDefinitionException ex) {
				// Shouldn't happen - probably a result of circular reference resolution...
				if (logger.isTraceEnabled()) logger.trace("Failed to check manually registered singleton with name '" + beanName + "'", ex);
			}
		}
		return StringUtils.toStringArray(result);
	}

	/**
	 * Check whether the specified bean would need to be eagerly initialized in order to determine its type.
	 * @param factoryBeanName a factory-bean reference that the bean definition defines a factory method for
	 * @return whether eager initialization is necessary
	 */
	private boolean requiresEagerInitForType(@Nullable String factoryBeanName) {
		return (factoryBeanName != null && isFactoryBean(factoryBeanName) && !containsSingleton(factoryBeanName));
	}

	@Override
	public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type) throws BeansException {
		return getBeansOfType(type, true, true);
	}

	@Override
	@SuppressWarnings("unchecked")
	public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type, boolean includeNonSingletons, boolean allowEagerInit)throws BeansException {
		String[] beanNames = getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
		Map<String, T> result = new LinkedHashMap<>(beanNames.length);
		for (String beanName : beanNames) {
			try {
				Object beanInstance = getBean(beanName);
				if (!(beanInstance instanceof NullBean)) {
					result.put(beanName, (T) beanInstance);
				}
			}catch (BeanCreationException ex) {
				Throwable rootCause = ex.getMostSpecificCause();
				if (rootCause instanceof BeanCurrentlyInCreationException) {
					BeanCreationException bce = (BeanCreationException) rootCause;
					String exBeanName = bce.getBeanName();
					if (exBeanName != null && isCurrentlyInCreation(exBeanName)) {
						if (logger.isTraceEnabled()) logger.trace("Ignoring match to currently created bean '" + exBeanName + "': " + ex.getMessage());
						onSuppressedException(ex);
						// Ignore: indicates a circular reference when autowiring constructors.
						// We want to find matches other than the currently created bean itself.
						continue;
					}
				}
				throw ex;
			}
		}
		return result;
	}

	@Override
	public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
		List<String> result = new ArrayList<>();
		for (String beanName : beanDefinitionNames) {
			BeanDefinition beanDefinition = getBeanDefinition(beanName);
			if (!beanDefinition.isAbstract() && findAnnotationOnBean(beanName, annotationType) != null) {
				result.add(beanName);
			}
		}
		for (String beanName : manualSingletonNames) {
			if (!result.contains(beanName) && findAnnotationOnBean(beanName, annotationType) != null) {
				result.add(beanName);
			}
		}
		return StringUtils.toStringArray(result);
	}

	@Override
	public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) {
		String[] beanNames = getBeanNamesForAnnotation(annotationType);
		Map<String, Object> result = new LinkedHashMap<>(beanNames.length);
		for (String beanName : beanNames) {
			Object beanInstance = getBean(beanName);
			if (!(beanInstance instanceof NullBean)) {
				result.put(beanName, beanInstance);
			}
		}
		return result;
	}

	@Override
	@Nullable
	public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) throws NoSuchBeanDefinitionException {
		A ann = null;
		Class<?> beanType = getType(beanName);
		if (beanType != null) {
			ann = AnnotationUtils.findAnnotation(beanType, annotationType);
		}
		if (ann == null && containsBeanDefinition(beanName)) {
			// Check raw bean class, e.g. in case of a proxy.
			RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName);
			if (bd.hasBeanClass()) {
				Class<?> beanClass = bd.getBeanClass();
				if (beanClass != beanType) {
					ann = AnnotationUtils.findAnnotation(beanClass, annotationType);
				}
			}
		}
		return ann;
	}


	//---------------------------------------------------------------------
	// Implementation of 【ConfigurableListableBeanFactory】 interface
	//---------------------------------------------------------------------
	@Override
	public void registerResolvableDependency(Class<?> dependencyType, @Nullable Object autowiredValue) {
		Assert.notNull(dependencyType, "Dependency type must not be null");
		if (autowiredValue != null) {
			if (!(autowiredValue instanceof ObjectFactory || dependencyType.isInstance(autowiredValue))) {
				throw new IllegalArgumentException("Value [" + autowiredValue + "] does not implement specified dependency type [" + dependencyType.getName() + "]");
			}
			resolvableDependencies.put(dependencyType, autowiredValue);
		}
	}

	@Override
	public boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor) throws NoSuchBeanDefinitionException {
		return isAutowireCandidate(beanName, descriptor, getAutowireCandidateResolver());
	}

	@Override
	public Iterator<String> getBeanNamesIterator() {
		CompositeIterator<String> iterator = new CompositeIterator<>();
		iterator.add(beanDefinitionNames.iterator());
		iterator.add(manualSingletonNames.iterator());
		return iterator;
	}

	@Override
	public void freezeConfiguration() {
		configurationFrozen = true;
		frozenBeanDefinitionNames = StringUtils.toStringArray(beanDefinitionNames);
	}

	@Override
	public boolean isConfigurationFrozen() {
		return configurationFrozen;
	}

	/**
	 * Determine whether the specified bean definition qualifies as an autowire candidate,to be injected into other beans which declare a dependency of matching type.
	 * @param beanName the name of the bean definition to check
	 * @param descriptor the descriptor of the dependency to resolve
	 * @param resolver the AutowireCandidateResolver to use for the actual resolution algorithm
	 * @return whether the bean should be considered as autowire candidate
	 */
	protected boolean isAutowireCandidate(String beanName, DependencyDescriptor descriptor, AutowireCandidateResolver resolver) throws NoSuchBeanDefinitionException {
		String beanDefinitionName = BeanFactoryUtils.transformedBeanName(beanName);
		if (containsBeanDefinition(beanDefinitionName)) {
			return isAutowireCandidate(beanName, getMergedLocalBeanDefinition(beanDefinitionName), descriptor, resolver);
		} else if (containsSingleton(beanName)) {
			return isAutowireCandidate(beanName, new RootBeanDefinition(getType(beanName)), descriptor, resolver);
		}
		BeanFactory parent = getParentBeanFactory();
		if (parent instanceof DefaultListableBeanFactory) {
			// No bean definition found in this factory -> delegate to parent.
			return ((DefaultListableBeanFactory) parent).isAutowireCandidate(beanName, descriptor, resolver);
		}else if (parent instanceof ConfigurableListableBeanFactory) {
			// If no DefaultListableBeanFactory, can't pass the resolver along.
			return ((ConfigurableListableBeanFactory) parent).isAutowireCandidate(beanName, descriptor);
		}else {
			return true;
		}
	}

	/**
	 * Determine whether the specified bean definition qualifies as an autowire candidate,to be injected into other beans which declare a dependency of matching type.
	 * @param beanName the name of the bean definition to check
	 * @param mbd the merged bean definition to check
	 * @param descriptor the descriptor of the dependency to resolve
	 * @param resolver the AutowireCandidateResolver to use for the actual resolution algorithm
	 * @return whether the bean should be considered as autowire candidate
	 * isAutowireCandidate 判断候选对象是否可用。实际是都是委托给 AutowireCandidateResolver#isAutowireCandidate 接口判断，Spring 中默认的实现是 ContextAnnotationAutowireCandidateResolver。
	 * isAutowireCandidate 方法过滤候选对象有三重规则：①bd.autowireCandidate=true -> ②泛型匹配 -> ③@Qualifier。更多源码分析见 Spring 注解原理 AutowireCandidateResolver：@Qualifier @Value。
	 */
	protected boolean isAutowireCandidate(String beanName, RootBeanDefinition mbd, DependencyDescriptor descriptor, AutowireCandidateResolver resolver) {
		String beanDefinitionName = BeanFactoryUtils.transformedBeanName(beanName);
		resolveBeanClass(mbd, beanDefinitionName);
		if (mbd.isFactoryMethodUnique && mbd.factoryMethodToIntrospect == null) {
			new ConstructorResolver(this).resolveFactoryMethodIfPossible(mbd);
		}
		return resolver.isAutowireCandidate(new BeanDefinitionHolder(mbd, beanName, getAliases(beanDefinitionName)), descriptor);
	}

	@Override
	public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
		BeanDefinition bd = beanDefinitionMap.get(beanName);
		if (bd == null) {
			if (logger.isTraceEnabled()) logger.trace("No bean named '" + beanName + "' found in " + this);
			throw new NoSuchBeanDefinitionException(beanName);
		}
		return bd;
	}

	/**
	 * @see org.springframework.beans.factory.DefaultListableBeanFactoryTests#testUnreferencedSingletonWasInstantiated() 【测试用例】
	*/
	@Override
	public void preInstantiateSingletons() throws BeansException {
		if (logger.isTraceEnabled()) logger.trace("Pre-instantiating singletons in " + this);
		// Iterate over a copy to allow for init methods which in turn register new bean definitions.
		// While this may not be part of the regular factory bootstrap, it does otherwise work fine.
		// 遍历获取容器内加载的所有 bean的名称 ，对所有的非懒加载且单例的bean进行创建,已经创建的不会再次执行。
		List<String> beanNames = new ArrayList<>(beanDefinitionNames);
		// Trigger initialization of all non-lazy singleton beans... 遍历初始化所有非懒加载单例Bean
		for (String beanName : beanNames) {
			/**
			 * 合并父 Bean 中的配置，主意<bean id="" class="" parent="" /> 中的 parent属性
			 Bean定义公共的抽象类是AbstractBeanDefinition，普通的Bean在Spring加载Bean定义的时候，实例化出来的是GenericBeanDefinition
			 而Spring上下文包括实例化所有Bean，用的是RootBeanDefinition
			 这时候就使用getMergedLocalBeanDefinition方法做了一次转化，将非RootBeanDefinition转换为RootBeanDefinition以供后续操作。
			 注意如果当前BeanDefinition存在父BeanDefinition，会基于父BeanDefinition生成一个RootBeanDefinition,然后再将调用OverrideFrom子BeanDefinition的相关属性覆写进去。
			 // 该方法的merge是指如果bean类继承有父类，那么就将它所有的父类的bd融合成一个RootBeanDefinition返回
			 */
			RootBeanDefinition bd = getMergedLocalBeanDefinition(beanName); // 拿到bean的定义信息
			// 不是抽象类、是单例的且不是懒加载的 //bean不是抽象的，没有@Lazy注解，并且还是单例的
			if (!bd.isAbstract() && bd.isSingleton() && !bd.isLazyInit()) {
				/**
				 *  处理 FactoryBean
				 *  判断当前Bean是否是工厂bean (是否实现了FactoryBean接口)，如果实现了，判断是否要立即初始化
				 *  判断是否需要立即初始化，则根据Bean是否实现了SmartFactoryBean并且重写的内部方法isEagerInit 返回true
				 */
				if (isFactoryBean(beanName)) {
					// 在 beanName 前面加上“&” 符号
					Object bean = getBean(FACTORY_BEAN_PREFIX + beanName);
					// 判断当前 FactoryBean 是否是 SmartFactoryBean 的实现
					if (bean instanceof FactoryBean) {
						final FactoryBean<?> factory = (FactoryBean<?>) bean;
						boolean isEagerInit;
						if (System.getSecurityManager() != null && factory instanceof SmartFactoryBean) {
							isEagerInit = AccessController.doPrivileged((PrivilegedAction<Boolean>)	((SmartFactoryBean<?>) factory)::isEagerInit,getAccessControlContext());
						}else {
							isEagerInit = (factory instanceof SmartFactoryBean && ((SmartFactoryBean<?>) factory).isEagerInit());
						}
						if (isEagerInit) getBean(beanName);
					}
				}else {
					// 非工厂bean 就是普通的bean  对其获取  // 不是FactoryBean的直接使用此方法进行初始化
					getBean(beanName);
				}
			}
		}
		// 如果bean实现了 SmartInitializingSingleton 接口的，那么在这里得到回调
		// Trigger post-initialization callback for all applicable beans...
		// 完成所有bean的创建过程后，执行实现了SmartInitializingSingleton接口bean的afterSingletonsInstantiated回调方法。
		// org.springframework.boot.autoconfigure.condition.BeanTypeRegistry会清空bdmap
		for (String beanName : beanNames) {
			Object singletonInstance = getSingleton(beanName);
			if (singletonInstance instanceof SmartInitializingSingleton) {
				final SmartInitializingSingleton smartSingleton = (SmartInitializingSingleton) singletonInstance;
				if (System.getSecurityManager() != null) {
					AccessController.doPrivileged((PrivilegedAction<Object>) () -> { smartSingleton.afterSingletonsInstantiated();return null;}, getAccessControlContext());
				}else {
					smartSingleton.afterSingletonsInstantiated();
				}
			}
		}
	}

	//---------------------------------------------------------------------
	// Implementation of 【BeanDefinitionRegistry】 interface
	//---------------------------------------------------------------------
	@Override
	public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
		Assert.hasText(beanName, "Bean name must not be empty");
		Assert.notNull(beanDefinition, "BeanDefinition must not be null");
		// 1、如果beanDefinition是AbstractBeanDefinition实例,则验证
		if (beanDefinition instanceof AbstractBeanDefinition) {
			// 注册前的最后一次检验，主要是对 AbstractBeanDefinition 的属性methodOverrides进行校验
			try {
				// 验证不能将静态工厂方法与方法重写相结合(静态工厂方法必须创建实例)
				((AbstractBeanDefinition) beanDefinition).validate();
			}catch (BeanDefinitionValidationException ex) {
				throw new BeanDefinitionStoreException(beanDefinition.getResourceDescription(), beanName,"Validation of bean definition failed", ex);
			}
		}
		// 2、优先尝试从缓存中加载BeanDefinition
		BeanDefinition existingDefinition = beanDefinitionMap.get(beanName);
		// 如果该bean已经注册
		if (existingDefinition != null) {
			// 如果该bean已经注册，且设置为不允许覆盖，则抛出异常
			if (!isAllowBeanDefinitionOverriding()) {
				throw new BeanDefinitionOverrideException(beanName, beanDefinition, existingDefinition);
			}else if (existingDefinition.getRole() < beanDefinition.getRole()) {
				// e.g. was ROLE_APPLICATION, now overriding with ROLE_SUPPORT or ROLE_INFRASTRUCTURE
				if (logger.isInfoEnabled()) logger.info("Overriding user-defined bean definition for bean '" + beanName + "' with a framework-generated bean definition: replacing [" + existingDefinition + "] with [" + beanDefinition + "]");
			}else if (!beanDefinition.equals(existingDefinition)) {
				if (logger.isDebugEnabled()) logger.debug("Overriding bean definition for bean '" + beanName + "' with a different definition: replacing [" + existingDefinition + "] with [" + beanDefinition + "]");
			}else {
				if (logger.isTraceEnabled()) logger.trace("Overriding bean definition for bean '" + beanName + "' with an equivalent definition: replacing [" + existingDefinition + "] with [" + beanDefinition + "]");
			}
			logger.warn("【IOC容器 添加BeanDefinition 内存态 --- 覆盖方式】 beanName： " + beanName + "value：" + beanDefinition.getBeanClassName());
			beanDefinitionMap.put(beanName, beanDefinition);
		}else {
			// 3、缓存中无对应的BeanDefinition，则直接注册
			// 该bean还没注册，检查该工厂的bean创建阶段是否已经开始，即在此期间是否已将该bean标记为已创建。(为了解决单例bean的循环依赖问题)
			if (hasBeanCreationStarted()) {
				// Cannot modify startup-time collection elements anymore (for stable iteration)  // 无法再修改启动时集合元素（用于稳定迭代）
				synchronized (beanDefinitionMap) {
					// 这一步是真正注册bean状态： 由 概念态--->内存态
					logger.warn("【IOC容器中 添加BeanDefinition 内存态 --- 新建方式(创建中)】 beanName： " + beanName + "	value：" + beanDefinition.getBeanClassName());
					beanDefinitionMap.put(beanName, beanDefinition);
					List<String> updatedDefinitions = new ArrayList<>(beanDefinitionNames.size() + 1);
					updatedDefinitions.addAll(beanDefinitionNames);
					updatedDefinitions.add(beanName);
					beanDefinitionNames = updatedDefinitions;
					// doit  这里为啥不用 beanDefinitionNames.add(beanName);   非要updatedDefinitions中间插一手干嘛
					removeManualSingletonName(beanName);
				}
			}else {
				// Still in startup registration phase
				logger.warn("【IOC容器中 添加BeanDefinition 内存态 beanDefinitionMap   --- 新建方式(非创建中)】 beanName： " + beanName + "	value：" + beanDefinition.getBeanClassName());
				beanDefinitionMap.put(beanName, beanDefinition);
				beanDefinitionNames.add(beanName);
				// manualSingletonNames缓存了手动注册的单例bean，所以需要调用一下remove方法，防止beanName重复
				// 例如：xmlBeanFactory.registerSingleton("myDog", new Dog());
				// 就可以向manualSingletonNames中注册单例bean
				removeManualSingletonName(beanName);
			}
			frozenBeanDefinitionNames = null;
		}
		// 4、重置BeanDefinition，
		// 当前注册的bean的定义已经在beanDefinitionMap缓存中存在， 或者其实例已经存在于单例bean的缓存中
		if (existingDefinition != null || containsSingleton(beanName)) {
			resetBeanDefinition(beanName);
		}
	}

	@Override
	public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
		Assert.hasText(beanName, "'beanName' must not be empty");
		BeanDefinition bd = beanDefinitionMap.remove(beanName);
		if (bd == null) {
			if (logger.isTraceEnabled()) logger.trace("No bean named '" + beanName + "' found in " + this);
			throw new NoSuchBeanDefinitionException(beanName);
		}
		if (hasBeanCreationStarted()) {
			// Cannot modify startup-time collection elements anymore (for stable iteration)
			synchronized (beanDefinitionMap) {
				List<String> updatedDefinitions = new ArrayList<>(beanDefinitionNames);
				updatedDefinitions.remove(beanName);
				beanDefinitionNames = updatedDefinitions;
				// doit  这里为啥不用 beanDefinitionNames.add(beanName);   非要updatedDefinitions中间插一手干嘛
			}
		}else {
			// Still in startup registration phase
			beanDefinitionNames.remove(beanName);
		}
		frozenBeanDefinitionNames = null;
		resetBeanDefinition(beanName);
	}

	/**
	 * Reset all bean definition caches for the given bean, including the caches of beans that are derived from it.
	 * Called after an existing bean definition has been replaced or removed, triggering {@link #clearMergedBeanDefinition}, {@link #destroySingleton}
	 * and {@link MergedBeanDefinitionPostProcessor#resetBeanDefinition} on the  given bean and on all bean definitions that have the given bean as parent.
	 * @param beanName the name of the bean to reset
	 * @see #registerBeanDefinition
	 * @see #removeBeanDefinition
	 */
	protected void resetBeanDefinition(String beanName) {
		// Remove the merged bean definition for the given bean, if already created.  while (counter == -1 || registry.containsBeanDefinition(id)) {
		clearMergedBeanDefinition(beanName);
		// Remove corresponding bean from singleton cache, if any. Shouldn't usually  be necessary, rather just meant for overriding a context's default beans
		// (e.g. the default StaticMessageSource in a StaticApplicationContext).
		destroySingleton(beanName);
		// Notify all post-processors that the specified bean definition has been reset.
		for (BeanPostProcessor processor : getBeanPostProcessors()) {
			if (processor instanceof MergedBeanDefinitionPostProcessor) {
				((MergedBeanDefinitionPostProcessor) processor).resetBeanDefinition(beanName);
			}
		}
		// Reset all bean definitions that have the given bean as parent (recursively).
		for (String bdName : beanDefinitionNames) {
			if (!beanName.equals(bdName)) {
				BeanDefinition bd = beanDefinitionMap.get(bdName);
				if (beanName.equals(bd.getParentName())) {
					resetBeanDefinition(bdName);
				}
			}
		}
	}

	//---------------------------------------------------------------------
	// Implementation of 【AbstractBeanFactory】 class
	//---------------------------------------------------------------------
	@Override
	public void clearMetadataCache() {
		super.clearMetadataCache();
		clearByTypeCache();
	}

	/**
	 * Considers all beans as eligible for metadata caching if the factory's configuration has been marked as frozen.
	 * @see #freezeConfiguration()
	 */
	@Override
	protected boolean isBeanEligibleForMetadataCaching(String beanName) {
		return (configurationFrozen || super.isBeanEligibleForMetadataCaching(beanName));
	}

	//  Only allows alias overriding if bean definition overriding is allowed.
	@Override
	protected boolean allowAliasOverriding() {
		return isAllowBeanDefinitionOverriding();
	}

	@Override
	public void registerSingleton(String beanName, Object singletonObject) throws IllegalStateException {
		super.registerSingleton(beanName, singletonObject);
		updateManualSingletonNames(set -> set.add(beanName), set -> !beanDefinitionMap.containsKey(beanName));
		clearByTypeCache();
	}

	@Override
	public void destroySingletons() {
		super.destroySingletons();
		updateManualSingletonNames(Set::clear, set -> !set.isEmpty());
		clearByTypeCache();
	}

	@Override
	public void destroySingleton(String beanName) {
		super.destroySingleton(beanName);
		removeManualSingletonName(beanName);
		clearByTypeCache();
	}

	private void removeManualSingletonName(String beanName) {
		updateManualSingletonNames(set -> set.remove(beanName), set -> set.contains(beanName));
	}

	/**
	 * Update the factory's internal set of manual singleton names.
	 * @param action the modification action
	 * @param condition a precondition for the modification action (if this condition does not apply, the action can be skipped)
	 */
	private void updateManualSingletonNames(Consumer<Set<String>> action, Predicate<Set<String>> condition) {
		if (hasBeanCreationStarted()) {
			// Cannot modify startup-time collection elements anymore (for stable iteration)
			synchronized (beanDefinitionMap) {
				if (condition.test(manualSingletonNames)) {
					Set<String> updatedSingletons = new LinkedHashSet<>(manualSingletonNames);
					action.accept(updatedSingletons);
					manualSingletonNames = updatedSingletons;
				}
			}
		}else {
			// Still in startup registration phase
			if (condition.test(manualSingletonNames)) {
				action.accept(manualSingletonNames);
			}
		}
		logger.warn("【IOC容器 手动注册单例 manualSingletonNames  --- 】 beanName： " + manualSingletonNames);
	}

	// Remove any assumptions about by-type mappings.
	private void clearByTypeCache() {
		allBeanNamesByType.clear();
		singletonBeanNamesByType.clear();
	}

	//---------------------------------------------------------------------
	// Dependency resolution functionality  【AutowireCapableBeanFactory】
	//---------------------------------------------------------------------
	@Override
	public <T> NamedBeanHolder<T> resolveNamedBean(Class<T> requiredType) throws BeansException {
		Assert.notNull(requiredType, "Required type must not be null");
		NamedBeanHolder<T> namedBean = resolveNamedBean(ResolvableType.forRawClass(requiredType), null, false);
		if (namedBean != null) {
			return namedBean;
		}
		//如果当前Spring容器中没有获取到相应的Bean信息，则从父容器中获取 SpringMVC是一个很典型的父子容器
		BeanFactory parent = getParentBeanFactory();
		if (parent instanceof AutowireCapableBeanFactory) {
			return ((AutowireCapableBeanFactory) parent).resolveNamedBean(requiredType);
		}
		throw new NoSuchBeanDefinitionException(requiredType);
	}

	@SuppressWarnings("unchecked")
	@Nullable
	private <T> NamedBeanHolder<T> resolveNamedBean(ResolvableType requiredType, @Nullable Object[] args, boolean nonUniqueAsNull) throws BeansException {
		Assert.notNull(requiredType, "Required type must not be null");
		/**
		 * 		这个方法是根据传入的Class类型来获取BeanName，因为我们有一个接口有多个实现类的情况(多态)，
		 * 		所以这里返回的是一个String数组。这个过程也比较复杂。
		 * 		这里需要注意的是，我们调用getBean方法传入的type为com.zkn.spring.learn.service.FactoryBeanService类型，但是我们没有在Spring容器中注入FactoryBeanService类型的Bean
		 * 		正常来说我们在这里是获取不到beanName呢。但是事实是不是这样呢？看下面我们对getBeanNamesForType的分析
		*/
		String[] candidateNames = getBeanNamesForType(requiredType);
		// 如果有多个BeanName，则挑选合适的BeanName
		if (candidateNames.length > 1) {
			List<String> autowireCandidates = new ArrayList<>(candidateNames.length);
			for (String beanName : candidateNames) {
				if (!containsBeanDefinition(beanName) || getBeanDefinition(beanName).isAutowireCandidate()) {
					autowireCandidates.add(beanName);
				}
			}
			if (!autowireCandidates.isEmpty()) {
				candidateNames = StringUtils.toStringArray(autowireCandidates);
			}
		}
		// 如果只有一个BeanName 我们调用getBean方法来获取Bean实例来放入到NamedBeanHolder中
		// 这里获取bean是根据beanName，beanType和args来获取bean
		// 这里是我们要分析的重点 在下一篇文章中有介绍
		if (candidateNames.length == 1) {
			String beanName = candidateNames[0];
			T t = (T) getBean(beanName, requiredType.toClass(), args);
			return new NamedBeanHolder<>(beanName, t);
		}else if (candidateNames.length > 1) {  // 如果合适的BeanName还是有多个的话
			Map<String, Object> candidates = new LinkedHashMap<>(candidateNames.length);
			for (String beanName : candidateNames) {
				// 看看是不是已经创建多的单例Bean
				if (containsSingleton(beanName) && args == null) {
					Object beanInstance = getBean(beanName);
					candidates.put(beanName, (beanInstance instanceof NullBean ? null : beanInstance));
				}else {
					// 调用getType方法继续获取Bean实例
					candidates.put(beanName, getType(beanName));
				}
			}
			// 有多个Bean实例的话 则取带有Primary注解或者带有Primary信息的Bean
			String candidateName = determinePrimaryCandidate(candidates, requiredType.toClass());
			if (candidateName == null) {
				// 如果没有Primary注解或者Primary相关的信息，则去优先级高的Bean实例
				candidateName = determineHighestPriorityCandidate(candidates, requiredType.toClass());
			}
			if (candidateName != null) {
				// Class类型的话 继续调用getBean方法获取Bean实例
				Object beanInstance = candidates.get(candidateName);
				if (beanInstance == null || beanInstance instanceof Class) {
					beanInstance = getBean(candidateName, requiredType.toClass(), args);
				}
				return new NamedBeanHolder<>(candidateName, (T) beanInstance);
			}
			// 经过 determinePrimaryCandidate 和 determineHighestPriorityCandidate  都未能从多个候选者中，取出优先bean的话，则抛出异常。
			if (!nonUniqueAsNull) {
				throw new NoUniqueBeanDefinitionException(requiredType, candidates.keySet());
			}
		}
		return null;
	}


	/**
	 * descriptor          DependencyDescriptor 这个类实现了对字段、方法参数、构造器参数的进行依赖注入时的统一访问方式，你可以简单的认为是对这三种类型的封装。
	 * requestingBeanName  外层的 beanName
	 * autowiredBeanNames  根据类型查找可能有多个，autowiredBeanNames 就是指查找到的 beanName 集合，Spring 支持 Array、Map、Collection 的注入。
	 * typeConverter       类型转换器，BeanWrapper 自己就是一个转换器。
	*/
	@Override
	@Nullable
	public Object resolveDependency(DependencyDescriptor descriptor, @Nullable String requestingBeanName,@Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException {
		// ParameterNameDiscovery用于解析方法参数名称
		descriptor.initParameterNameDiscovery(getParameterNameDiscoverer());
		if (Optional.class == descriptor.getDependencyType()) {
			// 1. Optional<T>
			return createOptionalDependency(descriptor, requestingBeanName);
		}else if (ObjectFactory.class == descriptor.getDependencyType() || ObjectProvider.class == descriptor.getDependencyType()) {
			// 2. ObjectFactory<T>、ObjectProvider<T>
			return new DependencyObjectProvider(descriptor, requestingBeanName);
		}else if (javaxInjectProviderClass == descriptor.getDependencyType()) {
			// 3. javax.inject.Provider<T>
			return new Jsr330Factory().createDependencyProvider(descriptor, requestingBeanName);
		}else {
			// 4. @Lazy
			Object result = getAutowireCandidateResolver().getLazyResolutionProxyIfNecessary(descriptor, requestingBeanName);
			if (result == null) {
				// 5. 正常情况 解析依赖  这里是重点！
				result = doResolveDependency(descriptor, requestingBeanName, autowiredBeanNames, typeConverter);
			}
			return result;
		}
	}

	/**
	 *
	 *
	 * 在依赖查找之前，想办法快速查找，如缓存 beanName、@Value 等直接获取注入的值，
	 * 避免通过类型查找，最后才对集合依赖和单一依赖分别进行了处理。
	 * 实际上，无论是集合依赖还是单一依赖查找都是调用 findAutowireCandidates 方法
	 *
	 * 1.快速查找： @Autowired 注解处理场景。
	 * 		AutowiredAnnotationBeanPostProcessor 处理 @Autowired 注解时，如果注入的对象只有一个，会将该 bean 对应的名称缓存起来，下次直接通过名称查找会快很多。
	 * 2.注入指定值：@Value 注解处理场景。
	 * 		QualifierAnnotationAutowireCandidateResolver 处理 @Value 注解时，会读取 @Value 对应的值进行注入。
	 * 		如果是 String 要经过三个过程：①占位符处理 -> ②EL 表达式解析 -> ③类型转换，这也是一般的处理过程，BeanDefinitionValueResolver 处理 String 对象也是这个过程。
	 * 3.集合依赖查询：直接全部委托给 resolveMultipleBeans 方法。
	 * 4.单个依赖查询：先调用 findAutowireCandidates 查找所有可用的依赖，
	 * 		如果有多个依赖，则根据规则匹配： @Primary -> @Priority -> ③方法名称或字段名称。
	 *
	 * 	单个依赖查询是重点！！！
	*/
	/**
	 * @param  descriptor          DependencyDescriptor 这个类实现了对字段、方法参数、构造器参数的进行依赖注入时的统一访问方式，你可以简单的认为是对这三种类型的封装。
	 * @param  beanName  外层的 beanName
	 * @param  autowiredBeanNames  根据类型查找可能有多个，autowiredBeanNames 就是指查找到的 beanName 集合，Spring 支持 Array、Map、Collection 的注入。
	 * @param  typeConverter       类型转换器，BeanWrapper 自己就是一个转换器。
	 */
	@Nullable
	public Object doResolveDependency(DependencyDescriptor descriptor, @Nullable String beanName, @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) throws BeansException {
		// 将当前正在解决的依赖存放到 ThreadLocal 中，作用以后再研究？？？
		InjectionPoint previousInjectionPoint = ConstructorResolver.setCurrentInjectionPoint(descriptor);
		try {
			// 该方法最终调用了 beanFactory.getBean(String, Class)，从容器中获取依赖
			// 1. 快速查找，根据名称查找。AutowiredAnnotationBeanPostProcessor用到
			// 1. resolveShortcut 和 getSuggestedValue 都是对依赖解析前的拦截，这也很符全 Spring 的做法
			Object shortcut = descriptor.resolveShortcut(this);
			// 如果容器中存在所需依赖，这里进行断路操作，提前结束依赖解析逻辑
			if (shortcut != null) return shortcut;
			// 2. 解析出待注入属性的类型为 type
			Class<?> type = descriptor.getDependencyType();
			// 获取 @value 注解的值
			Object value = getAutowireCandidateResolver().getSuggestedValue(descriptor);
			if (value != null) {
				if (value instanceof String) {
					// 2.1 占位符解析
					String strVal = resolveEmbeddedValue((String) value);
					BeanDefinition bd = (beanName != null && containsBean(beanName) ? getMergedBeanDefinition(beanName) : null);
					// 2.2 Spring EL 表达式
					value = evaluateBeanDefinitionString(strVal, bd);
				}
				TypeConverter converter = (typeConverter != null ? typeConverter : getTypeConverter());
				try {
					// 2.3 类型转换
					return converter.convertIfNecessary(value, type, descriptor.getTypeDescriptor());
				}catch (UnsupportedOperationException ex) {
					// A custom TypeConverter which does not support TypeDescriptor resolution...
					return (descriptor.getField() != null ?
							converter.convertIfNecessary(value, type, descriptor.getField()) :
							converter.convertIfNecessary(value, type, descriptor.getMethodParameter()));
				}
			}
			// 2. 集合依赖，sine @Spring 4.3。如果 type 是 Array、List、Set、Map 类型的依赖， 走这里，内部查找依赖也是使用findAutowireCandidates
			Object multipleBeans = resolveMultipleBeans(descriptor, beanName, autowiredBeanNames, typeConverter);
			if (multipleBeans != null) {
				return multipleBeans;
			}
			/*
			 * 按类型查找候选列表，如果某个类型已经被实例化，则返回相应的实例。
			 * 比如下面的配置：
			 *   <bean name="mongoDao" class="xyz.coolblog.autowire.MongoDao" primary="true"/>
			 *   <bean name="service" class="xyz.coolblog.autowire.Service" autowire="byType"/>
			 *   <bean name="mysqlDao" class="xyz.coolblog.autowire.MySqlDao"/>
			 * MongoDao 和 MySqlDao 均实现自 Dao 接口，Service 对象（不是接口）中有一个 Dao 类型的属性。现在根据类型自动注入 Dao 的实现类。
			 * 这里有两个候选 bean，一个是mongoDao，另一个是 mysqlDao，其中 mongoDao 在 service 之前实例化，mysqlDao 在 service 之后实例化。
			 * 此时 findAutowireCandidates 方法会返回如下的结果：
			 *   matchingBeans = [ <mongoDao, Object@MongoDao>, <mysqlDao, Class@MySqlDao> ]
			 * 注意 mysqlDao 还未实例化，所以返回的是 MySqlDao.class。
			 *
			 * findAutowireCandidates 这个方法逻辑比较复杂，我简单说一下它的工作流程吧，如下：
			 *   1. 从 BeanFactory 中获取某种类型 bean 的名称，比如上面的配置中mongoDao 和 mysqlDao 均实现了 Dao 接口，所以他们是同一种类型的 bean。
			 *   2. 遍历上一步得到的名称列表，并判断 bean 名称对应的 bean 是否是合适的候选项，若合适则添加到候选列表中，并在最后返回候选列表
			 */
			// 4. 单个依赖查询  重点！！！  查找容器中所有可用依赖：findAutowireCandidates 方法根据类型查找依赖。
			// 3. 正常流程，只匹配一个，先找到所有可用的 matchingBeans
			Map<String, Object> matchingBeans = findAutowireCandidates(beanName, type, descriptor);
			// 4.1 没有查找到依赖，判断descriptor.require
			if (matchingBeans.isEmpty()) {
				if (isRequired(descriptor)) {
					raiseNoMatchingBeanFound(type, descriptor.getResolvableType(), descriptor);
				}
				return null;
			}
			String autowiredBeanName;
			Object instanceCandidate;
			// 4.2 有多个，如何过滤
			// 3.1 匹配到多个，则根据优先级选一个。注意集合类型允许为空
			// 如何有多个依赖怎么处理？其实 Spring 有一套通用的流程，先按 @Primary 查找，再按 @Priority，最后按方法名称或字段名称查找，直到只有一个 bean 为止。相关的匹配规则见 determineAutowireCandidate 方法。
			if (matchingBeans.size() > 1) {
				/*
				 * matchingBeans.size() > 1，则表明存在多个可注入的候选项，
				 * 这里判断使用哪一个候选项。比如下面的配置：
				 *   <bean name="mongoDao" class="xyz.coolblog.autowire.MongoDao" primary="true"/>
				 *   <bean name="mysqlDao" class="xyz.coolblog.autowire.MySqlDao"/>
				 * mongoDao 的配置中存在 primary 属性，所以 mongoDao 会被选为最终的候选项。
				 * 如果两个 bean 配置都没有 primary 属性，则需要根据优先级选择候选项。
				 */
				// 4.2.1 @Primary -> @Priority -> 方法名称或字段名称匹配
				// 3.1.1 根据 Primary 属性或 PriorityOrdered 接口指定优先级
				autowiredBeanName = determineAutowireCandidate(matchingBeans, descriptor);
				// 4.2.2 根据是否必须，抛出异常。注意这里如果是集合处理，则返回null
				if (autowiredBeanName == null) {
					if (isRequired(descriptor) || !indicatesMultipleBeans(type)) {
						// 3.1.2 如果是必需的或非集合类型，那么就根据 DependencyDescriptor 指定的规则选择一个最优的
						return descriptor.resolveNotUnique(descriptor.getResolvableType(), matchingBeans);
					}else {
						// In case of an optional Collection/Map, silently ignore a non-unique case:
						// possibly it was meant to be an empty collection of multiple regular beans
						// (before 4.3 in particular when we didn't even look for collection beans).
						// 3.1.3 集合类型允许为 null
						return null;
					}
				}
				// 根据解析出的 autowiredBeanName，获取相应的候选项
				instanceCandidate = matchingBeans.get(autowiredBeanName);
			}else {  // 3.2 精确匹配一个
				// 只有一个候选项，直接取出来即可
				// We have exactly one match.
				Map.Entry<String, Object> entry = matchingBeans.entrySet().iterator().next();
				autowiredBeanName = entry.getKey();
				instanceCandidate = entry.getValue();
			}
			// 4.3 到了这，说明有且仅有命中一个 ，从容器获取真实的 bean。descriptor.resolveCandidate 方法根据名称 autowiredBeanName 实例化对象。
			if (autowiredBeanNames != null) {
				autowiredBeanNames.add(autowiredBeanName);
			}
			// 这里是重点！！！ 4.4 实际上调用 getBean(autowiredBeanName, type)。
			if (instanceCandidate instanceof Class) { // A 依赖 B   DependencyDescriptor
				instanceCandidate = descriptor.resolveCandidate(autowiredBeanName, type, this);
			}
			Object result = instanceCandidate;
			// 返回候选项实例，如果实例是 Class 类型，则调用 beanFactory.getBean(String, Class) 获取相应的 bean。否则直接返回即可
			if (result instanceof NullBean) {
				if (isRequired(descriptor)) {
					raiseNoMatchingBeanFound(type, descriptor.getResolvableType(), descriptor);
				}
				result = null;
			}
			if (!ClassUtils.isAssignableValue(type, result)) {
				throw new BeanNotOfRequiredTypeException(autowiredBeanName, type, instanceCandidate.getClass());
			}
			return result;
		}finally {
			ConstructorResolver.setCurrentInjectionPoint(previousInjectionPoint);
		}
	}

	@Nullable
	private Object resolveMultipleBeans(DependencyDescriptor descriptor, @Nullable String beanName, @Nullable Set<String> autowiredBeanNames, @Nullable TypeConverter typeConverter) {
		final Class<?> type = descriptor.getDependencyType();
		if (descriptor instanceof StreamDependencyDescriptor) {
			Map<String, Object> matchingBeans = findAutowireCandidates(beanName, type, descriptor);
			if (autowiredBeanNames != null) {
				autowiredBeanNames.addAll(matchingBeans.keySet());
			}
			Stream<Object> stream = matchingBeans.keySet().stream()
					.map(name -> descriptor.resolveCandidate(name, type, this))
					.filter(bean -> !(bean instanceof NullBean));
			if (((StreamDependencyDescriptor) descriptor).isOrdered()) {
				stream = stream.sorted(adaptOrderComparator(matchingBeans));
			}
			return stream;
		}else if (type.isArray()) { // 处理数组类型
			Class<?> componentType = type.getComponentType();
			ResolvableType resolvableType = descriptor.getResolvableType();
			Class<?> resolvedArrayType = resolvableType.resolve(type);
			if (resolvedArrayType != type) {
				componentType = resolvableType.getComponentType().resolve();
			}
			if (componentType == null) return null;
			Map<String, Object> matchingBeans = findAutowireCandidates(beanName, componentType,new MultiElementDescriptor(descriptor));
			if (matchingBeans.isEmpty()) return null;
			if (autowiredBeanNames != null) {
				autowiredBeanNames.addAll(matchingBeans.keySet());
			}
			TypeConverter converter = (typeConverter != null ? typeConverter : getTypeConverter());
			Object result = converter.convertIfNecessary(matchingBeans.values(), resolvedArrayType);
			if (result instanceof Object[]) {
				Comparator<Object> comparator = adaptDependencyComparator(matchingBeans);
				if (comparator != null) {
					Arrays.sort((Object[]) result, comparator);
				}
			}
			return result;
		}else if (Collection.class.isAssignableFrom(type) && type.isInterface()) { //  处理集合类型  Set  List
			Class<?> elementType = descriptor.getResolvableType().asCollection().resolveGeneric();
			if (elementType == null) return null;
			Map<String, Object> matchingBeans = findAutowireCandidates(beanName, elementType,new MultiElementDescriptor(descriptor));
			if (matchingBeans.isEmpty()) return null;
			if (autowiredBeanNames != null) {
				autowiredBeanNames.addAll(matchingBeans.keySet());
			}
			TypeConverter converter = (typeConverter != null ? typeConverter : getTypeConverter());
			Object result = converter.convertIfNecessary(matchingBeans.values(), type);
			if (result instanceof List) {
				Comparator<Object> comparator = adaptDependencyComparator(matchingBeans);
				if (comparator != null) {
					((List<?>) result).sort(comparator);
				}
			}
			return result;
		}else if (Map.class == type) {  //  处理Map类型
			ResolvableType mapType = descriptor.getResolvableType().asMap();
			Class<?> keyType = mapType.resolveGeneric(0);
			if (String.class != keyType) {
				return null;
			}
			Class<?> valueType = mapType.resolveGeneric(1);
			if (valueType == null) return null;
			Map<String, Object> matchingBeans = findAutowireCandidates(beanName, valueType,new MultiElementDescriptor(descriptor));
			if (matchingBeans.isEmpty()) return null;
			if (autowiredBeanNames != null) {
				autowiredBeanNames.addAll(matchingBeans.keySet());
			}
			return matchingBeans;
		}else {
			return null;
		}
	}

	private boolean isRequired(DependencyDescriptor descriptor) {
		return getAutowireCandidateResolver().isRequired(descriptor);
	}

	private boolean indicatesMultipleBeans(Class<?> type) {
		return (type.isArray() || (type.isInterface() && (Collection.class.isAssignableFrom(type) || Map.class.isAssignableFrom(type))));
	}

	@Nullable
	private Comparator<Object> adaptDependencyComparator(Map<String, ?> matchingBeans) {
		Comparator<Object> comparator = getDependencyComparator();
		if (comparator instanceof OrderComparator) {
			return ((OrderComparator) comparator).withSourceProvider(createFactoryAwareOrderSourceProvider(matchingBeans));
		}else {
			return comparator;
		}
	}

	private Comparator<Object> adaptOrderComparator(Map<String, ?> matchingBeans) {
		Comparator<Object> dependencyComparator = getDependencyComparator();
		OrderComparator comparator = (dependencyComparator instanceof OrderComparator ?	(OrderComparator) dependencyComparator : OrderComparator.INSTANCE);
		return comparator.withSourceProvider(createFactoryAwareOrderSourceProvider(matchingBeans));
	}

	private OrderComparator.OrderSourceProvider createFactoryAwareOrderSourceProvider(Map<String, ?> beans) {
		IdentityHashMap<Object, String> instancesToBeanNames = new IdentityHashMap<>();
		beans.forEach((beanName, instance) -> instancesToBeanNames.put(instance, beanName));
		return new FactoryAwareOrderSourceProvider(instancesToBeanNames);
	}

	/**
	 * 真正在 Spring IoC 容器中进行依赖查找，依赖查找的来源有三：①内部对象 ②托管Bean ③BeanDefinition。
	 * 最后如果无法查找到依赖对象，会进行一些补偿机制，想方设法获取注入的对象，如泛型补偿，自引用补偿。
	 * Find bean instances that match the required type. Called during autowiring for the specified bean.
	 * @param beanName the name of the bean that is about to be wired
	 * @param requiredType the actual type of bean to look for (may be an array component type or collection element type) 待寻找的属性类型，例如：org.springframework.core.io.Resource  List<Resource> xxxx
	 * @param descriptor the descriptor of the dependency to resolve
	 * @return a Map of candidate names and candidate instances that match the required type (never {@code null})
	 * @throws BeansException in case of errors
	 * @see #autowireByType
	 * @see #autowireConstructor
	 * 从 findAutowireCandidates 方法，我们可以看到 Spring IoC 依赖注入的来源：
	 * 1.先查找 Spring IoC 内部依赖 resolvableDependencies。
	 * 		在 AbstractApplicationContext#prepareBeanFactory 方法中默认设置了如下内部依赖：BeanFactory、ResourceLoader、ApplicationEventPublisher、ApplicationContext。
	 * 2.在父子容器进行类型查找：
	 * 		查找类型匹配的 beanNames，beanFactory#beanNamesForType 方法根据类型查找是，先匹配单例实例类型（包括 Spring 托管 Bean）， 再匹配 BeanDefinition 的类型。
	 *		从这一步，我们可以看到 Spring 依赖注入的另外两个来源：一是 Spring 托管的外部 Bean，二是 Spring BeanDefinition。
	 */
	protected Map<String, Object> findAutowireCandidates(@Nullable String beanName, Class<?> requiredType, DependencyDescriptor descriptor) {
		// 1. 根据类型查找父子容器中所有可用的 beanName，调用 getBeanNamesForType 方法。注意 getBeanNamesForType 方法会过滤别名的情况
		String[] candidateNames = BeanFactoryUtils.beanNamesForTypeIncludingAncestors(this, requiredType, true, descriptor.isEager());
		Map<String, Object> result = new LinkedHashMap<>(candidateNames.length);
		// 1. Spring IoC 内部依赖 resolvableDependencies  // 2. 先查找缓存中 requiredType 的依赖，ok
		for (Map.Entry<Class<?>, Object> classObjectEntry : resolvableDependencies.entrySet()) {
			Class<?> autowiringType = classObjectEntry.getKey();
			if (autowiringType.isAssignableFrom(requiredType)) {
				Object autowiringValue = classObjectEntry.getValue();
				autowiringValue = AutowireUtils.resolveAutowiringValue(autowiringValue, requiredType);
				if (requiredType.isInstance(autowiringValue)) {
					result.put(ObjectUtils.identityToString(autowiringValue), autowiringValue);
					break;
				}
			}
		}
		// 2. 类型查找：本质上递归调用beanFactory#beanNamesForType。先匹配实例类型，再匹配bd。
		// 3. 如果是非循环引用并且是合法的，ok
		for (String candidate : candidateNames) {
			// 所谓的循环引用是指 candidateName 实例的工厂就是 beanName 或就是本身
			// 2.1 isSelfReference说明beanName和candidate本质是同一个对象，isAutowireCandidate进一步匹配bd.autowireCandidate、泛型、@@Qualifier等进行过滤
			if (!isSelfReference(beanName, candidate) && isAutowireCandidate(candidate, descriptor)) {
				// 2.2 添加到候选对象中
				addCandidateEntry(result, candidate, descriptor, requiredType);
			}
		}

		// 3. 补偿机制：如果依赖查找无法匹配，怎么办？包含泛型补偿和自身引用补偿两种。
		// 4. 如果没有找到，有两种解决方案：一是回退操作，如 @Autowire 回退到名称查找，二是非集合类型考虑循环引用
		if (result.isEmpty()) {
			// 判断要注入的类型是 Array、Map、Collection
			boolean multiple = indicatesMultipleBeans(requiredType);
			// 3.1 fallbackDescriptor: 泛型补偿，实际上是允许注入对象类型的泛型存在无法解析的情况
			// Consider fallback matches if the first pass failed to find anything...
			// 4.1 先执行回退操作
			DependencyDescriptor fallbackDescriptor = descriptor.forFallbackMatch();
			// 3.2 补偿1：不允许自称依赖，但如果是集合依赖，需要过滤非@Qualifier对象。什么场景？
			// 4.2 再考虑循环引用，但在集合类型中不允许循环引用自己
			for (String candidate : candidateNames) {
				if (!isSelfReference(beanName, candidate) && isAutowireCandidate(candidate, fallbackDescriptor) && (!multiple || getAutowireCandidateResolver().hasQualifier(descriptor))) {
					addCandidateEntry(result, candidate, descriptor, requiredType);
				}
			}
			// 3.3 补偿2：允许自称依赖，但如果是集合依赖，注入的集合依赖中需要过滤自己
			if (result.isEmpty() && !multiple) {
				// Consider self references as a final pass...  but in the case of a dependency collection, not the very same bean itself.
				for (String candidate : candidateNames) {
					if (isSelfReference(beanName, candidate) && (!(descriptor instanceof MultiElementDescriptor) || !beanName.equals(candidate)) && isAutowireCandidate(candidate, fallbackDescriptor)) {
						addCandidateEntry(result, candidate, descriptor, requiredType);
					}
				}
			}
		}
		return result;
	}

	/**
	 * Add an entry to the candidate map: a bean instance if available or just the resolved
	 * type, preventing early bean initialization ahead of primary candidate selection.
	 */
	private void addCandidateEntry(Map<String, Object> candidates, String candidateName,DependencyDescriptor descriptor, Class<?> requiredType) {
		// 1. 集合依赖，直接调用 getName(candidateName) 实例化
		if (descriptor instanceof MultiElementDescriptor) {
			Object beanInstance = descriptor.resolveCandidate(candidateName, requiredType, this);
			if (!(beanInstance instanceof NullBean)) {
				candidates.put(candidateName, beanInstance);
			}
		}else if (containsSingleton(candidateName) || (descriptor instanceof StreamDependencyDescriptor && ((StreamDependencyDescriptor) descriptor).isOrdered())) {
			// 2. 已经实例化，直接返回实例对象
			Object beanInstance = descriptor.resolveCandidate(candidateName, requiredType, this);
			candidates.put(candidateName, (beanInstance instanceof NullBean ? null : beanInstance));
		}else {
			// 3. 只获取candidateName的类型，真正需要注入时才实例化对象
			candidates.put(candidateName, getType(candidateName));
		}
	}

	/**
	 * Determine the autowire candidate in the given set of beans.
	 * Looks for {@code @Primary} and {@code @Priority} (in that order).
	 * @param candidates a Map of candidate names and candidate instances that match the required type, as returned by {@link #findAutowireCandidates}
	 * @param descriptor the target dependency to match against
	 * @return the name of the autowire candidate, or {@code null} if none found
	 */
	@Nullable
	protected String determineAutowireCandidate(Map<String, Object> candidates, DependencyDescriptor descriptor) {
		Class<?> requiredType = descriptor.getDependencyType();
		// 检测 符合  @Primary 注解
		String primaryCandidate = determinePrimaryCandidate(candidates, requiredType);
		if (primaryCandidate != null) {
			return primaryCandidate;
		}
		// 检测 符合 @Priority 注解
		String priorityCandidate = determineHighestPriorityCandidate(candidates, requiredType);
		if (priorityCandidate != null) {
			return priorityCandidate;
		}
		// Fallback
		// 如果上面两种方法都没能匹配到，则根据变量名称再次进行匹配 （就是所谓的 byName）
		for (Map.Entry<String, Object> entry : candidates.entrySet()) {
			String candidateName = entry.getKey();
			Object beanInstance = entry.getValue();
			if ((beanInstance != null && resolvableDependencies.containsValue(beanInstance)) || matchesBeanName(candidateName, descriptor.getDependencyName())) {
				return candidateName;
			}
		}
		return null;
	}

	/**
	 * Determine the primary candidate in the given set of beans.
	 * @param candidates a Map of candidate names and candidate instances (or candidate classes if not created yet) that match the required type
	 * @param requiredType the target dependency type to match against
	 * @return the name of the primary candidate, or {@code null} if none found
	 * @see #isPrimary(String, Object)
	 */
	@Nullable
	protected String determinePrimaryCandidate(Map<String, Object> candidates, Class<?> requiredType) {
		String primaryBeanName = null;
		for (Map.Entry<String, Object> entry : candidates.entrySet()) {
			String candidateBeanName = entry.getKey();
			Object beanInstance = entry.getValue();
			if (isPrimary(candidateBeanName, beanInstance)) {
				if (primaryBeanName != null) {
					boolean candidateLocal = containsBeanDefinition(candidateBeanName);
					boolean primaryLocal = containsBeanDefinition(primaryBeanName);
					if (candidateLocal && primaryLocal) {
						throw new NoUniqueBeanDefinitionException(requiredType, candidates.size(),"more than one 'primary' bean found among candidates: " + candidates.keySet());
					}else if (candidateLocal) {
						primaryBeanName = candidateBeanName;
					}
				}else {
					primaryBeanName = candidateBeanName;
				}
			}
		}
		return primaryBeanName;
	}

	/**
	 * Determine the candidate with the highest priority in the given set of beans.
	 * Based on {@code @javax.annotation.Priority}. As defined by the related {@link org.springframework.core.Ordered} interface, the lowest value has  the highest priority.
	 * @param candidates a Map of candidate names and candidate instances (or candidate classes if not created yet) that match the required type
	 * @param requiredType the target dependency type to match against
	 * @return the name of the candidate with the highest priority, or {@code null} if none found
	 * @see #getPriority(Object)
	 */
	@Nullable
	protected String determineHighestPriorityCandidate(Map<String, Object> candidates, Class<?> requiredType) {
		String highestPriorityBeanName = null;
		Integer highestPriority = null;
		for (Map.Entry<String, Object> entry : candidates.entrySet()) {
			String candidateBeanName = entry.getKey();
			Object beanInstance = entry.getValue();
			if (beanInstance != null) {
				Integer candidatePriority = getPriority(beanInstance);
				if (candidatePriority != null) {
					if (highestPriorityBeanName != null) {
						if (candidatePriority.equals(highestPriority)) {
							throw new NoUniqueBeanDefinitionException(requiredType, candidates.size(),"Multiple beans found with the same priority ('" + highestPriority + "') among candidates: " + candidates.keySet());
						}else if (candidatePriority < highestPriority) {
							highestPriorityBeanName = candidateBeanName;
							highestPriority = candidatePriority;
						}
					}else {
						highestPriorityBeanName = candidateBeanName;
						highestPriority = candidatePriority;
					}
				}
			}
		}
		return highestPriorityBeanName;
	}

	/**
	 * Return whether the bean definition for the given bean name has been marked as a primary bean.
	 * @param beanName the name of the bean
	 * @param beanInstance the corresponding bean instance (can be null)
	 * @return whether the given bean qualifies as primary
	 */
	protected boolean isPrimary(String beanName, Object beanInstance) {
		if (containsBeanDefinition(beanName)) {
			return getMergedLocalBeanDefinition(beanName).isPrimary();
		}
		BeanFactory parent = getParentBeanFactory();
		return (parent instanceof DefaultListableBeanFactory && ((DefaultListableBeanFactory) parent).isPrimary(beanName, beanInstance));
	}

	/**
	 * Return the priority assigned for the given bean instance by the {@code javax.annotation.Priority} annotation.
	 * The default implementation delegates to the specified {@link #setDependencyComparator dependency comparator},
	 * checking its {@link OrderComparator#getPriority method} if it is an extension of
	 * Spring's common {@link OrderComparator} - typically, an {@link org.springframework.core.annotation.AnnotationAwareOrderComparator}.
	 * If no such comparator is present, this implementation returns {@code null}.
	 * @param beanInstance the bean instance to check (can be {@code null})
	 * @return the priority assigned to that bean or {@code null} if none is set
	 */
	@Nullable
	protected Integer getPriority(Object beanInstance) {
		Comparator<Object> comparator = getDependencyComparator();
		if (comparator instanceof OrderComparator) {
			return ((OrderComparator) comparator).getPriority(beanInstance);
		}
		return null;
	}

	// Determine whether the given candidate name matches the bean name or the aliases stored in this bean definition.
	protected boolean matchesBeanName(String beanName, @Nullable String candidateName) {
		return (candidateName != null && (candidateName.equals(beanName) || ObjectUtils.containsElement(getAliases(beanName), candidateName)));
	}

	/**
	 * Determine whether the given beanName/candidateName pair indicates a self reference,
	 * i.e. whether the candidate points back to the original bean or to a factory method on the original bean.
	 */
	private boolean isSelfReference(@Nullable String beanName, @Nullable String candidateName) {
		return (beanName != null && candidateName != null && (beanName.equals(candidateName) || (containsBeanDefinition(candidateName) &&
						beanName.equals(getMergedLocalBeanDefinition(candidateName).getFactoryBeanName()))));
	}

	//  Raise a NoSuchBeanDefinitionException or BeanNotOfRequiredTypeException  for an unresolvable dependency.
	private void raiseNoMatchingBeanFound(Class<?> type, ResolvableType resolvableType, DependencyDescriptor descriptor) throws BeansException {
		checkBeanNotOfRequiredType(type, descriptor);
		throw new NoSuchBeanDefinitionException(resolvableType,"expected at least 1 bean which qualifies as autowire candidate. Dependency annotations: "+ ObjectUtils.nullSafeToString(descriptor.getAnnotations()));
	}

	/**
	 * Raise a BeanNotOfRequiredTypeException for an unresolvable dependency, if applicable,
	 * i.e. if the target type of the bean would match but an exposed proxy doesn't.
	 */
	private void checkBeanNotOfRequiredType(Class<?> type, DependencyDescriptor descriptor) {
		for (String beanName : beanDefinitionNames) {
			RootBeanDefinition mbd = getMergedLocalBeanDefinition(beanName);
			Class<?> targetType = mbd.getTargetType();
			if (targetType != null && type.isAssignableFrom(targetType) && isAutowireCandidate(beanName, mbd, descriptor, getAutowireCandidateResolver())) {
				// Probably a proxy interfering with target type match -> throw meaningful exception.
				Object beanInstance = getSingleton(beanName, false);
				Class<?> beanType = (beanInstance != null && beanInstance.getClass() != NullBean.class ? beanInstance.getClass() : predictBeanType(beanName, mbd));
				if (beanType != null && !type.isAssignableFrom(beanType)) {
					throw new BeanNotOfRequiredTypeException(beanName, type, beanType);
				}
			}
		}
		BeanFactory parent = getParentBeanFactory();
		if (parent instanceof DefaultListableBeanFactory) {
			((DefaultListableBeanFactory) parent).checkBeanNotOfRequiredType(type, descriptor);
		}
	}

	// Create an {@link Optional} wrapper for the specified dependency.
	private Optional<?> createOptionalDependency(DependencyDescriptor descriptor, @Nullable String beanName, final Object... args) {
		DependencyDescriptor descriptorToUse = new NestedDependencyDescriptor(descriptor) {
			@Override
			public boolean isRequired() {
				return false;
			}
			@Override
			public Object resolveCandidate(String beanName, Class<?> requiredType, BeanFactory beanFactory) {
				return (!ObjectUtils.isEmpty(args) ? beanFactory.getBean(beanName, args) : super.resolveCandidate(beanName, requiredType, beanFactory));
			}
		};
		Object result = doResolveDependency(descriptorToUse, beanName, null, null);
		return (result instanceof Optional ? (Optional<?>) result : Optional.ofNullable(result));
	}


	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(ObjectUtils.identityToString(this));
		sb.append(": defining beans [");
		sb.append(StringUtils.collectionToCommaDelimitedString(beanDefinitionNames));
		sb.append("]; ");
		BeanFactory parent = getParentBeanFactory();
		if (parent == null) {
			sb.append("root of factory hierarchy");
		}else {
			sb.append("parent: ").append(ObjectUtils.identityToString(parent));
		}
		return sb.toString();
	}

	//---------------------------------------------------------------------
	// Serialization support
	//---------------------------------------------------------------------
	private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
		throw new NotSerializableException("DefaultListableBeanFactory itself is not deserializable - just a SerializedBeanFactoryReference is");
	}

	protected Object writeReplace() throws ObjectStreamException {
		if (serializationId != null) {
			return new SerializedBeanFactoryReference(serializationId);
		}else {
			throw new NotSerializableException("DefaultListableBeanFactory has no serialization id");
		}
	}

	//  Minimal id reference to the factory.  Resolved to the actual factory instance on deserialization.
	private static class SerializedBeanFactoryReference implements Serializable {

		private final String id;

		public SerializedBeanFactoryReference(String id) {
			this.id = id;
		}

		private Object readResolve() {
			Reference<?> ref = serializableFactories.get(id);
			if (ref != null) {
				Object result = ref.get();
				if (result != null) {
					return result;
				}
			}
			// Lenient fallback: dummy factory in case of original factory not found...
			DefaultListableBeanFactory dummyFactory = new DefaultListableBeanFactory();
			dummyFactory.serializationId = id;
			return dummyFactory;
		}
	}

	// A dependency descriptor marker for nested elements.
	private static class NestedDependencyDescriptor extends DependencyDescriptor {
		public NestedDependencyDescriptor(DependencyDescriptor original) {
			super(original);
			increaseNestingLevel();
		}
	}

	// A dependency descriptor for a multi-element declaration with nested elements.
	private static class MultiElementDescriptor extends NestedDependencyDescriptor {
		public MultiElementDescriptor(DependencyDescriptor original) {
			super(original);
		}
	}

	//  A dependency descriptor marker for stream access to multiple elements.
	private static class StreamDependencyDescriptor extends DependencyDescriptor {

		private final boolean ordered;

		public StreamDependencyDescriptor(DependencyDescriptor original, boolean ordered) {
			super(original);
			this.ordered = ordered;
		}

		public boolean isOrdered() {
			return ordered;
		}
	}

	private interface BeanObjectProvider<T> extends ObjectProvider<T>, Serializable {
	}

	// Serializable ObjectFactory/ObjectProvider for lazy resolution of a dependency.
	private class DependencyObjectProvider implements BeanObjectProvider<Object> {

		private final DependencyDescriptor descriptor;

		private final boolean optional;

		@Nullable
		private final String beanName;

		public DependencyObjectProvider(DependencyDescriptor descriptor, @Nullable String beanName) {
			this.descriptor = new NestedDependencyDescriptor(descriptor);
			this.optional = (this.descriptor.getDependencyType() == Optional.class);
			this.beanName = beanName;
		}

		@Override
		public Object getObject() throws BeansException {
			if (optional) {
				return createOptionalDependency(descriptor, beanName);
			}else {
				Object result = doResolveDependency(descriptor, beanName, null, null);
				if (result == null) throw new NoSuchBeanDefinitionException(descriptor.getResolvableType());
				return result;
			}
		}

		@Override
		public Object getObject(final Object... args) throws BeansException {
			if (optional) {
				return createOptionalDependency(descriptor, beanName, args);
			}else {
				DependencyDescriptor descriptorToUse = new DependencyDescriptor(descriptor) {
					@Override
					public Object resolveCandidate(String beanName, Class<?> requiredType, BeanFactory beanFactory) {
						return beanFactory.getBean(beanName, args);
					}
				};
				Object result = doResolveDependency(descriptorToUse, beanName, null, null);
				if (result == null) throw new NoSuchBeanDefinitionException(descriptor.getResolvableType());
				return result;
			}
		}

		@Override
		@Nullable
		public Object getIfAvailable() throws BeansException {
			if (optional) {
				return createOptionalDependency(descriptor, beanName);
			}else {
				DependencyDescriptor descriptorToUse = new DependencyDescriptor(descriptor) {
					@Override
					public boolean isRequired() {
						return false;
					}
				};
				return doResolveDependency(descriptorToUse, beanName, null, null);
			}
		}

		@Override
		@Nullable
		public Object getIfUnique() throws BeansException {
			DependencyDescriptor descriptorToUse = new DependencyDescriptor(descriptor) {
				@Override
				public boolean isRequired() {
					return false;
				}
				@Override
				@Nullable
				public Object resolveNotUnique(ResolvableType type, Map<String, Object> matchingBeans) {
					return null;
				}
			};
			if (optional) {
				return createOptionalDependency(descriptorToUse, beanName);
			}else {
				return doResolveDependency(descriptorToUse, beanName, null, null);
			}
		}

		@Nullable
		protected Object getValue() throws BeansException {
			if (optional) {
				return createOptionalDependency(descriptor, beanName);
			}else {
				return doResolveDependency(descriptor, beanName, null, null);
			}
		}

		@Override
		public Stream<Object> stream() {
			return resolveStream(false);
		}

		@Override
		public Stream<Object> orderedStream() {
			return resolveStream(true);
		}

		@SuppressWarnings("unchecked")
		private Stream<Object> resolveStream(boolean ordered) {
			DependencyDescriptor descriptorToUse = new StreamDependencyDescriptor(descriptor, ordered);
			Object result = doResolveDependency(descriptorToUse, beanName, null, null);
			return (result instanceof Stream ? (Stream<Object>) result : Stream.of(result));
		}
	}


	/**
	 * Separate inner class for avoiding a hard dependency on the {@code javax.inject} API.
	 * Actual {@code javax.inject.Provider} implementation is nested here in order to make it invisible for Graal's introspection of DefaultListableBeanFactory's nested classes.
	 */
	private class Jsr330Factory implements Serializable {

		public Object createDependencyProvider(DependencyDescriptor descriptor, @Nullable String beanName) {
			return new Jsr330Provider(descriptor, beanName);
		}

		private class Jsr330Provider extends DependencyObjectProvider implements Provider<Object> {

			public Jsr330Provider(DependencyDescriptor descriptor, @Nullable String beanName) {
				super(descriptor, beanName);
			}

			@Override
			@Nullable
			public Object get() throws BeansException {
				return getValue();
			}
		}
	}

	/**
	 * An {@link org.springframework.core.OrderComparator.OrderSourceProvider} implementation  that is aware of the bean metadata of the instances to sort.
	 * Lookup for the method factory of an instance to sort, if any, and let the comparator retrieve the {@link org.springframework.core.annotation.Order} value defined on it.
	 * This essentially allows for the following construct:
	 */
	private class FactoryAwareOrderSourceProvider implements OrderComparator.OrderSourceProvider {

		private final Map<Object, String> instancesToBeanNames;

		public FactoryAwareOrderSourceProvider(Map<Object, String> instancesToBeanNames) {
			this.instancesToBeanNames = instancesToBeanNames;
		}
		@Override
		@Nullable
		public Object getOrderSource(Object obj) {
			String beanName = instancesToBeanNames.get(obj);
			if (beanName == null || !containsBeanDefinition(beanName)) {
				return null;
			}
			RootBeanDefinition beanDefinition = getMergedLocalBeanDefinition(beanName);
			List<Object> sources = new ArrayList<>(2);
			Method factoryMethod = beanDefinition.getResolvedFactoryMethod();
			if (factoryMethod != null) {
				sources.add(factoryMethod);
			}
			Class<?> targetType = beanDefinition.getTargetType();
			if (targetType != null && targetType != obj.getClass()) {
				sources.add(targetType);
			}
			return sources.toArray();
		}
	}
}
