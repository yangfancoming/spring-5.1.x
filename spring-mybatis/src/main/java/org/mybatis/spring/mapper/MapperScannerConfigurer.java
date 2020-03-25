
package org.mybatis.spring.mapper;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyResourceConfigurer;
import org.springframework.beans.factory.config.TypedStringValue;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.Optional;

import static org.springframework.util.Assert.notNull;

/**
 * BeanDefinitionRegistryPostProcessor that searches recursively starting from a base package for interfaces and
 * registers them as {@code MapperFactoryBean}. Note that only interfaces with at least one method will be registered;
 * concrete classes will be ignored.
 * <p>
 * This class was a {code BeanFactoryPostProcessor} until 1.0.1 version. It changed to
 * {@code BeanDefinitionRegistryPostProcessor} in 1.0.2. See https://jira.springsource.org/browse/SPR-8269 for the
 * details.
 * <p>
 * The {@code basePackage} property can contain more than one package name, separated by either commas or semicolons.
 * <p>
 * This class supports filtering the mappers created by either specifying a marker interface or an annotation. The
 * {@code annotationClass} property specifies an annotation to search for. The {@code markerInterface} property
 * specifies a parent interface to search for. If both properties are specified, mappers are added for interfaces that
 * match <em>either</em> criteria. By default, these two properties are null, so all interfaces in the given
 * {@code basePackage} are added as mappers.
 * <p>
 * This configurer enables autowire for all the beans that it creates so that they are automatically autowired with the
 * proper {@code SqlSessionFactory} or {@code SqlSessionTemplate}. If there is more than one {@code SqlSessionFactory}
 * in the application, however, autowiring cannot be used. In this case you must explicitly specify either an
 * {@code SqlSessionFactory} or an {@code SqlSessionTemplate} to use via the <em>bean name</em> properties. Bean names
 * are used rather than actual objects because Spring does not initialize property placeholders until after this class
 * is processed.
 * <p>
 * Passing in an actual object which may require placeholders (i.e. DB user password) will fail. Using bean names defers
 * actual object creation until later in the startup process, after all placeholder substitution is completed. However,
 * note that this configurer does support property placeholders of its <em>own</em> properties. The
 * <code>basePackage</code> and bean name properties all support <code>${property}</code> style substitution.
 * <p>
 * Configuration sample:
 *
 * <pre class="code">
 * {@code
 *   <bean class="org.mybatis.spring.mapper.MapperScannerConfigurer">
 *       <property name="basePackage" value="org.mybatis.spring.sample.mapper" />
 *       <!-- optional unless there are multiple session factories defined -->
 *       <property name="sqlSessionFactoryBeanName" value="sqlSessionFactory" />
 *   </bean>
 * }
 * </pre>
 *
 * @see MapperFactoryBean
 * @see ClassPathMapperScanner
 * MapperScannerConfigurer 类实现了 BeanDefinitionRegistryPostProcessor 接口，
 * 该接口中的 postProcessBeanDefinitionRegistry() 方法会在系统初始化的过程中被调用，
 * 该方法扫描了配置文件中配置的basePackage 下的所有 Mapper 类，最终生成 Spring 的 Bean 对象，注册到容器中。
 */
public class MapperScannerConfigurer implements BeanDefinitionRegistryPostProcessor, InitializingBean, ApplicationContextAware, BeanNameAware {

	private String basePackage;

	private boolean addToConfig = true;

	private String lazyInitialization;

	private SqlSessionFactory sqlSessionFactory;

	private SqlSessionTemplate sqlSessionTemplate;

	private String sqlSessionFactoryBeanName;

	private String sqlSessionTemplateBeanName;

	private Class<? extends Annotation> annotationClass;

	private Class<?> markerInterface;

	private Class<? extends MapperFactoryBean> mapperFactoryBeanClass;

	private ApplicationContext applicationContext;

	private String beanName;

	private boolean processPropertyPlaceHolders;

	private BeanNameGenerator nameGenerator;

	/**
	 * This property lets you set the base package for your mapper interface files.
	 * You can set more than one package by using a semicolon or comma as a separator.
	 * Mappers will be searched for recursively starting in the specified package(s).
	 * @param basePackage base package name
	 */
	public void setBasePackage(String basePackage) {
		this.basePackage = basePackage;
	}

	/**
	 * Same as {@code MapperFactoryBean#setAddToConfig(boolean)}.
	 * @param addToConfig  a flag that whether add mapper to MyBatis or not
	 * @see MapperFactoryBean#setAddToConfig(boolean)
	 */
	public void setAddToConfig(boolean addToConfig) {
		this.addToConfig = addToConfig;
	}

	/**
	 * Set whether enable lazy initialization for mapper bean.
	 * Default is {@code false}.
	 * @param lazyInitialization Set the @{code true} to enable
	 * @since 2.0.2
	 */
	public void setLazyInitialization(String lazyInitialization) {
		this.lazyInitialization = lazyInitialization;
	}

	/**
	 * This property specifies the annotation that the scanner will search for.
	 * The scanner will register all interfaces in the base package that also have the specified annotation.
	 * Note this can be combined with markerInterface.
	 * @param annotationClass annotation class
	 */
	public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
		this.annotationClass = annotationClass;
	}

	/**
	 * This property specifies the parent that the scanner will search for.
	 * The scanner will register all interfaces in the base package that also have the specified interface class as a parent.
	 * Note this can be combined with annotationClass.
	 * @param superClass parent class
	 *
	 */
	public void setMarkerInterface(Class<?> superClass) {
		this.markerInterface = superClass;
	}

	/**
	 * Specifies which {@code SqlSessionTemplate} to use in the case that there is more than one in the spring context.
	 * Usually this is only needed when you have more than one datasource.
	 * @deprecated Use {@link #setSqlSessionTemplateBeanName(String)} instead
	 * @param sqlSessionTemplate  a template of SqlSession
	 */
	@Deprecated
	public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}

	/**
	 * Specifies which {@code SqlSessionTemplate} to use in the case that there is more than one in the spring context.
	 * Usually this is only needed when you have more than one datasource.
	 * Note bean names are used, not bean references. This is because the scanner loads early during the start process and
	 * it is too early to build mybatis object instances.
	 * @since 1.1.0
	 * @param sqlSessionTemplateName Bean name of the {@code SqlSessionTemplate}
	 */
	public void setSqlSessionTemplateBeanName(String sqlSessionTemplateName) {
		this.sqlSessionTemplateBeanName = sqlSessionTemplateName;
	}

	/**
	 * Specifies which {@code SqlSessionFactory} to use in the case that there is more than one in the spring context.
	 * Usually this is only needed when you have more than one datasource.
	 * @deprecated Use {@link #setSqlSessionFactoryBeanName(String)} instead.
	 * @param sqlSessionFactory a factory of SqlSession
	 */
	@Deprecated
	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	/**
	 * Specifies which {@code SqlSessionFactory} to use in the case that there is more than one in the spring context.
	 * Usually this is only needed when you have more than one datasource.
	 * Note bean names are used, not bean references. This is because the scanner loads early during the start process and
	 * it is too early to build mybatis object instances.
	 * @since 1.1.0
	 * @param sqlSessionFactoryName Bean name of the {@code SqlSessionFactory}
	 */
	public void setSqlSessionFactoryBeanName(String sqlSessionFactoryName) {
		this.sqlSessionFactoryBeanName = sqlSessionFactoryName;
	}

	/**
	 * Specifies a flag that whether execute a property placeholder processing or not.
	 * The default is {@literal false}. This means that a property placeholder processing does not execute.
	 * @since 1.1.1
	 * @param processPropertyPlaceHolders a flag that whether execute a property placeholder processing or not
	 */
	public void setProcessPropertyPlaceHolders(boolean processPropertyPlaceHolders) {
		this.processPropertyPlaceHolders = processPropertyPlaceHolders;
	}

	/**
	 * The class of the {@link MapperFactoryBean} to return a mybatis proxy as spring bean.
	 * @param mapperFactoryBeanClass  The class of the MapperFactoryBean
	 * @since 2.0.1
	 */
	public void setMapperFactoryBeanClass(Class<? extends MapperFactoryBean> mapperFactoryBeanClass) {
		this.mapperFactoryBeanClass = mapperFactoryBeanClass;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) {
		this.applicationContext = applicationContext;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setBeanName(String name) {
		this.beanName = name;
	}

	/**
	 * Gets beanNameGenerator to be used while running the scanner.
	 * @return the beanNameGenerator BeanNameGenerator that has been configured
	 * @since 1.2.0
	 */
	public BeanNameGenerator getNameGenerator() {
		return nameGenerator;
	}

	/**
	 * Sets beanNameGenerator to be used while running the scanner.
	 * @param nameGenerator the beanNameGenerator to set
	 * @since 1.2.0
	 */
	public void setNameGenerator(BeanNameGenerator nameGenerator) {
		this.nameGenerator = nameGenerator;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void afterPropertiesSet() throws Exception {
		notNull(this.basePackage, "Property 'basePackage' is required");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) {
		// left intentionally blank
	}

	/**
	 * {@inheritDoc}
	 * @since 1.0.2
	 */
	@Override
	public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) {
		if (this.processPropertyPlaceHolders) {
			processPropertyPlaceHolders();
		}
		// 创建ClassPathMapperScanner对象
		ClassPathMapperScanner scanner = new ClassPathMapperScanner(registry);
		scanner.setAddToConfig(this.addToConfig);
		scanner.setAnnotationClass(this.annotationClass);
		scanner.setMarkerInterface(this.markerInterface);
		scanner.setSqlSessionFactory(this.sqlSessionFactory);
		scanner.setSqlSessionTemplate(this.sqlSessionTemplate);
		scanner.setSqlSessionFactoryBeanName(this.sqlSessionFactoryBeanName);
		scanner.setSqlSessionTemplateBeanName(this.sqlSessionTemplateBeanName);
		scanner.setResourceLoader(this.applicationContext);
		scanner.setBeanNameGenerator(this.nameGenerator);
		scanner.setMapperFactoryBeanClass(this.mapperFactoryBeanClass);
		if (StringUtils.hasText(lazyInitialization)) {
			scanner.setLazyInitialization(Boolean.valueOf(lazyInitialization));
		}
		// 根据上面的配置，生成相应的过滤器。这些过滤器在扫描过程中会过滤掉不符合添加的内容，例如，
		// annotationClass字段不为null时，则会添加AnnotationTypeFilter过滤器，通过该过滤器
		// 实现只扫描annotationClass注解标识的接口的功能
		scanner.registerFilters();
		scanner.scan(StringUtils.tokenizeToStringArray(this.basePackage, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS));
	}

	/*
	 * BeanDefinitionRegistries are called early in application startup, before BeanFactoryPostProcessors. This means that
	 * PropertyResourceConfigurers will not have been loaded and any property substitution of this class' properties will
	 * fail. To avoid this, find any PropertyResourceConfigurers defined in the context and run them on this class' bean
	 * definition. Then update the values.
	 */
	private void processPropertyPlaceHolders() {
		Map<String, PropertyResourceConfigurer> prcs = applicationContext.getBeansOfType(PropertyResourceConfigurer.class);

		if (!prcs.isEmpty() && applicationContext instanceof ConfigurableApplicationContext) {
			BeanDefinition mapperScannerBean = ((ConfigurableApplicationContext) applicationContext).getBeanFactory().getBeanDefinition(beanName);
			// PropertyResourceConfigurer does not expose any methods to explicitly perform
			// property placeholder substitution. Instead, create a BeanFactory that just
			// contains this mapper scanner and post process the factory.
			DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
			factory.registerBeanDefinition(beanName, mapperScannerBean);

			for (PropertyResourceConfigurer prc : prcs.values()) {
				prc.postProcessBeanFactory(factory);
			}

			PropertyValues values = mapperScannerBean.getPropertyValues();

			this.basePackage = updatePropertyValue("basePackage", values);
			this.sqlSessionFactoryBeanName = updatePropertyValue("sqlSessionFactoryBeanName", values);
			this.sqlSessionTemplateBeanName = updatePropertyValue("sqlSessionTemplateBeanName", values);
			this.lazyInitialization = updatePropertyValue("lazyInitialization", values);
		}
		this.basePackage = Optional.ofNullable(this.basePackage).map(getEnvironment()::resolvePlaceholders).orElse(null);
		this.sqlSessionFactoryBeanName = Optional.ofNullable(this.sqlSessionFactoryBeanName)
				.map(getEnvironment()::resolvePlaceholders).orElse(null);
		this.sqlSessionTemplateBeanName = Optional.ofNullable(this.sqlSessionTemplateBeanName)
				.map(getEnvironment()::resolvePlaceholders).orElse(null);
		this.lazyInitialization = Optional.ofNullable(this.lazyInitialization).map(getEnvironment()::resolvePlaceholders)
				.orElse(null);
	}

	private Environment getEnvironment() {
		return this.applicationContext.getEnvironment();
	}

	private String updatePropertyValue(String propertyName, PropertyValues values) {
		PropertyValue property = values.getPropertyValue(propertyName);

		if (property == null) {
			return null;
		}

		Object value = property.getValue();

		if (value == null) {
			return null;
		} else if (value instanceof String) {
			return value.toString();
		} else if (value instanceof TypedStringValue) {
			return ((TypedStringValue) value).getValue();
		} else {
			return null;
		}
	}

}
