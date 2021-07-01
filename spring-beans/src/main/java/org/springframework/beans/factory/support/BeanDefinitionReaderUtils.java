

package org.springframework.beans.factory.support;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Utility methods that are useful for bean definition reader implementations.Mainly intended for internal use.
 * @since 1.1
 * @see PropertiesBeanDefinitionReader
 * @see org.springframework.beans.factory.xml.DefaultBeanDefinitionDocumentReader
 */
public abstract class BeanDefinitionReaderUtils {

	private static final Logger logger = Logger.getLogger(BeanDefinitionReaderUtils.class);

	// Separator for generated bean names. If a class name or parent name is not unique, "#1", "#2" etc will be appended, until the name becomes unique.
	public static final String GENERATED_BEAN_NAME_SEPARATOR = BeanFactoryUtils.GENERATED_BEAN_NAME_SEPARATOR;

	/**
	 * Create a new GenericBeanDefinition for the given parent name and class name,eagerly loading the bean class if a ClassLoader has been specified.
	 * @param parentName the name of the parent bean, if any
	 * @param className the name of the bean class, if any
	 * @param classLoader the ClassLoader to use for loading bean classes (can be {@code null} to just register bean classes by name)
	 * @return the bean definition
	 * @throws ClassNotFoundException if the bean class could not be loaded
	 */
	public static AbstractBeanDefinition createBeanDefinition(@Nullable String parentName, @Nullable String className, @Nullable ClassLoader classLoader) throws ClassNotFoundException {
		GenericBeanDefinition bd = new GenericBeanDefinition();
		bd.setParentName(parentName);
		if (className != null) {
			if (classLoader != null) {
				bd.setBeanClass(ClassUtils.forName(className, classLoader));
			}else {
				bd.setBeanClassName(className);
			}
		}
		return bd;
	}

	/**
	 * Generate a bean name for the given top-level bean definition,unique within the given bean factory.
	 * @param beanDefinition the bean definition to generate a bean name for
	 * @param registry the bean factory that the definition is going to be registered with (to check for existing bean names)
	 * @return the generated bean name
	 * @throws BeanDefinitionStoreException if no unique name can be generated for the given bean definition
	 * @see #generateBeanName(BeanDefinition, BeanDefinitionRegistry, boolean)
	 */
	public static String generateBeanName(BeanDefinition beanDefinition, BeanDefinitionRegistry registry) throws BeanDefinitionStoreException {
		return generateBeanName(beanDefinition, registry, false);
	}

	/**
	 * Generate a bean name for the given bean definition, unique within the given bean factory.
	 * @param definition the bean definition to generate a bean name for
	 * @param registry the bean factory that the definition is going to be registered with (to check for existing bean names)
	 * @param isInnerBean whether the given bean definition will be registered as inner bean or as top-level bean (allowing for special name generation for inner beans versus top-level beans)
	 * @return the generated bean name
	 * @throws BeanDefinitionStoreException if no unique name can be generated for the given bean definition
	 * 假如是innerBean（比如Spring AOP产生的Bean），使用【类全路径+#+对象HashCode的16进制】的格式来命名Bean
	 * 假如不是innerBean，使用【类全路径+#+数字】的格式来命名Bean，其中数字指的是，同一个Bean出现1次，只要该Bean没有id，就从0开始依次向上累加，比如a.b.c#0、a.b.c#1、a.b.c#2
	 *
	 * 　重新整理下流程：生成流程分为前后两部分，前面生成的叫前缀，后面生成的叫后缀。
	 * 　　　　1，读取待生成name实例的类名称，未必是运行时的实际类型。
	 * 　　　　2，如果类型为空，则判断是否存在parent bean，如果存在，读取parent bean的name+"$child"。
	 * 　　　　3，如果parent bean 为空，那么判断是否存在factory bean ，如存在，factory bean name + "$created".前缀生成完毕。
	 * 　　　　4，如果前缀为空，直接抛出异常，没有可以定义这个bean的任何依据。
	 * 　　　　5，前缀存在，判断是否为内部bean（innerBean，此处默认为false），如果是，最终为前缀+分隔符+十六进制的hashcode码、
	 * 　　　　6，如果是顶级bean（top-level bean ），则判断前缀+数字的bean是否已存在，循环查询，知道查询到没有使用的id为止。处理完成。
	 */
	public static String generateBeanName(BeanDefinition definition, BeanDefinitionRegistry registry, boolean isInnerBean) throws BeanDefinitionStoreException {
		// 1、获取bean的className，generatedBeanName定义为类前缀， 读取bean的className,不一定是运行时的实际类型。
		String generatedBeanName = definition.getBeanClassName();
		// 2、如果generatedBeanName为null，  // 如果类名称为空，那读取bean的parent bean name
		if (generatedBeanName == null) {
			// parent属性不为空
			if (definition.getParentName() != null) {
				generatedBeanName = definition.getParentName() + "$child";
			}else if (definition.getFactoryBeanName() != null) {
			//否则，读取生成该bean的factoryBean name名称做前缀。 factory-bean属性不为空
				generatedBeanName = definition.getFactoryBeanName() + "$created";
			}
		}
		// 3、如果上述方法均无法生成generatedBeanName，则抛出异常
		if (!StringUtils.hasText(generatedBeanName)) {
			throw new BeanDefinitionStoreException("Unnamed bean definition specifies neither 'class' nor 'parent' nor 'factory-bean' - can't generate bean name");
		}
		// 4、判断是否内部bean，并根据生成的generatedBeanName，拼接最终的beanName返回
		String id = generatedBeanName; // shit
		if (isInnerBean) {   //当为内部bean时，调用系统底层的object唯一标识码生成
			// Inner bean: generate identity hashcode suffix.
			id = generatedBeanName + GENERATED_BEAN_NAME_SEPARATOR + ObjectUtils.getIdentityHexString(definition);
		}else { //否则即为顶级bean，生成策略是前缀+循环数字，直到找到对应自增id
			// Top-level bean: use plain class name with unique suffix if necessary.
			logger.warn("【生成顶级bean名称】： " + generatedBeanName);
			return uniqueBeanName(generatedBeanName, registry);
		}
		logger.warn("【生成内部bean名称】： " + generatedBeanName);
		return id;
	}

	/**
	 * Turn the given bean name into a unique bean name for the given bean factory,appending a unique counter as suffix if necessary.
	 * @param beanName the original bean name
	 * @param registry the bean factory that the definition is going to be registered with (to check for existing bean names)
	 * @return the unique bean name to use
	 * @since 5.1
	 */
	public static String uniqueBeanName(String beanName, BeanDefinitionRegistry registry) {
		String id = beanName;
		int counter = -1;
		// Increase counter until the id is unique.
		while (counter == -1 || registry.containsBeanDefinition(id)) {
			counter++;
			id = beanName + GENERATED_BEAN_NAME_SEPARATOR + counter;
		}
		return id;
	}

	/**
	 * Register the given bean definition with the given bean factory.
	 * 解析bean标签将其转换为definition并注册到BeanDefinitionRegistry
	 * @param definitionHolder the bean definition including name and aliases
	 * @param registry the bean factory to register with
	 * @throws BeanDefinitionStoreException if registration failed
	 */
	public static void registerBeanDefinition(BeanDefinitionHolder definitionHolder, BeanDefinitionRegistry registry) throws BeanDefinitionStoreException {
		// 1、注册BeanDefinition    Register bean definition under primary name.
		String beanName = definitionHolder.getBeanName();
		// 使用 P1=beanName 和  P2=AbstractBeanDefinition 向容器中注册bean定义
		registry.registerBeanDefinition(beanName, definitionHolder.getBeanDefinition());
		//  2、注册aliases（别名） Register aliases for bean name, if any.
		String[] aliases = definitionHolder.getAliases();
		if (aliases != null) {
			for (String alias : aliases) {
				registry.registerAlias(beanName, alias);
			}
		}
	}

	/**
	 * Register the given bean definition with a generated name,unique within the given bean factory.
	 * @param definition the bean definition to generate a bean name for
	 * @param registry the bean factory to register with
	 * @return the generated bean name
	 * @throws BeanDefinitionStoreException if no unique name can be generated for the given bean definition or the definition cannot be registered
	 */
	public static String registerWithGeneratedName(AbstractBeanDefinition definition, BeanDefinitionRegistry registry) throws BeanDefinitionStoreException {
		String generatedName = generateBeanName(definition, registry, false);
		registry.registerBeanDefinition(generatedName, definition);
		return generatedName;
	}
}
