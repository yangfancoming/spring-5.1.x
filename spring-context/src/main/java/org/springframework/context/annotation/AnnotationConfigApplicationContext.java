

package org.springframework.context.annotation;

import java.util.function.Supplier;

import org.springframework.beans.factory.config.BeanDefinitionCustomizer;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Standalone application context, accepting annotated classes as input - in particular {@link Configuration @Configuration}-annotated classes,
 * but also plain {@link org.springframework.stereotype.Component @Component} types and JSR-330 compliant classes using {@code javax.inject} annotations.
 * Allows for registering classes one by one using {@link #register(Class...)} as well as for classpath scanning using {@link #scan(String...)}.
 *
 * In case of multiple {@code @Configuration} classes, @{@link Bean} methods defined in later classes will override those defined in earlier classes.
 * This can be leveraged to deliberately override certain bean definitions via an extra {@code @Configuration} class.
 * See @{@link Configuration}'s javadoc for usage examples.
 * @since 3.0
 * @see #register
 * @see #scan
 * @see AnnotatedBeanDefinitionReader
 * @see ClassPathBeanDefinitionScanner
 * @see org.springframework.context.support.GenericXmlApplicationContext
 * AnnotatedBeanDefinitionReader和ClassPathBeanDefinitionScanner的初始化是spring上线文初始化的起点，很多预加载的类会在spring接下来的初始化中发挥重要作用
 */
public class AnnotationConfigApplicationContext extends GenericApplicationContext implements AnnotationConfigRegistry {

	private final AnnotatedBeanDefinitionReader reader;
	private final ClassPathBeanDefinitionScanner scanner;

	/**
	 * Create a new AnnotationConfigApplicationContext that needs to be populated through {@link #register} calls and then manually {@linkplain #refresh refreshed}.
	 */
	public AnnotationConfigApplicationContext() {
		super();
		// 初始化注解模式下的 bean定义 读取器
		reader = new AnnotatedBeanDefinitionReader(this);
		// 初始化类路径下 bean定义 扫描器
		scanner = new ClassPathBeanDefinitionScanner(this);
	}

	/**
	 * Create a new AnnotationConfigApplicationContext with the given DefaultListableBeanFactory.
	 * @param beanFactory the DefaultListableBeanFactory instance to use for this context
	 */
	public AnnotationConfigApplicationContext(DefaultListableBeanFactory beanFactory) {
		super(beanFactory);
		reader = new AnnotatedBeanDefinitionReader(this);
		scanner = new ClassPathBeanDefinitionScanner(this);
	}

	/**
	 * Create a new AnnotationConfigApplicationContext, deriving bean definitions from the given annotated classes and automatically refreshing the context.
	 * @param annotatedClasses one or more annotated classes,e.g. {@link Configuration @Configuration} classes
	 */
	public AnnotationConfigApplicationContext(Class<?>... annotatedClasses) {
		this();
		register(annotatedClasses);
		refresh();
	}

	/**
	 * Create a new AnnotationConfigApplicationContext, scanning for bean definitions in the given packages and automatically refreshing the context.
	 * @param basePackages the packages to check for annotated classes
	 */
	public AnnotationConfigApplicationContext(String... basePackages) {
		this();
		scan(basePackages);
		refresh();
	}

	/**
	 * Propagates the given custom {@code Environment} to the underlying {@link AnnotatedBeanDefinitionReader} and {@link ClassPathBeanDefinitionScanner}.
	 */
	@Override
	public void setEnvironment(ConfigurableEnvironment environment) {
		super.setEnvironment(environment);
		reader.setEnvironment(environment);
		scanner.setEnvironment(environment);
	}

	/**
	 * Provide a custom {@link BeanNameGenerator} for use with {@link AnnotatedBeanDefinitionReader} and/or {@link ClassPathBeanDefinitionScanner}, if any.
	 * Default is {@link org.springframework.context.annotation.AnnotationBeanNameGenerator}.
	 * Any call to this method must occur prior to calls to {@link #register(Class...)} and/or {@link #scan(String...)}.
	 * @see AnnotatedBeanDefinitionReader#setBeanNameGenerator
	 * @see ClassPathBeanDefinitionScanner#setBeanNameGenerator
	 */
	public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
		reader.setBeanNameGenerator(beanNameGenerator);
		scanner.setBeanNameGenerator(beanNameGenerator);
		getBeanFactory().registerSingleton(AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR, beanNameGenerator);
	}

	/**
	 * Set the {@link ScopeMetadataResolver} to use for detected bean classes.
	 * The default is an {@link AnnotationScopeMetadataResolver}.
	 * Any call to this method must occur prior to calls to {@link #register(Class...)} and/or {@link #scan(String...)}.
	 */
	public void setScopeMetadataResolver(ScopeMetadataResolver scopeMetadataResolver) {
		reader.setScopeMetadataResolver(scopeMetadataResolver);
		scanner.setScopeMetadataResolver(scopeMetadataResolver);
	}

	//---------------------------------------------------------------------
	// Implementation of 【AnnotationConfigRegistry】 interface
	//---------------------------------------------------------------------
	/**
	 * Register one or more annotated classes to be processed.
	 * Note that {@link #refresh()} must be called in order for the context to fully process the new classes.
	 *  注意，必须要调用refresh()方法，上下文才能完全处理新类。 (in order for 才能)
	 * @param annotatedClasses one or more annotated classes,e.g. {@link Configuration @Configuration} classes
	 * @see #scan(String...)
	 * @see #refresh()
	 */
	public void register(Class<?>... annotatedClasses) {
		Assert.notEmpty(annotatedClasses, "At least one annotated class must be specified");
		reader.register(annotatedClasses);
	}

	/**
	 * Perform a scan within the specified base packages.
	 * Note that {@link #refresh()} must be called in order for the context to fully process the new classes.
	 * @param basePackages the packages to check for annotated classes
	 * @see #register(Class...)
	 * @see #refresh()
	 */
	public void scan(String... basePackages) {
		Assert.notEmpty(basePackages, "At least one base package must be specified");
		scanner.scan(basePackages);
	}


	//---------------------------------------------------------------------
	// Convenient methods for registering individual beans
	//---------------------------------------------------------------------
	/**
	 * Register a bean from the given bean class, deriving its metadata from class-declared annotations,
	 * and optionally providing explicit constructor arguments for consideration in the autowiring process.
	 * The bean name will be generated according to annotated component rules.
	 * @param annotatedClass the class of the bean
	 * @param constructorArguments argument values to be fed into Spring's
	 * constructor resolution algorithm, resolving either all arguments or just
	 * specific ones, with the rest to be resolved through regular autowiring (may be {@code null} or empty)
	 * @since 5.0
	 */
	public <T> void registerBean(Class<T> annotatedClass, Object... constructorArguments) {
		registerBean(null, annotatedClass, constructorArguments);
	}

	/**
	 * Register a bean from the given bean class, deriving its metadata from class-declared annotations,
	 * and optionally providing explicit constructor arguments for consideration in the autowiring process.
	 * @param beanName the name of the bean (may be {@code null})
	 * @param annotatedClass the class of the bean
	 * @param constructorArguments argument values to be fed into Spring's
	 * constructor resolution algorithm, resolving either all arguments or just
	 * specific ones, with the rest to be resolved through regular autowiring (may be {@code null} or empty)
	 * @since 5.0
	 */
	public <T> void registerBean(@Nullable String beanName, Class<T> annotatedClass, Object... constructorArguments) {
		reader.doRegisterBean(annotatedClass, null, beanName, null,
				bd -> {
					for (Object arg : constructorArguments) {
						bd.getConstructorArgumentValues().addGenericArgumentValue(arg);
					}
				});
	}

	@Override
	public <T> void registerBean(@Nullable String beanName, Class<T> beanClass, @Nullable Supplier<T> supplier,BeanDefinitionCustomizer... customizers) {
		reader.doRegisterBean(beanClass, supplier, beanName, null, customizers);
	}
}
