

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
	// includeFilters中的就是满足过滤规则的  过滤后的类被认为是候选组件
	private final List<TypeFilter> includeFilters = new LinkedList<>();
	// excludeFilters则是不满足过滤规则的  排除在候选组件之外
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
	 * This will implicitly register all annotations that have the {@link Component @Component} meta-annotation
	 * including the {@link Repository @Repository}, {@link Service @Service}, and {@link Controller @Controller} stereotype annotations.
	 * Also supports Java EE 6's {@link javax.annotation.ManagedBean} and  JSR-330's {@link javax.inject.Named} annotations, if available.
	 */
	@SuppressWarnings("unchecked")
	protected void registerDefaultFilters() {
		// 这里需要注意，默认情况下都是添加了@Component这个注解的（相当于@Service @Controller @Respository等都会扫描，因为这些注解都属于@Component）  另外@Configuration也属于哦
		logger.warn("【 设置默认注解扫描类型】 @Component ");
		includeFilters.add(new AnnotationTypeFilter(Component.class));
		ClassLoader cl = ClassPathScanningCandidateComponentProvider.class.getClassLoader();
		//下面两个 是兼容JSR-250的 @ManagedBean 和330的 @Named 注解
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
	 * 扫描指定的包路径，获取相应的BeanDefinition。扫描后的类可以通过过滤器进行排除。
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
			// Spring 5之前的方式（绝大多数情况下，都是此方式）
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
	 * @Title：扫描指定路径下的所有候选组件 @Component 系列注解 （通常要排除主配置类，以免进入死循环）
	 * @author fan.yang
	 * @date 2019年11月28日14:46:38
	 * @param basePackage  example.scannable
	 * @return
	 */
	private Set<BeanDefinition> scanCandidateComponents(String basePackage) {
		Set<BeanDefinition> candidates = new LinkedHashSet<>();
		try {
			// 获取包路径
			// 1.根据指定包名 生成包搜索路径
			// 通过观察 resolveBasePackage()方法的实现, 我们可以在设置basePackage时, 使用形如${}的占位符, Spring会在这里进行替换 只要在Enviroment里面就行
			// 本次值为：classpath*:com/fsx/config/**/*.class 即 扫描所有.class文件 classpath*:com/mydemo/**/*.class
			String packageSearchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + resolveBasePackage(basePackage) + '/' + resourcePattern;
			//2. 资源加载器 加载搜索路径下的 所有class 转换为 Resource[]
			// 拿着上面的路径，就可以getResources获取出所有的.class类，这个很强大~~~
			// 真正干事的为：PathMatchingResourcePatternResolver#getResources方法
			// 此处能扫描到两个类AppConfig（普通类，没任何注解标注）和RootConfig。所以接下里就是要解析类上的注解，以及过滤掉不是候选的类（比如AppConfig）
			// 注意：这里会拿到类路径下（不包含jar包内的）的所有的.class文件 可能有上百个，然后后面再交给后面进行筛选~~~~~~~~~~~~~~~~（这个方法，其实我们也可以使用）
			// 当然和getResourcePatternResolver和这个模版有关
			Resource[] resources = getResourcePatternResolver().getResources(packageSearchPath);// classpath*:example/scannable/**/*.class
			boolean traceEnabled = logger.isTraceEnabled();
			boolean debugEnabled = logger.isDebugEnabled();
			// 接下来的这个for循环：就是把一个个的resource组装成
			for (Resource resource : resources) {
				if (traceEnabled) logger.trace("Scanning " + resource);
				// 需要时可读的 // 文件必须可读 否则直接返回空了
				if (resource.isReadable()) {
					try {
						// 获取封装了resource的MetadataReader //3. 使用ASM进行元信息读取
						// 读取类的 注解信息 和 类信息 ，两大信息储存到  MetadataReader
						MetadataReader metadataReader = getMetadataReaderFactory().getMetadataReader(resource);
						// 检查metadataReader中的对象的className是否符合指定的excludeFilters和includeFilters的筛选
						// 根据TypeFilter过滤排除组件。因为AppConfig没有标准@Component或者子注解，所以肯定不属于候选组件  返回false
						// 注意：这里一般(默认处理的情况下)标注了默认注解的才会true，什么叫默认注解呢？就是@Component或者派生注解。还有javax....的，这里省略啦
						// 4. 这里很关键里面会进行IncludeFilter和ExcludeFilter的判断，也是能自定义扩展组件扫描的核心方法
						// 判断该类是否符合@CompoentScan的过滤规则
						// 过滤匹配排除excludeFilters排除过滤器(可以没有),包含includeFilter中的包含过滤器（至少包含一个）。
						if (isCandidateComponent(metadataReader)) {
							// 把符合条件的 类转换成 ScannedGenericBeanDefinition 对象
							// 5. 拼装成BeanDefinition，后面会给BeanDefinition设置beanClass为MapperFactoryBean代理对象
							//6. 最后注册到IOC容器中，此时我们已经可以使用Mybatis的Mapper来完成依赖注入和依赖查找了
							ScannedGenericBeanDefinition sbd = new ScannedGenericBeanDefinition(metadataReader);
							sbd.setResource(resource);
							sbd.setSource(resource);
							// 检查对应的对象1.是不是一个独立的类；2.要是一个具体的类，不能是抽象类或接口，如果是抽象的那么必须有对应的Lookup注解指定实现的方法
							// 再次判断 如果是实体类 返回true,如果是抽象类，但是抽象方法 被 @Lookup 注解注释返回true （注意 这个和上面那个是重载的方法）
							// 这和上面是个重载方法  个人觉得旨在处理循环引用以及@Lookup上
							if (isCandidateComponent(sbd)) {
								if (debugEnabled) logger.debug("Identified candidate component class: " + resource);
								// 加入到候选的bean中
								candidates.add(sbd);
							}else {
								// 不合格，不是顶级类、具体类
								if (debugEnabled) logger.debug("Ignored because not a concrete top-level class: " + resource);
							}
						}else {
							// 不合格，不符合@CompoentScan过滤规则
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
	 * 判断通过filter筛选后的class是否是候选组件，默认实现是一个具体类。这是一个 protected 的方法，可以通过子类重写它。
	 * 有些框架只需要扫描接口，并注册FactoryBean到bd，然后通过动态代理实现该接口得到目标bean，比如feign。
	 * Determine whether the given class does not match any exclude filter and does match at least one include filter.
	 * @param metadataReader the ASM ClassReader for the class
	 * @return whether the class qualifies as a candidate component
	 */
	protected boolean isCandidateComponent(MetadataReader metadataReader) throws IOException {
		// exclude  TypeFilter 满足直接返回false
		// this.excludeFilters 除非用户显式配置, 否则默认为空
		for (TypeFilter tf : excludeFilters) {
			if (tf.match(metadataReader, getMetadataReaderFactory())) {
				return false;
			}
		}
		// 这里就需要注意一下了
		// 我在前面没有进行讲解的configureScanner方法里有这么一个细节,
		//   <context:component-scan/> 的"use-default-filters"的属性值默认是true . 这一点可以在configureScanner方法中进行验证
		//  于是我们追踪对useDefaultFilters字段的调用来到ClassPathBeanDefinitionScanner的基类 ClassPathScanningCandidateComponentProvider 中就会发现
		//   useDefaultFilters字段为true时, 会默认注册如下几个AnnotationTypeFilter到includeFilters字段中:
		//      1. new AnnotationTypeFilter(Component.class)
		//      2. new AnnotationTypeFilter(((Class<? extends Annotation>) ClassUtils.forName("javax.annotation.ManagedBean", cl)), false)
		//      3. new AnnotationTypeFilter(((Class<? extends Annotation>) ClassUtils.forName("javax.inject.Named", cl)), false)
		//   @Repository、 @Controller、 @Service、@Configuration都被@Component注解所修饰

		// 遍历所有的IncludeFilter，若匹配则进行Conditional条件注解判断，这里includeFilters中就包括了之前在ClassPathMapperScanner#registerFilters()方法中注册的includeFilters。
		// 这也是为什么我们配置了 @MapperScan(basePakages="xxxx")就能扫描到xxx包下的所有类到ioc容器中的所有原理
		// 即使通过了includeFilters， 还会再去判断一次 Conditional 注解。 只有在被@Conditional注解时, 才会进行更详细的判断, 否则直接返回true
		// Condition的实例化也是直接反射,  ConditionEvaluator.getCondition()中
		for (TypeFilter tf : includeFilters) {
			//判断当前类的注解是否match规则
			if (tf.match(metadataReader, getMetadataReaderFactory())) {
				//是否有@Conditional注解，进行相关处理
				return isConditionMatch(metadataReader);
			}
		}
		//如果读取的类的注解既不在排除规则，也不在包含规则中，则返回false
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
