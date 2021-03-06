package org.springframework.beans.factory.support;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * 为所有BeanDefinitionReader接口实现类的抽象父类
 * Abstract base class for bean definition readers which implement the {@link BeanDefinitionReader} interface.
 * Provides common properties like the bean factory to work on and the class loader to use for loading bean classes.
 * @since 11.12.2003
 * @see BeanDefinitionReaderUtils
 * 其作用是提供公共属性，就好比容器使用类加载器来加载类。
 * AbstractBeanDefinitionReader 中实现了3个loadBeanDefinitions 留下了一个 loadBeanDefinitions(Resource resource) 给子类实现。
 */
public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader, EnvironmentCapable {

	/** Logger available to subclasses. */
	protected final Log logger = LogFactory.getLog(getClass());

	// bd读取器持有的bean容器
	private final BeanDefinitionRegistry registry;

	@Nullable
	private ResourceLoader resourceLoader;

	@Nullable
	private ClassLoader beanClassLoader;

	private Environment environment;
	// 默认的名字生成器（类名首字母小写）
	private BeanNameGenerator beanNameGenerator = new DefaultBeanNameGenerator();

	/**
	 * Create a new AbstractBeanDefinitionReader for the given bean factory.
	 * If the passed-in bean factory does not only implement the BeanDefinitionRegistry nterface but also the ResourceLoader interface,
	 * iit will be used as default ResourceLoader as well.
	 * This will usually be the case for {@link org.springframework.context.ApplicationContext} implementations.
	 * If given a plain BeanDefinitionRegistry, the default ResourceLoader will be a {@link org.springframework.core.io.support.PathMatchingResourcePatternResolver}.
	 * If the passed-in bean factory also implements {@link EnvironmentCapable} its environment will be used by this reader.
	 * Otherwise, the reader will initialize and use a {@link StandardEnvironment}.
	 * All ApplicationContext implementations are  EnvironmentCapable, while normal BeanFactory implementations are not.
	 * @param registry the BeanFactory to load bean definitions into,in the form of a BeanDefinitionRegistry
	 * @see #setResourceLoader
	 * @see #setEnvironment
	 */
	protected AbstractBeanDefinitionReader(BeanDefinitionRegistry registry) {
		logger.warn("进入 【AbstractBeanDefinitionReader】 构造函数 {}");
		Assert.notNull(registry, "BeanDefinitionRegistry must not be null");
		this.registry = registry;
		// Determine ResourceLoader to use. 1、确定ResourceLoader使用。
		if (this.registry instanceof ResourceLoader) {
			resourceLoader = (ResourceLoader) this.registry;
		}else {
			resourceLoader = new PathMatchingResourcePatternResolver();
		}
		// Inherit Environment if possible   2、如果环境可继承则继承registry的环境,否则重新创建环境
		// 如果注册器里有环境变量，就用它的 否则new一个标准的~~~~  它下面也提供了set方法可以设置
		if (this.registry instanceof EnvironmentCapable) {
			environment = ((EnvironmentCapable) this.registry).getEnvironment();
		}else {
			environment = new StandardEnvironment();
		}
	}

	/**
	 * Set the ResourceLoader to use for resource locations.
	 * If specifying a ResourcePatternResolver, the bean definition reader will be capable of resolving resource patterns to Resource arrays.
	 * Default is PathMatchingResourcePatternResolver, also capable of resource pattern resolving through the ResourcePatternResolver interface.
	 * Setting this to {@code null} suggests that absolute resource loading is not available for this bean definition reader.
	 * @see org.springframework.core.io.support.ResourcePatternResolver
	 * @see org.springframework.core.io.support.PathMatchingResourcePatternResolver
	 */
	public void setResourceLoader(@Nullable ResourceLoader resourceLoader) {
		this.resourceLoader = resourceLoader;
	}

	public final BeanDefinitionRegistry getBeanFactory() {
		return registry;
	}

	/**
	 * Set the ClassLoader to use for bean classes.
	 * Default is {@code null}, which suggests to not load bean classes eagerly but rather to just register bean definitions with class names,
	 * with the corresponding Classes to be resolved later (or never).
	 * @see Thread#getContextClassLoader()
	 */
	public void setBeanClassLoader(@Nullable ClassLoader beanClassLoader) {
		this.beanClassLoader = beanClassLoader;
	}

	/**
	 * Set the Environment to use when reading bean definitions.
	 * Most often used for evaluating profile information to determine which bean definitions should be read and which should be omitted.
	 */
	public void setEnvironment(Environment environment) {
		Assert.notNull(environment, "Environment must not be null");
		this.environment = environment;
	}

	/**
	 * Set the BeanNameGenerator to use for anonymous beans (without explicit bean name specified).
	 * Default is a {@link DefaultBeanNameGenerator}.
	 */
	public void setBeanNameGenerator(@Nullable BeanNameGenerator beanNameGenerator) {
		this.beanNameGenerator = (beanNameGenerator != null ? beanNameGenerator : new DefaultBeanNameGenerator());
	}

	/**
	 * Load bean definitions from the specified resource location.
	 * The location can also be a location pattern, provided that the  ResourceLoader of this bean definition reader is a ResourcePatternResolver.
	 * @param location the resource location, to be loaded with the ResourceLoader  (or ResourcePatternResolver) of this bean definition reader
	 * @param actualResources a Set to be filled with the actual Resource objects that have been resolved during the loading process.
	 * May be {@code null} to indicate that the caller is not interested in those Resource objects.
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 * @see #getResourceLoader()
	 * @see #loadBeanDefinitions(org.springframework.core.io.Resource)
	 * @see #loadBeanDefinitions(org.springframework.core.io.Resource[])
	 */
	public int loadBeanDefinitions(String location, @Nullable Set<Resource> actualResources) throws BeanDefinitionStoreException {
		ResourceLoader resourceLoader = getResourceLoader();
		if (resourceLoader == null) {
			throw new BeanDefinitionStoreException("Cannot load bean definitions from location [" + location + "]: no ResourceLoader available");
		}
		if (resourceLoader instanceof ResourcePatternResolver) {
			// Resource pattern matching available.
			try {
				// 把字符串类型的xml文件路径，（例如：classpath*:user/**/*-context.xml）转换成resources对象
				Resource[] resources = ((ResourcePatternResolver) resourceLoader).getResources(location);
				// 这里重点解析xml
				int count = loadBeanDefinitions(resources);
				if (actualResources != null) {
					Collections.addAll(actualResources, resources);
				}
				if (logger.isTraceEnabled()) logger.trace("Loaded " + count + " bean definitions from location pattern [" + location + "]");
				return count;
			}catch (IOException ex) {
				throw new BeanDefinitionStoreException("Could not resolve bean definition resource pattern [" + location + "]", ex);
			}
		}else {
			// Can only load single resources by absolute URL.
			Resource resource = resourceLoader.getResource(location);
			int count = loadBeanDefinitions(resource);
			if (actualResources != null) {
				actualResources.add(resource);
			}
			if (logger.isTraceEnabled()) {
				logger.trace("Loaded " + count + " bean definitions from location [" + location + "]");
			}
			return count;
		}
	}

	//---------------------------------------------------------------------
	// Implementation of 【BeanDefinitionReader】 interface
	//---------------------------------------------------------------------
	@Override
	public final BeanDefinitionRegistry getRegistry() {
		return registry;
	}

	@Override
	@Nullable
	public ResourceLoader getResourceLoader() {
		return resourceLoader;
	}

	@Override
	@Nullable
	public ClassLoader getBeanClassLoader() {
		return beanClassLoader;
	}

	@Override
	public BeanNameGenerator getBeanNameGenerator() {
		return beanNameGenerator;
	}

	@Override
	public int loadBeanDefinitions(Resource... resources) throws BeanDefinitionStoreException {
		Assert.notNull(resources, "Resource array must not be null");
		int count = 0;
		// 注意这里是个 for 循环，也就是每个文件是一个 resource
		for (Resource resource : resources) {
			// 模板方法设计模式  XmlBeanDefinitionReader
			count += loadBeanDefinitions(resource);
		}
		return count;
	}

	@Override
	public int loadBeanDefinitions(String location) throws BeanDefinitionStoreException {
		return loadBeanDefinitions(location, null);
	}

	@Override
	public int loadBeanDefinitions(String... locations) throws BeanDefinitionStoreException {
		Assert.notNull(locations, "Location array must not be null");
		int count = 0;
		for (String location : locations) {
			count += loadBeanDefinitions(location);
		}
		return count;
	}

	//---------------------------------------------------------------------
	// Implementation of 【EnvironmentCapable】 interface
	//---------------------------------------------------------------------
	@Override
	public Environment getEnvironment() {
		return environment;
	}
}
