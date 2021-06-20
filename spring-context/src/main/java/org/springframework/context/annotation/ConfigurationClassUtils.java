

package org.springframework.context.annotation;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.core.Conventions;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.core.type.StandardAnnotationMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

/**
 * Utilities for identifying {@link Configuration} classes.
 * @since 3.1
 *
 *
 * Full模式和Lite模式
 * Full模式和Lite模式均是针对于Spring配置类而言的，和xml配置文件无关。
 * 值得注意的是：判断是Full模式 or Lite模式的前提是，首先你得是个容器组件。
 * 至于一个实例是如何“晋升”成为容器组件的，可以用注解也可以没有注解，本文就不展开讨论了，这属于Spring的基础知识。
 *
 * Lite模式
 * 当@Bean方法在没有使用@Configuration注释的类中声明时，它们被称为在Lite模式下处理。
 * 它包括：在@Component中声明的@Bean方法，甚至只是在一个非常普通的类中声明的Bean方法，都被认为是Lite版的配置类。@Bean方法是一种通用的工厂方法（factory-method）机制。
 * 和Full模式的@Configuration不同，Lite模式的@Bean方法不能声明Bean之间的依赖关系。
 * 因此，这样的@Bean方法不应该调用其他@Bean方法。每个这样的方法实际上只是一个特定Bean引用的工厂方法(factory-method)，没有任何特殊的运行时语义。
 *
 * 何时为Lite模式
 * 官方定义为：在没有标注@Configuration的类里面有@Bean方法就称为Lite模式的配置。透过源码再看这个定义是不完全正确的，而应该是有如下case均认为是Lite模式的配置类：
 * 类上标注有@Component注解
 * 类上标注有@ComponentScan注解
 * 类上标注有@Import注解
 * 类上标注有@ImportResource注解
 * 若类上没有任何注解，但类内存在@Bean方法
 *
 * 优点：
 *
 * 运行时不再需要给对应类生成CGLIB子类，提高了运行性能，降低了启动时间
 * 可以该配置类当作一个普通类使用喽：也就是说@Bean方法 可以是private、可以是final
 * 缺点：
 *
 * 不能声明@Bean之间的依赖，也就是说不能通过方法调用来依赖其它Bean
 * （其实这个缺点还好，很容易用其它方式“弥补”，比如：把依赖Bean放进方法入参里即可）
 *
 * 小总结
 * 该模式下，配置类本身不会被CGLIB增强，放进IoC容器内的就是本尊
 * 该模式下，对于内部类是没有限制的：可以是Full模式或者Lite模式
 * 该模式下，配置类内部不能通过方法调用来处理依赖，否则每次生成的都是一个新实例而并非IoC容器内的单例
 * 该模式下，配置类就是一普通类嘛，所以@Bean方法可以使用private/final等进行修饰（static自然也是阔仪的）
 *
 *
 * Full模式
 * 在常见的场景中，@Bean方法都会在标注有@Configuration的类中声明，以确保总是使用“Full模式”，这么一来，交叉方法引用会被重定向到容器的生命周期管理，所以就可以更方便的管理Bean依赖。
 * 何时为Full模式
 * 标注有@Configuration注解的类被称为full模式的配置类。自Spring5.2后这句话改为下面这样我觉得更为精确些：
 *
 * 标注有@Configuration或者@Configuration(proxyBeanMethods = true)的类被称为Full模式的配置类 （当然喽，proxyBeanMethods属性的默认值是true，所以一般需要Full模式我们只需要标个注解即可）
 *优点：
 *
 * 可以支持通过常规Java调用相同类的@Bean方法而保证是容器内的Bean，这有效规避了在“Lite模式”下操作时难以跟踪的细微错误。特别对于萌新程序员，这个特点很有意义
 * 缺点：
 *
 * 运行时会给该类生成一个CGLIB子类放进容器，有一定的性能、时间开销（这个开销在Spring Boot这种拥有大量配置类的情况下是不容忽视的，这也是为何Spring 5.2新增了proxyBeanMethods属性的最直接原因）
 * 正因为被代理了，所以@Bean方法 不可以是private、不可以是final
 *
 * 小总结
 * 该模式下，配置类会被CGLIB增强(生成代理对象)，放进IoC容器内的是代理
 * 该模式下，对于内部类是没有限制的：可以是Full模式或者Lite模式
 * 该模式下，配置类内部可以通过方法调用来处理依赖，并且能够保证是同一个实例，都指向IoC内的那个单例
 * 该模式下，@Bean方法不能被private/final等进行修饰（很简单，因为方法需要被复写嘛，所以不能私有和final。defualt/protected/public都可以哦），否则启动报错（其实IDEA编译器在编译器就提示可以提示你了）：
 *
 * 使用建议
 * 了解了Spring配置类的Full模式和Lite模式，那么在工作中我该如何使用呢？这里A哥给出使用建议，仅供参考：
 *
 * 如果是在公司的业务功能/服务上做开发，使用Full模式
 * 如果你是个容器开发者，或者你在开发中间件、通用组件等，那么使用Lite模式是一种更被推荐的方式，它对Cloud Native更为友好
 */
abstract class ConfigurationClassUtils {

	// configuration class如果是@Configuration注解标注的类属性标注为full
	private static final String CONFIGURATION_CLASS_FULL = "full";

	// 非@Configuration注解标注的类（@Component、@Import等注解标注）属性标注为lite
	private static final String CONFIGURATION_CLASS_LITE = "lite";

	// 即值：org.springframework.context.annotation.ConfigurationClassPostProcessor.configurationClass作为属性配置类型标记属性的key
	private static final String CONFIGURATION_CLASS_ATTRIBUTE = Conventions.getQualifiedAttributeName(ConfigurationClassPostProcessor.class, "configurationClass");

	// 即值：org.springframework.context.annotation.ConfigurationClassPostProcessor.order配置属性配置类排序的属性key
	private static final String ORDER_ATTRIBUTE = Conventions.getQualifiedAttributeName(ConfigurationClassPostProcessor.class, "order");

	private static final Log logger = LogFactory.getLog(ConfigurationClassUtils.class);

	//字典，存储标注配置类的注解
	private static final Set<String> candidateIndicators = new HashSet<>(8);

	static {
		candidateIndicators.add(Component.class.getName());
		candidateIndicators.add(ComponentScan.class.getName());
		candidateIndicators.add(Import.class.getName());
		candidateIndicators.add(ImportResource.class.getName());
	}

	/**
	 * Check whether the given bean definition is a candidate for a configuration class
	 * (or a nested component class declared within a configuration/component class,to be auto-registered as well), and mark it accordingly.
	 * @param beanDef the bean definition to check
	 * @param metadataReaderFactory the current factory in use by the caller
	 * @return whether the candidate qualifies as (any kind of) configuration class
	 */
	public static boolean checkConfigurationClassCandidate(BeanDefinition beanDef, MetadataReaderFactory metadataReaderFactory) {
		String className = beanDef.getBeanClassName();
		if (className == null || beanDef.getFactoryMethodName() != null) {
			return false;
		}
		AnnotationMetadata metadata;
		if (beanDef instanceof AnnotatedBeanDefinition && className.equals(((AnnotatedBeanDefinition) beanDef).getMetadata().getClassName())) {
			// Can reuse the pre-parsed metadata from the given BeanDefinition...
			metadata = ((AnnotatedBeanDefinition) beanDef).getMetadata();
		}else if (beanDef instanceof AbstractBeanDefinition && ((AbstractBeanDefinition) beanDef).hasBeanClass()) {
			// 检查已经加载的类，如果存在的话
			// 因为我们甚至可能无法加载这个类的类文件
			// 获取bean定义的class实例对象，如果class实例是下面四种类或接口的相同、子类、父接口等任何一种情况，直接返回
			// Check already loaded Class if present...  since we possibly can't even load the class file for this Class.
			Class<?> beanClass = ((AbstractBeanDefinition) beanDef).getBeanClass();
			metadata = new StandardAnnotationMetadata(beanClass, true);
		}else {
			try {
				// 获取className的MetadataReader实例
				MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(className);
				// 读取底层类的完整注释元数据，包括带注释方法的元数据
				metadata = metadataReader.getAnnotationMetadata();
			}catch (IOException ex) {
				if (logger.isDebugEnabled()) logger.debug("Could not find class file for introspecting configuration annotations: " + className, ex);
				return false;
			}
		}

		if (isFullConfigurationCandidate(metadata)) {
			beanDef.setAttribute(CONFIGURATION_CLASS_ATTRIBUTE, CONFIGURATION_CLASS_FULL);
		}else if (isLiteConfigurationCandidate(metadata)) {
			beanDef.setAttribute(CONFIGURATION_CLASS_ATTRIBUTE, CONFIGURATION_CLASS_LITE);
		}else {
			return false;
		}
		// bean定义是一个标记为full或lite的候选项，如果设置order则设置order属性值
		// It's a full or lite configuration candidate... Let's determine the order value, if any.
		Integer order = getOrder(metadata);
		if (order != null) {
			//设置bean定义的order值
			beanDef.setAttribute(ORDER_ATTRIBUTE, order);
		}
		return true;
	}

	/**
	 * Check the given metadata for a configuration class candidate (or nested component class declared within a configuration/component class).
	 * @param metadata the metadata of the annotated class
	 * @return {@code true} if the given class is to be registered as a reflection-detected bean definition; {@code false} otherwise
	 * // 不管是Full模式还是Lite模式，都被认为是候选的配置类  是上面两个方法的结合
	 */
	public static boolean isConfigurationCandidate(AnnotationMetadata metadata) {
		return (isFullConfigurationCandidate(metadata) || isLiteConfigurationCandidate(metadata));
	}

	/**
	 * Check the given metadata for a full configuration class candidate  (i.e. a class annotated with {@code @Configuration}).
	 * @param metadata the metadata of the annotated class
	 * @return {@code true} if the given class is to be processed as a full  configuration class, including cross-method call interception
	 * 只要这个类标注了：@Configuration注解就行  哪怕是接口、抽象类都木有问题
	 * 1.如果类上有@Configuration注解说明是一个完全（Full）的配置类
	 * 2.如果如果类上面有@Component，@ComponentScan，@Import，@ImportResource这些注解，那么就是一个简化配置类。如果不是上面两种情况，那么有@Bean注解修饰的方法也是简化配置类
	 */
	public static boolean isFullConfigurationCandidate(AnnotationMetadata metadata) {
		return metadata.isAnnotated(Configuration.class.getName());
	}

	/**
	 * Check the given metadata for a lite configuration class candidate  (e.g. a class annotated with {@code @Component} or just having
	 * {@code @Import} declarations or {@code @Bean methods}).
	 * @param metadata the metadata of the annotated class
	 * @return {@code true} if the given class is to be processed as a lite configuration class, just registering it and scanning it for {@code @Bean} methods
	 *
	// 判断是Lite模式：（首先肯定没有@Configuration注解）
	// 1、不能是接口
	// 2、但凡只有标注了一个下面注解，都算lite模式：@Component @ComponentScan @Import @ImportResource
	// 3、只有存在有一个方法标注了@Bean注解，那就是lite模式
	 */
	public static boolean isLiteConfigurationCandidate(AnnotationMetadata metadata) {
		// Do not consider an interface or an annotation... // 不能是接口
		if (metadata.isInterface()) {
			return false;
		}
		// Any of the typical annotations found? // 但凡只有标注了一个下面注解，都算lite模式：@Component @ComponentScan @Import @ImportResource
		for (String indicator : candidateIndicators) {
			if (metadata.isAnnotated(indicator)) {
				return true;
			}
		}
		// Finally, let's look for @Bean methods... // 只有存在有一个方法标注了@Bean注解，那就是lite模式
		try {
			return metadata.hasAnnotatedMethods(Bean.class.getName());
		}catch (Throwable ex) {
			if (logger.isDebugEnabled()) {
				logger.debug("Failed to introspect @Bean methods on class [" + metadata.getClassName() + "]: " + ex);
			}
			return false;
		}
	}

	/**
	 * Determine whether the given bean definition indicates a full {@code @Configuration} class, through checking {@link #checkConfigurationClassCandidate}'s metadata marker.
	 * // 下面两个方法是直接判断Bean定义信息，是否是配置类，至于Bean定义里这个属性啥时候放进去的，请参考
	 * 	//ConfigurationClassUtils.checkConfigurationClassCandidate(beanDef, this.metadataReaderFactory)方法，它会对每个Bean定义信息进行检测（毕竟刚开始Bean定义信息是非常少的，所以速度也很快）
	 */
	public static boolean isFullConfigurationClass(BeanDefinition beanDef) {
		return CONFIGURATION_CLASS_FULL.equals(beanDef.getAttribute(CONFIGURATION_CLASS_ATTRIBUTE));
	}

	/**
	 * Determine whether the given bean definition indicates a lite {@code @Configuration} class, through checking {@link #checkConfigurationClassCandidate}'s metadata marker.
	 */
	public static boolean isLiteConfigurationClass(BeanDefinition beanDef) {
		return CONFIGURATION_CLASS_LITE.equals(beanDef.getAttribute(CONFIGURATION_CLASS_ATTRIBUTE));
	}

	/**
	 * Determine the order for the given configuration class metadata.
	 * @param metadata the metadata of the annotated class
	 * @return the {@code @Order} annotation value on the configuration class,or {@code Ordered.LOWEST_PRECEDENCE} if none declared
	 * @since 5.0
	 */
	@Nullable
	public static Integer getOrder(AnnotationMetadata metadata) {
		Map<String, Object> orderAttributes = metadata.getAnnotationAttributes(Order.class.getName());
		return (orderAttributes != null ? ((Integer) orderAttributes.get(AnnotationUtils.VALUE)) : null);
	}

	/**
	 * Determine the order for the given configuration class bean definition,as set by {@link #checkConfigurationClassCandidate}.
	 * @param beanDef the bean definition to check
	 * @return the {@link Order @Order} annotation value on the configuration class, or {@link Ordered#LOWEST_PRECEDENCE} if none declared
	 * @since 4.2
	 */
	public static int getOrder(BeanDefinition beanDef) {
		Integer order = (Integer) beanDef.getAttribute(ORDER_ATTRIBUTE);
		return (order != null ? order : Ordered.LOWEST_PRECEDENCE);
	}

}
