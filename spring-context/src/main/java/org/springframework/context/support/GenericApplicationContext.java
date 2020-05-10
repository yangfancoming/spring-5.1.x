

package org.springframework.context.support;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Generic ApplicationContext implementation that holds a single internal {@link org.springframework.beans.factory.support.DefaultListableBeanFactory}
 * instance and does not assume a specific bean definition format.
 * Implements  the {@link org.springframework.beans.factory.support.BeanDefinitionRegistry} interface in order to allow for applying any bean definition readers to it.
 *
 * Typical usage is to register a variety of bean definitions via the {@link org.springframework.beans.factory.support.BeanDefinitionRegistry}
 * interface and then call {@link #refresh()} to initialize those beans with application context semantics (handling {@link org.springframework.context.ApplicationContextAware},
 * auto-detecting {@link org.springframework.beans.factory.config.BeanFactoryPostProcessor BeanFactoryPostProcessors}, etc).
 *
 * In contrast to other ApplicationContext implementations that create a new internal BeanFactory instance for each refresh,
 * the internal BeanFactory of this context is available right from the start, to be able to register bean definitions on it. {@link #refresh()} may only be called once.
 *
 * Usage example:
 * <pre class="code">
 * GenericApplicationContext ctx = new GenericApplicationContext();
 * XmlBeanDefinitionReader xmlReader = new XmlBeanDefinitionReader(ctx);
 * xmlReader.loadBeanDefinitions(new ClassPathResource("applicationContext.xml"));
 * PropertiesBeanDefinitionReader propReader = new PropertiesBeanDefinitionReader(ctx);
 * propReader.loadBeanDefinitions(new ClassPathResource("otherBeans.properties"));
 * ctx.refresh();
 * MyBean myBean = (MyBean) ctx.getBean("myBean");
 * ...</pre>
 *
 * For the typical case of XML bean definitions, simply use  {@link ClassPathXmlApplicationContext} or {@link FileSystemXmlApplicationContext},
 * which are easier to set up - but less flexible, since you can just use standard  resource locations for XML bean definitions,
 * rather than mixing arbitrary bean definition formats. The equivalent in a web environment is {@link org.springframework.web.context.support.XmlWebApplicationContext}.
 *
 * For custom application context implementations that are supposed to read  special bean definition formats in a refreshable manner,
 * consider deriving  from the {@link AbstractRefreshableApplicationContext} base class.
 * @since 1.1.2
 * @see #registerBeanDefinition
 * @see #refresh()
 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
 * @see org.springframework.beans.factory.support.PropertiesBeanDefinitionReader
 */
public class GenericApplicationContext extends AbstractApplicationContext implements BeanDefinitionRegistry {

	private final DefaultListableBeanFactory beanFactory;

	@Nullable
	private ResourceLoader resourceLoader;

	private boolean customClassLoader = false;

	private final AtomicBoolean refreshed = new AtomicBoolean();

	/**
	 * Create a new GenericApplicationContext.
	 * @see #registerBeanDefinition
	 * @see #refresh
	 */
	public GenericApplicationContext() {
		logger.warn("进入 【GenericApplicationContext】 构造函数 {}");
		beanFactory = new DefaultListableBeanFactory();
	}

	/**
	 * Create a new GenericApplicationContext with the given DefaultListableBeanFactory.
	 * @param beanFactory the DefaultListableBeanFactory instance to use for this context
	 * @see #registerBeanDefinition
	 * @see #refresh
	 */
	public GenericApplicationContext(DefaultListableBeanFactory beanFactory) {
		logger.warn("进入 【GenericApplicationContext】 构造函数 {}");
		Assert.notNull(beanFactory, "BeanFactory must not be null");
		this.beanFactory = beanFactory;
	}

	/**
	 * Create a new GenericApplicationContext with the given parent.
	 * @param parent the parent application context
	 * @see #registerBeanDefinition
	 * @see #refresh
	 */
	public GenericApplicationContext(@Nullable ApplicationContext parent) {
		this();
		setParent(parent);
	}

	/**
	 * Create a new GenericApplicationContext with the given DefaultListableBeanFactory.
	 * @param beanFactory the DefaultListableBeanFactory instance to use for this context
	 * @param parent the parent application context
	 * @see #registerBeanDefinition
	 * @see #refresh
	 */
	public GenericApplicationContext(DefaultListableBeanFactory beanFactory, ApplicationContext parent) {
		this(beanFactory);
		setParent(parent);
	}

	/**
	 * Set whether it should be allowed to override bean definitions by registering a different definition with the same name, automatically replacing the former.
	 * If not, an exception will be thrown. Default is "true".
	 * @since 3.0
	 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowBeanDefinitionOverriding
	 */
	public void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
		beanFactory.setAllowBeanDefinitionOverriding(allowBeanDefinitionOverriding);
	}

	/**
	 * Set whether to allow circular references between beans - and automatically try to resolve them.
	 * Default is "true". Turn this off to throw an exception when encountering a circular reference, disallowing them completely.
	 * @since 3.0
	 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowCircularReferences
	 */
	public void setAllowCircularReferences(boolean allowCircularReferences) {
		beanFactory.setAllowCircularReferences(allowCircularReferences);
	}

	/**
	 * Set a ResourceLoader to use for this context. If set, the context will delegate all {@code getResource} calls to the given ResourceLoader.
	 * If not set, default resource loading will apply.
	 * The main reason to specify a custom ResourceLoader is to resolve  resource paths (without URL prefix) in a specific fashion.
	 * The default behavior is to resolve such paths as class path locations.
	 * To resolve resource paths as file system locations, specify a FileSystemResourceLoader here.
	 * You can also pass in a full ResourcePatternResolver, which will be autodetected by the context and used for {@code getResources} calls as well.
	 * Else, default resource pattern matching will apply.
	 * @see #getResource
	 * @see org.springframework.core.io.DefaultResourceLoader
	 * @see org.springframework.core.io.FileSystemResourceLoader
	 * @see org.springframework.core.io.support.ResourcePatternResolver
	 * @see #getResources
	 */
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	/**
	 * Return the underlying bean factory of this context, available for registering bean definitions <b>NOTE:</b> You need to call {@link #refresh()} to initialize the
	 * bean factory and its contained beans with application context semantics (autodetecting BeanFactoryPostProcessors, etc).
	 * @return the internal bean factory (as DefaultListableBeanFactory)
	 */
	public final DefaultListableBeanFactory getDefaultListableBeanFactory() {
		return beanFactory;
	}

	//---------------------------------------------------------------------
	// ResourceLoader / ResourcePatternResolver override if necessary
	//---------------------------------------------------------------------
	/**
	 * This implementation delegates to this context's ResourceLoader if set,falling back to the default superclass behavior else.
	 * 如果已设置，则此实现委托给此上下文的ResourceLoader，返回到默认的超类行为else。
	 * @see #setResourceLoader
	 */
	@Override
	public Resource getResource(String location) {
		if (resourceLoader != null) {
			return resourceLoader.getResource(location);
		}
		return super.getResource(location);
	}

	@Override
	public void setClassLoader(@Nullable ClassLoader classLoader) {
		super.setClassLoader(classLoader);
		customClassLoader = true;
	}

	@Override
	@Nullable
	public ClassLoader getClassLoader() {
		if (resourceLoader != null && !customClassLoader) {
			return resourceLoader.getClassLoader();
		}
		return super.getClassLoader();
	}

	//---------------------------------------------------------------------
	// Implementations of 【AbstractApplicationContext's】 template methods
	//---------------------------------------------------------------------
	/**
	 * Do nothing: We hold a single internal BeanFactory and rely on callers to register beans through our public methods (or the BeanFactory's).
	 * 什么都不做：我们拥有一个内部beanfactory，并依靠调用方通过我们的公共方法（或beanfactory）注册bean。
	 * @see #registerBeanDefinition
	 */
	@Override
	protected final void refreshBeanFactory() throws IllegalStateException {
		if (!refreshed.compareAndSet(false, true)) {
			throw new IllegalStateException("GenericApplicationContext does not support multiple refresh attempts: just call 'refresh' once");
		}
		beanFactory.setSerializationId(getId());
	}

	@Override
	protected void cancelRefresh(BeansException ex) {
		beanFactory.setSerializationId(null);
		super.cancelRefresh(ex);
	}

	// Not much to do: We hold a single internal BeanFactory that will never  get released.
	@Override
	protected final void closeBeanFactory() {
		beanFactory.setSerializationId(null);
	}

	// Return the single internal BeanFactory held by this context (as ConfigurableListableBeanFactory).
	@Override
	public final ConfigurableListableBeanFactory getBeanFactory() {
		return beanFactory;
	}

	/**
	 * Set the parent of this application context, also setting the parent of the internal BeanFactory accordingly.
	 * @see org.springframework.beans.factory.config.ConfigurableBeanFactory#setParentBeanFactory
	 */
	@Override
	public void setParent(@Nullable ApplicationContext parent) {
		super.setParent(parent);
		beanFactory.setParentBeanFactory(getInternalParentBeanFactory());
	}

	/**
	 * This implementation delegates to this context's ResourceLoader if it implements the ResourcePatternResolver interface,falling back to the default superclass behavior else.
	 * @see #setResourceLoader
	 */
	@Override
	public Resource[] getResources(String locationPattern) throws IOException {
		if (resourceLoader instanceof ResourcePatternResolver) {
			return ((ResourcePatternResolver) resourceLoader).getResources(locationPattern);
		}
		return super.getResources(locationPattern);
	}

	@Override
	public AutowireCapableBeanFactory getAutowireCapableBeanFactory() throws IllegalStateException {
		assertBeanFactoryActive();
		return beanFactory;
	}

	//---------------------------------------------------------------------
	// Implementation of 【AliasRegistry】 interface  还有一个 getAliases 方法 与 BeanFactory接口重合
	//---------------------------------------------------------------------
	@Override
	public void registerAlias(String beanName, String alias) {
		beanFactory.registerAlias(beanName, alias);
	}

	@Override
	public void removeAlias(String alias) {
		beanFactory.removeAlias(alias);
	}

	@Override
	public boolean isAlias(String beanName) {
		return beanFactory.isAlias(beanName);
	}

	//---------------------------------------------------------------------
	// Implementation of 【BeanDefinitionRegistry】 interface 其中下面三个方法与 ListableBeanFactory接口重合
	// containsBeanDefinition()、getBeanDefinitionNames()、 getBeanDefinitionCount()
	//---------------------------------------------------------------------
	@Override
	public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException {
		beanFactory.registerBeanDefinition(beanName, beanDefinition);
	}

	@Override
	public void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
		beanFactory.removeBeanDefinition(beanName);
	}

	@Override
	public BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException {
		return beanFactory.getBeanDefinition(beanName);
	}

	@Override
	public boolean isBeanNameInUse(String beanName) {
		return beanFactory.isBeanNameInUse(beanName);
	}

	//---------------------------------------------------------------------
	// Convenient methods for registering individual beans
	//---------------------------------------------------------------------
	/**
	 * Register a bean from the given bean class, optionally customizing its bean definition metadata (typically declared as a lambda expression).
	 * @param beanClass the class of the bean (resolving a public constructor to be autowired, possibly simply the default constructor)
	 * @param customizers one or more callbacks for customizing the factory's {@link BeanDefinition}, e.g. setting a lazy-init or primary flag
	 * @since 5.0
	 * @see #registerBean(String, Class, Supplier, BeanDefinitionCustomizer...)
	 */
	public final <T> void registerBean(Class<T> beanClass, BeanDefinitionCustomizer... customizers) {
		registerBean(null, beanClass, null, customizers);
	}

	/**
	 * Register a bean from the given bean class, optionally customizing its bean definition metadata (typically declared as a lambda expression).
	 * @param beanName the name of the bean (may be {@code null})
	 * @param beanClass the class of the bean (resolving a public constructor to be autowired, possibly simply the default constructor)
	 * @param customizers one or more callbacks for customizing the factory's {@link BeanDefinition}, e.g. setting a lazy-init or primary flag
	 * @since 5.0
	 * @see #registerBean(String, Class, Supplier, BeanDefinitionCustomizer...)
	 */
	public final <T> void registerBean(@Nullable String beanName, Class<T> beanClass, BeanDefinitionCustomizer... customizers) {
		registerBean(beanName, beanClass, null, customizers);
	}

	/**
	 * Register a bean from the given bean class, using the given supplier for obtaining a new instance (typically declared as a lambda expression or method reference),
	 * optionally customizing its bean definition metadata (again typically declared as a lambda expression).
	 * @param beanClass the class of the bean
	 * @param supplier a callback for creating an instance of the bean
	 * @param customizers one or more callbacks for customizing the factory's {@link BeanDefinition}, e.g. setting a lazy-init or primary flag
	 * @since 5.0
	 * @see #registerBean(String, Class, Supplier, BeanDefinitionCustomizer...)
	 */
	public final <T> void registerBean(Class<T> beanClass, Supplier<T> supplier, BeanDefinitionCustomizer... customizers) {
		registerBean(null, beanClass, supplier, customizers);
	}

	/**
	 * Register a bean from the given bean class, using the given supplier for obtaining a new instance (typically declared as a lambda expression or method reference),
	 * optionally customizing its bean definition metadata (again typically declared as a lambda expression).
	 * This method can be overridden to adapt the registration mechanism for all {@code registerBean} methods (since they all delegate to this one).
	 * @param beanName the name of the bean (may be {@code null})
	 * @param beanClass the class of the bean
	 * @param supplier a callback for creating an instance of the bean (in case of {@code null}, resolving a public constructor to be autowired instead)
	 * @param customizers one or more callbacks for customizing the factory's {@link BeanDefinition}, e.g. setting a lazy-init or primary flag
	 * @since 5.0
	 */
	public <T> void registerBean(@Nullable String beanName, Class<T> beanClass,@Nullable Supplier<T> supplier, BeanDefinitionCustomizer... customizers) {
		ClassDerivedBeanDefinition beanDefinition = new ClassDerivedBeanDefinition(beanClass);
		if (supplier != null) {
			beanDefinition.setInstanceSupplier(supplier);
		}
		for (BeanDefinitionCustomizer customizer : customizers) {
			customizer.customize(beanDefinition);
		}
		String nameToUse = (beanName != null ? beanName : beanClass.getName());
		registerBeanDefinition(nameToUse, beanDefinition);
	}

	/**
	 * {@link RootBeanDefinition} marker subclass for {@code #registerBean} based  registrations with flexible autowiring for public constructors.
	 */
	@SuppressWarnings("serial")
	private static class ClassDerivedBeanDefinition extends RootBeanDefinition {

		public ClassDerivedBeanDefinition(Class<?> beanClass) {
			super(beanClass);
		}

		public ClassDerivedBeanDefinition(ClassDerivedBeanDefinition original) {
			super(original);
		}

		@Override
		@Nullable
		public Constructor<?>[] getPreferredConstructors() {
			Class<?> clazz = getBeanClass();
			Constructor<?> primaryCtor = BeanUtils.findPrimaryConstructor(clazz);
			if (primaryCtor != null) {
				return new Constructor<?>[] {primaryCtor};
			}
			Constructor<?>[] publicCtors = clazz.getConstructors();
			if (publicCtors.length > 0) {
				return publicCtors;
			}
			return null;
		}

		@Override
		public RootBeanDefinition cloneBeanDefinition() {
			return new ClassDerivedBeanDefinition(this);
		}
	}
}
