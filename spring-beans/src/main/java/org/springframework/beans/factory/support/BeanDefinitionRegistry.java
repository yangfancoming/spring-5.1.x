package org.springframework.beans.factory.support;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.core.AliasRegistry;

/**
 * Interface for registries that hold bean definitions, for example RootBeanDefinition and ChildBeanDefinition instances.
 * Typically implemented by BeanFactories that internally work with the AbstractBeanDefinition hierarchy.
 *
 * This is the only interface in Spring's bean factory packages that encapsulates <i>registration</i> of bean definitions.
 * The standard BeanFactory interfaces only cover access to a <i>fully configured factory instance</i>.
 *
 * Spring's bean definition readers expect to work on an implementation of this interface.
 * Known implementors within the Spring core are DefaultListableBeanFactory and GenericApplicationContext.
 * @since 26.11.2003
 * @see org.springframework.beans.factory.config.BeanDefinition
 * @see AbstractBeanDefinition
 * @see RootBeanDefinition
 * @see ChildBeanDefinition
 * @see DefaultListableBeanFactory
 * @see org.springframework.context.support.GenericApplicationContext
 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
 * @see PropertiesBeanDefinitionReader
 * 继承 AliasRegistry  接口， 定义了对 BeanDefinition 的各种增删改查操作
 */
public interface BeanDefinitionRegistry extends AliasRegistry {

	/**
	 * Register a new bean definition with this registry. Must support RootBeanDefinition and ChildBeanDefinition.
	 * @param beanName the name of the bean instance to register
	 * @param beanDefinition definition of the bean instance to register
	 * @throws BeanDefinitionStoreException if the BeanDefinition is invalid
	 * @throws BeanDefinitionOverrideException if there is already a BeanDefinition for the specified bean name and we are not allowed to override it
	 * @see GenericBeanDefinition
	 * @see RootBeanDefinition
	 * @see ChildBeanDefinition
	 *  给定bean名称，注册一个新的bean定义
	 */
	void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) throws BeanDefinitionStoreException;

	/**
	 * Remove the BeanDefinition for the given name.
	 * @param beanName the name of the bean instance to register
	 * @throws NoSuchBeanDefinitionException if there is no such bean definition
	 * 根据指定Bean名移除对应的Bean定义
	 */
	void removeBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

	/**
	 * Return the BeanDefinition for the given bean name.
	 * @param beanName name of the bean to find a definition for
	 * @return the BeanDefinition for the given name (never {@code null})
	 * @throws NoSuchBeanDefinitionException if there is no such bean definition
	 *  根据指定bean名得到对应的Bean定义
	 */
	BeanDefinition getBeanDefinition(String beanName) throws NoSuchBeanDefinitionException;

	/**
	 * Check if this registry contains a bean definition with the given name.
	 * @param beanName the name of the bean to look for
	 * @return if this registry contains a bean definition with the given name
	 * 查找，指定的Bean名是否包含Bean定义
	 */
	boolean containsBeanDefinition(String beanName);

	/**
	 *  获取容器中所有已经注册的Bean定义名称
	 * Return the names of all beans defined in this registry.
	 * @return the names of all beans defined in this registry,or an empty array if none defined
	 */
	String[] getBeanDefinitionNames();

	/**
	 * Return the number of beans defined in the registry.
	 * @return the number of beans defined in the registry
	 * 指定Bean名是否被注册过。
	 */
	int getBeanDefinitionCount();

	/**
	 * Determine whether the given bean name is already in use within this registry,i.e. whether there is a local bean or alias registered under this name.
	 * @param beanName the name to check
	 * @return whether the given bean name is already in use
	 * 指定Bean名是否被占用。
	 */
	boolean isBeanNameInUse(String beanName);
}
