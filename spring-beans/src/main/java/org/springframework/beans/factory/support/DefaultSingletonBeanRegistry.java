

package org.springframework.beans.factory.support;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanCreationNotAllowedException;
import org.springframework.beans.factory.BeanCurrentlyInCreationException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.core.SimpleAliasRegistry;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * Generic registry for shared bean instances, implementing the {@link org.springframework.beans.factory.config.SingletonBeanRegistry}.
 * Allows for registering singleton instances that should be shared  for all callers of the registry, to be obtained via bean name.
 *
 * Also supports registration of {@link org.springframework.beans.factory.DisposableBean} instances,
 * (which might or might not correspond to registered singletons),to be destroyed on shutdown of the registry.
 * Dependencies between beans can be registered to enforce an appropriate shutdown order.
 *
 * This class mainly serves as base class for {@link org.springframework.beans.factory.BeanFactory} implementations, factoring out the common management of singleton bean instances.
 * Note that the {@link org.springframework.beans.factory.config.ConfigurableBeanFactory} interface extends the {@link SingletonBeanRegistry} interface.
 * Note that this class assumes neither a bean definition concept nor a specific creation process for bean instances,
 * in contrast to {@link AbstractBeanFactory} and {@link DefaultListableBeanFactory} (which inherit from it).
 * Can alternatively also be used as a nested helper to delegate to.
 * @since 2.0
 * @see #registerSingleton
 * @see #registerDisposableBean
 * @see org.springframework.beans.factory.DisposableBean
 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory
 * 同SimpleAliasRegistry，这里也是用Map来做缓存。但是单例bean的注册要来的复杂，因为bean还涉及到初始化的问题，因此这里有多个缓存用的对象
 *
 * 关于 3个缓存池的解释：
 * singletonFactories：保存对象的BeanName和创建bean的工厂AbstractAutowireCapableBeanFactory(ObjectFactory)，（对象的构造函数是在这一步完成的）
 * earlySingletonObjects：保存对象BeanName和对象的早期实例（ObjectFactory#getObject得到的对象）（此时对象还没注入属性），此时可以作为对象填充依赖。
 * singletonObjects：保存BeanName和bean的实例（此时对象已经完成了属性注入）
 */
public class DefaultSingletonBeanRegistry extends SimpleAliasRegistry implements SingletonBeanRegistry {

	/**
	 * 【一级缓存】
	 * Cache of singleton objects: bean name to bean instance.
	 * 缓存beanName和bean实例 key-->beanName,value-->beanInstance
	 * 单例bean缓存池 用于存放已注册的SingleBean实例
	 * 用于存放完全初始化好的 bean，从该缓存中取出的 bean 可以直接使用
	 */
	private final Map<String, Object> singletonObjects = new ConcurrentHashMap<>(256);

	/**
	 *  【二级缓存】
	 * Cache of early singleton objects: bean name to bean instance.早期的单例对象(对象属性还没有进行赋值)  纯净态
	 * 存储bean名称和预加载bean实例 映射关系
	 * 缓存beanName和bean实例 key-->beanName,value-->beanInstance 该缓存主要为了解决bean的循环依赖引用
	 * 提前曝光的单例对象的cache，存放原始的 bean 对象（尚未填充属性），主要用于解决 一般清下的循环依赖
	 */
	private final Map<String, Object> earlySingletonObjects = new HashMap<>(16);

	/**
	 * 【三级缓存】
	 * Cache of singleton factories: bean name to ObjectFactory. 单例对应的工厂缓存，可以使用工厂来创建单例对象 bean name --> ObjectFactory
	 * 用于存放bean工厂  bean 工厂所产生的 bean 是还未完成初始化的 bean   如代码所示，bean 工厂所生成的对象最终会被缓存到 earlySingletonObjects 中
	 * 缓存beanName和beanFactory key-->beanName,value-->beanFactory
	 * 单例对象工厂的cache，存放 bean 工厂对象，主要用于解决  基于AOP代理的循环依赖
	 */
	private final Map<String, ObjectFactory<?>> singletonFactories = new HashMap<>(16);

	/**
	 * Set of registered singletons, containing the bean names in registration order. 已经注册过了的单例对象
	 * 缓存所有已注册的SingleBean对象的名称
	*/
	private final Set<String> registeredSingletons = new LinkedHashSet<>(256);

	/**
	 * Names of beans that are currently in creation.
	 * 存储当前正在创建的单例对象集合
	 * 作用是解决单例对象只会创建一次，当创建一个单例对象的时候会向singletonsCurrentlyInCreation添加beanName,
	 * 另外一个线程创建的时候，也会添加beanname到singletonsCurrentlyInCreation，add方法返回false就报异常
	 * 这个缓存也十分重要：它表示bean创建过程中都会在里面呆着~ 它在Bean开始创建时放值，创建完成时会将其移出~
	 */
	private final Set<String> singletonsCurrentlyInCreation = Collections.newSetFromMap(new ConcurrentHashMap<>(16));

	/**
	 *  Names of beans currently excluded from in creation checks.
	 * 需要排除的单例，什么意思呢？在创建单例的时候，如果该单例正在创建，就不会再创建了，就应该排除掉，如果某个单例在该集合中，则表示该单例正在创建
	 * inCreationCheckExclusions存在的beanName，可以并发创建。
	 * 当这个Bean被创建完成后，会标记为这个 注意：这里是set集合 不会重复，至少被创建了一次的  都会放进这里~~~~
	 */
	private final Set<String> inCreationCheckExclusions = Collections.newSetFromMap(new ConcurrentHashMap<>(16));

	/**
	 * List of suppressed Exceptions, available for associating related causes.
	 * 用来存储异常
	 * suppressedExceptions 作用当从ObjectFactory获得对象时出现异常，把suppressedExceptions的异常一并抛出。作用不大。
	 */
	@Nullable
	private Set<Exception> suppressedExceptions;

	/**
	 * Flag that indicates whether we're currently within destroySingletons.
	 * 当前是否有singleton被销毁
	 * 作用是当AbstractApplicationContext 销毁的时候，会销毁beanFactory里面所有单例对象。
	 * 销毁所有单例对象的时候，singletonsCurrentlyInDestruction设为true
	 * 在getSingleton的时候，识别singletonsCurrentylInDestruction就拒绝获得bean，并报异常。
	 */
	private boolean singletonsCurrentlyInDestruction = false;

	/** Disposable bean instances: bean name to disposable instance.
	 * bean对应的DisposableBean， DisposableBean接口有一个destroy()。为bean指定DisposableBean,作用类似于设置destroy-method
	 * 保存需要销毁的beans。 存储bean名称和Disposable接口实现bean实例 映射关系
	 */
	private final Map<String, Object> disposableBeans = new LinkedHashMap<>();

	/** Map between containing bean names: bean name to Set of bean names that the bean contains. bean包含关系的缓存：bean name  对应 该bean包含的所有bean的name */
	private final Map<String, Set<String>> containedBeanMap = new ConcurrentHashMap<>(16);

	/** Map between dependent bean names: bean name to Set of dependent bean names. bean依赖关系的缓存：bean name 对应 依赖于该bean的所有bean的name (value中bean要先于key表示的bean被销毁)*/
	private final Map<String, Set<String>> dependentBeanMap = new ConcurrentHashMap<>(64);

	/** Map between depending bean names: bean name to Set of bean names for the bean's dependencies. bean依赖关系的缓存：bean name 对应 该bean依赖的所有bean的name */
	private final Map<String, Set<String>> dependenciesForBeanMap = new ConcurrentHashMap<>(64);

	/**
	 * Add the given singleton object to the singleton cache of this factory. To be called for eager registration of singletons.
	 * @param beanName the name of the bean
	 * @param singletonObject the singleton object
	 */
	protected void addSingleton(String beanName, Object singletonObject) {
		synchronized (singletonObjects) {
			// 将动态代理后的bean 或是正常的非代理的单例bean， 存入一级单例缓存池中  全局唯一入口
			logger.warn("【IOC容器 添加 singletonObjects 一级单例缓冲池 全局唯一入口！ 】 beanName： " + beanName);
			singletonObjects.put(beanName, singletonObject);
			// bean一旦存入一级缓存，则要将其从二三级缓存中清除。
			singletonFactories.remove(beanName);
			earlySingletonObjects.remove(beanName);
			/**
			 * 向已经注册的单例集合中添加该实例 ，用来记录已经处理过的bean。
			 * @see DefaultSingletonBeanRegistry#getSingletonNames()  唯一出口
			*/
			registeredSingletons.add(beanName);
		}
	}

	/**
	 * Add the given singleton factory for building the specified singleton if necessary.
	 * To be called for eager registration of singletons, e.g. to be able to resolve circular references.
	 * @param beanName the name of the bean
	 * @param singletonFactory the factory for the singleton object
	 * Spring 在 bean 实例化后就会调用 addSingletonFactory 将这个对象提前暴露到容器中，这们就可以通过 getBean(A) 得到这个对象，即使这个对象仍正在创建。用于解决循环依赖。
	 */
	protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
		Assert.notNull(singletonFactory, "Singleton factory must not be null");
		synchronized (singletonObjects) {
			if (!singletonObjects.containsKey(beanName)) {
				singletonFactories.put(beanName, singletonFactory);
				logger.warn("【IOC容器 添加 singletonFactories 三级单例缓冲池 全局唯一入口！ 】 beanName： " + beanName);
				earlySingletonObjects.remove(beanName);
				registeredSingletons.add(beanName);
			}
		}
	}

	/**
	 *  返回在给定名称下注册的(原始)单例对象。
	 *  检查已经实例化的单例，并允许对当前创建的单例的早期引用(解决循环引用)。
	 * Return the (raw) singleton object registered under the given name.
	 * Checks already instantiated singletons and also allows for an early  reference to a currently created singleton (resolving a circular reference).
	 * @param beanName the name of the bean to look for
	 * @param allowEarlyReference whether early references should be created or not  是否允许从singletonFactories中通过getObject拿到对象 （该参数是为了解决循环引用） 默认为true
	 * @return the registered singleton object, or {@code null} if none found
	 * @see org.springframework.beans.factory.support.DefaultSingletonBeanRegistryTests#testGetSingleton() 【测试用例】
	 */
	@Nullable
	protected Object getSingleton(String beanName, boolean allowEarlyReference) {
		// 1、先从一级缓存池中去获取，如果获取到就直接return。 （有可能是aop获取代理后的对象）
		Object singletonObject = singletonObjects.get(beanName);
		// 如果获取不到并且对象正在创建中，就从二级缓存earlySingletonObjects中获取。（如果获取到就直接return）
		if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
			synchronized (singletonObjects) {
				// 从 earlySingletonObjects 中获取提前曝光的 bean，用于处理循环引用
				singletonObject = earlySingletonObjects.get(beanName);
				// 如果 singletonObject = null，且允许提前曝光 bean 实例，则从相应的 ObjectFactory 获取一个原始的（raw）bean（尚未填充属性）
				// 如果允许创建早期对象,则通过singletionFactory创建
				// 如果还是获取不到，且当前的bean允许被创建早期依赖 singletonFactories（allowEarlyReference=true）通过getObject()获取。 就从三级缓存 singletonFactory.getObject()获取。
				if (singletonObject == null && allowEarlyReference) {
					// getSingleton()从缓存里获取单例对象步骤分析可知，Spring解决循环依赖的诀窍：就在于singletonFactories这个三级缓存。这个Cache里面都是ObjectFactory，它是解决问题的关键。
					ObjectFactory<?> singletonFactory = singletonFactories.get(beanName);
					// （如果获取到了就从singletonFactories中移除，并且放进earlySingletonObjects。其实也就是从三级缓存移动（是剪切、不是复制哦~）到了二级缓存）
					// 加入singletonFactories三级缓存的前提是执行了构造器，所以构造器的循环依赖没法解决
					if (singletonFactory != null) {
						// 通过getObject()方法获取bean,注意:通过此方法获取的bean不是被缓存的
						// 提前曝光 bean 实例，用于解决循环依赖
						singletonObject = singletonFactory.getObject(); // getEarlyBeanReference 获取提前引用
						// 放入缓存中，如果还有其他 bean 依赖当前 bean，其他 bean 可以直接从 earlySingletonObjects 取结果
						// 将获取到的singletonObject缓存至earlySingletonObjects二级缓存
						logger.warn("【IOC容器 添加 earlySingletonObjects 二级单例缓冲池 全局唯一入口！ 】 beanName： " + beanName);
						earlySingletonObjects.put(beanName, singletonObject);
						// 创建成功后，需要从singletonFactories三级缓存中移除
						singletonFactories.remove(beanName);
					}
				}
			}
		}
		return singletonObject;// 返回单例对象
	}

	/**
	 * Return the (raw) singleton object registered under the given name,creating and registering a new one if none registered yet.
	 * 返回以给定名称注册的（原始）singleton对象，如果尚未注册，则创建并注册一个新对象。
	 * @param beanName the name of the bean
	 * @param singletonFactory the ObjectFactory to lazily create the singleton with, if necessary
	 * @return the registered singleton object
	 */
	public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
		Assert.notNull(beanName, "Bean name must not be null");
		synchronized (singletonObjects) {
			// 双重判定从缓存中获取单例 bean，若不为空，则直接返回，不用再初始化
			Object singletonObject = singletonObjects.get(beanName);
			if (singletonObject != null) return singletonObject; // -modify
			if (singletonsCurrentlyInDestruction) {
				throw new BeanCreationNotAllowedException(beanName,"Singleton bean creation not allowed while singletons of this factory are in destruction (Do not request a bean from a BeanFactory in a destroy method implementation!)");
			}
			if (logger.isDebugEnabled()) logger.debug("Creating shared instance of singleton bean '" + beanName + "'");
			// 将这个 beanName 添加到 singletonsCurrentlyInCreation 集合中，用于标记 beanName 对应的 bean 正在创建中
			beforeSingletonCreation(beanName);// 创建前置检查，默认实现是记录当前beanName正在注册中
			boolean newSingleton = false;
			boolean recordSuppressedExceptions = (suppressedExceptions == null);
			if (recordSuppressedExceptions) {
				suppressedExceptions = new LinkedHashSet<>();
			}
			try {
				/**
				 * 	初始化bean  调用签名定义的内部类进行创建，内部调用了createBean(String beanName, RootBeanDefinition mbd, Object[] args)
				 * 	singletonFactory.getObject() 其实是调用上一层函数的 sharedInstance = getSingleton(beanName, () -> 中的  createBean(beanName, mbd, args) 方法
				 * 	因为 ObjectFactory 接口中 只有一个方法，所以说可以直接使用匿名方法
				 * 	通过 getObject 方法调用 createBean 方法创建 bean 实例
				 * @see DefaultSingletonBeanRegistry#getSingleton(java.lang.String, org.springframework.beans.factory.ObjectFactory)
				 */
				singletonObject = singletonFactory.getObject();
				newSingleton = true;
			}catch (IllegalStateException ex) {
				// Has the singleton object implicitly appeared in the meantime -> if yes, proceed with it since the exception indicates that state.
				singletonObject = singletonObjects.get(beanName);
				if (singletonObject == null) throw ex;
			}catch (BeanCreationException ex) {
				if (recordSuppressedExceptions) {
					for (Exception suppressedException : suppressedExceptions) {
						ex.addRelatedCause(suppressedException);
					}
				}
				throw ex;
			}finally {
				if (recordSuppressedExceptions) suppressedExceptions = null;
				/**
				 * 对应前面的 {@link DefaultSingletonBeanRegistry#beforeSingletonCreation(java.lang.String)}
				 * 这里将这个beanName，移动正在创建中
				*/
				afterSingletonCreation(beanName);
			}
			if (newSingleton) {
				// 将上面 singletonFactory.getObject() 创建好的单例bean，存到一级单例缓存池中
				addSingleton(beanName, singletonObject);
			}
			return singletonObject;
		}
	}

	//  Clear all cached singleton instances in this registry. @since 4.3.15
	protected void clearSingletonCache() {
		synchronized (singletonObjects) {
			singletonObjects.clear();
			singletonFactories.clear();
			earlySingletonObjects.clear();
			registeredSingletons.clear();
			singletonsCurrentlyInDestruction = false;
		}
	}

	/**
	 * Destroy the given bean. Delegates to {@code destroyBean} if a corresponding disposable bean instance is found.
	 * 销毁给定的bean。如果找到相应的可释放bean实例，则委托给 destroybean。
	 * @param beanName the name of the bean
	 * @see #destroyBean
	 */
	public void destroySingleton(String beanName) {
		// Remove a registered singleton of the given name, if any.
		removeSingleton(beanName);
		// Destroy the corresponding DisposableBean instance.
		DisposableBean disposableBean;
		synchronized (disposableBeans) {
			disposableBean = (DisposableBean) disposableBeans.remove(beanName);
		}
		destroyBean(beanName, disposableBean);
	}

	/**
	 * 删除一个bean，删除其依赖信息
	 * Destroy the given bean. Must destroy beans that depend on the given bean before the bean itself.Should not throw any exceptions.
	 * @param beanName the name of the bean
	 * @param bean the bean instance to destroy
	 */
	protected void destroyBean(String beanName, @Nullable DisposableBean bean) {
		// Trigger destruction of dependent beans first...
		Set<String> dependencies;
		synchronized (dependentBeanMap) {
			// Within full synchronization in order to guarantee a disconnected Set
			dependencies = dependentBeanMap.remove(beanName);
		}
		if (dependencies != null) {
			if (logger.isTraceEnabled()) logger.trace("Retrieved dependent beans for bean '" + beanName + "': " + dependencies);
			for (String dependentBeanName : dependencies) {
				destroySingleton(dependentBeanName);
			}
		}
		// Actually destroy the bean now...
		if (bean != null) {
			try {
				// 销毁bean  对应代码中的  @Bean(initMethod = "init", destroyMethod = "destroy")  destroy 方法
				bean.destroy();
			}catch (Throwable ex) {
				if (logger.isInfoEnabled()) logger.info("Destroy method on bean with name '" + beanName + "' threw an exception", ex);
			}
		}
		// Trigger destruction of contained beans...
		Set<String> containedBeans;
		synchronized (containedBeanMap) {
			// Within full synchronization in order to guarantee a disconnected Set
			containedBeans = containedBeanMap.remove(beanName);
		}
		if (containedBeans != null) {
			for (String containedBeanName : containedBeans) {
				destroySingleton(containedBeanName);
			}
		}
		// Remove destroyed bean from other beans' dependencies.
		synchronized (dependentBeanMap) {
			for (Iterator<Map.Entry<String, Set<String>>> it = dependentBeanMap.entrySet().iterator(); it.hasNext();) {
				Map.Entry<String, Set<String>> entry = it.next();
				Set<String> dependenciesToClean = entry.getValue();
				dependenciesToClean.remove(beanName);
				if (dependenciesToClean.isEmpty()) it.remove();
			}
		}
		// Remove destroyed bean's prepared dependency information.
		dependenciesForBeanMap.remove(beanName);
	}

	/**
	 * Register an Exception that happened to get suppressed during the creation of a singleton bean instance, e.g. a temporary circular reference resolution problem.
	 * @param ex the Exception to register
	 */
	protected void onSuppressedException(Exception ex) {
		synchronized (singletonObjects) {
			if (suppressedExceptions != null) {
				suppressedExceptions.add(ex);
			}
		}
	}

	/**
	 * Remove the bean with the given name from the singleton cache of this factory,to be able to clean up eager registration of a singleton if creation failed.
	 * 从工厂的singleton缓存中删除具有给定名称的bean，以便在创建失败时清理singleton的早期注册
	 * @param beanName the name of the bean
	 * @see #getSingletonMutex()
	 */
	protected void removeSingleton(String beanName) {
		synchronized (singletonObjects) {
			singletonObjects.remove(beanName);
			singletonFactories.remove(beanName);
			earlySingletonObjects.remove(beanName);
			registeredSingletons.remove(beanName);
		}
	}

	protected boolean isActuallyInCreation(String beanName) {
		return isSingletonCurrentlyInCreation(beanName);
	}

	/**
	 * Return whether the specified singleton bean is currently in creation (within the entire factory).
	 * 返回指定的singleton bean当前是否正在创建中（在整个工厂内）。
	 * 创建中 解释：
	 * isSingletonCurrentlyInCreation 判断对应的单例对象是否正在创建中，当单例对象没有被初始化完全
	 * (例如A定义的构造函数依赖了B对象，得先去创建B对象，或者在populatebean过程中依赖了B对象，得先去创建B对象，此时A处于创建中)
	 * @param beanName the name of the bean
	 * @see DefaultSingletonBeanRegistry#beforeSingletonCreation(java.lang.String) 唯一入口
	 */
	public boolean isSingletonCurrentlyInCreation(String beanName) {
		return singletonsCurrentlyInCreation.contains(beanName);
	}

	/**
	 * Callback before singleton creation. The default implementation register the singleton as currently in creation.
	 * @param beanName the name of the singleton about to be created
	 * @see #isSingletonCurrentlyInCreation
	 */
	protected void beforeSingletonCreation(String beanName) {
		if (!inCreationCheckExclusions.contains(beanName) && !singletonsCurrentlyInCreation.add(beanName)) {
			throw new BeanCurrentlyInCreationException(beanName);
		}
	}

	/**
	 * Callback after singleton creation.The default implementation marks the singleton as not in creation anymore.
	 * @param beanName the name of the singleton that has been created
	 * @see #isSingletonCurrentlyInCreation
	 */
	protected void afterSingletonCreation(String beanName) {
		if (!inCreationCheckExclusions.contains(beanName) && !singletonsCurrentlyInCreation.remove(beanName)) {
			throw new IllegalStateException("Singleton '" + beanName + "' isn't currently in creation");
		}
	}

	/**
	 * Add the given bean to the list of disposable beans in this registry.
	 * Disposable beans usually correspond to registered singletons,matching the bean name but potentially being a different instance
	 * (for example, a DisposableBean adapter for a singleton that does not naturally implement Spring's DisposableBean interface).
	 * @param beanName the name of the bean
	 * @param bean the bean instance
	 */
	public void registerDisposableBean(String beanName, DisposableBean bean) {
		synchronized (disposableBeans) {
			disposableBeans.put(beanName, bean);
		}
	}

	/**
	 * 注册Bean的包含关系，及依赖关系 containedBeanName
	 * Register a containment relationship between two beans, e.g. between an inner bean and its containing outer bean.
	 * Also registers the containing bean as dependent on the contained bean in terms of destruction order.
	 * @param containedBeanName the name of the contained (inner) bean
	 * @param containingBeanName the name of the containing (outer) bean
	 * @see #registerDependentBean
	 */
	public void registerContainedBean(String containedBeanName, String containingBeanName) {
		synchronized (containedBeanMap) {
			Set<String> containedBeans = containedBeanMap.computeIfAbsent(containingBeanName, k -> new LinkedHashSet<>(8));
			if (!containedBeans.add(containedBeanName)) return;
		}
		registerDependentBean(containedBeanName, containingBeanName);
	}

	/**
	 * Determine whether the specified dependent bean has been registered as dependent on the given bean or on any of its transitive dependencies.
	 * 确定指定的依赖bean是否已注册为依赖于给定bean或其任何可传递依赖项
	 * @param beanName the name of the bean to check
	 * @param dependentBeanName the name of the dependent bean
	 * @since 4.0
	 */
	/*
	 * 检测是否存在 depends-on 循环依赖，若存在则抛异常。比如 A 依赖 B，B 又依赖 A，他们的配置如下：
	 *   <bean id="beanA" class="BeanA" depends-on="beanB">
	 *   <bean id="beanB" class="BeanB" depends-on="beanA">
	 * beanA 要求 beanB 在其之前被创建，但 beanB 又要求 beanA 先于它创建。
	 * 这个时候形成了循环，对于 depends-on 循环，Spring 会直接抛出异常
	 */
	protected boolean isDependent(String beanName, String dependentBeanName) {
		synchronized (dependentBeanMap) {
			return isDependent(beanName, dependentBeanName, null);
		}
	}

	// 返回denpendentBean是否为bean的依赖bean
	private boolean isDependent(String beanName, String dependentBeanName, @Nullable Set<String> alreadySeen) {
		if (alreadySeen != null && alreadySeen.contains(beanName)) return false;
		String canonicalName = canonicalName(beanName);
		Set<String> dependentBeans = dependentBeanMap.get(canonicalName);
		if (dependentBeans == null) return false;
		if (dependentBeans.contains(dependentBeanName)) return true;
		for (String transitiveDependency : dependentBeans) {
			if (alreadySeen == null) alreadySeen = new HashSet<>();
			alreadySeen.add(beanName);
			if (isDependent(transitiveDependency, dependentBeanName, alreadySeen)) return true;
		}
		return false;
	}

	// Determine whether a dependent bean has been registered for the given name.  @param beanName the name of the bean to check
	protected boolean hasDependentBean(String beanName) {
		return dependentBeanMap.containsKey(beanName);
	}

	public void setCurrentlyInCreation(String beanName, boolean inCreation) {
		Assert.notNull(beanName, "Bean name must not be null");
		if (!inCreation) {
			logger.warn("【IOC容器中 添加 inCreationCheckExclusions 创建中排除校检   ---  beanName： " + beanName);
			inCreationCheckExclusions.add(beanName);
		}else {
			inCreationCheckExclusions.remove(beanName);
		}
	}

	public boolean isCurrentlyInCreation(String beanName) {
		Assert.notNull(beanName, "Bean name must not be null");
		return (!inCreationCheckExclusions.contains(beanName) && isActuallyInCreation(beanName));
	}

	/**
	 * Register a dependent bean for the given bean, to be destroyed before the given bean is destroyed.
	 * 为给定bean注册一个依赖bean，在销毁给定bean之前将其销毁
	 * @param beanName the name of the bean
	 * @param dependentBeanName the name of the dependent bean
	 */
	public void registerDependentBean(String beanName, String dependentBeanName) {
		String canonicalName = canonicalName(beanName);
		synchronized (dependentBeanMap) {
			Set<String> dependentBeans = dependentBeanMap.computeIfAbsent(canonicalName, k -> new LinkedHashSet<>(8));
			if (!dependentBeans.add(dependentBeanName)) return;
		}
		synchronized (dependenciesForBeanMap) {
			Set<String> dependenciesForBean = dependenciesForBeanMap.computeIfAbsent(dependentBeanName, k -> new LinkedHashSet<>(8));
			dependenciesForBean.add(canonicalName);
		}
	}

	/**
	 * 返回bean的所有dependentBean
	 * Return the names of all beans which depend on the specified bean, if any.
	 * @param beanName the name of the bean
	 * @return the array of dependent bean names, or an empty array if none
	 */
	public String[] getDependentBeans(String beanName) {
		Set<String> dependentBeans = dependentBeanMap.get(beanName);
		if (dependentBeans == null) return new String[0];
		synchronized (dependentBeanMap) {
			return StringUtils.toStringArray(dependentBeans);
		}
	}

	/**
	 * Return the names of all beans that the specified bean depends on, if any.
	 * @param beanName the name of the bean
	 * @return the array of names of beans which the bean depends on,or an empty array if none
	 */
	public String[] getDependenciesForBean(String beanName) {
		Set<String> dependenciesForBean = dependenciesForBeanMap.get(beanName);
		if (dependenciesForBean == null) return new String[0];
		synchronized (dependenciesForBeanMap) {
			return StringUtils.toStringArray(dependenciesForBean);
		}
	}

	public void destroySingletons() {
		if (logger.isTraceEnabled()) logger.trace("Destroying singletons in " + this);
		synchronized (singletonObjects) {
			singletonsCurrentlyInDestruction = true;
		}
		String[] disposableBeanNames;
		synchronized (disposableBeans) {
			disposableBeanNames = StringUtils.toStringArray(disposableBeans.keySet());
		}
		for (int i = disposableBeanNames.length - 1; i >= 0; i--) {
			destroySingleton(disposableBeanNames[i]);
		}
		// bean销毁后 清空所有相关map集合
		containedBeanMap.clear();
		dependentBeanMap.clear();
		dependenciesForBeanMap.clear();
		clearSingletonCache();
	}

	//---------------------------------------------------------------------
	// Implementation of 【SingletonBeanRegistry】 interface
	//---------------------------------------------------------------------
	/**
	 * Exposes the singleton mutex to subclasses and external collaborators.
	 * Subclasses should synchronize on the given Object if they perform any sort of extended singleton creation phase.
	 * In particular, subclasses should <i>not</i> have their own mutexes involved in singleton creation,to avoid the potential for deadlocks in lazy-init situations.
	 * @see org.springframework.beans.factory.support.DefaultSingletonBeanRegistryTests#testGetSingletonMutex() 【测试用例】
	 */
	@Override
	public final Object getSingletonMutex() {
		return singletonObjects;
	}

	@Override
	public boolean containsSingleton(String beanName) {
		return singletonObjects.containsKey(beanName);
	}

	// 获取所有单例的name
	@Override
	public String[] getSingletonNames() {
		synchronized (singletonObjects) {
			return StringUtils.toStringArray(registeredSingletons);
		}
	}

	// 获取所有已经注册了的单例的个数
	@Override
	public int getSingletonCount() {
		synchronized (singletonObjects) {
			return registeredSingletons.size();
		}
	}

	@Override
	public void registerSingleton(String beanName, Object singletonObject) throws IllegalStateException {
		// 由于 singletonObjects 是 ConcurrentHashMap (Neither the key nor the value can be null.) 所以key和value都需要校检非null
		Assert.notNull(beanName, "Bean name must not be null");
		Assert.notNull(singletonObject, "Singleton object must not be null");
		// 加锁： doit 为什么用 ConcurrentHashMap 了还需要加锁？
		synchronized (singletonObjects) {
			// 不允许key重复 不使用 singletonObjects.containsKey(beanName) 是因为需要使用 oldObject 进行异常内容提示
			Object oldObject = singletonObjects.get(beanName);
			if (oldObject != null) throw new IllegalStateException("Could not register object [" + singletonObject + "] under bean name '" + beanName + "': there is already object [" + oldObject + "] bound");
			addSingleton(beanName, singletonObject);
		}
	}

	/**
	 * 这里先尝试从缓存中获取，获取不到再走后面创建的流程
	 * 获取到有两种情况：
	 * 1.是Bean创建完成存储到最终的缓存中。
	 * 2.是未创建完成，但先预存到一个单独的缓存中，这种是针对可能存在循环引用的情况的处理。
	 * 如A引用B,B又引用了A,因而在初始化A时，A会先调用构造函数创建出一个实例，在依赖注入B之前，现将A实例缓存起来
	 * 然后在初始化A时，依赖注入阶段，会触发初始化B，B创建后需要依赖注入A时，先从缓存中获取A（这个时候的A是不完整的)，避免循环依赖的问题出现。
	 * @see org.springframework.beans.factory.support.DefaultSingletonBeanRegistryTests#testGetSingleton() 【测试用例】
	 */
	@Override
	@Nullable
	public Object getSingleton(String beanName) {
		return getSingleton(beanName, true);
	}
}
