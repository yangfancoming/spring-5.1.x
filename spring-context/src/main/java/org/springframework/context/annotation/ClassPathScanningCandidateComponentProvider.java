

package org.springframework.context.annotation;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.annotation.AnnotatedGenericBeanDefinition;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.context.index.CandidateComponentsIndex;
import org.springframework.context.index.CandidateComponentsIndexLoader;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Indexed;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

/**
 * A component provider that provides candidate components from a base package.
 * Can use {@link CandidateComponentsIndex the index} if it is available of scans the classpath otherwise.
 * Candidate components are identified by applying exclude and include filters.
 * {@link AnnotationTypeFilter}, {@link AssignableTypeFilter} include filters on an annotation/superclass that are annotated with {@link Indexed} are
 * supported: if any other include filter is specified, the index is ignored and classpath scanning is used instead.
 * This implementation is based on Spring's {@link org.springframework.core.type.classreading.MetadataReader MetadataReader}
 * facility, backed by an ASM {@link org.springframework.asm.ClassReader ClassReader}.
 * @since 2.5
 * @see org.springframework.core.type.classreading.MetadataReaderFactory
 * @see org.springframework.core.type.AnnotationMetadata
 * @see ScannedGenericBeanDefinition
 * @see CandidateComponentsIndex
 */
public class ClassPathScanningCandidateComponentProvider implements EnvironmentCapable, ResourceLoaderAware {

	static final String DEFAULT_RESOURCE_PATTERN = "**/*.class";

	protected final Log logger = LogFactory.getLog(getClass());

	private String resourcePattern = DEFAULT_RESOURCE_PATTERN;
	// includeFilters中的就是满足过滤规则的
	private final List<TypeFilter> includeFilters = new LinkedList<>();
	// excludeFilters则是不满足过滤规则的
	private final List<TypeFilter> excludeFilters = new LinkedList<>();

	@Nullable
	private Environment environment;

	@Nullable
	private ConditionEvaluator conditionEvaluator;

	@Nullable
	private ResourcePatternResolver resourcePatternResolver;

	@Nullable
	private MetadataReaderFactory metadataReaderFactory;

	@Nullable
	private CandidateComponentsIndex componentsIndex;

	/**
	 * Protected constructor for flexible subclass initialization.
	 * @since 4.3.6
	 */
	protected ClassPathScanningCandidateComponentProvider() {
	}

	/**
	 * Create a ClassPathScanningCandidateComponentProvider with a {@link StandardEnvironment}.
	 * @param useDefaultFilters whether to register the default filters for the
	 * {@link Component @Component}, {@link Repository @Repository},{@link Service @Service}, and {@link Controller @Controller} stereotype annotations
	 * @see #registerDefaultFilters()
	 */
	public ClassPathScanningCandidateComponentProvider(boolean useDefaultFilters) {
		this(useDefaultFilters, new StandardEnvironment());
	}

	/**
	 * Create a ClassPathScanningCandidateComponentProvider with the given {@link Environment}.
	 * @param useDefaultFilters whether to register the default filters for the
	 * {@link Component @Component}, {@link Repository @Repository},{@link Service @Service}, and {@link Controller @Controller} stereotype annotations
	 * @param environment the Environment to use
	 * @see #registerDefaultFilters()
	 */
	public ClassPathScanningCandidateComponentProvider(boolean useDefaultFilters, Environment environment) {
		if (useDefaultFilters) {
			registerDefaultFilters();
		}
		setEnvironment(environment);
		setResourceLoader(null);
	}

	/**
	 * Set the resource pattern to use when scanning the classpath.
	 * This value will be appended to each base package name.
	 * @see #findCandidateComponents(String)
	 * @see #DEFAULT_RESOURCE_PATTERN
	 */
	public void setResourcePattern(String resourcePattern) {
		Assert.notNull(resourcePattern, "'resourcePattern' must not be null");
		this.resourcePattern = resourcePattern;
	}

	/**
	 * Add an include type filter to the <i>end</i> of the inclusion list.
	 */
	public void addIncludeFilter(TypeFilter includeFilter) {
		includeFilters.add(includeFilter);
	}

	/**
	 * Add an exclude type filter to the <i>front</i> of the exclusion list.
	 */
	public void addExcludeFilter(TypeFilter excludeFilter) {
		excludeFilters.add(0, excludeFilter);
	}

	/**
	 * Reset the configured type filters.
	 * @param useDefaultFilters whether to re-register the default filters for the {@link Component @Component}, {@link Repository @Repository},
	 * {@link Service @Service}, and {@link Controller @Controller} stereotype annotations
	 * @see #registerDefaultFilters()
	 */
	public void resetFilters(boolean useDefaultFilters) {
		includeFilters.clear();
		excludeFilters.clear();
		if (useDefaultFilters) {
			registerDefaultFilters();
		}
	}

	/**
	 * Register the default filter for {@link Component @Component}.
	 * This will implicitly register all annotations that have the
	 * {@link Component @Component} meta-annotation including the
	 * {@link Repository @Repository}, {@link Service @Service}, and
	 * {@link Controller @Controller} stereotype annotations.
	 * Also supports Java EE 6's {@link javax.annotation.ManagedBean} and  JSR-330's {@link javax.inject.Named} annotations, if available.
	 */
	@SuppressWarnings("unchecked")
	protected void registerDefaultFilters() {
		// 这里需要注意，默认情况下都是添加了@Component这个注解的（相当于@Service @Controller @Respository等都会扫描，因为这些注解都属于@Component）  另外@Configuration也属于哦
		includeFilters.add(new AnnotationTypeFilter(Component.class));
		ClassLoader cl = ClassPathScanningCandidateComponentProvider.class.getClassLoader();
		//下面两个 是兼容JSR-250的@ManagedBean和330的@Named注解
		try {
			includeFilters.add(new AnnotationTypeFilter(((Class<? extends Annotation>) ClassUtils.forName("javax.annotation.ManagedBean", cl)), false));
			logger.trace("JSR-250 'javax.annotation.ManagedBean' found and supported for component scanning");
		}catch (ClassNotFoundException ex) {
			// JSR-250 1.1 API (as included in Java EE 6) not available - simply skip.
		}
		try {
			includeFilters.add(new AnnotationTypeFilter(((Class<? extends Annotation>) ClassUtils.forName("javax.inject.Named", cl)), false));
			logger.trace("JSR-330 'javax.inject.Named' annotation found and supported for component scanning");
		}catch (ClassNotFoundException ex) {
			// JSR-330 API not available - simply skip.
		}
		// 所以，如果你想Spring连你自定义的注解都扫描，自己实现一个AnnotationTypeFilter就可以啦
	}

	/**
	 * Set the Environment to use when resolving placeholders and evaluating {@link Conditional @Conditional}-annotated component classes.
	 * The default is a {@link StandardEnvironment}.
	 * @param environment the Environment to use
	 */
	public void setEnvironment(Environment environment) {
		Assert.notNull(environment, "Environment must not be null");
		this.environment = environment;
		this.conditionEvaluator = null;
	}

	@Override
	public final Environment getEnvironment() {
		if (environment == null) environment = new StandardEnvironment();
		return environment;
	}

	/**
	 * Return the {@link BeanDefinitionRegistry} used by this scanner, if any.
	 */
	@Nullable
	protected BeanDefinitionRegistry getRegistry() {
		return null;
	}

	/**
	 * Set the {@link ResourceLoader} to use for resource locations.
	 * This will typically be a {@link ResourcePatternResolver} implementation.
	 * Default is a {@code PathMatchingResourcePatternResolver}, also capable of resource pattern resolving through the {@code ResourcePatternResolver} interface.
	 * @see org.springframework.core.io.support.ResourcePatternResolver
	 * @see org.springframework.core.io.support.PathMatchingResourcePatternResolver
	 */
	@Override
	public void setResourceLoader(@Nullable ResourceLoader resourceLoader) {
		resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
		metadataReaderFactory = new CachingMetadataReaderFactory(resourceLoader);
		// Spring5以后才有这句，优化了bean扫描
		componentsIndex = CandidateComponentsIndexLoader.loadIndex(resourcePatternResolver.getClassLoader());
	}

	/**
	 * Return the ResourceLoader that this component provider uses.
	 */
	public final ResourceLoader getResourceLoader() {
		return getResourcePatternResolver();
	}

	private ResourcePatternResolver getResourcePatternResolver() {
		if (resourcePatternResolver == null) {
			resourcePatternResolver = new PathMatchingResourcePatternResolver();
		}
		return resourcePatternResolver;
	}

	/**
	 * Set the {@link MetadataReaderFactory} to use.
	 * Default is a {@link CachingMetadataReaderFactory} for the specified {@linkplain #setResourceLoader resource loader}.
	 * Call this setter method <i>after</i> {@link #setResourceLoader} in order for the given MetadataReaderFactory to override the default factory.
	 */
	public void setMetadataReaderFactory(MetadataReaderFactory metadataReaderFactory) {
		this.metadataReaderFactory = metadataReaderFactory;
	}

	/**
	 * Return the MetadataReaderFactory used by this component provider.
	 */
	public final MetadataReaderFactory getMetadataReaderFactory() {
		if (metadataReaderFactory == null) metadataReaderFactory = new CachingMetadataReaderFactory();
		return metadataReaderFactory;
	}

	/**
	 * Scan the class path for candidate components.
	 * @param basePackage the package to check for annotated classes
	 * @return a corresponding Set of autodetected bean definitions
	 */
	public Set<BeanDefinition> findCandidateComponents(String basePackage) {
		//componentsIndex对象包含了扫描“META-INF/spring.components”文件后封装起来的需要注册的bean的信息，在这里与来basePackage同时进行处理，
		//如果“META-INF/spring.components”文件不存在，则componentsIndex为null
		if (componentsIndex != null && indexSupportsIncludeFilters()) {
			return addCandidateComponentsFromIndex(componentsIndex, basePackage);
		}else { //只处理basePackage
			return scanCandidateComponents(basePackage);
		}
	}

	/**
	 * Determine if the index can be used by this instance.
	 * @return {@code true} if the index is available and the configuration of this instance is supported by it, {@code false} otherwise
	 * @since 5.0
	 */
	private boolean indexSupportsIncludeFilters() {
		for (TypeFilter includeFilter : includeFilters) {
			if (!indexSupportsIncludeFilter(includeFilter)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Determine if the specified include {@link TypeFilter} is supported by the index.
	 * @param filter the filter to check
	 * @return whether the index supports this include filter
	 * @since 5.0
	 * @see #extractStereotype(TypeFilter)
	 */
	private boolean indexSupportsIncludeFilter(TypeFilter filter) {
		if (filter instanceof AnnotationTypeFilter) {
			Class<? extends Annotation> annotation = ((AnnotationTypeFilter) filter).getAnnotationType();
			return (AnnotationUtils.isAnnotationDeclaredLocally(Indexed.class, annotation) || annotation.getName().startsWith("javax."));
		}
		if (filter instanceof AssignableTypeFilter) {
			Class<?> target = ((AssignableTypeFilter) filter).getTargetType();
			return AnnotationUtils.isAnnotationDeclaredLocally(Indexed.class, target);
		}
		return false;
	}

	/**
	 * Extract the stereotype to use for the specified compatible filter.
	 * @param filter the filter to handle
	 * @return the stereotype in the index matching this filter
	 * @since 5.0
	 * @see #indexSupportsIncludeFilter(TypeFilter)
	 */
	@Nullable
	private String extractStereotype(TypeFilter filter) {
		if (filter instanceof AnnotationTypeFilter) {
			return ((AnnotationTypeFilter) filter).getAnnotationType().getName();
		}
		if (filter instanceof AssignableTypeFilter) {
			return ((AssignableTypeFilter) filter).getTargetType().getName();
		}
		return null;
	}

	private Set<BeanDefinition> addCandidateComponentsFromIndex(CandidateComponentsIndex index, String basePackage) {
		Set<BeanDefinition> candidates = new LinkedHashSet<>();
		try {
			Set<String> types = new HashSet<>();
			for (TypeFilter filter : includeFilters) {
				String stereotype = extractStereotype(filter);
				if (stereotype == null) throw new IllegalArgumentException("Failed to extract stereotype from " + filter);
				types.addAll(index.getCandidateTypes(basePackage, stereotype));
			}
			boolean traceEnabled = logger.isTraceEnabled();
			boolean debugEnabled = logger.isDebugEnabled();
			for (String type : types) {
				MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(type);
				if (isCandidateComponent(metadataReader)) {
					AnnotatedGenericBeanDefinition sbd = new AnnotatedGenericBeanDefinition(metadataReader.getAnnotationMetadata());
					if (isCandidateComponent(sbd)) {
						if (debugEnabled) logger.debug("Using candidate component class from index: " + type);
						candidates.add(sbd);
					}else {
						if (debugEnabled) logger.debug("Ignored because not a concrete top-level class: " + type);
					}
				}else {
					if (traceEnabled) logger.trace("Ignored because matching an exclude filter: " + type);
				}
			}
		}catch (IOException ex) {
			throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
		}
		return candidates;
	}

	/**
	 * @Title：
	 * @author fan.yang
	 * @date 2019年11月28日14:46:38
	 * @param basePackage  example.scannable
	 * @return
	 */
	private Set<BeanDefinition> scanCandidateComponents(String basePackage) {
		Set<BeanDefinition> candidates = new LinkedHashSet<>();
		try {
			//获取包路径
			String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resolveBasePackage(basePackage) + '/' + resourcePattern;
			//将对应的包中的类封装成resources
			Resource[] resources = getResourcePatternResolver().getResources(packageSearchPath);// classpath*:example/scannable/**/*.class
			boolean traceEnabled = logger.isTraceEnabled();
			boolean debugEnabled = logger.isDebugEnabled();
			for (Resource resource : resources) {
				if (traceEnabled) logger.trace("Scanning " + resource);
				//需要时可读的
				if (resource.isReadable()) {
					try {
						//获取封装了resource的MetadataReader
						MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(resource);
						//检查metadataReader中的对象的className是否符合指定的excludeFilters跟includeFilters的筛选
						if (isCandidateComponent(metadataReader)) {
							//创建一个ScannedGenericBeanDefinition对象
							ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
							sbd.setResource(resource);
							sbd.setSource(resource);
							//检查对应的对象1.是不是一个独立的类；2.一个具体的类不是抽象的不是接口类，如果是抽象的那么必须有对应的Lookup注解指定实现的方法
							if (isCandidateComponent(sbd)) {
								if (debugEnabled) logger.debug("Identified candidate component class: " + resource);
								//加入到候选的bean中
								candidates.add(sbd);
							}else {
								if (debugEnabled) logger.debug("Ignored because not a concrete top-level class: " + resource);
							}
						}else {
							if (traceEnabled) logger.trace("Ignored because not matching any filter: " + resource);
						}
					}catch (Throwable ex) {
						throw new BeanDefinitionStoreException("Failed to read candidate component class: " + resource, ex);
					}
				}else {
					if (traceEnabled) logger.trace("Ignored because not readable: " + resource);
				}
			}
		}catch (IOException ex) {
			throw new BeanDefinitionStoreException("I/O failure during classpath scanning", ex);
		}
		return candidates;
	}

	/**
	 * Resolve the specified base package into a pattern specification for the package search path.
	 * The default implementation resolves placeholders against system properties,
	 * and converts a "."-based package path to a "/"-based resource path.
	 * @param basePackage the base package as specified by the user
	 * @return the pattern specification to be used for package searching
	 */
	protected String resolveBasePackage(String basePackage) {
		return ClassUtils.convertClassNameToResourcePath(getEnvironment().resolveRequiredPlaceholders(basePackage));
	}

	/**
	 * Determine whether the given class does not match any exclude filter and does match at least one include filter.
	 * @param metadataReader the ASM ClassReader for the class
	 * @return whether the class qualifies as a candidate component
	 */
	protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
		for (TypeFilter tf : excludeFilters) {
			if (tf.match(metadataReader, getMetadataReaderFactory())) {
				return false;
			}
		}
		for (TypeFilter tf : includeFilters) {
			if (tf.match(metadataReader, getMetadataReaderFactory())) {
				return isConditionMatch(metadataReader);
			}
		}
		return false;
	}

	/**
	 * Determine whether the given class is a candidate component based on any {@code @Conditional} annotations.
	 * @param metadataReader the ASM ClassReader for the class
	 * @return whether the class qualifies as a candidate component
	 */
	private boolean isConditionMatch(MetadataReader metadataReader) {
		if (conditionEvaluator == null) {
			conditionEvaluator = new ConditionEvaluator(getRegistry(), environment, resourcePatternResolver);
		}
		return !conditionEvaluator.shouldSkip(metadataReader.getAnnotationMetadata());
	}

	/**
	 * Determine whether the given bean definition qualifies as candidate.
	 * The default implementation checks whether the class is not an interface and not dependent on an enclosing class.
	 * Can be overridden in subclasses.
	 * @param beanDefinition the bean definition to check
	 * @return whether the bean definition qualifies as a candidate component
	 */
	protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
		AnnotationMetadata metadata = beanDefinition.getMetadata();
		return (metadata.isIndependent() && (metadata.isConcrete() || (metadata.isAbstract() && metadata.hasAnnotatedMethods(Lookup.class.getName()))));
	}

	/**
	 * Clear the local metadata cache, if any, removing all cached class metadata.
	 */
	public void clearCache() {
		if (metadataReaderFactory instanceof CachingMetadataReaderFactory) {
			// Clear cache in externally provided MetadataReaderFactory; this is a no-op for a shared cache since it'll be cleared by the ApplicationContext.
			((CachingMetadataReaderFactory) metadataReaderFactory).clearCache();
		}
	}
}
