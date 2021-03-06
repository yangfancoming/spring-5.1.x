

package org.springframework.beans.factory.xml;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.core.io.Resource;

/**
 * Convenience extension of {@link DefaultListableBeanFactory} that reads bean definitions from an XML document.
 * Delegates to {@link XmlBeanDefinitionReader} underneath; effectively equivalent to using an XmlBeanDefinitionReader with a DefaultListableBeanFactory.
 *
 * The structure, element and attribute names of the required XML document are hard-coded in this class.
 * (Of course a transform could be run if necessary to produce this format).
 * "beans" doesn't need to be the root element of the XML document: This class will parse all bean definition elements in the XML file.
 *
 * This class registers each bean definition with the {@link DefaultListableBeanFactory}
 * superclass, and relies on the latter's implementation of the {@link BeanFactory} interface.
 * It supports singletons, prototypes, and references to either of these kinds of bean.
 * See {@code "spring-beans-3.x.xsd"} (or historically, {@code "spring-beans-2.0.dtd"}) for details on options and configuration style.
 *
 * <b>For advanced needs, consider using a {@link DefaultListableBeanFactory} with an {@link XmlBeanDefinitionReader}.
 * </b> The latter allows for reading from multiple XML resources and is highly configurable in its actual XML parsing behavior.
 *
 * @since 15 April 2001
 * @see org.springframework.beans.factory.support.DefaultListableBeanFactory
 * @see XmlBeanDefinitionReader
 * @deprecated as of Spring 3.1 in favor of {@link DefaultListableBeanFactory} and  {@link XmlBeanDefinitionReader}
 *
 * 对 DefaultListableBeanFactory 进行扩展，主要使用自定义读取器 XmlBeanDefinitionReader 从配置文件中读取 BeanDefinition
 *
 * spring使用 XmlBeanDefinitionReader 来读取并解析 xml 文件，XmlBeanDefinitionReader 是 BeanDefinitionReader 接口的实现。
 * BeanDefinitionReader 定义了 spring 读取 bean 定义的一个接口，这个接口中有一些loadBeanDefinitions 方法，
 * 从它们的方法签名可知，spring 把读取 bean 配置的来源抽象为 Resource 接口。
 * BeanDefinitionReader 接口有两个具体的实现，其中之一就是从 xml 文件中读取配置的XmlBeanDefinitionReader，
 * 另一个则是从 java properties 文件中读取配置的PropertiesBeanDefinitionReader。
 * 开发人员也可以提供自己的 BeanDefinitionReader 实现，根据自己的需要来读取 spring bean 定义的配置。
 * 在 XmlBeanFactory 中创建了 XmlBeanDefinitionReader 的实例，并在 XmlBeanFactory 的构造方法中调用了XmlBeanDefinitionReader 的 loadBeanDefinitions 方法，
 * 由 loadBeanDefinitions 方法负责加载 bean 配置并把 bean 配置注册到 XmlBeanFactory 中。
 */
@Deprecated
@SuppressWarnings({"serial", "all"})
public class XmlBeanFactory extends DefaultListableBeanFactory {

	// 实例化XmlBeanDefinitionReader对象
	private final XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(this);

	/**
	 * Create a new XmlBeanFactory with the given resource,
	 * which must be parsable using DOM.
	 * @param resource the XML resource to load bean definitions from
	 * @throws BeansException in case of loading or parsing errors
	 * 通过指定Resource对象创建XmlBeanFactory实例
	 */
	public XmlBeanFactory(Resource resource) throws BeansException {
		this(resource, null);
	}

	/**
	 * Create a new XmlBeanFactory with the given input stream,
	 * which must be parsable using DOM.
	 * @param resource the XML resource to load bean definitions from
	 * @param parentBeanFactory parent bean factory
	 * @throws BeansException in case of loading or parsing errors
	 * 通过指定Resource对象和父BeanFactory创建XmlBeanFactory实例
	 */
	public XmlBeanFactory(Resource resource, BeanFactory parentBeanFactory) throws BeansException {
		// 依次向上实例化父类构造器
		super(parentBeanFactory);
		// 解析xml配置文件,将其转换为IoC容器的内部表示
		this.reader.loadBeanDefinitions(resource);
	}

}
