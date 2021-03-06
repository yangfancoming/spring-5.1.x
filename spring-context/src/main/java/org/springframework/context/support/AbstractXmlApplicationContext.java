

package org.springframework.context.support;

import java.io.IOException;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

/**
 * Convenient base class for {@link org.springframework.context.ApplicationContext} implementations,
 * drawing configuration from XML documents containing bean definitions  understood by an {@link org.springframework.beans.factory.xml.XmlBeanDefinitionReader}.
 * Subclasses just have to implement the {@link #getConfigResources} and/or the {@link #getConfigLocations} method.
 * Furthermore, they might override the {@link #getResourceByPath} hook to interpret relative paths in an
 * environment-specific fashion, and/or {@link #getResourcePatternResolver} for extended pattern resolution.
 * @see #getConfigResources
 * @see #getConfigLocations
 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
 */
public abstract class AbstractXmlApplicationContext extends AbstractRefreshableConfigApplicationContext {

	private boolean validating = true;

	//  Create a new AbstractXmlApplicationContext with no parent.
	public AbstractXmlApplicationContext() {
		logger.warn("进入 【AbstractXmlApplicationContext】 构造函数 {}");
	}

	/**
	 * Create a new AbstractXmlApplicationContext with the given parent context.
	 * @param parent the parent context
	 */
	public AbstractXmlApplicationContext(@Nullable ApplicationContext parent) {
		super(parent);
		logger.warn("进入 【AbstractXmlApplicationContext】 构造函数 {}");
	}

	// Set whether to use XML validation. Default is {@code true}.
	public void setValidating(boolean validating) {
		this.validating = validating;
	}

	/**
	 * Loads the bean definitions via an XmlBeanDefinitionReader.
	 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader
	 * @see #initBeanDefinitionReader
	 * @see #loadBeanDefinitions
	 */
	@Override
	protected void loadBeanDefinitions(DefaultListableBeanFactory beanFactory) throws BeansException, IOException {
		// Create a new XmlBeanDefinitionReader for the given BeanFactory. // 为当前工厂创建一个BeanDefinition读取器！ 委托模式
		XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(beanFactory);
		// Configure the bean definition reader with this context's resource loading environment.
		// 根据Contextn的资源对该读取器进行配置，得告诉他去哪读啊，怎么读啊！
		beanDefinitionReader.setEnvironment(getEnvironment());
		beanDefinitionReader.setResourceLoader(this);
		beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(this));
		// Allow a subclass to provide custom initialization of the reader, then proceed with actually loading the bean definitions.
		// 这里可以设置一个定制化的Reader
		// 初始化 BeanDefinitionReader，其实这个是提供给子类覆写的，我看了一下，没有类覆写这个方法，我们姑且当做不重要吧
		initBeanDefinitionReader(beanDefinitionReader);
		// 重点来了，继续往下 //此处调用了重载的方法，把已经装饰好的能用的BeanDefinitionReader读取器当参数传了进去
		loadBeanDefinitions(beanDefinitionReader);
	}

	/**
	 * Initialize the bean definition reader used for loading the bean definitions of this context. Default implementation is empty.
	 * Can be overridden in subclasses, e.g. for turning off XML validation  or using a different XmlBeanDefinitionParser implementation.
	 * @param reader the bean definition reader used by this context
	 * @see org.springframework.beans.factory.xml.XmlBeanDefinitionReader#setDocumentReaderClass
	 */
	protected void initBeanDefinitionReader(XmlBeanDefinitionReader reader) {
		reader.setValidating(this.validating);
	}

	/**
	 * Load the bean definitions with the given XmlBeanDefinitionReader.
	 * The lifecycle of the bean factory is handled by the {@link #refreshBeanFactory} method; hence this method is just supposed to load and/or register bean definitions.
	 * @param reader the XmlBeanDefinitionReader to use
	 * @throws BeansException in case of bean registration errors
	 * @throws IOException if the required XML document isn't found
	 * @see #refreshBeanFactory
	 * @see #getConfigLocations
	 * @see #getResources
	 * @see #getResourcePatternResolver
	 */
	protected void loadBeanDefinitions(XmlBeanDefinitionReader reader) throws BeansException, IOException {
		// 这里的reader已经可以正常干活儿了，首先获取bean定义资源（多数情况下为null）
		Resource[] configResources = getConfigResources();
		if (configResources != null) {
			reader.loadBeanDefinitions(configResources);
		}
		// 获取之前已经读取好的配置文件路径。 setConfigLocations(configLocations); 显然这个configRuations我们之前给传了一个路径"bean.xml"，所以执行这个重载方法
		String[] configLocations = getConfigLocations(); // classpath:CNamespaceReferenceTest-context.xml
		if (configLocations != null) {
			reader.loadBeanDefinitions(configLocations);
		}
	}

	/**
	 * Return an array of Resource objects, referring to the XML bean definition files that this context should be built with.
	 * The default implementation returns {@code null}. Subclasses can override  this to provide pre-built Resource objects rather than location Strings.
	 * @return an array of Resource objects, or {@code null} if none
	 * @see #getConfigLocations()
	 */
	@Nullable
	protected Resource[] getConfigResources() {
		return null;
	}
}
