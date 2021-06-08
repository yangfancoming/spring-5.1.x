

package org.springframework.context.annotation;

import java.util.LinkedHashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionDefaults;
import org.springframework.beans.factory.support.BeanDefinitionReaderUtils;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.PatternMatchUtils;

/**
 * A bean definition scanner that detects bean candidates on the classpath,registering corresponding bean definitions with a given registry ({@code BeanFactory} or {@code ApplicationContext}).
 * Candidate classes are detected through configurable type filters. The default filters include classes that are annotated with Spring's
 * {@link org.springframework.stereotype.Component @Component},{@link org.springframework.stereotype.Repository @Repository},
 * {@link org.springframework.stereotype.Service @Service}, or {@link org.springframework.stereotype.Controller @Controller} stereotype.
 * Also supports Java EE 6's {@link javax.annotation.ManagedBean} and JSR-330's {@link javax.inject.Named} annotations, if available.
 * @since 2.5
 * @see AnnotationConfigApplicationContext#scan
 * @see org.springframework.stereotype.Component
 * @see org.springframework.stereotype.Repository
 * @see org.springframework.stereotype.Service
 * @see org.springframework.stereotype.Controller
 * 是一个扫描指定类路径中注解Bean定义的扫描器，在它初始化的时候，会初始化一些需要被扫描的注解，初始化用于加载包下的资源的Loader。
 * @see AnnotatedBeanDefinitionReader
 */
public class ClassPathBeanDefinitionScanner extends ClassPathScanningCandidateComponentProvider {

	private final BeanDefinitionRegistry registry;

	private BeanDefinitionDefaults beanDefinitionDefaults = new BeanDefinitionDefaults();

	@Nullable
	private String[] autowireCandidatePatterns;

	private BeanNameGenerator beanNameGenerator = new AnnotationBeanNameGenerator();

	private ScopeMetadataResolver scopeMetadataResolver = new AnnotationScopeMetadataResolver();

	private boolean includeAnnotationConfig = true;

	/**
	 * Create a new {@code ClassPathBeanDefinitionScanner} for the given bean factory.
	 * @param registry the {@code BeanFactory} to load bean definitions into, in the form of a {@code BeanDefinitionRegistry}
	 */
	public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry) {
		this(registry, true);
	}

	/**
	 * Create a new {@code ClassPathBeanDefinitionScanner} for the given bean factory.
	 * If the passed-in bean factory does not only implement the {@code BeanDefinitionRegistry} interface but also the {@code ResourceLoader} interface,
	 * it will be used as default {@code ResourceLoader} as well.
	 * This will usually be the case for {@link org.springframework.context.ApplicationContext} implementations.
	 * If given a plain {@code BeanDefinitionRegistry}, the default {@code ResourceLoader} will be a {@link org.springframework.core.io.support.PathMatchingResourcePatternResolver}.
	 * If the passed-in bean factory also implements {@link EnvironmentCapable} its environment will be used by this reader.
	 * Otherwise, the reader will initialize and  use a {@link org.springframework.core.env.StandardEnvironment}.
	 * All {@code ApplicationContext} implementations are {@code EnvironmentCapable}, while normal {@code BeanFactory} implementations are not.
	 * @param registry the {@code BeanFactory} to load bean definitions into, in the form of a {@code BeanDefinitionRegistry}
	 * @param useDefaultFilters whether to include the default filters for the
	 * {@link org.springframework.stereotype.Component @Component},
	 * {@link org.springframework.stereotype.Repository @Repository},
	 * {@link org.springframework.stereotype.Service @Service}, and
	 * {@link org.springframework.stereotype.Controller @Controller} stereotype annotations
	 * @see #setResourceLoader
	 * @see #setEnvironment
	 */
	public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters) {
		this(registry, useDefaultFilters, getOrCreateEnvironment(registry));
	}

	/**
	 * Create a new {@code ClassPathBeanDefinitionScanner} for the given bean factory and  using the given {@link Environment} when evaluating bean definition profile metadata.
	 * If the passed-in bean factory does not only implement the {@code  BeanDefinitionRegistry} interface but also the {@link ResourceLoader} interface,
	 * it will be used as default {@code ResourceLoader} as well. This will usually be the
	 * case for {@link org.springframework.context.ApplicationContext} implementations.
	 * If given a plain {@code BeanDefinitionRegistry}, the default {@code ResourceLoader}  will be a {@link org.springframework.core.io.support.PathMatchingResourcePatternResolver}.
	 * @param registry the {@code BeanFactory} to load bean definitions into, in the form of a {@code BeanDefinitionRegistry}
	 * @param useDefaultFilters whether to include the default filters for the
	 * {@link org.springframework.stereotype.Component @Component},
	 * {@link org.springframework.stereotype.Repository @Repository},
	 * {@link org.springframework.stereotype.Service @Service}, and
	 * {@link org.springframework.stereotype.Controller @Controller} stereotype annotations
	 * @param environment the Spring {@link Environment} to use when evaluating bean
	 * definition profile metadata
	 * @since 3.1
	 * @see #setResourceLoader
	 */
	public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters,Environment environment) {
		this(registry, useDefaultFilters, environment,(registry instanceof ResourceLoader ? (ResourceLoader) registry : null));
	}

	/**
	 * 最终构造函数
	 * Create a new {@code ClassPathBeanDefinitionScanner} for the given bean factory and using the given {@link Environment} when evaluating bean definition profile metadata.
	 * @param registry the {@code BeanFactory} to load bean definitions into, in the form of a {@code BeanDefinitionRegistry}
	 * @param useDefaultFilters whether to include the default filters for the
	 * {@link org.springframework.stereotype.Component @Component},
	 * {@link org.springframework.stereotype.Repository @Repository},
	 * {@link org.springframework.stereotype.Service @Service}, and
	 * {@link org.springframework.stereotype.Controller @Controller} stereotype annotations
	 * @param environment the Spring {@link Environment} to use when evaluating bean definition profile metadata
	 * @param resourceLoader the {@link ResourceLoader} to use
	 * @since 4.3.6
	 */
	public ClassPathBeanDefinitionScanner(BeanDefinitionRegistry registry, boolean useDefaultFilters,Environment environment, @Nullable ResourceLoader resourceLoader) {
		logger.warn("进入 【ClassPathBeanDefinitionScanner】 构造函数 {}");
		Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
		this.registry = registry;
		//useDefaultFilters为true，所以此处一般都会执行，当然我们也可以设置为false，比如@ComponentScan里就可以设置为false，只扫描指定的注解/类等等
		if (useDefaultFilters) {
			registerDefaultFilters();
		}
		// 设置环境
		setEnvironment(environment);
		// 详情如下：  这里resourceLoader传值，还是我们的工厂。否则为null
		setResourceLoader(resourceLoader);
	}

	/**
	 * Return the BeanDefinitionRegistry that this scanner operates on.
	 */
	@Override
	public final BeanDefinitionRegistry getRegistry() {
		return registry;
	}

	/**
	 * Set the defaults to use for detected beans.
	 * @see BeanDefinitionDefaults
	 */
	public void setBeanDefinitionDefaults(@Nullable BeanDefinitionDefaults beanDefinitionDefaults) {
		this.beanDefinitionDefaults = (beanDefinitionDefaults != null ? beanDefinitionDefaults : new BeanDefinitionDefaults());
	}

	/**
	 * Return the defaults to use for detected beans (never {@code null}).
	 * @since 4.1
	 */
	public BeanDefinitionDefaults getBeanDefinitionDefaults() {
		return beanDefinitionDefaults;
	}

	/**
	 * Set the name-matching patterns for determining autowire candidates.
	 * @param autowireCandidatePatterns the patterns to match against
	 */
	public void setAutowireCandidatePatterns(@Nullable String... autowireCandidatePatterns) {
		this.autowireCandidatePatterns = autowireCandidatePatterns;
	}

	/**
	 * Set the BeanNameGenerator to use for detected bean classes.
	 * Default is a {@link AnnotationBeanNameGenerator}.
	 */
	public void setBeanNameGenerator(@Nullable BeanNameGenerator beanNameGenerator) {
		this.beanNameGenerator = (beanNameGenerator != null ? beanNameGenerator : new AnnotationBeanNameGenerator());
	}

	/**
	 * Set the ScopeMetadataResolver to use for detected bean classes.
	 * Note that this will override any custom "scopedProxyMode" setting.
	 * The default is an {@link AnnotationScopeMetadataResolver}.
	 * @see #setScopedProxyMode
	 */
	public void setScopeMetadataResolver(@Nullable ScopeMetadataResolver scopeMetadataResolver) {
		this.scopeMetadataResolver = (scopeMetadataResolver != null ? scopeMetadataResolver : new AnnotationScopeMetadataResolver());
	}

	/**
	 * Specify the proxy behavior for non-singleton scoped beans.
	 * Note that this will override any custom "scopeMetadataResolver" setting.
	 * The default is {@link ScopedProxyMode#NO}.
	 * @see #setScopeMetadataResolver
	 */
	public void setScopedProxyMode(ScopedProxyMode scopedProxyMode) {
		scopeMetadataResolver = new AnnotationScopeMetadataResolver(scopedProxyMode);
	}

	/**
	 * Specify whether to register annotation config post-processors.
	 * The default is to register the post-processors. Turn this off
	 * to be able to ignore the annotations or to process them differently.
	 */
	public void setIncludeAnnotationConfig(boolean includeAnnotationConfig) {
		this.includeAnnotationConfig = includeAnnotationConfig;
	}

	/**
	 * Perform a scan within the specified base packages.
	 * @param basePackages the packages to check for annotated classes
	 * @return number of beans registered
	 */
	public int scan(String... basePackages) {
		int beanCountAtScanStart = registry.getBeanDefinitionCount();
		// 包扫描 核心逻辑在 doScan() 方法中实现
		doScan(basePackages);
		// Register annotation config processors, if necessary. // 注册注解处理器
		if (includeAnnotationConfig) {
			AnnotationConfigUtils.registerAnnotationConfigProcessors(registry);
		}
		return (registry.getBeanDefinitionCount() - beanCountAtScanStart);
	}

	/**
	 * Perform a scan within the specified base packages,returning the registered bean definitions.
	 * This method does <i>not</i> register an annotation config processor but rather leaves this up to the caller.
	 * @param basePackages the packages to check for annotated classes
	 * @return set of beans registered if any for tooling registration purposes (never {@code null})
	 */
	public Set<BeanDefinitionHolder> doScan(String... basePackages) {
		Assert.notEmpty(basePackages, "At least one base package must be specified");
		// 调用父类的doScan()方法，遍历basePackages中指定的所有包，扫描每个包下的Java文件并进行解析。
		// 使用之前注册的过滤器进行过滤，得到符合条件的BeanDefinitionHolder对象
		// 装载扫描到的Bean
		Set<BeanDefinitionHolder> beanDefinitions = new LinkedHashSet<>();
		// 循环需要扫描的包basePackage
		for (String basePackage : basePackages) {
			// 寻找合适的候选bean，并封装成BeanDefinition
			// 这个是重点，会把该包下面所有的Bean都扫描进去。Spring5和一下的处理方式不一样哦~
			Set<BeanDefinition> candidates = findCandidateComponents(basePackage);
			//处理寻找到的候选bean
			for (BeanDefinition candidate : candidates) {
				//调用ScopeMetadataResolver的resolveScopeMetadata为候选bean设置代理的方式ScopedProxyMode，默认是DEFAULT
				//这里ScopeMetadataResolver跟ScopedProxyMode都可以在ComponentScan中设置，分别是scopeResolver跟scopedProxy
				// 拿到Scope元数据：此处为singleton
				ScopeMetadata scopeMetadata = scopeMetadataResolver.resolveScopeMetadata(candidate);
				candidate.setScope(scopeMetadata.getScopeName());
				//使用BeanNameGenerator为候选bean生产bean的名称，默认使用的是AnnotationBeanNameGenerator。可以通过nameGenerator指定
				// 生成Bean的名称，默认为首字母小写。此处为"rootConfig"
				String beanName = beanNameGenerator.generateBeanName(candidate, registry);
				// 如果bean是AbstractBeanDefinition类型的，则使用AbstractBeanDefinition的默认属性
				// 此处为扫描的Bean，为 ScannedGenericBeanDefinition，所以肯定为true
				// 因此进来，执行postProcessBeanDefinition（对Bean定义信息做）   如下详解
				// 注意：只是添加些默认的Bean定义信息，并不是执行后置处理器~~~
				if (candidate instanceof AbstractBeanDefinition) {
					postProcessBeanDefinition((AbstractBeanDefinition) candidate, beanName);
				}
				// 如果是AnnotatedBeanDefinition类型的则设置对应的默认属性
				// 显然，此处也是true  也是完善比如Bean上的一些注解信息：比如@Lazy、@Primary、@DependsOn、@Role、@Description   @Role注解用于Bean的分类分组，没有太大的作用
				if (candidate instanceof AnnotatedBeanDefinition) {
					AnnotationConfigUtils.processCommonDefinitionAnnotations((AnnotatedBeanDefinition) candidate);
				}
				// 检查是不是候选bean，就是检查是否已经存在了
				// 检查这个Bean  比如如果dao包（一般配置的basePakage是这个）下的类是符合mybaits要求的则向spring IOC容器中注册它的BeanDefinition  所以这步检查第三方Bean的时候有必要检查一下
				if (checkCandidate(beanName, candidate)) {
					// 创建BeanDefinitionHolder对象
					BeanDefinitionHolder definitionHolder = new BeanDefinitionHolder(candidate, beanName);
					// 将BeanDefinition的属性设置到BeanDefinitionHolder
					// AnnotationConfigUtils类的applyScopedProxyMode方法根据注解Bean定义类中配置的作用域@Scope注解的值，为Bean定义应用相应的代理模式，主要是在Spring面向切面编程(AOP)中使用
					definitionHolder = AnnotationConfigUtils.applyScopedProxyMode(scopeMetadata, definitionHolder, registry);
					beanDefinitions.add(definitionHolder);
					//注册bean
					// 注意 注意 注意：这里已经把Bean注册进去工厂了，所有doScan()方法不接收返回值，也是没有任何问题的。。。。
					registerBeanDefinition(definitionHolder, registry);
				}
			}
		}
		return beanDefinitions;
	}

	/**
	 * Apply further settings to the given bean definition,
	 * beyond the contents retrieved from scanning the component class.
	 * @param beanDefinition the scanned bean definition
	 * @param beanName the generated bean name for the given bean
	 */
	protected void postProcessBeanDefinition(AbstractBeanDefinition beanDefinition, String beanName) {
		// 位Bean定义 执行些默认的信息
		// BeanDefinitionDefaults是个标准的javaBean，有一些默认值
		beanDefinition.applyDefaults(beanDefinitionDefaults);
		// 自动依赖注入 匹配路径（此处为null，不进来）
		if (autowireCandidatePatterns != null) {
			beanDefinition.setAutowireCandidate(PatternMatchUtils.simpleMatch(autowireCandidatePatterns, beanName));
		}
	}

	/**
	 * Register the specified bean with the given registry.
	 * Can be overridden in subclasses, e.g. to adapt the registration process or to register further bean definitions for each scanned bean.
	 * @param definitionHolder the bean definition plus bean name for the bean
	 * @param registry the BeanDefinitionRegistry to register the bean with
	 */
	protected void registerBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) {
		BeanDefinitionReaderUtils.registerBeanDefinition(definitionHolder, registry);
	}

	/**
	 * Check the given candidate's bean name, determining whether the corresponding bean definition needs to be registered or conflicts with an existing definition.
	 * @param beanName the suggested name for the bean
	 * @param beanDefinition the corresponding bean definition
	 * @return {@code true} if the bean can be registered as-is;  {@code false} if it should be skipped because there is an
	 * existing, compatible bean definition for the specified name
	 * @throws ConflictingBeanDefinitionException if an existing, incompatible bean definition has been found for the specified name
	 */
	protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) throws IllegalStateException {
		if (!registry.containsBeanDefinition(beanName)) {
			return true;
		}
		BeanDefinition existingDef = registry.getBeanDefinition(beanName);
		BeanDefinition originatingDef = existingDef.getOriginatingBeanDefinition();
		if (originatingDef != null) {
			existingDef = originatingDef;
		}
		if (isCompatible(beanDefinition, existingDef)) {
			return false;
		}
		throw new ConflictingBeanDefinitionException("Annotation-specified bean name '" + beanName +
				"' for bean class [" + beanDefinition.getBeanClassName() + "] conflicts with existing, " +
				"non-compatible bean definition of same name and class [" + existingDef.getBeanClassName() + "]");
	}

	/**
	 * Determine whether the given new bean definition is compatible with the given existing bean definition.
	 * The default implementation considers them as compatible when the existing bean definition comes from the same source or from a non-scanning source.
	 * @param newDefinition the new bean definition, originated from scanning
	 * @param existingDefinition the existing bean definition, potentially an explicitly defined one or a previously generated one from scanning
	 * @return whether the definitions are considered as compatible, with the new definition to be skipped in favor of the existing definition
	 */
	protected boolean isCompatible(BeanDefinition newDefinition, BeanDefinition existingDefinition) {
		return (!(existingDefinition instanceof ScannedGenericBeanDefinition) ||  // explicitly registered overriding bean
				(newDefinition.getSource() != null && newDefinition.getSource().equals(existingDefinition.getSource())) ||  // scanned same file twice
				newDefinition.equals(existingDefinition));  // scanned equivalent class twice
	}

	/**
	 * Get the Environment from the given registry if possible, otherwise return a new StandardEnvironment.
	 */
	private static Environment getOrCreateEnvironment(BeanDefinitionRegistry registry) {
		Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
		if (registry instanceof EnvironmentCapable) {
			return ((EnvironmentCapable) registry).getEnvironment();
		}
		return new StandardEnvironment();
	}
}
