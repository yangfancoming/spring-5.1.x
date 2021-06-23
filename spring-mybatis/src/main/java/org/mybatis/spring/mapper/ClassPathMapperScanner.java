
package org.mybatis.spring.mapper;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.config.RuntimeBeanReference;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.Set;

/**
 * A {@link ClassPathBeanDefinitionScanner} that registers Mappers by {@code basePackage}, {@code annotationClass}, or {@code markerInterface}.
 * If an {@code annotationClass} and/or {@code markerInterface} is specified, only the specified types will be searched (searching for all interfaces will be disabled).
 * This functionality was previously a private class of {@link MapperScannerConfigurer}, but was broken out in version 1.2.0.
 * @see MapperFactoryBean
 * @since 1.2.0
 */
public class ClassPathMapperScanner extends ClassPathBeanDefinitionScanner {

	private static final Logger LOGGER = LoggerFactory.getLogger(ClassPathMapperScanner.class);

	private boolean addToConfig = true;

	private boolean lazyInitialization;

	private SqlSessionFactory sqlSessionFactory;

	private SqlSessionTemplate sqlSessionTemplate;

	private String sqlSessionTemplateBeanName;

	private String sqlSessionFactoryBeanName;

	private Class<? extends Annotation> annotationClass;

	private Class<?> markerInterface;

	private Class<? extends MapperFactoryBean> mapperFactoryBeanClass = MapperFactoryBean.class;

	public ClassPathMapperScanner(BeanDefinitionRegistry registry) {
		super(registry, false);
	}

	public void setAddToConfig(boolean addToConfig) {
		this.addToConfig = addToConfig;
	}

	public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
		this.annotationClass = annotationClass;
	}

	/**
	 * Set whether enable lazy initialization for mapper bean.
	 * Default is {@code false}.
	 * @param lazyInitialization Set the @{code true} to enable
	 * @since 2.0.2
	 */
	public void setLazyInitialization(boolean lazyInitialization) {
		this.lazyInitialization = lazyInitialization;
	}

	public void setMarkerInterface(Class<?> markerInterface) {
		this.markerInterface = markerInterface;
	}

	public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionFactory = sqlSessionFactory;
	}

	public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate) {
		this.sqlSessionTemplate = sqlSessionTemplate;
	}

	public void setSqlSessionTemplateBeanName(String sqlSessionTemplateBeanName) {
		this.sqlSessionTemplateBeanName = sqlSessionTemplateBeanName;
	}

	public void setSqlSessionFactoryBeanName(String sqlSessionFactoryBeanName) {
		this.sqlSessionFactoryBeanName = sqlSessionFactoryBeanName;
	}

	/**
	 * @deprecated Since 2.0.1, Please use the {@link #setMapperFactoryBeanClass(Class)}.
	 */
	@Deprecated
	public void setMapperFactoryBean(MapperFactoryBean<?> mapperFactoryBean) {
		this.mapperFactoryBeanClass = mapperFactoryBean == null ? MapperFactoryBean.class : mapperFactoryBean.getClass();
	}

	/**
	 * Set the {@code MapperFactoryBean} class.
	 * @param mapperFactoryBeanClass  the {@code MapperFactoryBean} class
	 * @since 2.0.1
	 */
	public void setMapperFactoryBeanClass(Class<? extends MapperFactoryBean> mapperFactoryBeanClass) {
		this.mapperFactoryBeanClass = mapperFactoryBeanClass == null ? MapperFactoryBean.class : mapperFactoryBeanClass;
	}

	/**
	 * Configures parent scanner to search for the right interfaces. It can search for all interfaces or just for those
	 * that extends a markerInterface or/and those annotated with the annotationClass
	 */
	public void registerFilters() {
		boolean acceptAllInterfaces = true;
		// 如果指定了annotationClass配置，则添加注解类型过滤器，使用给定的注解和/或标记接口
		// 2. 这里的annotationClass是前面注册MapperScannerConfigurer时传递进来的自定义注解属性
		// if specified, use the given annotation and / or marker interface
		if (this.annotationClass != null) {
			// 2.1 这里添加IncludeFilter表示，要添加一个允许的扫描注解，只要标注了该注解就会被ClassLoader扫描到
			addIncludeFilter(new AnnotationTypeFilter(this.annotationClass));
			acceptAllInterfaces = false;
		}

		// 3. 这里的annotationClass是前面注册MapperScannerConfigurer时传递进来的自定义接口Class
		// 如果指定了markerInterface配置，则添加可分配给定类型的过滤器，重写AssignableTypeFilter忽略实际标记接口上的匹配
		// override AssignableTypeFilter to ignore matches on the actual marker interface
		if (this.markerInterface != null) {
			//3.1 扫描自定义的接口类型，并且
			addIncludeFilter(new AssignableTypeFilter(this.markerInterface) {
				@Override
				protected boolean matchClassName(String className) {
					/**
					 * 不能实现类Class，只能是抽象接口或者抽象类
					 * @see AbstractTypeHierarchyTraversingFilter#match(org.springframework.core.type.classreading.MetadataReader, org.springframework.core.type.classreading.MetadataReaderFactory)
					*/
					return false;
				}
			});
			acceptAllInterfaces = false;
		}
		// 如果上面两个没有配置，则添加接受所有接口的过滤器
		// 4. 如果没有自定义注解或者自定义接口扫描，那么添加一个TypeFilter默认全部扫描所有
		if (acceptAllInterfaces) {
			/**
			 * default include filter that accepts all classes
			 * sos 这里就使得 BookMapper 接口类再没有任何【组件系列】注解标注的情况下，也能够被spring扫描并注册bean定义。
			 * @see com.goat.chapter651.dao.BookMapper
			*/
			addIncludeFilter((metadataReader, metadataReaderFactory) -> true);
		}
		// 添加过滤掉package-info.java的过滤器
		// exclude package-info.java
		addExcludeFilter((metadataReader, metadataReaderFactory) -> {
			String className = metadataReader.getClassMetadata().getClassName();
			return className.endsWith("package-info");
		});
	}

	private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
		GenericBeanDefinition definition;
		// 将扫描到的所有dao层mapper接口，循环全部更改其bean定义，替换成 MapperFactoryBean
		for (BeanDefinitionHolder holder : beanDefinitions) {
			definition = (GenericBeanDefinition) holder.getBeanDefinition();
			String beanClassName = definition.getBeanClassName();
			LOGGER.warn(() -> "【mybatis】 Creating MapperFactoryBean with name '" + holder.getBeanName() + "' and '" + beanClassName + "' mapperInterface");
			// the mapper interface is the original class of the bean  but, the actual class of the bean is MapperFactoryBean
			// mapper的接口是bean的原始类，但是，实际的bean类是MapperFactoryBean
			definition.getConstructorArgumentValues().addGenericArgumentValue(beanClassName); // issue #59
			// 偷天换日 // 将BeanDefinition中记录的Bean类型修改为MapperFactoryBean
			// sos 原来的beanDefinitionMap里面key是"bookMapper" value是 BookMapper.class 接口类，这里给改成了  key是"bookMapper" value却是 MapperFactoryBean.class 工厂类
			// 构造MapperFactoryBean的属性，将sqlSessionFactory、sqlSessionTemplate等信息填充到BeanDefinition中
			// 其实是通过更改注册容器中bean的beanClass属性为MapperFactoryBean（工厂bean）生成实例的。
			// 容器中注册的bean其实是工厂bean，spring中的工厂bean获得实例是调用工厂方法获得。
			// 此时的 definition 就是beanDefinitionMap中对应的definition！
			definition.setBeanClass(this.mapperFactoryBeanClass);
			definition.getPropertyValues().add("addToConfig", this.addToConfig);
			boolean explicitFactoryUsed = false;
			if (StringUtils.hasText(this.sqlSessionFactoryBeanName)) {
				definition.getPropertyValues().add("sqlSessionFactory",new RuntimeBeanReference(this.sqlSessionFactoryBeanName));
				explicitFactoryUsed = true;
			} else if (this.sqlSessionFactory != null) {
				definition.getPropertyValues().add("sqlSessionFactory", this.sqlSessionFactory);
				explicitFactoryUsed = true;
			}
			if (StringUtils.hasText(this.sqlSessionTemplateBeanName)) {
				if (explicitFactoryUsed) {
					LOGGER.warn(() -> "Cannot use both: sqlSessionTemplate and sqlSessionFactory together. sqlSessionFactory is ignored.");
				}
				definition.getPropertyValues().add("sqlSessionTemplate",new RuntimeBeanReference(this.sqlSessionTemplateBeanName));
				explicitFactoryUsed = true;
			} else if (this.sqlSessionTemplate != null) {
				if (explicitFactoryUsed) {
					LOGGER.warn(() -> "Cannot use both: sqlSessionTemplate and sqlSessionFactory together. sqlSessionFactory is ignored.");
				}
				definition.getPropertyValues().add("sqlSessionTemplate", this.sqlSessionTemplate);
				explicitFactoryUsed = true;
			}
			// 如果没有显示的指定SqlSessionFactoryBean，则设置当前bd的属性注入方式为按类型注入，与在属性上加了@Autowired效果一致
			if (!explicitFactoryUsed) {
				LOGGER.warn(() -> "Enabling autowire by type for MapperFactoryBean with name '" + holder.getBeanName() + "'.");
				// 设置属性按类型注入
				definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
			}
			definition.setLazyInit(lazyInitialization);
		}
	}

	//---------------------------------------------------------------------
	// Implementation of 【ClassPathScanningCandidateComponentProvider】 class
	//---------------------------------------------------------------------
	@Override
	protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
		return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
	}

	//---------------------------------------------------------------------
	// 重写 of 【ClassPathBeanDefinitionScanner】 的doScan方法
	//---------------------------------------------------------------------
	/**
	 * Calls the parent search that will search and register all the candidates. Then the registered objects are post
	 * processed to set them as MapperFactoryBeans
	 */
	@Override
	public Set<BeanDefinitionHolder> doScan(String... basePackages) {
		LOGGER.warn(() -> "【mybatis】 使用父类doSan() 扫描并注册bean定义，basePackages：" + Arrays.toString(basePackages));
		Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
		if (beanDefinitions.isEmpty()) {
			LOGGER.warn(() -> "No MyBatis mapper was found in '" + Arrays.toString(basePackages)  + "' package. Please check your configuration.");
		} else {
			// 准备偷天换日
			processBeanDefinitions(beanDefinitions);
		}
		return beanDefinitions;
	}

	@Override
	protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) {
		if (super.checkCandidate(beanName, beanDefinition)) {
			return true;
		} else {
			LOGGER.warn(() -> "Skipping MapperFactoryBean with name '" + beanName + "' and '"  + beanDefinition.getBeanClassName() + "' mapperInterface" + ". Bean already defined with the same name!");
			return false;
		}
	}
}
