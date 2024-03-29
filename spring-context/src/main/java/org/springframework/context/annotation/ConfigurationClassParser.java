package org.springframework.context.annotation;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.parsing.Location;
import org.springframework.beans.factory.parsing.Problem;
import org.springframework.beans.factory.parsing.ProblemReporter;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionReader;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ConfigurationCondition.ConfigurationPhase;
import org.springframework.context.annotation.DeferredImportSelector.Group;
import org.springframework.core.NestedIOException;
import org.springframework.core.OrderComparator;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.AnnotationAwareOrderComparator;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.*;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.DefaultPropertySourceFactory;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.core.io.support.ResourcePropertySource;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.MethodMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.UnknownHostException;
import java.util.*;

/**
 * Parses a {@link Configuration} class definition, populating a collection of
 * {@link ConfigurationClass} objects (parsing a single Configuration class may result in
 * any number of ConfigurationClass objects because one Configuration class may import another using the {@link Import} annotation).
 * This class helps separate the concern of parsing the structure of a Configuration class from the concern of registering BeanDefinition objects based on the content of
 * that model (with the exception of {@code @ComponentScan} annotations which need to be registered immediately).
 * This ASM-based implementation avoids reflection and eager class loading in order to interoperate effectively with lazy class loading in a Spring ApplicationContext.
 * @since 3.0
 * @see ConfigurationClassBeanDefinitionReader
 * 这个工具类自身的逻辑并不注册bean定义，它的主要任务是发现@Configuration注解的所有配置类并将这些配置类交给调用者(调用者会通过其他方式注册其中的bean定义)，
 * 而对于非@Configuration注解的其他bean定义，比如@Component注解的bean定义，该工具类使用另外一个工具ComponentScanAnnotationParser扫描和注册它们。
 *
 * 一般情况下一个@Configuration注解的类只会产生一个ConfigurationClass对象，但是因为@Configuration注解的类可能会使用注解@Import引入其他配置类，
 * 也可能内部嵌套定义配置类，所以总的来看，ConfigurationClassParser分析一个@Configuration注解的类，可能产生任意多个ConfigurationClass对象。
 */
// modify- 添加 public
public class ConfigurationClassParser {

	private static final PropertySourceFactory DEFAULT_PROPERTY_SOURCE_FACTORY = new DefaultPropertySourceFactory();

	private static final Comparator<DeferredImportSelectorHolder> DEFERRED_IMPORT_COMPARATOR = (o1, o2) -> AnnotationAwareOrderComparator.INSTANCE.compare(o1.getImportSelector(), o2.getImportSelector());

	private final Log logger = LogFactory.getLog(getClass());

	private final MetadataReaderFactory metadataReaderFactory;

	private final ProblemReporter problemReporter;

	private final Environment environment;

	private final ResourceLoader resourceLoader;

	private final BeanDefinitionRegistry registry;

	private final ComponentScanAnnotationParser componentScanParser;

	private final ConditionEvaluator conditionEvaluator;

	private final Map<ConfigurationClass, ConfigurationClass> configurationClasses = new LinkedHashMap<>();

	private final Map<String, ConfigurationClass> knownSuperclasses = new HashMap<>();

	// 主配置类中 已加载的资源文件名称
	private final List<String> propertySourceNames = new ArrayList<>();

	private final ImportStack importStack = new ImportStack();

	private final DeferredImportSelectorHandler deferredImportSelectorHandler = new DeferredImportSelectorHandler();

	/**
	 * Create a new {@link ConfigurationClassParser} instance that will be used to populate the set of configuration classes.
	 */
	public ConfigurationClassParser(MetadataReaderFactory metadataReaderFactory,ProblemReporter problemReporter, Environment environment, ResourceLoader resourceLoader,BeanNameGenerator componentScanBeanNameGenerator, BeanDefinitionRegistry registry) {
		this.metadataReaderFactory = metadataReaderFactory;
		this.problemReporter = problemReporter;
		this.environment = environment;
		this.resourceLoader = resourceLoader;
		this.registry = registry;
		this.componentScanParser = new ComponentScanAnnotationParser(environment, resourceLoader, componentScanBeanNameGenerator, registry);
		this.conditionEvaluator = new ConditionEvaluator(registry, environment, resourceLoader);
	}

	public void parse(Set<BeanDefinitionHolder> configCandidates) {
		for (BeanDefinitionHolder holder : configCandidates) {
			BeanDefinition bd = holder.getBeanDefinition();
			try {
				if (bd instanceof AnnotatedBeanDefinition) {
					parse(((AnnotatedBeanDefinition) bd).getMetadata(), holder.getBeanName());
				}else if (bd instanceof AbstractBeanDefinition && ((AbstractBeanDefinition) bd).hasBeanClass()) {
					parse(((AbstractBeanDefinition) bd).getBeanClass(), holder.getBeanName());
				}else {
					parse(bd.getBeanClassName(), holder.getBeanName());
				}
			}catch (BeanDefinitionStoreException ex) {
				throw ex;
			}catch (Throwable ex) {
				throw new BeanDefinitionStoreException("Failed to parse configuration class [" + bd.getBeanClassName() + "]", ex);
			}
		}
		// 最后再处理DeferredImportSelector的实现类
		deferredImportSelectorHandler.process();
	}

	protected final void parse(@Nullable String className, String beanName) throws IOException {
		Assert.notNull(className, "No bean class name for configuration class bean definition");
		MetadataReader reader = metadataReaderFactory.getMetadataReader(className);
		processConfigurationClass(new ConfigurationClass(reader, beanName));
	}

	protected final void parse(Class<?> clazz, String beanName) throws IOException {
		processConfigurationClass(new ConfigurationClass(clazz, beanName));
	}

	protected final void parse(AnnotationMetadata metadata, String beanName) throws IOException {
		processConfigurationClass(new ConfigurationClass(metadata, beanName));
	}

	/**
	 * Validate each {@link ConfigurationClass} object.
	 * @see ConfigurationClass#validate
	 */
	public void validate() {
		for (ConfigurationClass configClass : configurationClasses.keySet()) {
			configClass.validate(problemReporter);
		}
	}

	public Set<ConfigurationClass> getConfigurationClasses() {
		return configurationClasses.keySet();
	}

	protected void processConfigurationClass(ConfigurationClass configClass) throws IOException {
		if (conditionEvaluator.shouldSkip(configClass.getMetadata(), ConfigurationPhase.PARSE_CONFIGURATION)) {
			return;
		}
		ConfigurationClass existingClass = configurationClasses.get(configClass);
		if (existingClass != null) {
			if (configClass.isImported()) {
				if (existingClass.isImported()) {
					// 如果要处理的配置类configClass在已经分析处理的配置类记录中已存在，合并二者的importedBy属性
					existingClass.mergeImportedBy(configClass);
				}
				// Otherwise ignore new imported config class; existing non-imported class overrides it.
				return;
			}else {
				// Explicit bean definition found, probably replacing an import.Let's remove the old one and go with the new one.
				configurationClasses.remove(configClass);
				knownSuperclasses.values().removeIf(configClass::equals);
			}
		}
		// Recursively process the configuration class and its superclass hierarchy.
		// 从当前配置类configClass开始向上沿着类继承结构逐层执行doProcessConfigurationClass,直到遇到的父类是由Java提供的类结束循环
		SourceClass sourceClass = asSourceClass(configClass);
		do {
			// 循环处理配置类configClass直到sourceClass变为null
			// doProcessConfigurationClass的返回值是其参数configClass的父类，
			// 如果该父类是由Java提供的类或者已经处理过，返回null
			// 递归处理循环中当前类的父类
			sourceClass = doProcessConfigurationClass(configClass, sourceClass);
		}while (sourceClass != null);
		logger.warn("【 @"+configClass.getMetadata().getAnnotationTypes()+"注解 在configurationClasses中注册】 beanName： " + configClass.getResource().getDescription());
		// 需要被处理的配置类configClass已经被分析处理，将它记录到已处理配置类记录
		configurationClasses.put(configClass, configClass);
	}

	/**
	 * 该方法是处理 @Component、@PropertySources、@ComponentScan、@Import、 @ImportResource、@Bean 注解的类上的标签信息的方法。
	 * 在这里面会处理@Component，@PropertySources，@ComponentScans，@ComponentScan，@Import，@ImportResource以及@Bean注解
	 * Apply processing and build a complete {@link ConfigurationClass} by reading the annotations, members and methods from the source class.
	 * This method can be called multiple times as relevant sources are discovered.
	 * @param configClass the configuration class being build
	 * @param sourceClass a source class
	 * @return the superclass, or {@code null} if none found or previously processed
	 * 1.如果包含@ComponentScans或者@ComponentScan类型的注解则获取对应的注解中的信息包装成AnnotationAttributes对象集合。
	 * 2.检查步骤1中集合是否为空，为空则跳过下面的步骤。不为空则继续检查，贴有@ComponentScans或者@ComponentScan注解的bean上面是否贴有@Conditional类中注解，又的话则进行匹配判断检查是否匹配来决定是否进入下一个步骤。@Conditional注解的解析
	 * 3.循环对上面的集合进行处理，调用ComponentScanAnnotationParser的解析方法，获取扫描结果，然后对结果集进行解析。分析是否是候选bean，是的则进行注册。
	 */
	@Nullable
	protected final SourceClass doProcessConfigurationClass(ConfigurationClass configClass, SourceClass sourceClass) throws IOException {
		// 判断指定类上是否有@Component注解（会判断嵌套类上注解） @Configuration、@Service、@Controller、@Repository
		if (configClass.getMetadata().isAnnotated(Component.class.getName())) {
			// Recursively process any member (nested) classes first // 处理子类的注解
			processMemberClasses(configClass, sourceClass);
		}
		// Process any 【@PropertySource】 annotations
		// 获取指定主配置类上的所有【@PropertySource】注解信息集合
		Set<AnnotationAttributes> annotationAttributes = AnnotationConfigUtils.attributesForRepeatable(sourceClass.getMetadata(), PropertySources.class, org.springframework.context.annotation.PropertySource.class);
		// 循环解析每个@PropertySource注解
		for (AnnotationAttributes propertySource :annotationAttributes ) {
			if (environment instanceof ConfigurableEnvironment) {
				//  解析【@PropertySource】注解
				processPropertySource(propertySource);
			}else {
				logger.info("Ignoring @PropertySource annotation on [" + sourceClass.getMetadata().getClassName() + "]. Reason: Environment must implement ConfigurableEnvironment");
			}
		}
		// Process any 【@ComponentScan】 annotations  // @ComponentScans 注解是对@ComponentScan注解的包装，一个@ComponentScans可以包含多个 @ComponentScan
		// sos @ComponentScan 注解不能扫描jar包
		Set<AnnotationAttributes> componentScans = AnnotationConfigUtils.attributesForRepeatable(sourceClass.getMetadata(), ComponentScans.class, ComponentScan.class);
		// 如果解析注解后存在扫描相关的注解，并且贴有当前注解的bean不需要跳过注册（贴有@condition注解）
		if (!componentScans.isEmpty() && !conditionEvaluator.shouldSkip(sourceClass.getMetadata(), ConfigurationPhase.REGISTER_BEAN)) {
			// 对 对应的注解信息进行循环处理
			for (AnnotationAttributes componentScan : componentScans) {
				// The config class is annotated with @ComponentScan -> perform the scan immediately //ComponentScanAnnotationParser进行解析
				// 这步是注册@ComponentScan注解basePackages属性的所有bean定义并进行注册！ （排除主配置类、接口、抽象类）
				logger.warn("【IOC容器 处理 @ComponentScan 注解  --- 】 value属性： " + ((String[])componentScan.get("value"))[0]);
				Set<BeanDefinitionHolder> scannedBeanDefinitions = componentScanParser.parse(componentScan, sourceClass.getMetadata().getClassName());
				// Check the set of scanned definitions for any further config classes and parse recursively if needed
				// 处理扫描到的并封装成BeanDefinitionHolder对象的bean
				for (BeanDefinitionHolder holder : scannedBeanDefinitions) {
					// 检查时候是已经存在的bean
					BeanDefinition bdCand = holder.getBeanDefinition().getOriginatingBeanDefinition();
					if (bdCand == null) bdCand = holder.getBeanDefinition();
					// 检查是否可作为候选bean，
					if (ConfigurationClassUtils.checkConfigurationClassCandidate(bdCand, metadataReaderFactory)) {
						parse(bdCand.getBeanClassName(), holder.getBeanName());
					}
				}
			}
		}
		// Process any 【@Import】 annotations  sos @Import 注解 可以扫描jar包中的类
		processImports(configClass, sourceClass, getImports(sourceClass), true);
		// Process any 【@ImportResource】 annotations
		AnnotationAttributes importResource = AnnotationConfigUtils.attributesFor(sourceClass.getMetadata(), ImportResource.class);
		if (importResource != null) {
			String[] resources = importResource.getStringArray("locations");
			Class<? extends BeanDefinitionReader> readerClass = importResource.getClass("reader");
			for (String resource : resources) {
				String resolvedResource = environment.resolveRequiredPlaceholders(resource);
				configClass.addImportedResource(resolvedResource, readerClass);
			}
		}
		// Process individual 【@Bean】 methods
		// 这里是处理 @Configuration 主配置类中的 @Bean 方法！
		Set<MethodMetadata> beanMethods = retrieveBeanMethodMetadata(sourceClass);
		for (MethodMetadata methodMetadata : beanMethods) {
			logger.warn("【IOC容器 记录 @Configuration 主配置类 【" + configClass.getBeanName() + "】 中的 @Bean 方法，准备后续进行处理  --- 】 " );
			/**
			 * 再此处进行添加，再后面进行处理
			 * @see ConfigurationClassBeanDefinitionReader#loadBeanDefinitionsForBeanMethod(org.springframework.context.annotation.BeanMethod)
			*/
			configClass.addBeanMethod(new BeanMethod(methodMetadata, configClass));
		}
		// Process default methods on interfaces
		processInterfaces(configClass, sourceClass);
		// Process superclass, if any
		if (sourceClass.getMetadata().hasSuperClass()) {
			String superclass = sourceClass.getMetadata().getSuperClassName();
			if (superclass != null && !superclass.startsWith("java") && !knownSuperclasses.containsKey(superclass)) {
				knownSuperclasses.put(superclass, configClass);
				// Superclass found, return its annotation metadata and recurse
				return sourceClass.getSuperClass();
			}
		}
		// No superclass -> processing is complete
		return null;
	}


	// Register member (nested) classes that happen to be configuration classes themselves.
	private void processMemberClasses(ConfigurationClass configClass, SourceClass sourceClass) throws IOException {
		Collection<SourceClass> memberClasses = sourceClass.getMemberClasses();
		if (!memberClasses.isEmpty()) {
			List<SourceClass> candidates = new ArrayList<>(memberClasses.size());
			for (SourceClass memberClass : memberClasses) {
				if (ConfigurationClassUtils.isConfigurationCandidate(memberClass.getMetadata()) && !memberClass.getMetadata().getClassName().equals(configClass.getMetadata().getClassName())) {
					candidates.add(memberClass);
				}
			}
			OrderComparator.sort(candidates);
			for (SourceClass candidate : candidates) {
				if (importStack.contains(configClass)) {
					problemReporter.error(new CircularImportProblem(configClass, importStack));
				}else {
					importStack.push(configClass);
					try {
						processConfigurationClass(candidate.asConfigClass(configClass));
					}finally {
						importStack.pop();
					}
				}
			}
		}
	}

	// Register default methods on interfaces implemented by the configuration class.
	private void processInterfaces(ConfigurationClass configClass, SourceClass sourceClass) throws IOException {
		for (SourceClass ifc : sourceClass.getInterfaces()) {
			Set<MethodMetadata> beanMethods = retrieveBeanMethodMetadata(ifc);
			for (MethodMetadata methodMetadata : beanMethods) {
				if (!methodMetadata.isAbstract()) {
					// A default method or other concrete method on a Java 8+ interface...
					configClass.addBeanMethod(new BeanMethod(methodMetadata, configClass));
				}
			}
			processInterfaces(configClass, ifc);
		}
	}

	// Retrieve the metadata for all <code>@Bean</code> methods.
	private Set<MethodMetadata> retrieveBeanMethodMetadata(SourceClass sourceClass) {
		AnnotationMetadata original = sourceClass.getMetadata();
		// 获取 指定主配置类(sourceClass)中 含有@Bean注解的方法的元信息
		Set<MethodMetadata> beanMethods = original.getAnnotatedMethods(Bean.class.getName());
		/**
		 * 如果含有@Bean注解的方法超过两个，并且是标准注解信息，则Spring会使用ASM技术
		 * Spring使用了JAVA的反射机制获取的Class，但是反射不能保证方法的声明顺序，也就是它所返回的方法顺序
		 * 并不一定是代码从上到下编写的顺序，有可能类中的最下面的一个方法在beanMethods集合中是第一个
		 * Spring为保证方法的声明顺序，使用ASM技术读取作比较
		 * 注意：这里只有ASM获取的方法比反射获取的方法多或者相等才会比较
		 */
		if (beanMethods.size() > 1 && original instanceof StandardAnnotationMetadata) {
			// Try reading the class file via ASM for deterministic declaration order...
			// Unfortunately, the JVM's standard reflection returns methods in arbitrary order, even between different runs of the same application on the same JVM.
			try {
				// 利用ASM技术返回类的元信息，并获取含有@Bean注解的方法元信息
				AnnotationMetadata asm = metadataReaderFactory.getMetadataReader(original.getClassName()).getAnnotationMetadata();
				Set<MethodMetadata> asmMethods = asm.getAnnotatedMethods(Bean.class.getName());
				// 这里重新创建了一个LinkedHashSet集合保证放入的顺序，遍历ASM方法名与java反射方法名一致，则可以放入集合中并赋值给局部变量用于返回
				if (asmMethods.size() >= beanMethods.size()) {
					Set<MethodMetadata> selectedMethods = new LinkedHashSet<>(asmMethods.size());
					for (MethodMetadata asmMethod : asmMethods) {
						for (MethodMetadata beanMethod : beanMethods) {
							if (beanMethod.getMethodName().equals(asmMethod.getMethodName())) {
								selectedMethods.add(beanMethod);
								break;
							}
						}
					}
					if (selectedMethods.size() == beanMethods.size()) {
						// All reflection-detected methods found in ASM method set -> proceed
						beanMethods = selectedMethods;
					}
				}
			}catch (IOException ex) {
				logger.debug("Failed to read class file via ASM for determining @Bean method order", ex);
				// No worries, let's continue with the reflection metadata we started with...
			}
		}
		return beanMethods;
	}

	/**
	 * 解析【@PropertySource】注解
	 * Process the given <code>@PropertySource</code> annotation metadata.
	 * @param propertySource metadata for the <code>@PropertySource</code> annotation found
	 * @throws IOException if loading a property source failed
	 */
	private void processPropertySource(AnnotationAttributes propertySource) throws IOException {
		String name = propertySource.getString("name");
		if (!StringUtils.hasLength(name)) {
			name = null;
		}
		String encoding = propertySource.getString("encoding");
		if (!StringUtils.hasLength(encoding)) {
			encoding = null;
		}
		// 解析value属性
		String[] locations = propertySource.getStringArray("value");
		Assert.isTrue(locations.length > 0, "At least one @PropertySource(value) location is required");
		boolean ignoreResourceNotFound = propertySource.getBoolean("ignoreResourceNotFound");

		Class<? extends PropertySourceFactory> factoryClass = propertySource.getClass("factory");
		PropertySourceFactory factory = (factoryClass == PropertySourceFactory.class) ? DEFAULT_PROPERTY_SOURCE_FACTORY : BeanUtils.instantiateClass(factoryClass);
		// 循环处理value属性值
		for (String location : locations) {
			try {
				// 严格解析value属性值中的 	$[] $() ${}	占位符
				String resolvedLocation = environment.resolveRequiredPlaceholders(location);
				// 将经过严格解析后的路径文件信息，加载到内存
				Resource resource = resourceLoader.getResource(resolvedLocation);
				// 将内容转换成 PropertySource 对象后，存储到 environment 的MutablePropertySources对象中
				addPropertySource(factory.createPropertySource(name, new EncodedResource(resource, encoding)));
			}catch (IllegalArgumentException | FileNotFoundException | UnknownHostException ex) {
				// Placeholders not resolvable or resource not found when trying to open it
				if (ignoreResourceNotFound) {
					if (logger.isInfoEnabled()) logger.info("Properties location [" + location + "] not resolvable: " + ex.getMessage());
				}else {
					throw ex;
				}
			}
		}
	}

	private void addPropertySource(PropertySource<?> propertySource) {
		String name = propertySource.getName(); // 获取资源文件名
		MutablePropertySources propertySources = ((ConfigurableEnvironment) environment).getPropertySources();
		if (propertySourceNames.contains(name)) {
			// We've already added a version, we need to extend it
			PropertySource<?> existing = propertySources.get(name);
			if (existing != null) {
				PropertySource<?> newSource = (propertySource instanceof ResourcePropertySource ? ((ResourcePropertySource) propertySource).withResourceName() : propertySource);
				if (existing instanceof CompositePropertySource) {
					((CompositePropertySource) existing).addFirstPropertySource(newSource);
				}else {
					if (existing instanceof ResourcePropertySource) {
						existing = ((ResourcePropertySource) existing).withResourceName();
					}
					CompositePropertySource composite = new CompositePropertySource(name);
					composite.addPropertySource(newSource);
					composite.addPropertySource(existing);
					propertySources.replace(name, composite);
				}
				return;
			}
		}
		if (propertySourceNames.isEmpty()) {
			propertySources.addLast(propertySource);
		}else {
			String firstProcessed = propertySourceNames.get(propertySourceNames.size() - 1);
			propertySources.addBefore(firstProcessed, propertySource);
		}
		logger.warn("【IOC容器中 记录容器中已加载的资源文件 】 文件名： " + name);
		propertySourceNames.add(name);
	}

	/**
	 * 递归获取 @Configuration配置类中 所有的@Import注解 导入的类，添加到imports集合中
	 * Returns {@code @Import} class, considering all meta-annotations.
	 * @return  配置类中递归包含的所有 @Import注解的导入类
	 * @param  sourceClass 配置类
	 */
	private Set<SourceClass> getImports(SourceClass sourceClass) throws IOException {
		Set<SourceClass> imports = new LinkedHashSet<>();
		Set<SourceClass> visited = new LinkedHashSet<>();
		collectImports(sourceClass, imports, visited);
		return imports;
	}

	/**
	 * 递归获取所有 @Import注解 导入的类，添加到imports集合中
	 * Recursively collect all declared {@code @Import} values.
	 * Unlike most meta-annotations it is valid to have several {@code @Import}s declared with different values;
	 * the usual process of returning values from the first  meta-annotation on a class is not sufficient.
	 * For example, it is common for a {@code @Configuration} class to declare direct {@code @Import}s in addition to meta-imports originating from an {@code @Enable} annotation.
	 * @param sourceClass the class to search
	 * @param imports the imports collected so far
	 * @param visited used to track visited classes to prevent infinite recursion
	 * @throws IOException if there is any problem reading metadata from the named class
	 */
	private void collectImports(SourceClass sourceClass, Set<SourceClass> imports, Set<SourceClass> visited) throws IOException {
		if (visited.add(sourceClass)) {
			for (SourceClass annotation : sourceClass.getAnnotations()) {
				String annName = annotation.getMetadata().getClassName();
				if (!annName.equals(Import.class.getName())) {
					collectImports(annotation, imports, visited);
				}
			}
			// 获取 @Import({Blue.class, Red.class}) 注解中的values数组  com.goat.chapter105.model.Blue,com.goat.chapter105.model.Red
			imports.addAll(sourceClass.getAnnotationAttributes(Import.class.getName(), "value"));
		}
	}

	private void processImports(ConfigurationClass configClass, SourceClass currentSourceClass,Collection<SourceClass> importCandidates, boolean checkForCircularImports) {
		if (importCandidates.isEmpty()) return;
		if (checkForCircularImports && isChainedImportOnStack(configClass)) {
			problemReporter.error(new CircularImportProblem(configClass, importStack));
		}else {
			importStack.push(configClass);
			try {
				for (SourceClass candidate : importCandidates) {
					/**
					 * 这里分别对应 @Import注解 使用的三种方式
					 * sos 其中要注意的是，只有方式三 直接导入指定类对象 @Import({Blue.class, Red.class})  中指定的bean才会被注入到容器中
					 * 	其他方式一和方式二中  @Import(MyImportSelector.class) @Import(MyImportBeanDefinitionRegistrar.class) 中指定的bean 不会被注入到容器中
					 * 	因为这两种方式  是实现接口，从而使用自定义的逻辑去注册bean定义，而不是走spring流水线的方式创建bean定义。
					*/
					// 方式一  @Import(MyImportSelector.class) 导入的类实现了 ImportSelector 接口
					if (candidate.isAssignable(ImportSelector.class)) {
						// Candidate class is an ImportSelector -> delegate to it to determine imports
						Class<?> candidateClass = candidate.loadClass();
						logger.warn("【IOC容器 处理 @Import 注解  --- ImportSelector 接口方式 】 beanName： " + candidateClass.getName());
						ImportSelector selector = BeanUtils.instantiateClass(candidateClass, ImportSelector.class);
						ParserStrategyUtils.invokeAwareMethods(selector, environment, resourceLoader, registry);
						// 方式二  @Import(MyDeferredImportSelector.class) 导入的类实现了 DeferredImportSelector 接口
						if (selector instanceof DeferredImportSelector) {
							deferredImportSelectorHandler.handle(configClass, (DeferredImportSelector) selector);
						}else {
							String[] importClassNames = selector.selectImports(currentSourceClass.getMetadata());
							// 根据实现类返回的待导入容器的bean名称(importClassNames),通过反射创建出实例
							Collection<SourceClass> importSourceClasses = asSourceClasses(importClassNames);
							// 递归调用，下次就走到方式四
							processImports(configClass, currentSourceClass, importSourceClasses, false);
						}
					}else if (candidate.isAssignable(ImportBeanDefinitionRegistrar.class)) {
						// 方式三  @Import(MyImportSelector.class) 导入的类实现了 ImportBeanDefinitionRegistrar 接口
						// Candidate class is an ImportBeanDefinitionRegistrar -> delegate to it to register additional bean definitions
						Class<?> candidateClass = candidate.loadClass();
						logger.warn("【IOC容器 处理 @Import 注解  --- ImportBeanDefinitionRegistrar 接口方式 】 beanName： " + candidateClass.getName());
						ImportBeanDefinitionRegistrar registrar = BeanUtils.instantiateClass(candidateClass, ImportBeanDefinitionRegistrar.class);
						ParserStrategyUtils.invokeAwareMethods(registrar, environment, resourceLoader, registry);
						configClass.addImportBeanDefinitionRegistrar(registrar, currentSourceClass.getMetadata());
					}else {
						// 	方式四  直接导入指定类对象 @Import({Blue.class, Red.class})
						// Candidate class not an ImportSelector or ImportBeanDefinitionRegistrar ->  process it as an @Configuration class
						logger.warn("【IOC容器 处理 @Import 注解  --- 直接导入方式 】 beanName： " + candidate.getMetadata().getClassName());
						importStack.registerImport(currentSourceClass.getMetadata(), candidate.getMetadata().getClassName());
						processConfigurationClass(candidate.asConfigClass(configClass));
					}
				}
			}catch (BeanDefinitionStoreException ex) {
				throw ex;
			}catch (Throwable ex) {
				throw new BeanDefinitionStoreException("Failed to process import candidates for configuration class [" + configClass.getMetadata().getClassName() + "]", ex);
			}finally {
				importStack.pop();
			}
		}
	}

	private boolean isChainedImportOnStack(ConfigurationClass configClass) {
		if (importStack.contains(configClass)) {
			String configClassName = configClass.getMetadata().getClassName();
			AnnotationMetadata importingClass = importStack.getImportingClassFor(configClassName);
			while (importingClass != null) {
				if (configClassName.equals(importingClass.getClassName())) {
					return true;
				}
				importingClass = importStack.getImportingClassFor(importingClass.getClassName());
			}
		}
		return false;
	}

	ImportRegistry getImportRegistry() {
		return importStack;
	}

	/**
	 * Factory method to obtain a {@link SourceClass} from a {@link ConfigurationClass}.
	 */
	private SourceClass asSourceClass(ConfigurationClass configurationClass) throws IOException {
		AnnotationMetadata metadata = configurationClass.getMetadata();
		if (metadata instanceof StandardAnnotationMetadata) {
			return asSourceClass(((StandardAnnotationMetadata) metadata).getIntrospectedClass());
		}
		return asSourceClass(metadata.getClassName());
	}

	/**
	 * Factory method to obtain a {@link SourceClass} from a {@link Class}.
	 */
	SourceClass asSourceClass(@Nullable Class<?> classType) throws IOException {
		if (classType == null) return new SourceClass(Object.class);
		try {
			// Sanity test that we can reflectively read annotations,
			// including Class attributes; if not -> fall back to ASM
			for (Annotation ann : classType.getAnnotations()) {
				AnnotationUtils.validateAnnotation(ann);
			}
			return new SourceClass(classType);
		}catch (Throwable ex) {
			// Enforce ASM via class name resolution
			return asSourceClass(classType.getName());
		}
	}

	/**
	 * Factory method to obtain {@link SourceClass SourceClasss} from class names.
	 */
	private Collection<SourceClass> asSourceClasses(String... classNames) throws IOException {
		List<SourceClass> annotatedClasses = new ArrayList<>(classNames.length);
		for (String className : classNames) {
			annotatedClasses.add(asSourceClass(className));
		}
		return annotatedClasses;
	}

	/**
	 * Factory method to obtain a {@link SourceClass} from a class name.
	 */
	SourceClass asSourceClass(@Nullable String className) throws IOException {
		if (className == null) return new SourceClass(Object.class);
		if (className.startsWith("java")) {
			// Never use ASM for core java types
			try {
				return new SourceClass(ClassUtils.forName(className, resourceLoader.getClassLoader()));
			}catch (ClassNotFoundException ex) {
				throw new NestedIOException("Failed to load class [" + className + "]", ex);
			}
		}
		return new SourceClass(metadataReaderFactory.getMetadataReader(className));
	}

	@SuppressWarnings("serial")
	private static class ImportStack extends ArrayDeque<ConfigurationClass> implements ImportRegistry {

		private final MultiValueMap<String, AnnotationMetadata> imports = new LinkedMultiValueMap<>();

		public void registerImport(AnnotationMetadata importingClass, String importedClass) {
			imports.add(importedClass, importingClass);
		}

		@Override
		@Nullable
		public AnnotationMetadata getImportingClassFor(String importedClass) {
			return CollectionUtils.lastElement(imports.get(importedClass));
		}

		@Override
		public void removeImportingClass(String importingClass) {
			for (List<AnnotationMetadata> list : imports.values()) {
				for (Iterator<AnnotationMetadata> iterator = list.iterator(); iterator.hasNext();) {
					if (iterator.next().getClassName().equals(importingClass)) {
						iterator.remove();
						break;
					}
				}
			}
		}

		/**
		 * Given a stack containing (in order)
		 * <li>com.acme.Foo</li>
		 * <li>com.acme.Bar</li>
		 * <li>com.acme.Baz</li>
		 * return "[Foo->Bar->Baz]".
		 */
		@Override
		public String toString() {
			StringBuilder builder = new StringBuilder("[");
			Iterator<ConfigurationClass> iterator = iterator();
			while (iterator.hasNext()) {
				builder.append(iterator.next().getSimpleName());
				if (iterator.hasNext()) {
					builder.append("->");
				}
			}
			return builder.append(']').toString();
		}
	}

	private class DeferredImportSelectorHandler {

		@Nullable
		private List<DeferredImportSelectorHolder> deferredImportSelectors = new ArrayList<>();

		/**
		 * Handle the specified {@link DeferredImportSelector}. If deferred import selectors are being collected, this registers this instance to the list.
		 * If they are being processed, the {@link DeferredImportSelector} is also processed immediately according to its {@link DeferredImportSelector.Group}.
		 * @param configClass the source configuration class
		 * @param importSelector the selector to handle
		 */
		public void handle(ConfigurationClass configClass, DeferredImportSelector importSelector) {
			DeferredImportSelectorHolder holder = new DeferredImportSelectorHolder(configClass, importSelector);
			if (deferredImportSelectors == null) {
				DeferredImportSelectorGroupingHandler handler = new DeferredImportSelectorGroupingHandler();
				handler.register(holder);
				handler.processGroupImports();
			}else {
				deferredImportSelectors.add(holder);
			}
		}

		public void process() {
			List<DeferredImportSelectorHolder> deferredImports = deferredImportSelectors;
			deferredImportSelectors = null;
			try {
				if (deferredImports != null) {
					DeferredImportSelectorGroupingHandler handler = new DeferredImportSelectorGroupingHandler();
					deferredImports.sort(DEFERRED_IMPORT_COMPARATOR);
					deferredImports.forEach(handler::register);
					handler.processGroupImports();
				}
			}finally {
				deferredImportSelectors = new ArrayList<>();
			}
		}
	}


	private class DeferredImportSelectorGroupingHandler {

		private final Map<Object, DeferredImportSelectorGrouping> groupings = new LinkedHashMap<>();

		private final Map<AnnotationMetadata, ConfigurationClass> configurationClasses = new HashMap<>();

		public void register(DeferredImportSelectorHolder deferredImport) {
			Class<? extends Group> group = deferredImport.getImportSelector().getImportGroup();
			DeferredImportSelectorGrouping grouping = groupings.computeIfAbsent((group != null ? group : deferredImport),key -> new DeferredImportSelectorGrouping(createGroup(group)));
			grouping.add(deferredImport);
			configurationClasses.put(deferredImport.getConfigurationClass().getMetadata(),deferredImport.getConfigurationClass());
		}

		public void processGroupImports() {
			for (DeferredImportSelectorGrouping grouping : groupings.values()) {
				grouping.getImports().forEach(entry -> { ConfigurationClass configurationClass = configurationClasses.get(entry.getMetadata());
					try {
						processImports(configurationClass, asSourceClass(configurationClass),asSourceClasses(entry.getImportClassName()), false);
					}catch (BeanDefinitionStoreException ex) {
						throw ex;
					}catch (Throwable ex) {
						throw new BeanDefinitionStoreException("Failed to process import candidates for configuration class [" + configurationClass.getMetadata().getClassName() + "]", ex);
					}
				});
			}
		}

		private Group createGroup(@Nullable Class<? extends Group> type) {
			Class<? extends Group> effectiveType = (type != null ? type : DefaultDeferredImportSelectorGroup.class);
			Group group = BeanUtils.instantiateClass(effectiveType);
			ParserStrategyUtils.invokeAwareMethods(group,ConfigurationClassParser.this.environment,ConfigurationClassParser.this.resourceLoader,ConfigurationClassParser.this.registry);
			return group;
		}

	}


	private static class DeferredImportSelectorHolder {

		private final ConfigurationClass configurationClass;

		private final DeferredImportSelector importSelector;

		public DeferredImportSelectorHolder(ConfigurationClass configClass, DeferredImportSelector selector) {
			configurationClass = configClass;
			importSelector = selector;
		}

		public ConfigurationClass getConfigurationClass() {
			return configurationClass;
		}

		public DeferredImportSelector getImportSelector() {
			return importSelector;
		}
	}


	private static class DeferredImportSelectorGrouping {

		private final DeferredImportSelector.Group group;

		private final List<DeferredImportSelectorHolder> deferredImports = new ArrayList<>();

		DeferredImportSelectorGrouping(Group group) {
			this.group = group;
		}

		public void add(DeferredImportSelectorHolder deferredImport) {
			deferredImports.add(deferredImport);
		}
		/**
		 * Return the imports defined by the group.
		 * @return each import with its associated configuration class
		 */
		public Iterable<Group.Entry> getImports() {
			for (DeferredImportSelectorHolder deferredImport : deferredImports) {
				group.process(deferredImport.getConfigurationClass().getMetadata(),deferredImport.getImportSelector());
			}
			return group.selectImports();
		}
	}

	private static class DefaultDeferredImportSelectorGroup implements Group {
		private final List<Entry> imports = new ArrayList<>();
		@Override
		public void process(AnnotationMetadata metadata, DeferredImportSelector selector) {
			for (String importClassName : selector.selectImports(metadata)) {
				imports.add(new Entry(metadata, importClassName));
			}
		}
		@Override
		public Iterable<Entry> selectImports() {
			return imports;
		}
	}

	// Simple wrapper that allows annotated source classes to be dealt with  in a uniform manner, regardless of how they are loaded.
	// 简单的包装器，允许以统一的方式处理带注释的源类，而不管它们是如何加载的
	private class SourceClass implements Ordered {

		private final Object source;  // Class or MetadataReader

		private final AnnotationMetadata metadata;

		public SourceClass(Object source) {
			this.source = source;
			if (source instanceof Class) {
				metadata = new StandardAnnotationMetadata((Class<?>) source, true);
			}else {
				metadata = ((MetadataReader) source).getAnnotationMetadata();
			}
		}

		public final AnnotationMetadata getMetadata() {
			return metadata;
		}

		@Override
		public int getOrder() {
			Integer order = ConfigurationClassUtils.getOrder(metadata);
			return (order != null ? order : Ordered.LOWEST_PRECEDENCE);
		}

		public Class<?> loadClass() throws ClassNotFoundException {
			if (source instanceof Class) {
				return (Class<?>) source;
			}
			String className = ((MetadataReader) source).getClassMetadata().getClassName();
			return ClassUtils.forName(className, resourceLoader.getClassLoader());
		}

		public boolean isAssignable(Class<?> clazz) throws IOException {
			if (source instanceof Class) {
				return clazz.isAssignableFrom((Class<?>) source);
			}
			return new AssignableTypeFilter(clazz).match((MetadataReader) source, metadataReaderFactory);
		}

		public ConfigurationClass asConfigClass(ConfigurationClass importedBy) {
			if (source instanceof Class) {
				return new ConfigurationClass((Class<?>) source, importedBy);
			}
			return new ConfigurationClass((MetadataReader) source, importedBy);
		}

		public Collection<SourceClass> getMemberClasses() throws IOException {
			Object sourceToProcess = source;
			if (sourceToProcess instanceof Class) {
				Class<?> sourceClass = (Class<?>) sourceToProcess;
				try {
					Class<?>[] declaredClasses = sourceClass.getDeclaredClasses();
					List<SourceClass> members = new ArrayList<>(declaredClasses.length);
					for (Class<?> declaredClass : declaredClasses) {
						members.add(asSourceClass(declaredClass));
					}
					return members;
				}catch (NoClassDefFoundError err) {
					// getDeclaredClasses() failed because of non-resolvable dependencies
					// -> fall back to ASM below
					sourceToProcess = metadataReaderFactory.getMetadataReader(sourceClass.getName());
				}
			}

			// ASM-based resolution - safe for non-resolvable classes as well
			MetadataReader sourceReader = (MetadataReader) sourceToProcess;
			String[] memberClassNames = sourceReader.getClassMetadata().getMemberClassNames();
			List<SourceClass> members = new ArrayList<>(memberClassNames.length);
			for (String memberClassName : memberClassNames) {
				try {
					members.add(asSourceClass(memberClassName));
				}catch (IOException ex) {
					// Let's skip it if it's not resolvable - we're just looking for candidates
					if (logger.isDebugEnabled()) logger.debug("Failed to resolve member class [" + memberClassName + "] - not considering it as a configuration class candidate");
				}
			}
			return members;
		}

		public SourceClass getSuperClass() throws IOException {
			if (source instanceof Class) {
				return asSourceClass(((Class<?>) source).getSuperclass());
			}
			return asSourceClass(((MetadataReader) source).getClassMetadata().getSuperClassName());
		}

		public Set<SourceClass> getInterfaces() throws IOException {
			Set<SourceClass> result = new LinkedHashSet<>();
			if (source instanceof Class) {
				Class<?> sourceClass = (Class<?>) source;
				for (Class<?> ifcClass : sourceClass.getInterfaces()) {
					result.add(asSourceClass(ifcClass));
				}
			}else {
				for (String className : metadata.getInterfaceNames()) {
					result.add(asSourceClass(className));
				}
			}
			return result;
		}

		public Set<SourceClass> getAnnotations() {
			Set<SourceClass> result = new LinkedHashSet<>();
			if (source instanceof Class) {
				Class<?> sourceClass = (Class<?>) source;
				for (Annotation ann : sourceClass.getAnnotations()) {
					Class<?> annType = ann.annotationType();
					if (!annType.getName().startsWith("java")) {
						try {
							result.add(asSourceClass(annType));
						}catch (Throwable ex) {
							// An annotation not present on the classpath is being ignored  by the JVM's class loading -> ignore here as well.
						}
					}
				}
			}else {
				for (String className : metadata.getAnnotationTypes()) {
					if (!className.startsWith("java")) {
						try {
							result.add(getRelated(className));
						}catch (Throwable ex) {
							// An annotation not present on the classpath is being ignored  by the JVM's class loading -> ignore here as well.
						}
					}
				}
			}
			return result;
		}

		public Collection<SourceClass> getAnnotationAttributes(String annType, String attribute) throws IOException {
			Map<String, Object> annotationAttributes = metadata.getAnnotationAttributes(annType, true);
			if (annotationAttributes == null || !annotationAttributes.containsKey(attribute)) {
				return Collections.emptySet();
			}
			String[] classNames = (String[]) annotationAttributes.get(attribute);
			Set<SourceClass> result = new LinkedHashSet<>();
			for (String className : classNames) {
				result.add(getRelated(className));
			}
			return result;
		}

		private SourceClass getRelated(String className) throws IOException {
			if (source instanceof Class) {
				try {
					Class<?> clazz = ClassUtils.forName(className, ((Class<?>) source).getClassLoader());
					return asSourceClass(clazz);
				}catch (ClassNotFoundException ex) {
					// Ignore -> fall back to ASM next, except for core java types.
					if (className.startsWith("java")) {
						throw new NestedIOException("Failed to load class [" + className + "]", ex);
					}
					return new SourceClass(metadataReaderFactory.getMetadataReader(className));
				}
			}
			return asSourceClass(className);
		}

		@Override
		public boolean equals(Object other) {
			return (this == other || (other instanceof SourceClass && metadata.getClassName().equals(((SourceClass) other).metadata.getClassName())));
		}

		@Override
		public int hashCode() {
			return metadata.getClassName().hashCode();
		}

		@Override
		public String toString() {
			return metadata.getClassName();
		}
	}

	/**
	 * {@link Problem} registered upon detection of a circular {@link Import}.
	 */
	private static class CircularImportProblem extends Problem {
		public CircularImportProblem(ConfigurationClass attemptedImport, Deque<ConfigurationClass> importStack) {
			super(String.format("A circular @Import has been detected: Illegal attempt by @Configuration class '%s' to import class '%s' as '%s' is already present in the current import stack %s",
					importStack.element().getSimpleName(),attemptedImport.getSimpleName(), attemptedImport.getSimpleName(), importStack),new Location(importStack.element().getResource(), attemptedImport.getMetadata()));
		}
	}
}
