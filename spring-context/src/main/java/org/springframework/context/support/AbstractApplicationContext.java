

package org.springframework.context.support;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.CachedIntrospectionResults;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.support.ResourceEditorRegistrar;
import org.springframework.context.*;
import org.springframework.context.event.*;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.context.weaving.LoadTimeWeaverAware;
import org.springframework.context.weaving.LoadTimeWeaverAwareProcessor;
import org.springframework.core.ResolvableType;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.ConfigurablePropertyResolver;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ReflectionUtils;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Abstract implementation of the {@link org.springframework.context.ApplicationContext} interface.
 * Doesn't mandate the type of storage used for configuration; simply implements common context functionality.
 * Uses the Template Method design pattern,requiring concrete subclasses to implement abstract methods.
 *
 * In contrast to a plain BeanFactory, an ApplicationContext is supposed to detect special beans defined in its internal bean factory:
 * Therefore, this class automatically registers
 * {@link org.springframework.beans.factory.config.BeanFactoryPostProcessor BeanFactoryPostProcessors},
 * {@link org.springframework.beans.factory.config.BeanPostProcessor BeanPostProcessors},
 * and {@link org.springframework.context.ApplicationListener ApplicationListeners} which are defined as beans in the context.
 *
 * A {@link org.springframework.context.MessageSource} may also be supplied as a bean in the context, with the name "messageSource";
 * otherwise, message resolution is delegated to the parent context.
 * Furthermore, a multicaster for application events can be supplied as an "applicationEventMulticaster" bean of type {@link org.springframework.context.event.ApplicationEventMulticaster}
 * in the context; otherwise, a default multicaster of type {@link org.springframework.context.event.SimpleApplicationEventMulticaster} will be used.
 * Implements resource loading by extending {@link org.springframework.core.io.DefaultResourceLoader}.
 * Consequently treats non-URL resource paths as class path resources (supporting full class path resource names that include the package path,
 * e.g. "mypackage/myresource.dat"), unless the {@link #getResourceByPath} method is overridden in a subclass.
 * @since January 21, 2001
 * @see #refreshBeanFactory
 * @see #getBeanFactory
 * @see org.springframework.beans.factory.config.BeanFactoryPostProcessor
 * @see org.springframework.beans.factory.config.BeanPostProcessor
 * @see org.springframework.context.event.ApplicationEventMulticaster
 * @see org.springframework.context.ApplicationListener
 * @see org.springframework.context.MessageSource
 *
 */
public abstract class AbstractApplicationContext extends DefaultResourceLoader implements ConfigurableApplicationContext {

	/**
	 * Name of the MessageSource bean in the factory.If none is supplied, message resolution is delegated to the parent.
	 * @see MessageSource
	 */
	public static final String MESSAGE_SOURCE_BEAN_NAME = "messageSource";

	/**
	 * Name of the LifecycleProcessor bean in the factory.If none is supplied, a DefaultLifecycleProcessor is used.
	 * @see org.springframework.context.LifecycleProcessor
	 * @see org.springframework.context.support.DefaultLifecycleProcessor
	 */
	public static final String LIFECYCLE_PROCESSOR_BEAN_NAME = "lifecycleProcessor";

	/**
	 * Name of the ApplicationEventMulticaster bean in the factory.If none is supplied, a default SimpleApplicationEventMulticaster is used.
	 * @see org.springframework.context.event.ApplicationEventMulticaster
	 * @see org.springframework.context.event.SimpleApplicationEventMulticaster
	 */
	public static final String APPLICATION_EVENT_MULTICASTER_BEAN_NAME = "applicationEventMulticaster";

	static {
		/**
		 *  Eagerly load the ContextClosedEvent class to avoid weird classloader issues  on application shutdown in WebLogic 8.1. (Reported by Dustin Woods.)
		 * 急切地加载ContextClosedEvent类，以避免WebLogic8.1中应用程序关闭时出现奇怪的类加载器问题。（达斯汀·伍兹报道）
		*/
		ContextClosedEvent.class.getName();
	}

	/** Logger used by this class. Available to subclasses. */
	protected final Log logger = LogFactory.getLog(getClass());

	/** Unique id for this context, if any. */
	private String id = ObjectUtils.identityToString(this);

	/** Display name. */
	private String displayName = ObjectUtils.identityToString(this);

	/** Parent context. */
	@Nullable
	private ApplicationContext parent;

	/** Environment used by this context. */
	@Nullable
	private ConfigurableEnvironment environment;

	/** BeanFactoryPostProcessors to apply on refresh. */
	private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();

	/** System time in milliseconds when this context started. */
	private long startupDate;

	/** Flag that indicates whether this context is currently active. */
	private final AtomicBoolean active = new AtomicBoolean();

	/** Flag that indicates whether this context has been closed already. */
	private final AtomicBoolean closed = new AtomicBoolean();

	/** Synchronization monitor for the "refresh" and "destroy". */
	private final Object startupShutdownMonitor = new Object();

	/** Reference to the JVM shutdown hook, if registered. */
	@Nullable
	private Thread shutdownHook;

	/** ResourcePatternResolver used by this context. */
	private ResourcePatternResolver resourcePatternResolver;

	/** LifecycleProcessor for managing the lifecycle of beans within this context. */
	@Nullable
	private LifecycleProcessor lifecycleProcessor;

	/** MessageSource we delegate our implementation of this interface to. */
	@Nullable
	private MessageSource messageSource;

	/** Helper class used in event publishing. */
	@Nullable
	private ApplicationEventMulticaster applicationEventMulticaster;

	/** Statically specified listeners. */
	private final Set<ApplicationListener<?>> applicationListeners = new LinkedHashSet<>();

	/** Local listeners registered before refresh. */
	@Nullable
	private Set<ApplicationListener<?>> earlyApplicationListeners;

	/** ApplicationEvents published before the multicaster setup. */
	@Nullable
	private Set<ApplicationEvent> earlyApplicationEvents;

	/**
	 * Create a new AbstractApplicationContext with no parent.
	 */
	public AbstractApplicationContext() {
		logger.warn("进入 【AbstractApplicationContext】 构造函数 {}");
		resourcePatternResolver = getResourcePatternResolver();
	}

	/**
	 * Create a new AbstractApplicationContext with the given parent context.
	 * @param parent the parent context
	 */
	public AbstractApplicationContext(@Nullable ApplicationContext parent) {
		this();
		setParent(parent);
	}

	//---------------------------------------------------------------------
	// Implementation of ApplicationContext interface
	//---------------------------------------------------------------------
	@Override
	public String getId() {
		return id;
	}

	@Override
	public String getApplicationName() {
		return "";
	}

	/**
	 * Set a friendly name for this context.
	 * Typically done during initialization of concrete context implementations.Default is the object id of the context instance.
	 */
	public void setDisplayName(String displayName) {
		Assert.hasLength(displayName, "Display name must not be empty");
		this.displayName = displayName;
	}

	/**
	 * Return a friendly name for this context.
	 * @return a display name for this context (never {@code null})
	 */
	@Override
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * Return the parent context, or {@code null} if there is no parent (that is, this context is the root of the context hierarchy).
	 */
	@Override
	@Nullable
	public ApplicationContext getParent() {
		return parent;
	}

	/**
	 * Set the {@code Environment} for this application context.Default value is determined by {@link #createEnvironment()}.
	 * Replacing the default with this method is one option but configuration through {@link #getEnvironment()} should also be considered.
	 * In either case, such modifications should be performed <em>before</em> {@link #refresh()}.
	 * @see org.springframework.context.support.AbstractApplicationContext#createEnvironment
	 */
	@Override
	public void setEnvironment(ConfigurableEnvironment environment) {
		this.environment = environment;
	}

	/**
	 * Return the {@code Environment} for this application context in configurable form, allowing for further customization.
	 * If none specified, a default environment will be initialized via {@link #createEnvironment()}.
	 */
	@Override
	public ConfigurableEnvironment getEnvironment() {
		if (environment == null) environment = createEnvironment();
		return environment;
	}

	/**
	 * Create and return a new {@link StandardEnvironment}.
	 * Subclasses may override this method in order to supply a custom {@link ConfigurableEnvironment} implementation.
	 */
	protected ConfigurableEnvironment createEnvironment() {
		return new StandardEnvironment();
	}

	/**
	 * Return this context's internal bean factory as AutowireCapableBeanFactory, if already available.
	 * @see #getBeanFactory()
	 */
	@Override
	public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
		return getBeanFactory();
	}

	// Return the timestamp (ms) when this context was first loaded.
	@Override
	public long getStartupDate() {
		return startupDate;
	}

	/**
	 * Publish the given event to all listeners.
	 * Note: Listeners get initialized after the MessageSource, to be able to access it within listener implementations.
	 * Thus, MessageSource implementations cannot publish events.
	 * @param event the event to publish (may be application-specific or a standard framework event)
	 */
	@Override
	public void publishEvent(ApplicationEvent event) {
		publishEvent(event, null);
	}

	/**
	 * Publish the given event to all listeners.
	 * Note: Listeners get initialized after the MessageSource, to be able to access it within listener implementations.
	 * Thus, MessageSource  implementations cannot publish events.
	 * @param event the event to publish (may be an {@link ApplicationEvent} or a payload object to be turned into a {@link PayloadApplicationEvent})
	 */
	@Override
	public void publishEvent(Object event) {
		publishEvent(event, null);
	}

	/**
	 * Publish the given event to all listeners.
	 * @param event the event to publish (may be an {@link ApplicationEvent} or a payload object to be turned into a {@link PayloadApplicationEvent})
	 * @param eventType the resolved event type, if known
	 * @since 4.2
	 */
	protected void publishEvent(Object event, @Nullable ResolvableType eventType) {
		Assert.notNull(event, "Event must not be null");
		// Decorate event as an ApplicationEvent if necessary
		ApplicationEvent applicationEvent;
		if (event instanceof ApplicationEvent) {
			applicationEvent = (ApplicationEvent) event;
		}else {
			applicationEvent = new PayloadApplicationEvent<>(this, event);
			if (eventType == null) {
				eventType = ((PayloadApplicationEvent<?>) applicationEvent).getResolvableType();
			}
		}
		// Multicast right now if possible - or lazily once the multicaster is initialized
		if (earlyApplicationEvents != null) {
			earlyApplicationEvents.add(applicationEvent);
		}else {
			getApplicationEventMulticaster().multicastEvent(applicationEvent, eventType);
		}
		// Publish event via parent context as well...
		if (parent != null) {
			if (parent instanceof AbstractApplicationContext) {
				((AbstractApplicationContext) parent).publishEvent(event, eventType);
			}else {
				parent.publishEvent(event);
			}
		}
	}

	/**
	 * Return the internal ApplicationEventMulticaster used by the context.
	 * @return the internal ApplicationEventMulticaster (never {@code null})
	 * @throws IllegalStateException if the context has not been initialized yet
	 */
	ApplicationEventMulticaster getApplicationEventMulticaster() throws IllegalStateException {
		if (applicationEventMulticaster == null) {
			throw new IllegalStateException("ApplicationEventMulticaster not initialized - call 'refresh' before multicasting events via the context: " + this);
		}
		return applicationEventMulticaster;
	}

	/**
	 * Return the internal LifecycleProcessor used by the context.
	 * @return the internal LifecycleProcessor (never {@code null})
	 * @throws IllegalStateException if the context has not been initialized yet
	 */
	LifecycleProcessor getLifecycleProcessor() throws IllegalStateException {
		if (lifecycleProcessor == null) {
			throw new IllegalStateException("LifecycleProcessor not initialized - call 'refresh' before invoking lifecycle methods via the context: " + this);
		}
		return lifecycleProcessor;
	}

	/**
	 * Return the ResourcePatternResolver to use for resolving location patterns into Resource instances.
	 *  Default is a {@link org.springframework.core.io.support.PathMatchingResourcePatternResolver},supporting Ant-style location patterns.
	 * Can be overridden in subclasses, for extended resolution strategies,for example in a web environment.
	 * <b>Do not call this when needing to resolve a location pattern.</b>
	 * Call the context's {@code getResources} method instead, which will delegate to the ResourcePatternResolver.
	 * @return the ResourcePatternResolver for this context
	 * @see #getResources
	 * @see org.springframework.core.io.support.PathMatchingResourcePatternResolver
	 */
	protected ResourcePatternResolver getResourcePatternResolver() {
		return new PathMatchingResourcePatternResolver(this);
	}

	//---------------------------------------------------------------------
	// Implementation of ConfigurableApplicationContext interface
	//---------------------------------------------------------------------

	/**
	 * Set the unique id of this application context.
	 * Default is the object id of the context instance, or the name of the context bean if the context is itself defined as a bean.
	 * @param id the unique id of the context
	 */
	@Override
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Set the parent of this application context.
	 * The parent {@linkplain ApplicationContext#getEnvironment() environment} is {@linkplain ConfigurableEnvironment#merge(ConfigurableEnvironment) merged} with
	 * this (child) application context environment if the parent is non-{@code null} and its environment is an instance of {@link ConfigurableEnvironment}.
	 * @see ConfigurableEnvironment#merge(ConfigurableEnvironment)
	 */
	@Override
	public void setParent(@Nullable ApplicationContext parent) {
		this.parent = parent;
		if (parent != null) {
			Environment parentEnvironment = parent.getEnvironment();
			if (parentEnvironment instanceof ConfigurableEnvironment) {
				getEnvironment().merge((ConfigurableEnvironment) parentEnvironment);
			}
		}
	}

	@Override
	public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor) {
		Assert.notNull(postProcessor, "BeanFactoryPostProcessor must not be null");
		beanFactoryPostProcessors.add(postProcessor);
	}

	// Return the list of BeanFactoryPostProcessors that will get applied  to the internal BeanFactory.
	public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
		return beanFactoryPostProcessors;
	}

	@Override
	public void addApplicationListener(ApplicationListener<?> listener) {
		Assert.notNull(listener, "ApplicationListener must not be null");
		if (applicationEventMulticaster != null) {
			applicationEventMulticaster.addApplicationListener(listener);
		}
		applicationListeners.add(listener);
	}

	//  Return the list of statically specified ApplicationListeners.
	public Collection<ApplicationListener<?>> getApplicationListeners() {
		return applicationListeners;
	}

	/**
	 * 【Spring IOC 容器就此开始！】
	 * ApplicationContext 建立起来以后，其实我们是可以通过调用 refresh() 这个方法重建的，refresh() 会将原来的 ApplicationContext 销毁，然后再重新执行一次初始化操作。
	*/
	@Override
	public void refresh() throws BeansException, IllegalStateException {
		/**
		 * 为了避免`refresh()` 还没结束，再次发起启动或者销毁容器引起的冲突，避免多线程同时刷新上下文
		 * 尽管加锁可以看到是针对整个方法体的，但是没有在方法前加synchronized关键字，而使用了对象锁startUpShutdownMonitor，这样做有两个好处：
		 * （1）refresh()方法和close()方法都使用了startUpShutdownMonitor对象锁加锁，这就保证了在调用refresh()方法的时候无法调用close()方法，反之亦然，避免了冲突
		 * （2）另外一个好处不在这个方法中体现，但是提一下，使用对象锁可以减小了同步的范围，只对不能并发的代码块进行加锁，提高了整体代码运行的效率
		*/
		synchronized (startupShutdownMonitor) {
			/**
			 *  Prepare this context for refreshing. 准备工作，记录下容器的启动时间、标记“已启动”状态、处理配置文件中的占位符,初始化和验证一些预定义的属性（有的话）
			 *  供子类拓展，添加创建前必需属性，校验如果必需属性不存在则抛出MissingRequiredPropertiesException
			 */
			prepareRefresh();
			/**
			 * Tell the subclass to refresh the internal bean factory.
			 *    这步比较关键，这步完成后，xml 配置文件就会解析成一个个 Bean 定义(BeanDefinition)，注册到 BeanFactory 中， (说到底核心是一个 beanName-> beanDefinition 的 map)
			 *    当然，这里说的 Bean 还没有初始化，只是配置信息都从xml文件中提取出来了，
			 *    调用子类实现方法获取（创建或刷新）BeanFacotry容器，对于ClassPathXmlApplicationContext，主要调用了AbstractRefreshableApplicationContext中实现的方法
			 * 	  初始化BeanFactory，注意，此处是获取新的，销毁旧的，这就是刷新的意义
			 *
			 * 	一、创建BeanFactor对象  只对xml配置有效，注解配置方式该方法直接跳过了。
			 * 	二、传统标签解析： bean、import等
			 * 	自定义标签解析： <context: component-scan>
			 * 	自定义标签解析流程：
			 * 		1.根据当前解析标签的头信息找到对应的 namespaceUri
			 * 		2.加载spring所有jar中的spring.handlers文件。并建立映射关系
			 * 		3.根据namespaceUri 从映射关系中找到对应的实现了 NamespaceHandler 接口的实现类
			 * 		4.根据namespaceUri找到对应的解析类，然后调用paser方法完成标签的解析工作
			 * 	二、解析出的xml标签封装成BeanDefinition对象 存储到Map中
			 */
			ConfigurableListableBeanFactory beanFactory = obtainFreshBeanFactory();
			// 设定类加载器，spel解析器，属性编辑解析器等，忽略特定接口的依赖注册（在特定时刻相关Bean再完成注入)，注册一些系统Bean供依赖注入使用，设置回调方法等。
			// Prepare the bean factory for use in this context.
			// 就是 上一步 obtainFreshBeanFactory() 创建好beanFactory后 再对其进行初始化设置
			prepareBeanFactory(beanFactory);
			try {
				/**
				 *  【这里需要知道 BeanFactoryPostProcessor 这个知识点，Bean 如果实现了此接口，那么在容器初始化以后，Spring 会负责调用里面的 postProcessBeanFactory 方法。】
				 *  这里是提供给子类的扩展点，到这里的时候，所有的 Bean 都加载、注册完成了，但是都还没有初始化
				 *  具体的子类可以在这步的时候添加一些特殊的 BeanFactoryPostProcessor 的实现类或做点什么事
				 *  Allows post-processing of the bean factory in context subclasses.
				 *  上一步 prepareBeanFactory(beanFactory) 的 BeanFactory 创建完成后 进行的后置处理。
				 *  当前为空实现，供子类拓展 BeanFactory构建完成之后事件，这个方法没有实现，我们可以实现一个。
				 *  这个方法再当前版本中 没有任何作用，可能spring想要再后续的版本中进行扩展吧
				*/
				postProcessBeanFactory(beanFactory);
				// 以上都是对beanFactory的预准备、创建、后置处理工作 ，以下都是 利用创建好的beanFactory来创建各种组件！
				// Invoke factory processors registered as beans in the context. 调用在上下文中注册为bean的工厂处理器
				// 调用BeanFacotry的相关后置处理器，如果实现了 Order 或 PriorityOrdered 相关接口，会先进行排序。
				// 执行上面的事件
				// 调用 BeanFactoryPostProcessor 各个实现类的 postProcessBeanFactory(factory) 方法
				// 设置执行 自定义或spring内置的BeanPostProcessor
				// @MapperScan  @Import  @ImportResource 等注解都在这里处理
				invokeBeanFactoryPostProcessors(beanFactory);
				// 以上都是对 bean工厂 的后置处理器 以下开始是 bean 的后置处理器的注册 （并不执行）
				// Register bean processors that intercept bean creation. 注册bean的后置处理器，用来拦截bean的创建过程
				// 注册相关 BeanPostProcessor，供Bean生成前后调用。
				// 在创建Bean过程中注册拦截器，这些个拦截器会在bean成为真正的成熟bean（applicationContext管理的bean）之前调用
				// 调用 bean的后置处理器
				// classpath 下xml配置文件 和 @ComponentScan("com.goat.chapter110.bean") 注解 就是在这里解析 起作用的
				// 注册 BeanPostProcessor 的实现类，注意看和 BeanFactoryPostProcessor 的区别
				// 此接口两个方法: postProcessBeforeInitialization 和 postProcessAfterInitialization
				// 两个方法分别在 Bean 初始化之前和初始化之后得到执行。注意，到这里 Bean 还没初始化
				//实例化和注册beanFactory中扩展了BeanPostProcessor的bean。
				//例如：
				//AutowiredAnnotationBeanPostProcessor(处理被@Autowired注解修饰的bean并注入)
				//RequiredAnnotationBeanPostProcessor(处理被@Required注解修饰的方法)
				//CommonAnnotationBeanPostProcessor(处理@PreDestroy、@PostConstruct、@Resource等多个注解的作用)等。
				registerBeanPostProcessors(beanFactory);
				// Initialize message source for this context.
				// 初始化国际化信息源   // 初始化信息源，信息源bean可以国际化的读取properties文件
				initMessageSource();
				// Initialize event multicaster for this context.
				// 初始化Spring相关上下文时间广播器
				// 初始化事件广播器，对于他内部的监听者applicationListeners，每次事件到来都会一一获取通知（这里使用了观察者模式）
				initApplicationEventMulticaster();
				// Initialize other special beans in specific context subclasses.
				// 模版方法供子类实现，用于初始化一些特殊Bean配置等
				// 模板方法模式，埋了一个钩子，那些想要实现特殊实例化bean的类可以重写这个方法以实现自己的定制化初始化方案
				// 主要用途：不同的子类实现，执行到这里时，出现不同的执行逻辑
				onRefresh();
				// Check for listener beans and register them.  注册事件监听器，监听器需要实现 ApplicationListener 接口。这也不是我们的重点，过
				// 注册实现了ApplicationListener接口的事件监听器，用于后续广播器广播事件
				// 给事件广播器注册一些监听器（观察者模式）
				registerListeners();
				/**
				 * 完成 容器的所有 初始化工作！
				 * 刚才我们提到了bean还没有初始化。这个方法就是负责初始化所有的没有设置懒加载的singleton bean
				 * Instantiate all remaining (non-lazy-init) singletons.  初始化所有（lazy-init 的除外）的 singleton beans
				 * BeanFactory 初始化完成时调用，初始ConversionService Bean，冻结beanFactory配置，并开始创建BeanFactory中所有非懒加载的单例Bean
				 */
				finishBeanFactoryInitialization(beanFactory);
				// Last step: publish corresponding event. 最后，广播事件，ApplicationContext 初始化完成
				// 初始化Lifecycle处理器，调用onRefresh方法，广播ContextRefreshedEvent。
				// 初始化容器的生命周期事件处理器，并发布容器的生命周期事件
				// 清除缓存的资源信息，初始化一些声明周期相关的bean，并且发布Context已被初始化的事件
				//  重点，重点，重点  初始化所有的 singleton beans （lazy-init 的除外）
				finishRefresh();
			}catch (BeansException ex) {
				if (logger.isWarnEnabled()) logger.warn("Exception encountered during context initialization - cancelling refresh attempt: " + ex);
				// Destroy already created singletons to avoid dangling resources.// 发生异常则销毁已经生成的bean
				// 销毁已经初始化的 singleton 的 Beans，以免有些 bean 会一直占用资源
				destroyBeans();
				// Reset 'active' flag.
				cancelRefresh(ex);   // 重置refresh字段信息
				// Propagate exception to caller. // 把异常往外抛
				throw ex;
			}finally {
				// Reset common introspection caches in Spring's core, since we
				// might not ever need metadata for singleton beans anymore...
				// 初始化一些缓存信息
				resetCommonCaches();
			}
		}
	}

	/**
	 * Prepare this context for refreshing, setting its startup date and active flag as well as performing any initialization of property sources.
	 */
	protected void prepareRefresh() {
		logger.warn("进入 【prepareRefresh】 方法");
		// Switch to active. 记录启动时间 将 active 属性设置为 true，closed 属性设置为 false，它们都是 AtomicBoolean 类型
		startupDate = System.currentTimeMillis();
		closed.set(false); // 设置容器非关闭状态
		active.set(true); // 设置容器为激活状态
		if (logger.isDebugEnabled()) {
			if (logger.isTraceEnabled()) {
				logger.trace("Refreshing " + this);
			}else {
				logger.debug("Refreshing " + getDisplayName());
			}
		}
		// Initialize any placeholder property sources in the context environment.
		// 子类自定义个性化的属性设置方法，内部是一个空实现，主要供子类拓展自己ApplicationContext，设置必需的属性
		initPropertySources();
		/**
		 * Validate that all properties marked as required are resolvable: 验证所有标记为“必需”的属性是否可解析
		 * 校验 xml 配置文件  校验必需的属性是否存在，一旦发现不存则停止启动spring容器，并抛出异常。
		 * @see ConfigurablePropertyResolver#setRequiredProperties(java.lang.String...)
		 */
		getEnvironment().validateRequiredProperties();
		// Store pre-refresh ApplicationListeners...  保存容器中一些早期的事件
		if (earlyApplicationListeners == null) {
			earlyApplicationListeners = new LinkedHashSet<>(applicationListeners);
		}else {
			// Reset local application listeners to pre-refresh state.
			applicationListeners.clear();
			applicationListeners.addAll(earlyApplicationListeners);
		}
		// Allow for the collection of early ApplicationEvents,to be published once the multicaster is available...
		// 允许收集早期的ApplicationEvents，在多主机发布时候有效…
		earlyApplicationEvents = new LinkedHashSet<>();
	}

	/**
	 * Replace any stub property sources with actual instances.用实际实例替换任何存根属性源
	 * @see org.springframework.core.env.PropertySource.StubPropertySource
	 */
	protected void initPropertySources() {
		// For subclasses: do nothing by default.
	}

	/**
	 * Tell the subclass to refresh the internal bean factory.
	 * @return the fresh BeanFactory instance
	 * @see #refreshBeanFactory()
	 * @see #getBeanFactory()
	 * 该方法为BeanFactory准备创建Bean的原材料，即BeanDefinition，准备好之后放到一个ConcurrentHashMap里面，key为beanName，value为BeanDefinition
	 */
	protected ConfigurableListableBeanFactory obtainFreshBeanFactory() {
		logger.warn("进入 【obtainFreshBeanFactory】 函数");
		// 重点方法！ 初始化BeanFactory并加载xml文件信息  // 关闭旧的 BeanFactory (如果有)，创建新的 BeanFactory，加载 Bean 定义、注册 Bean 等等
		refreshBeanFactory();
		// 返回刚刚创建的 BeanFactory
		return getBeanFactory();
	}

	/**
	 * Configure the factory's standard context characteristics,such as the context's ClassLoader and post-processors.
	 * 配置工厂的标准上下文特征，例如上下文的类加载器和后处理器等。
	 * @param beanFactory the BeanFactory to configure
	 */
	protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		logger.warn("进入 【prepareBeanFactory】 方法");
		// Tell the internal bean factory to use the context's class loader etc.
		// 给beanFactory设置类加载器
		beanFactory.setBeanClassLoader(getClassLoader());
		// 给beanFactory设置表达式解析器
		beanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver(beanFactory.getBeanClassLoader()));
		beanFactory.addPropertyEditorRegistrar(new ResourceEditorRegistrar(this, getEnvironment()));
		// Configure the bean factory with context callbacks.
		// 添加部分 后置处理器
		beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
		// 设置忽略的自动装配接口 ( EnvironmentAware ResourceLoaderAware、、这些接口的实现类不能够通过接口类型实现自动注入) 即不能通过 @Autowire 注解注入
		beanFactory.ignoreDependencyInterface(EnvironmentAware.class);
		beanFactory.ignoreDependencyInterface(EmbeddedValueResolverAware.class);
		beanFactory.ignoreDependencyInterface(ResourceLoaderAware.class);
		beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
		beanFactory.ignoreDependencyInterface(MessageSourceAware.class);
		beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);
		// BeanFactory interface not registered as resolvable type in a plain factory. BeanFactory接口未在普通工厂中注册为可解析类型
		// MessageSource registered (and found for autowiring) as a bean. messagesource注册（并为自动连接找到）为bean
		// 设置 可以自动装配的接口 即可以通过 @Autowire 注解注入
		beanFactory.registerResolvableDependency(BeanFactory.class, beanFactory);
		beanFactory.registerResolvableDependency(ResourceLoader.class, this);
		beanFactory.registerResolvableDependency(ApplicationEventPublisher.class, this);
		beanFactory.registerResolvableDependency(ApplicationContext.class, this);
		// Register early post-processor for detecting inner beans as ApplicationListeners.
		beanFactory.addBeanPostProcessor(new ApplicationListenerDetector(this));
		// Detect a LoadTimeWeaver and prepare for weaving, if found.
		if (beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
			beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
			// Set a temporary ClassLoader for type matching.
			beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
		}
		// Register default environment beans.
		if (!beanFactory.containsLocalBean(ENVIRONMENT_BEAN_NAME)) {
			beanFactory.registerSingleton(ENVIRONMENT_BEAN_NAME, getEnvironment());
		}
		if (!beanFactory.containsLocalBean(SYSTEM_PROPERTIES_BEAN_NAME)) {
			beanFactory.registerSingleton(SYSTEM_PROPERTIES_BEAN_NAME, getEnvironment().getSystemProperties());
		}
		if (!beanFactory.containsLocalBean(SYSTEM_ENVIRONMENT_BEAN_NAME)) {
			beanFactory.registerSingleton(SYSTEM_ENVIRONMENT_BEAN_NAME, getEnvironment().getSystemEnvironment());
		}
	}

	/**
	 * Modify the application context's internal bean factory after its standard initialization.
	 * All bean definitions will have been loaded, but no beans will have been instantiated yet.
	 * This allows for registering special  BeanPostProcessors etc in certain ApplicationContext implementations.
	 * @param beanFactory the bean factory used by the application context
	 */
	protected void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) { }

	/**
	 * Instantiate and invoke all registered BeanFactoryPostProcessor beans,respecting explicit order if given.
	 * 实例化并调用所有注册的BeanFactoryPostProcessor Bean，如果给定，则遵循显式顺序。
	 * Must be called before singleton instantiation. 必须在单例实例化之前调用。
	 */
	protected void invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory beanFactory) {
		PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors(beanFactory, getBeanFactoryPostProcessors());
		// Detect a LoadTimeWeaver and prepare for weaving, if found in the meantime  (e.g. through an @Bean method registered by ConfigurationClassPostProcessor)
		if (beanFactory.getTempClassLoader() == null && beanFactory.containsBean(LOAD_TIME_WEAVER_BEAN_NAME)) {
			beanFactory.addBeanPostProcessor(new LoadTimeWeaverAwareProcessor(beanFactory));
			beanFactory.setTempClassLoader(new ContextTypeMatchClassLoader(beanFactory.getBeanClassLoader()));
		}
	}

	/**
	 * Instantiate and register all BeanPostProcessor beans,respecting explicit order if given.
	 * 实例化并注册所有Bean的 BeanPostProcessor后置处理器，如果给定，则遵循显式顺序。
	 * Must be called before any instantiation of application beans.
	 */
	protected void registerBeanPostProcessors(ConfigurableListableBeanFactory beanFactory) {
		PostProcessorRegistrationDelegate.registerBeanPostProcessors(beanFactory, this);
	}

	// Initialize the MessageSource.Use parent's if none defined in this context.
	protected void initMessageSource() {
		ConfigurableListableBeanFactory beanFactory = getBeanFactory();
		if (beanFactory.containsLocalBean(MESSAGE_SOURCE_BEAN_NAME)) {
			// 如果有 则 直接取出来 赋值
			messageSource = beanFactory.getBean(MESSAGE_SOURCE_BEAN_NAME, MessageSource.class);
			// Make MessageSource aware of parent MessageSource.
			if (parent != null && messageSource instanceof HierarchicalMessageSource) {
				HierarchicalMessageSource hms = (HierarchicalMessageSource) messageSource;
				if (hms.getParentMessageSource() == null) {
					// Only set parent context as parent MessageSource if no parent MessageSource
					// registered already.
					hms.setParentMessageSource(getInternalParentMessageSource());
				}
			}
			if (logger.isTraceEnabled()) logger.trace("Using MessageSource [" + messageSource + "]");
		}else {
			// 如果没有就直接创建一个默认的 DelegatingMessageSource
			// Use empty MessageSource to be able to accept getMessage calls.
			DelegatingMessageSource dms = new DelegatingMessageSource();
			dms.setParentMessageSource(getInternalParentMessageSource());
			messageSource = dms;
			beanFactory.registerSingleton(MESSAGE_SOURCE_BEAN_NAME, messageSource);
			if (logger.isTraceEnabled()) logger.trace("No '" + MESSAGE_SOURCE_BEAN_NAME + "' bean, using [" + messageSource + "]");
		}
	}

	/**
	 * Initialize the ApplicationEventMulticaster.Uses SimpleApplicationEventMulticaster if none defined in the context.
	 * @see org.springframework.context.event.SimpleApplicationEventMulticaster
	 */
	protected void initApplicationEventMulticaster() {
		ConfigurableListableBeanFactory beanFactory = getBeanFactory();
		// 如果我们自己有配置事件派发器 则获取后进行赋值
		if (beanFactory.containsLocalBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME)) {
			applicationEventMulticaster = beanFactory.getBean(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, ApplicationEventMulticaster.class);
			if (logger.isTraceEnabled()) logger.trace("Using ApplicationEventMulticaster [" + applicationEventMulticaster + "]");
		}else {
		// 如果我们没有配置 则 创建一个默认的 事件派发器 并添加的 beanFactory 中 (spring容器中) 以后就可以通过@Autowire自动注入来使用了
			applicationEventMulticaster = new SimpleApplicationEventMulticaster(beanFactory);
			//
			beanFactory.registerSingleton(APPLICATION_EVENT_MULTICASTER_BEAN_NAME, applicationEventMulticaster);
			if (logger.isTraceEnabled()) logger.trace("No '" + APPLICATION_EVENT_MULTICASTER_BEAN_NAME + "' bean, using " +	"[" + applicationEventMulticaster.getClass().getSimpleName() + "]");
		}
	}

	/**
	 * Initialize the LifecycleProcessor.Uses DefaultLifecycleProcessor if none defined in the context.
	 * @see org.springframework.context.support.DefaultLifecycleProcessor
	 */
	protected void initLifecycleProcessor() {
		ConfigurableListableBeanFactory beanFactory = getBeanFactory();
		if (beanFactory.containsLocalBean(LIFECYCLE_PROCESSOR_BEAN_NAME)) {
			lifecycleProcessor = beanFactory.getBean(LIFECYCLE_PROCESSOR_BEAN_NAME, LifecycleProcessor.class);
			if (logger.isTraceEnabled()) logger.trace("Using LifecycleProcessor [" + lifecycleProcessor + "]");
		}else {
			DefaultLifecycleProcessor defaultProcessor = new DefaultLifecycleProcessor();
			defaultProcessor.setBeanFactory(beanFactory);
			lifecycleProcessor = defaultProcessor;
			beanFactory.registerSingleton(LIFECYCLE_PROCESSOR_BEAN_NAME, lifecycleProcessor);
			if (logger.isTraceEnabled()) logger.trace("No '" + LIFECYCLE_PROCESSOR_BEAN_NAME + "' bean, using " + "[" + lifecycleProcessor.getClass().getSimpleName() + "]");
		}
	}

	/**
	 * Template method which can be overridden to add context-specific refresh work.
	 * Called on initialization of special beans, before instantiation of singletons.
	 * This implementation is empty.
	 * @throws BeansException in case of errors
	 * @see #refresh()
	 */
	protected void onRefresh() throws BeansException {
		// For subclasses: do nothing by default.
	}

	/**
	 * Add beans that implement ApplicationListener as listeners.Doesn't affect other listeners, which can be added without being beans.
	 */
	protected void registerListeners() {
		// Register statically specified listeners first.
		for (ApplicationListener<?> listener : getApplicationListeners()) {
			getApplicationEventMulticaster().addApplicationListener(listener);
		}
		// Do not initialize FactoryBeans here: We need to leave all regular beans uninitialized to let post-processors apply to them!
		String[] listenerBeanNames = getBeanNamesForType(ApplicationListener.class, true, false);
		for (String listenerBeanName : listenerBeanNames) {
			getApplicationEventMulticaster().addApplicationListenerBean(listenerBeanName);
		}
		// Publish early application events now that we finally have a multicaster...
		Set<ApplicationEvent> earlyEventsToProcess = earlyApplicationEvents;
		earlyApplicationEvents = null;
		if (earlyEventsToProcess != null) {
			for (ApplicationEvent earlyEvent : earlyEventsToProcess) {
				getApplicationEventMulticaster().multicastEvent(earlyEvent);
			}
		}
	}

	//  Finish the initialization of this context's bean factory, initializing all remaining singleton beans.
	protected void finishBeanFactoryInitialization(ConfigurableListableBeanFactory beanFactory) {
		// Initialize conversion service for this context.
		if (beanFactory.containsBean(CONVERSION_SERVICE_BEAN_NAME) && beanFactory.isTypeMatch(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class)) {
			beanFactory.setConversionService(beanFactory.getBean(CONVERSION_SERVICE_BEAN_NAME, ConversionService.class));
		}
		// Register a default embedded value resolver if no bean post-processor (such as a PropertyPlaceholderConfigurer bean) registered any before:
		// at this point, primarily for resolution in annotation attribute values.
		if (!beanFactory.hasEmbeddedValueResolver()) {
			beanFactory.addEmbeddedValueResolver(strVal -> getEnvironment().resolvePlaceholders(strVal));
		}
		// Initialize LoadTimeWeaverAware beans early to allow for registering their transformers early. 先初始化 LoadTimeWeaverAware 类型的 Bean
		String[] weaverAwareNames = beanFactory.getBeanNamesForType(LoadTimeWeaverAware.class, false, false);
		for (String weaverAwareName : weaverAwareNames) {
			getBean(weaverAwareName);
		}
		// Stop using the temporary ClassLoader for type matching.  // 停止使用用于类型匹配的临时类加载器
		beanFactory.setTempClassLoader(null);
		// Allow for caching all bean definition metadata, not expecting further changes. 冻结所有的bean定义，即已注册的bean定义将不会被修改或后处理
		beanFactory.freezeConfiguration();
		// Instantiate all remaining (non-lazy-init) singletons. 预实例化所有非懒加载单例Bean // 初始化
		//  // 真正的干货在这！！！ 1.对所有非lazy的bean进行创建；2.执行某些特殊bean创建完成后的回调方法。
		beanFactory.preInstantiateSingletons();
	}

	/**
	 * Finish the refresh of this context, invoking the LifecycleProcessor's onRefresh() method and publishing the
	 * {@link org.springframework.context.event.ContextRefreshedEvent}.
	 */
	protected void finishRefresh() {
		// Clear context-level resource caches (such as ASM metadata from scanning).
		clearResourceCaches();
		// Initialize lifecycle processor for this context.
		initLifecycleProcessor();
		// Propagate refresh to lifecycle processor first.
		getLifecycleProcessor().onRefresh();
		// Publish the final event.
		publishEvent(new ContextRefreshedEvent(this));
		// Participate in LiveBeansView MBean, if active.
		LiveBeansView.registerApplicationContext(this);
	}

	/**
	 * Cancel this context's refresh attempt, resetting the {@code active} flag after an exception got thrown.
	 * @param ex the exception that led to the cancellation
	 */
	protected void cancelRefresh(BeansException ex) {
		active.set(false);
	}

	/**
	 * Reset Spring's common reflection metadata caches, in particular the {@link ReflectionUtils}, {@link AnnotationUtils}, {@link ResolvableType} and {@link CachedIntrospectionResults} caches.
	 * @since 4.2
	 * @see ReflectionUtils#clearCache()
	 * @see AnnotationUtils#clearCache()
	 * @see ResolvableType#clearCache()
	 * @see CachedIntrospectionResults#clearClassLoader(ClassLoader)
	 */
	protected void resetCommonCaches() {
		ReflectionUtils.clearCache();
		AnnotationUtils.clearCache();
		ResolvableType.clearCache();
		CachedIntrospectionResults.clearClassLoader(getClassLoader());
	}

	/**
	 * Register a shutdown hook with the JVM runtime, closing this context on JVM shutdown unless it has already been closed at that time.
	 * Delegates to {@code doClose()} for the actual closing procedure.
	 * @see Runtime#addShutdownHook
	 * @see #close()
	 * @see #doClose()
	 */
	@Override
	public void registerShutdownHook() {
		if (shutdownHook == null) {
			// No shutdown hook registered yet.
			shutdownHook = new Thread(()->{
				synchronized (startupShutdownMonitor) {
					doClose();
				}
			});
			Runtime.getRuntime().addShutdownHook(shutdownHook);
		}
	}

	/**
	 * Callback for destruction of this instance, originally attached  to a {@code DisposableBean} implementation (not anymore in 5.0).
	 * The {@link #close()} method is the native way to shut down an ApplicationContext, which this method simply delegates to.
	 * @deprecated as of Spring Framework 5.0, in favor of {@link #close()}
	 */
	@Deprecated
	public void destroy() {
		close();
	}

	/**
	 * Close this application context, destroying all beans in its bean factory.Delegates to {@code doClose()} for the actual closing procedure.
	 * Also removes a JVM shutdown hook, if registered, as it's not needed anymore.
	 * @see #doClose()
	 * @see #registerShutdownHook()
	 */
	@Override
	public void close() {
		synchronized (startupShutdownMonitor) {
			doClose();
			// If we registered a JVM shutdown hook, we don't need it anymore now:
			// We've already explicitly closed the context.
			if (shutdownHook != null) {
				try {
					Runtime.getRuntime().removeShutdownHook(shutdownHook);
				}catch (IllegalStateException ex) {
					// ignore - VM is already shutting down
				}
			}
		}
	}

	/**
	 * Actually performs context closing: publishes a ContextClosedEvent and  destroys the singletons in the bean factory of this application context.
	 * Called by both {@code close()} and a JVM shutdown hook, if any.
	 * @see org.springframework.context.event.ContextClosedEvent
	 * @see #destroyBeans()
	 * @see #close()
	 * @see #registerShutdownHook()
	 */
	protected void doClose() {
		// Check whether an actual close attempt is necessary...
		if (active.get() && closed.compareAndSet(false, true)) {
			if (logger.isDebugEnabled()) logger.debug("Closing " + this);
			LiveBeansView.unregisterApplicationContext(this);
			try {
				// Publish shutdown event.
				publishEvent(new ContextClosedEvent(this));
			}catch (Throwable ex) {
				logger.warn("Exception thrown from ApplicationListener handling ContextClosedEvent", ex);
			}
			// Stop all Lifecycle beans, to avoid delays during individual destruction.
			if (lifecycleProcessor != null) {
				try {
					lifecycleProcessor.onClose();
				}catch (Throwable ex) {
					logger.warn("Exception thrown from LifecycleProcessor on context close", ex);
				}
			}
			// Destroy all cached singletons in the context's BeanFactory.
			destroyBeans();
			// Close the state of this context itself.
			closeBeanFactory();
			// Let subclasses do some final clean-up if they wish...
			onClose();
			// Reset local application listeners to pre-refresh state.
			if (earlyApplicationListeners != null) {
				applicationListeners.clear();
				applicationListeners.addAll(earlyApplicationListeners);
			}
			// Switch to inactive.
			active.set(false);
		}
	}

	/**
	 * Template method for destroying all beans that this context manages.
	 * The default implementation destroy all cached singletons in this context,invoking {@code DisposableBean.destroy()} and/or the specified "destroy-method".
	 * Can be overridden to add context-specific bean destruction steps right before or right after standard singleton destruction, while the context's BeanFactory is still active.
	 * @see #getBeanFactory()
	 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory#destroySingletons()
	 */
	protected void destroyBeans() {
		getBeanFactory().destroySingletons();
	}

	/**
	 * Template method which can be overridden to add context-specific shutdown work.The default implementation is empty.
	 * Called at the end of {@link #doClose}'s shutdown procedure, after this context's BeanFactory has been closed.
	 * If custom shutdown logic needs to execute while the BeanFactory is still active, override the {@link #destroyBeans()} method instead.
	 */
	protected void onClose() {
		// For subclasses: do nothing by default.
	}

	@Override
	public boolean isActive() {
		return active.get();
	}

	/**
	 * Assert that this context's BeanFactory is currently active,throwing an {@link IllegalStateException} if it isn't.
	 * Invoked by all {@link BeanFactory} delegation methods that depend on an active context, i.e. in particular all bean accessor methods.
	 * The default implementation checks the {@link #isActive() 'active'} status of this context overall.
	 * May be overridden for more specific checks, or for a no-op if {@link #getBeanFactory()} itself throws an exception in such a case.
	 */
	protected void assertBeanFactoryActive() {
		if (!active.get()) {
			if (closed.get())
				throw new IllegalStateException(getDisplayName() + " has been closed already");
			else
				throw new IllegalStateException(getDisplayName() + " has not been refreshed yet");
		}
	}

	//---------------------------------------------------------------------
	// Implementation of BeanFactory interface
	//---------------------------------------------------------------------
	@Override
	public Object getBean(String name) throws BeansException {
		assertBeanFactoryActive();
		return getBeanFactory().getBean(name);
	}

	@Override
	public <T> T getBean(String name, Class<T> requiredType) throws BeansException {
		assertBeanFactoryActive();
		return getBeanFactory().getBean(name, requiredType);
	}

	@Override
	public Object getBean(String name, Object... args) throws BeansException {
		assertBeanFactoryActive();
		return getBeanFactory().getBean(name, args);
	}

	@Override
	public <T> T getBean(Class<T> requiredType) throws BeansException {
		assertBeanFactoryActive();
		return getBeanFactory().getBean(requiredType);
	}

	@Override
	public <T> T getBean(Class<T> requiredType, Object... args) throws BeansException {
		assertBeanFactoryActive();
		return getBeanFactory().getBean(requiredType, args);
	}

	@Override
	public <T> ObjectProvider<T> getBeanProvider(Class<T> requiredType) {
		assertBeanFactoryActive();
		return getBeanFactory().getBeanProvider(requiredType);
	}

	@Override
	public <T> ObjectProvider<T> getBeanProvider(ResolvableType requiredType) {
		assertBeanFactoryActive();
		return getBeanFactory().getBeanProvider(requiredType);
	}

	@Override
	public boolean containsBean(String name) {
		return getBeanFactory().containsBean(name);
	}

	@Override
	public boolean isSingleton(String name) throws NoSuchBeanDefinitionException {
		assertBeanFactoryActive();
		return getBeanFactory().isSingleton(name);
	}

	@Override
	public boolean isPrototype(String name) throws NoSuchBeanDefinitionException {
		assertBeanFactoryActive();
		return getBeanFactory().isPrototype(name);
	}

	@Override
	public boolean isTypeMatch(String name, ResolvableType typeToMatch) throws NoSuchBeanDefinitionException {
		assertBeanFactoryActive();
		return getBeanFactory().isTypeMatch(name, typeToMatch);
	}

	@Override
	public boolean isTypeMatch(String name, Class<?> typeToMatch) throws NoSuchBeanDefinitionException {
		assertBeanFactoryActive();
		return getBeanFactory().isTypeMatch(name, typeToMatch);
	}

	@Override
	@Nullable
	public Class<?> getType(String name) throws NoSuchBeanDefinitionException {
		assertBeanFactoryActive();
		return getBeanFactory().getType(name);
	}

	@Override
	public String[] getAliases(String name) {
		return getBeanFactory().getAliases(name);
	}

	//---------------------------------------------------------------------
	// Implementation of 【ListableBeanFactory】 interface
	//---------------------------------------------------------------------
	@Override
	public boolean containsBeanDefinition(String beanName) {
		return getBeanFactory().containsBeanDefinition(beanName);
	}

	@Override
	public int getBeanDefinitionCount() {
		return getBeanFactory().getBeanDefinitionCount();
	}

	@Override
	public String[] getBeanDefinitionNames() {
		return getBeanFactory().getBeanDefinitionNames();
	}

	@Override
	public String[] getBeanNamesForType(ResolvableType type) {
		assertBeanFactoryActive();
		return getBeanFactory().getBeanNamesForType(type);
	}

	@Override
	public String[] getBeanNamesForType(@Nullable Class<?> type) {
		assertBeanFactoryActive();
		return getBeanFactory().getBeanNamesForType(type);
	}

	@Override
	public String[] getBeanNamesForType(@Nullable Class<?> type, boolean includeNonSingletons, boolean allowEagerInit) {
		assertBeanFactoryActive();
		return getBeanFactory().getBeanNamesForType(type, includeNonSingletons, allowEagerInit);
	}

	@Override
	public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type) throws BeansException {
		assertBeanFactoryActive();
		return getBeanFactory().getBeansOfType(type);
	}

	@Override
	public <T> Map<String, T> getBeansOfType(@Nullable Class<T> type, boolean includeNonSingletons, boolean allowEagerInit) throws BeansException {
		assertBeanFactoryActive();
		return getBeanFactory().getBeansOfType(type, includeNonSingletons, allowEagerInit);
	}

	@Override
	public String[] getBeanNamesForAnnotation(Class<? extends Annotation> annotationType) {
		assertBeanFactoryActive();
		return getBeanFactory().getBeanNamesForAnnotation(annotationType);
	}

	@Override
	public Map<String, Object> getBeansWithAnnotation(Class<? extends Annotation> annotationType) throws BeansException {
		assertBeanFactoryActive();
		return getBeanFactory().getBeansWithAnnotation(annotationType);
	}

	@Override
	@Nullable
	public <A extends Annotation> A findAnnotationOnBean(String beanName, Class<A> annotationType) throws NoSuchBeanDefinitionException {
		assertBeanFactoryActive();
		return getBeanFactory().findAnnotationOnBean(beanName, annotationType);
	}

	//---------------------------------------------------------------------
	// Implementation of HierarchicalBeanFactory interface
	//---------------------------------------------------------------------
	@Override
	@Nullable
	public BeanFactory getParentBeanFactory() {
		return getParent();
	}

	@Override
	public boolean containsLocalBean(String name) {
		return getBeanFactory().containsLocalBean(name);
	}

	/**
	 * Return the internal bean factory of the parent context if it implements ConfigurableApplicationContext; else, return the parent context itself.
	 * @see org.springframework.context.ConfigurableApplicationContext#getBeanFactory
	 * 找到父的，若存在就返回 若存在父容器就存在父的BeanFactory
	 */
	@Nullable
	protected BeanFactory getInternalParentBeanFactory() {
		return (getParent() instanceof ConfigurableApplicationContext ? ((ConfigurableApplicationContext) getParent()).getBeanFactory() : getParent());
	}

	//---------------------------------------------------------------------
	// Implementation of MessageSource interface
	//---------------------------------------------------------------------
	@Override
	public String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale) {
		return getMessageSource().getMessage(code, args, defaultMessage, locale);
	}

	@Override
	public String getMessage(String code, @Nullable Object[] args, Locale locale) throws NoSuchMessageException {
		return getMessageSource().getMessage(code, args, locale);
	}

	@Override
	public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
		return getMessageSource().getMessage(resolvable, locale);
	}

	/**
	 * Return the internal MessageSource used by the context.
	 * @return the internal MessageSource (never {@code null})
	 * @throws IllegalStateException if the context has not been initialized yet
	 */
	private MessageSource getMessageSource() throws IllegalStateException {
		if (messageSource == null) {
			throw new IllegalStateException("MessageSource not initialized - call 'refresh' before accessing messages via the context: " + this);
		}
		return messageSource;
	}

	/**
	 * Return the internal message source of the parent context if it is an
	 * AbstractApplicationContext too; else, return the parent context itself.
	 */
	@Nullable
	protected MessageSource getInternalParentMessageSource() {
		return (getParent() instanceof AbstractApplicationContext ? ((AbstractApplicationContext) getParent()).messageSource : getParent());
	}

	//---------------------------------------------------------------------
	// Implementation of ResourcePatternResolver interface
	//---------------------------------------------------------------------
	@Override
	public Resource[] getResources(String locationPattern) throws IOException {
		return resourcePatternResolver.getResources(locationPattern);
	}

	//---------------------------------------------------------------------
	// Implementation of Lifecycle interface
	//---------------------------------------------------------------------
	@Override
	public void start() {
		getLifecycleProcessor().start();
		publishEvent(new ContextStartedEvent(this));
	}

	@Override
	public void stop() {
		getLifecycleProcessor().stop();
		publishEvent(new ContextStoppedEvent(this));
	}

	@Override
	public boolean isRunning() {
		return (lifecycleProcessor != null && lifecycleProcessor.isRunning());
	}

	//---------------------------------------------------------------------
	// Abstract methods that must be implemented by subclasses
	//---------------------------------------------------------------------
	/**
	 * Subclasses must implement this method to perform the actual configuration load.The method is invoked by {@link #refresh()} before any other initialization work.
	 * A subclass will either create a new bean factory and hold a reference to it,or return a single BeanFactory instance that it holds.
	 * In the latter case, it will usually throw an IllegalStateException if refreshing the context more than once.
	 * 在后一种情况下，如果多次刷新上下文，它通常会抛出非法状态异常
	 * @throws BeansException if initialization of the bean factory failed
	 * @throws IllegalStateException if already initialized and multiple refresh attempts are not supported
	 */
	protected abstract void refreshBeanFactory() throws BeansException, IllegalStateException;

	/**
	 * Subclasses must implement this method to release their internal bean factory.
	 * This method gets invoked by {@link #close()} after all other shutdown work.
	 * Should never throw an exception but rather log shutdown failures.
	 */
	protected abstract void closeBeanFactory();

	/**
	 * Subclasses must return their internal bean factory here.
	 * They should implement the lookup efficiently, so that it can be called repeatedly without a performance penalty.
	 * Note: Subclasses should check whether the context is still active before returning the internal bean factory.
	 * The internal factory should generally be considered unavailable once the context has been closed.
	 * @return this application context's internal bean factory (never {@code null})
	 * @throws IllegalStateException if the context does not hold an internal bean factory yet (usually if {@link #refresh()} has never been called) or if the context has been closed already
	 * @see #refreshBeanFactory()
	 * @see #closeBeanFactory()
	 */
	@Override
	public abstract ConfigurableListableBeanFactory getBeanFactory() throws IllegalStateException;

	// Return information about this context.
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder(getDisplayName());
		sb.append(", started on ").append(new Date(getStartupDate()));
		ApplicationContext parent = getParent();
		if (parent != null) {
			sb.append(", parent: ").append(parent.getDisplayName());
		}
		return sb.toString();
	}
}
