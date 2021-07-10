package org.springframework.beans.factory.support;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;

/**
 * Simple interface for bean definition readers.
 * Specifies load methods with Resource and String location parameters.
 * Concrete bean definition readers can of course add additional load and register methods for bean definitions, specific to their bean definition format.
 * Note that a bean definition reader does not have to implement this interface.
 * It only serves as suggestion for bean definition readers that want to follow standard naming conventions.
 * @since 1.1
 * @see org.springframework.core.io.Resource
 * BeanDefinitionReader用于加载Bean,常规用到最多的就是bean的xml配置
 */
public interface BeanDefinitionReader {

	/**
	 * Return the bean factory to register the bean definitions with.
	 * The factory is exposed through the BeanDefinitionRegistry interface,encapsulating the methods that are relevant for bean definition handling.
	 * // 得到Bean定义的register
	 */
	BeanDefinitionRegistry getRegistry();

	/**
	 * Return the resource loader to use for resource locations.
	 * Can be checked for the <b>ResourcePatternResolver</b> interface and cast accordingly, for loading multiple resources for a given resource pattern.
	 * A {@code null} return value suggests that absolute resource loading  is not available for this bean definition reader.
	 * This is mainly meant to be used for importing further resources from within a bean definition resource, for example via the "import" tag in XML bean definitions.
	 * It is recommended, however, to apply such imports relative to the defining resource; only explicit full resource locations will trigger absolute resource loading.
	 * There is also a {@code loadBeanDefinitions(String)} method available,for loading bean definitions from a resource location (or location pattern).
	 * This is a convenience to avoid explicit ResourceLoader handling.
	 * @see #loadBeanDefinitions(String)
	 * @see org.springframework.core.io.support.ResourcePatternResolver
	 */
	@Nullable
	ResourceLoader getResourceLoader();

	/**
	 * Return the class loader to use for bean classes. {@code null} suggests to not load bean classes eagerly
	 * but rather to just register bean definitions with class names,with the corresponding Classes to be resolved later (or never).
	 */
	@Nullable
	ClassLoader getBeanClassLoader();

	/**
	 * 生成Bean名称的名字生成器（若没有指定名称的话，会调用它生成）
	 * Return the BeanNameGenerator to use for anonymous beans (without explicit bean name specified).
	 */
	BeanNameGenerator getBeanNameGenerator();

	//---------------------------------------------------------------------
	//  核心方法，loadbean定义进来，然后注册到上面的register 里面去
	//---------------------------------------------------------------------
	/**
	 * 以Resource形式，读取bean定义信息
	 * Load bean definitions from the specified resource.
	 * @param resource the resource descriptor
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 */
	int loadBeanDefinitions(Resource resource) throws BeanDefinitionStoreException;

	/**
	 * 以Resource形式，读取bean定义信息
	 * Load bean definitions from the specified resources.
	 * @param resources the resource descriptors
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 */
	int loadBeanDefinitions(Resource... resources) throws BeanDefinitionStoreException;

	/**
	 * 以资源文件路径形式，读取bean定义信息
	 * Load bean definitions from the specified resource location.
	 * The location can also be a location pattern, provided that the ResourceLoader of this bean definition reader is a ResourcePatternResolver.
	 * @param location the resource location, to be loaded with the ResourceLoader (or ResourcePatternResolver) of this bean definition reader
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 * @see #getResourceLoader()
	 * @see #loadBeanDefinitions(org.springframework.core.io.Resource)
	 * @see #loadBeanDefinitions(org.springframework.core.io.Resource[])
	 */
	int loadBeanDefinitions(String location) throws BeanDefinitionStoreException;

	/**
	 * 以资源文件路径形式，读取bean定义信息
	 * Load bean definitions from the specified resource locations.
	 * @param locations the resource locations, to be loaded with the ResourceLoader (or ResourcePatternResolver) of this bean definition reader
	 * @return the number of bean definitions found
	 * @throws BeanDefinitionStoreException in case of loading or parsing errors
	 */
	int loadBeanDefinitions(String... locations) throws BeanDefinitionStoreException;
}
