

package org.springframework.context.annotation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.aop.framework.autoproxy.AutoProxyUtils;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.beans.factory.config.SingletonBeanRegistry;
import org.springframework.beans.factory.parsing.FailFastProblemReporter;
import org.springframework.beans.factory.parsing.PassThroughSourceExtractor;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.beans.factory.parsing.SourceExtractor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.annotation.ConfigurationClassEnhancer.EnhancedConfiguration;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import static org.springframework.context.annotation.AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR;

/**
 * {@link BeanFactoryPostProcessor} used for bootstrapping processing of {@link Configuration @Configuration} classes.
 * Registered by default when using {@code <context:annotation-config/>} or
 * {@code <context:component-scan/>}. Otherwise, may be declared manually as with any other BeanFactoryPostProcessor.
 * This post processor is priority-ordered as it is important that any {@link Bean} methods declared in {@code @Configuration} classes have
 * their corresponding bean definitions registered before any other {@link BeanFactoryPostProcessor} executes.
 * @since 3.0
 * 用来处理@Configuration，@Import，@ImportResource和类内部的@Bean 注解
 */
public class ConfigurationClassPostProcessor implements BeanDefinitionRegistryPostProcessor, PriorityOrdered, ResourceLoaderAware, BeanClassLoaderAware, EnvironmentAware {

	private static final String IMPORT_REGISTRY_BEAN_NAME = ConfigurationClassPostProcessor.class.getName() + ".importRegistry";

	private final Log logger = LogFactory.getLog(getClass());

	private SourceExtractor sourceExtractor = new PassThroughSourceExtractor();

	private ProblemReporter problemReporter = new FailFastProblemReporter();

	@Nullable
	private Environment environment;

	private ResourceLoader resourceLoader = new DefaultResourceLoader();

	@Nullable
	private ClassLoader beanClassLoader = ClassUtils.getDefaultClassLoader();

	private MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory();

	private boolean setMetadataReaderFactoryCalled = false;

	private final Set<Integer> registriesPostProcessed = new HashSet<>();

	private final Set<Integer> factoriesPostProcessed = new HashSet<>();

	@Nullable
	private ConfigurationClassBeanDefinitionReader reader;

	private boolean localBeanNameGeneratorSet = false;

	/* Using short class names as default bean names */
	private BeanNameGenerator componentScanBeanNameGenerator = new AnnotationBeanNameGenerator();

	/* Using fully qualified class names as default bean names */
	private BeanNameGenerator importBeanNameGenerator = new AnnotationBeanNameGenerator() {
		@Override
		protected String buildDefaultBeanName(BeanDefinition definition) {
			String beanClassName = definition.getBeanClassName();
			Assert.state(beanClassName != null, "No bean class name set");
			return beanClassName;
		}
	};

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;  // within PriorityOrdered
	}

	/**
	 * Set the {@link SourceExtractor} to use for generated bean definitions that correspond to {@link Bean} factory methods.
	 */
	public void setSourceExtractor(@Nullable SourceExtractor sourceExtractor) {
		this.sourceExtractor = (sourceExtractor != null ? sourceExtractor : new PassThroughSourceExtractor());
	}

	/**
	 * Set the {@link ProblemReporter} to use.
	 * Used to register any problems detected with {@link Configuration} or {@link Bean} declarations.
	 * For instance, an @Bean method marked as {@code final} is illegal and would be reported as a problem. Defaults to {@link FailFastProblemReporter}.
	 */
	public void setProblemReporter(@Nullable ProblemReporter problemReporter) {
		this.problemReporter = (problemReporter != null ? problemReporter : new FailFastProblemReporter());
	}

	/**
	 * Set the {@link MetadataReaderFactory} to use.
	 * Default is a {@link CachingMetadataReaderFactory} for the specified {@linkplain #setBeanClassLoader bean class loader}.
	 */
	public void setMetadataReaderFactory(MetadataReaderFactory metadataReaderFactory) {
		Assert.notNull(metadataReaderFactory, "MetadataReaderFactory must not be null");
		this.metadataReaderFactory = metadataReaderFactory;
		this.setMetadataReaderFactoryCalled = true;
	}

	/**
	 * Set the {@link BeanNameGenerator} to be used when triggering component scanning from {@link Configuration} classes and when registering {@link Import}'ed configuration classes.
	 * The default is a standard {@link AnnotationBeanNameGenerator} for scanned components (compatible with the default in {@link ClassPathBeanDefinitionScanner})
	 * and a variant thereof for imported configuration classes (using unique fully-qualified class names instead of standard component overriding).
	 * Note that this strategy does <em>not</em> apply to {@link Bean} methods.
	 * This setter is typically only appropriate when configuring the post-processor as a standalone bean definition in XML,
	 * e.g. not using the dedicated {@code AnnotationConfig*} application contexts or the {@code <context:annotation-config>} element.
	 * Any bean name generator specified against  the application context will take precedence over any value set here.
	 * @since 3.1.1
	 * @see AnnotationConfigApplicationContext#setBeanNameGenerator(BeanNameGenerator)
	 * @see AnnotationConfigUtils#CONFIGURATION_BEAN_NAME_GENERATOR
	 */
	public void setBeanNameGenerator(BeanNameGenerator beanNameGenerator) {
		Assert.notNull(beanNameGenerator, "BeanNameGenerator must not be null");
		localBeanNameGeneratorSet = true;
		componentScanBeanNameGenerator = beanNameGenerator;
		importBeanNameGenerator = beanNameGenerator;
	}

	@Override
	public void setEnvironment(Environment environment) {
		Assert.notNull(environment, "Environment must not be null");
		this.environment = environment;
	}

	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		Assert.notNull(resourceLoader, "ResourceLoader must not be null");
		this.resourceLoader = resourceLoader;
		if (!setMetadataReaderFactoryCalled) {
			metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
		}
	}

	@Override
	public void setBeanClassLoader(ClassLoader beanClassLoader) {
		this.beanClassLoader = beanClassLoader;
		if (!setMetadataReaderFactoryCalled) {
			metadataReaderFactory = new CachingMetadataReaderFactory(beanClassLoader);
		}
	}

	// Derive further bean definitions from the configuration classes in the registry.
	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
		// 根据BeanDefinitionRegistry,生成registryId 是全局唯一的。
		int registryId = System.identityHashCode(registry);
		// 判断，如果这个registryId 已经被执行过了，就不能够再执行了，否则抛出异常
		if (registriesPostProcessed.contains(registryId)) {
			throw new IllegalStateException("postProcessBeanDefinitionRegistry already called on this post-processor against " + registry);
		}
		if (factoriesPostProcessed.contains(registryId)) {
			throw new IllegalStateException("postProcessBeanFactory already called on this post-processor against " + registry);
		}
		// 已经执行过的registry  防止重复执行
		registriesPostProcessed.add(registryId);
		// 调用processConfigBeanDefinitions 进行Bean定义的加载
		processConfigBeanDefinitions(registry);
	}

	// Prepare the Configuration classes for servicing bean requests at runtime  by replacing them with CGLIB-enhanced subclasses.
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		int factoryId = System.identityHashCode(beanFactory);
		if (factoriesPostProcessed.contains(factoryId)) {
			throw new IllegalStateException("postProcessBeanFactory already called on this post-processor against " + beanFactory);
		}
		factoriesPostProcessed.add(factoryId);
		if (!registriesPostProcessed.contains(factoryId)) {
			// BeanDefinitionRegistryPostProcessor hook apparently not supported... Simply call processConfigurationClasses lazily at this point then.
			processConfigBeanDefinitions((BeanDefinitionRegistry) beanFactory);
		}
		enhanceConfigurationClasses(beanFactory);
		beanFactory.addBeanPostProcessor(new ImportAwareBeanPostProcessor(beanFactory));
	}

	/**
	 * Build and validate a configuration model based on the registry of {@link Configuration} classes.
	 * 方法内部处理@Configuration，@Import，@ImportResource和类内部的@Bean
	 */
	public void processConfigBeanDefinitions(BeanDefinitionRegistry registry) {
		List<BeanDefinitionHolder> configCandidates = new ArrayList<>();
		String[] candidateNames = registry.getBeanDefinitionNames();// 获取已经注册的bean名称（此处有7个   6个Bean+rootConfig）
		for (String beanName : candidateNames) {
			BeanDefinition beanDef = registry.getBeanDefinition(beanName);
			// 这个判断很有意思~~~ 如果你的beanDef现在就已经确定了是full或者lite，说明你肯定已经被解析过了，，所以再来的话输出个debug即可（其实我觉得输出warn也行~~~）
			if (ConfigurationClassUtils.isFullConfigurationClass(beanDef) || ConfigurationClassUtils.isLiteConfigurationClass(beanDef)) {
				if (logger.isDebugEnabled()) logger.debug("Bean definition has already been processed as a configuration class: " + beanDef);
			}else if (ConfigurationClassUtils.checkConfigurationClassCandidate(beanDef, metadataReaderFactory)) {
				// 检查是否是@Configuration的Class,如果是就标记下属性：full 或者lite。beanDef.setAttribute(CONFIGURATION_CLASS_ATTRIBUTE, CONFIGURATION_CLASS_FULL)
				// 加入到configCandidates里保存配置文件类的定义
				// 显然此处，仅仅只有rootConfig一个类符合条件
				configCandidates.add(new BeanDefinitionHolder(beanDef, beanName));
			}
		}
		// Return immediately if no @Configuration classes were found // 如果一个配置文件类都没找到，现在就不需要再继续下去了
		if (configCandidates.isEmpty()) return;
		// Sort by previously determined @Order value, if applicable
		configCandidates.sort((bd1, bd2) -> {
			int i1 = ConfigurationClassUtils.getOrder(bd1.getBeanDefinition());
			int i2 = ConfigurationClassUtils.getOrder(bd2.getBeanDefinition());
			return Integer.compare(i1, i2);
		});
		// Detect any custom bean name generation strategy supplied through the enclosing application context
		// 此处registry是DefaultListableBeanFactory 这里会进去
		// 尝试着给Bean扫描方式，以及import方法的BeanNameGenerator赋值(若我们都没指定，那就是默认的AnnotationBeanNameGenerator：扫描为首字母小写，import为全类名)
		SingletonBeanRegistry sbr = null;
		if (registry instanceof SingletonBeanRegistry) {
			sbr = (SingletonBeanRegistry) registry;
			if (!localBeanNameGeneratorSet) {
				BeanNameGenerator generator = (BeanNameGenerator) sbr.getSingleton(CONFIGURATION_BEAN_NAME_GENERATOR);
				if (generator != null) {
					componentScanBeanNameGenerator = generator;
					importBeanNameGenerator = generator;
				}
			}
		}
		// web环境，这里都设置了StandardServletEnvironment， 一般来说到此处，env环境不可能为null了~~~ 此处做一个容错处理~~~
		if (environment == null) environment = new StandardEnvironment();
		// Parse each @Configuration class
		// 这是重点：真正解析@Configuration类的，其实是ConfigurationClassParser 这个解析器来做的
		// parser 后面用于解析每一个配置类~~~~
		ConfigurationClassParser parser = new ConfigurationClassParser(metadataReaderFactory, problemReporter, environment,resourceLoader, componentScanBeanNameGenerator, registry);
		Set<BeanDefinitionHolder> candidates = new LinkedHashSet<>(configCandidates);
		// 装载已经处理过的配置类，最大长度为：configCandidates.size()
		Set<ConfigurationClass> alreadyParsed = new HashSet<>(configCandidates.size());
		do {
			// 核心方法：解析主配置类 包括处理配置类中的@Import注解
			parser.parse(candidates);
			// 校验 配置类不能使final的，因为需要使用CGLIB生成代理对象，见postProcessBeanFactory方法
			parser.validate();
			// 获取已经解析后的用户定义的配置类中的各种bean
			Set<ConfigurationClass> configClasses = new LinkedHashSet<>(parser.getConfigurationClasses());
			configClasses.removeAll(alreadyParsed);
			// Read the model and create bean definitions based on its content
			// 如果Reader为null，那就实例化ConfigurationClassBeanDefinitionReader来加载Bean，并加入到alreadyParsed中,用于去重（避免譬如@ComponentScan直接互扫）
			if (reader == null) {
				reader = new ConfigurationClassBeanDefinitionReader(registry, sourceExtractor, resourceLoader, environment,importBeanNameGenerator, parser.getImportRegistry());
			}
			// 此处注意：调用了ConfigurationClassBeanDefinitionReader的loadBeanDefinitionsd的加载配置文件里面的@Bean/@Import们，具体讲解请参见下面
			// 这个方法是非常重要的，因为它决定了向容器注册Bean定义信息的顺序问题~~~
			reader.loadBeanDefinitions(configClasses);
			alreadyParsed.addAll(configClasses);
			candidates.clear();
			// 如果registry中注册的bean的数量 大于 之前获得的数量,则意味着在解析过程中又新加入了很多,那么就需要对其进行解继续析
			if (registry.getBeanDefinitionCount() > candidateNames.length) {
				String[] newCandidateNames = registry.getBeanDefinitionNames();
				Set<String> oldCandidateNames = new HashSet<>(Arrays.asList(candidateNames));
				Set<String> alreadyParsedClasses = new HashSet<>();
				for (ConfigurationClass configurationClass : alreadyParsed) {
					alreadyParsedClasses.add(configurationClass.getMetadata().getClassName());
				}
				for (String candidateName : newCandidateNames) {
					// 这一步挺有意思：若老的oldCandidateNames不包含。也就是说你是新进来的候选的Bean定义们，那就进一步的进行一个处理
					// 比如这里的DelegatingWebMvcConfiguration，他就是新进的，因此它继续往下走
					// 这个@Import进来的配置类最终会被ConfigurationClassPostProcessor这个后置处理器的postProcessBeanFactory 方法，进行处理和cglib增强
					if (!oldCandidateNames.contains(candidateName)) {
						BeanDefinition bd = registry.getBeanDefinition(candidateName);
						if (ConfigurationClassUtils.checkConfigurationClassCandidate(bd, metadataReaderFactory) && !alreadyParsedClasses.contains(bd.getBeanClassName())) {
							candidates.add(new BeanDefinitionHolder(bd, candidateName));
						}
					}
				}
				candidateNames = newCandidateNames;
			}
		}
		while (!candidates.isEmpty());
		// Register the ImportRegistry as a bean in order to support ImportAware @Configuration classes
		// 如果SingletonBeanRegistry 不包含org.springframework.context.annotation.ConfigurationClassPostProcessor.importRegistry,
		// 则注册一个,bean 为 ImportRegistry. 一般都会进行注册的
		if (sbr != null && !sbr.containsSingleton(IMPORT_REGISTRY_BEAN_NAME)) {
			sbr.registerSingleton(IMPORT_REGISTRY_BEAN_NAME, parser.getImportRegistry());
		}
		//清除缓存 元数据缓存
		if (metadataReaderFactory instanceof CachingMetadataReaderFactory) {
			// Clear cache in externally provided MetadataReaderFactory; this is a no-op  for a shared cache since it'll be cleared by the ApplicationContext.
			((CachingMetadataReaderFactory) metadataReaderFactory).clearCache();
		}
	}

	/**
	 * Post-processes a BeanFactory in search of Configuration class BeanDefinitions;
	 * any candidates are then enhanced by a {@link ConfigurationClassEnhancer}.
	 * Candidate status is determined by BeanDefinition attribute metadata.
	 * @see ConfigurationClassEnhancer
	 */
	public void enhanceConfigurationClasses(ConfigurableListableBeanFactory beanFactory) {
		Map<String, AbstractBeanDefinition> configBeanDefs = new LinkedHashMap<>();
		for (String beanName : beanFactory.getBeanDefinitionNames()) {
			BeanDefinition beanDef = beanFactory.getBeanDefinition(beanName);
			if (ConfigurationClassUtils.isFullConfigurationClass(beanDef)) {
				if (!(beanDef instanceof AbstractBeanDefinition)) {
					throw new BeanDefinitionStoreException("Cannot enhance @Configuration bean definition '" + beanName + "' since it is not stored in an AbstractBeanDefinition subclass");
				}else if (logger.isInfoEnabled() && beanFactory.containsSingleton(beanName)) {
					logger.info("Cannot enhance @Configuration bean definition '" + beanName + "' since its singleton instance has been created too early. The typical cause " +
							"is a non-static @Bean method with a BeanDefinitionRegistryPostProcessor return type: Consider declaring such methods as 'static'.");
				}
				configBeanDefs.put(beanName, (AbstractBeanDefinition) beanDef);
			}
		}
		if (configBeanDefs.isEmpty()) {
			// nothing to enhance -> return immediately
			return;
		}
		ConfigurationClassEnhancer enhancer = new ConfigurationClassEnhancer();
		for (Map.Entry<String, AbstractBeanDefinition> entry : configBeanDefs.entrySet()) {
			AbstractBeanDefinition beanDef = entry.getValue();
			// If a @Configuration class gets proxied, always proxy the target class
			beanDef.setAttribute(AutoProxyUtils.PRESERVE_TARGET_CLASS_ATTRIBUTE, Boolean.TRUE);
			try {
				// Set enhanced subclass of the user-specified bean class
				Class<?> configClass = beanDef.resolveBeanClass(beanClassLoader);
				if (configClass != null) {
					Class<?> enhancedClass = enhancer.enhance(configClass, beanClassLoader);
					if (configClass != enhancedClass) {
						if (logger.isTraceEnabled()) {
							logger.trace(String.format("Replacing bean definition '%s' existing class '%s' with " + "enhanced class '%s'", entry.getKey(), configClass.getName(), enhancedClass.getName()));
						}
						beanDef.setBeanClass(enhancedClass);
					}
				}
			}catch (Throwable ex) {
				throw new IllegalStateException("Cannot load configuration class: " + beanDef.getBeanClassName(), ex);
			}
		}
	}

	private static class ImportAwareBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter {

		private final BeanFactory beanFactory;

		public ImportAwareBeanPostProcessor(BeanFactory beanFactory) {
			this.beanFactory = beanFactory;
		}

		@Override
		public PropertyValues postProcessProperties(@Nullable PropertyValues pvs, Object bean, String beanName) {
			// Inject the BeanFactory before AutowiredAnnotationBeanPostProcessor's
			// postProcessProperties method attempts to autowire other configuration beans.
			if (bean instanceof EnhancedConfiguration) {
				((EnhancedConfiguration) bean).setBeanFactory(beanFactory);
			}
			return pvs;
		}

		@Override
		public Object postProcessBeforeInitialization(Object bean, String beanName) {
			if (bean instanceof ImportAware) {
				ImportRegistry ir = beanFactory.getBean(IMPORT_REGISTRY_BEAN_NAME, ImportRegistry.class);
				AnnotationMetadata importingClass = ir.getImportingClassFor(bean.getClass().getSuperclass().getName());
				if (importingClass != null) {
					((ImportAware) bean).setImportMetadata(importingClass);
				}
			}
			return bean;
		}
	}
}
