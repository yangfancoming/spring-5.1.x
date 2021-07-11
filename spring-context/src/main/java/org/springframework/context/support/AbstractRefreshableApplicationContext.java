package org.springframework.context.support;
import java.io.IOException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextException;
import org.springframework.lang.Nullable;

/**
 * Base class for {@link org.springframework.context.ApplicationContext}
 * implementations which are supposed to support multiple calls to {@link #refresh()},creating a new internal bean factory instance every time.
 * Typically (but not necessarily), such a context will be driven by a set of config locations to load bean definitions from.
 *
 * The only method to be implemented by subclasses is {@link #loadBeanDefinitions},which gets invoked on each refresh.
 * A concrete implementation is supposed to load bean definitions into the given
 * {@link org.springframework.beans.factory.support.DefaultListableBeanFactory},typically delegating to one or more specific bean definition readers.
 *
 * <b>Note that there is a similar base class for WebApplicationContexts.</b>
 * provides the same subclassing strategy, but additionally pre-implements all context functionality for web environments.
 * There is also a pre-defined way to receive config locations for a web context.
 *
 * Concrete standalone subclasses of this base class, reading in a specific bean definition format,are {@link ClassPathXmlApplicationContext} and {@link FileSystemXmlApplicationContext},
 * which both derive from the common {@link AbstractXmlApplicationContext} base class;
 * {@link org.springframework.context.annotation.AnnotationConfigApplicationContext} supports {@code @Configuration}-annotated classes as a source of bean definitions.
 * @since 1.1.3
 * @see #loadBeanDefinitions
 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory
 * @see AbstractXmlApplicationContext
 * @see ClassPathXmlApplicationContext
 * @see FileSystemXmlApplicationContext
 * @see org.springframework.context.annotation.AnnotationConfigApplicationContext
 */
public abstract class AbstractRefreshableApplicationContext extends AbstractApplicationContext {

	// 是否允许 BeanDefinition 覆盖 ，是指是否允对一个名字相同但definition不同进行重新注册，这里默认为null
	@Nullable
	private Boolean allowBeanDefinitionOverriding;

	// 否允许循环引用，是指是否允许Bean之间循环引用，这里默认为null.
	@Nullable
	private Boolean allowCircularReferences;

	/** Bean factory for this context.  111*/
	@Nullable
	private DefaultListableBeanFactory beanFactory;

	/** Synchronization monitor for the internal BeanFactory. */
	private final Object beanFactoryMonitor = new Object();

	// Create a new AbstractRefreshableApplicationContext with no parent.
	public AbstractRefreshableApplicationContext() {}

	/**
	 * Create a new AbstractRefreshableApplicationContext with the given parent context.
	 * @param parent the parent context
	 */
	public AbstractRefreshableApplicationContext(@Nullable ApplicationContext parent) {
		super(parent);
		logger.warn("进入 【AbstractRefreshableApplicationContext】 构造函数 {}");
	}

	/**
	 * Set whether it should be allowed to override bean definitions by registering  a different definition with the same name, automatically replacing the former.
	 * If not, an exception will be thrown. Default is "true".
	 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowBeanDefinitionOverriding
	 */
	public void setAllowBeanDefinitionOverriding(boolean allowBeanDefinitionOverriding) {
		this.allowBeanDefinitionOverriding = allowBeanDefinitionOverriding;
	}

	/**
	 * Set whether to allow circular references between beans - and automatically try to resolve them.
	 * Default is "true". Turn this off to throw an exception when encountering  a circular reference, disallowing them completely.
	 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowCircularReferences
	 */
	public void setAllowCircularReferences(boolean allowCircularReferences) {
		this.allowCircularReferences = allowCircularReferences;
	}

	/**
	 * This implementation performs an actual refresh of this context's underlying bean factory,
	 * shutting down the previous bean factory (if any) and initializing a fresh bean factory for the next phase of the context's lifecycle.
	 */
	@Override
	protected final void refreshBeanFactory() throws BeansException {
		/**  如果已经存在BeanFactory那么就销毁，避免重复加载BeanFactory
		 如果 ApplicationContext 中已经加载过 BeanFactory 了，销毁所有 Bean，关闭 BeanFactory
		 注意，应用中 BeanFactory 本来就是可以多个的，这里可不是说应用全局是否有 BeanFactory，而是当前 ApplicationContext 是否有 BeanFactory
		 */
		if (hasBeanFactory()) {
			destroyBeans();
			closeBeanFactory();
		}
		try {
			// 创建一个默认的BeanFactory
			DefaultListableBeanFactory beanFactory = createBeanFactory();
			// 为当前BeanFactory设置一个标识id 用于 BeanFactory 的序列化，部分人应该都用不到
			beanFactory.setSerializationId(getId());
			// 设置 BeanFactory 的两个配置属性：是否允许 Bean 覆盖、是否允许循环引用
			customizeBeanFactory(beanFactory);
			// 这步就关键了，加载xml文件信息 ，载入BeanDefinations， 加载Bean到BeanFactory中，给BeanFactory工厂提供创建bean的原材料！
			// 它属于模版方法，由子类去实现加载的方式。  AbstractXmlApplicationContext
			loadBeanDefinitions(beanFactory);
			synchronized (this.beanFactoryMonitor) {
				this.beanFactory = beanFactory;
			}
		}catch (IOException ex) {
			throw new ApplicationContextException("I/O error parsing bean definition source for " + getDisplayName(), ex);
		}
	}

	@Override
	protected void cancelRefresh(BeansException ex) {
		synchronized (beanFactoryMonitor) {
			if (beanFactory != null) {
				beanFactory.setSerializationId(null);
			}
		}
		super.cancelRefresh(ex);
	}

	@Override
	protected final void closeBeanFactory() {
		synchronized (beanFactoryMonitor) {
			if (beanFactory != null) {
				beanFactory.setSerializationId(null);
				beanFactory = null;
			}
		}
	}

	//  Determine whether this context currently holds a bean factory, i.e. has been refreshed at least once and not been closed yet.
	protected final boolean hasBeanFactory() {
		synchronized (beanFactoryMonitor) {
			return (beanFactory != null);
		}
	}

	@Override
	public final ConfigurableListableBeanFactory getBeanFactory() {
		synchronized (beanFactoryMonitor) {
			if (beanFactory == null) throw new IllegalStateException("BeanFactory not initialized or already closed - call 'refresh' before accessing beans via the ApplicationContext");
			return beanFactory;
		}
	}

	/**
	 * Overridden to turn it into a no-op: With AbstractRefreshableApplicationContext,
	 * {@link #getBeanFactory()} serves a strong assertion for an active context anyway.
	 */
	@Override
	protected void assertBeanFactoryActive() {}

	/**
	 * Create an internal bean factory for this context. Called for each {@link #refresh()} attempt.
	 * The default implementation creates a {@link org.springframework.beans.factory.support.DefaultListableBeanFactory}
	 * with the {@linkplain #getInternalParentBeanFactory() internal bean factory} of this context's parent as parent bean factory.
	 * Can be overridden in subclasses,for example to customize DefaultListableBeanFactory's settings.
	 * @return the bean factory for this context
	 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowBeanDefinitionOverriding
	 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowEagerClassLoading
	 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowCircularReferences
	 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory#setAllowRawInjectionDespiteWrapping
	 */
	protected DefaultListableBeanFactory createBeanFactory() {
		return new DefaultListableBeanFactory(getInternalParentBeanFactory());
	}

	/**
	 * Customize the internal bean factory used by this context.
	 * Called for each {@link #refresh()} attempt.
	 * The default implementation applies this context's {@linkplain #setAllowBeanDefinitionOverriding "allowBeanDefinitionOverriding"}
	 * and {@linkplain #setAllowCircularReferences "allowCircularReferences"} settings,if specified.
	 * Can be overridden in subclasses to customize any of {@link DefaultListableBeanFactory}'s settings.
	 * @param beanFactory the newly created bean factory for this context
	 * @see DefaultListableBeanFactory#setAllowBeanDefinitionOverriding
	 * @see DefaultListableBeanFactory#setAllowCircularReferences
	 * @see DefaultListableBeanFactory#setAllowRawInjectionDespiteWrapping
	 * @see DefaultListableBeanFactory#setAllowEagerClassLoading
	 * customizeBeanFactory(beanFactory) 比较简单，就是配置是否允许 BeanDefinition 覆盖、是否允许循环引用。
	 * BeanDefinition 的覆盖问题可能会有开发者碰到这个坑，就是在配置文件中定义 bean 时使用了相同的 id 或 name，
	 * 默认情况下，allowBeanDefinitionOverriding 属性为 null，如果在同一配置文件中重复了，会抛错，但是如果不是同一配置文件中，会发生覆盖。
	 * 循环引用也很好理解：A 依赖 B，而 B 依赖 A。或 A 依赖 B，B 依赖 C，而 C 依赖 A。
	 * 这里 allowBeanDefinitionOverriding 和 allowCircularReferences 默认为null，若想自定义更改，则需要重写该方法，均设置为true后，再调用父类该方法
	 * @see com.goat.chapter201.extend.MyApplicationContext#customizeBeanFactory(org.springframework.beans.factory.support.DefaultListableBeanFactory) 【测试用例】
	 */
	protected void customizeBeanFactory(DefaultListableBeanFactory beanFactory) {
		if (allowBeanDefinitionOverriding != null) {
			beanFactory.setAllowBeanDefinitionOverriding(allowBeanDefinitionOverriding);
		}
		if (allowCircularReferences != null){
			beanFactory.setAllowCircularReferences(allowCircularReferences);
		}
	}

	/**
	 * Load bean definitions into the given bean factory, typically through delegating to one or more bean definition readers.
	 * @param beanFactory the bean factory to load bean definitions into
	 * @throws BeansException if parsing of the bean definitions failed
	 * @throws IOException if loading of bean definition files failed
	 * @see org.springframework.beans.factory.support.PropertiesBeanDefinitionReader
	 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
	 */
	protected abstract void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException;
}
