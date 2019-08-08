

package org.springframework.aop.aspectj.autoproxy;

import org.junit.Before;
import org.junit.Test;

import org.springframework.aop.config.AopConfigUtils;
import org.springframework.aop.config.AopNamespaceUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.parsing.PassThroughSourceExtractor;
import org.springframework.beans.factory.parsing.SourceExtractor;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.beans.factory.xml.XmlReaderContext;
import org.springframework.tests.beans.CollectingReaderEventListener;

import static org.junit.Assert.*;


public class AspectJNamespaceHandlerTests {

	private ParserContext parserContext;

	private CollectingReaderEventListener readerEventListener = new CollectingReaderEventListener();

	private BeanDefinitionRegistry registry = new DefaultListableBeanFactory();


	@Before
	public void setUp() throws Exception {
		SourceExtractor sourceExtractor = new PassThroughSourceExtractor();
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(this.registry);
		XmlReaderContext readerContext = new XmlReaderContext(null, null, this.readerEventListener, sourceExtractor, reader, null);
		this.parserContext = new ParserContext(readerContext, null);
	}

	@Test
	public void testRegisterAutoProxyCreator(){
		AopNamespaceUtils.registerAutoProxyCreatorIfNecessary(this.parserContext, null);
		assertEquals("Incorrect number of definitions registered", 1, registry.getBeanDefinitionCount());

		AopNamespaceUtils.registerAspectJAutoProxyCreatorIfNecessary(this.parserContext, null);
		assertEquals("Incorrect number of definitions registered", 1, registry.getBeanDefinitionCount());
	}

	@Test
	public void testRegisterAspectJAutoProxyCreator(){
		AopNamespaceUtils.registerAspectJAutoProxyCreatorIfNecessary(this.parserContext, null);
		assertEquals("Incorrect number of definitions registered", 1, registry.getBeanDefinitionCount());

		AopNamespaceUtils.registerAspectJAutoProxyCreatorIfNecessary(this.parserContext, null);
		assertEquals("Incorrect number of definitions registered", 1, registry.getBeanDefinitionCount());

		BeanDefinition definition = registry.getBeanDefinition(AopConfigUtils.AUTO_PROXY_CREATOR_BEAN_NAME);
		assertEquals("Incorrect APC class",
				AspectJAwareAdvisorAutoProxyCreator.class.getName(), definition.getBeanClassName());
	}

	@Test
	public void testRegisterAspectJAutoProxyCreatorWithExistingAutoProxyCreator(){
		AopNamespaceUtils.registerAutoProxyCreatorIfNecessary(this.parserContext, null);
		assertEquals(1, registry.getBeanDefinitionCount());

		AopNamespaceUtils.registerAspectJAutoProxyCreatorIfNecessary(this.parserContext, null);
		assertEquals("Incorrect definition count", 1, registry.getBeanDefinitionCount());

		BeanDefinition definition = registry.getBeanDefinition(AopConfigUtils.AUTO_PROXY_CREATOR_BEAN_NAME);
		assertEquals("APC class not switched",
				AspectJAwareAdvisorAutoProxyCreator.class.getName(), definition.getBeanClassName());
	}

	@Test
	public void testRegisterAutoProxyCreatorWhenAspectJAutoProxyCreatorAlreadyExists(){
		AopNamespaceUtils.registerAspectJAutoProxyCreatorIfNecessary(this.parserContext, null);
		assertEquals(1, registry.getBeanDefinitionCount());

		AopNamespaceUtils.registerAutoProxyCreatorIfNecessary(this.parserContext, null);
		assertEquals("Incorrect definition count", 1, registry.getBeanDefinitionCount());

		BeanDefinition definition = registry.getBeanDefinition(AopConfigUtils.AUTO_PROXY_CREATOR_BEAN_NAME);
		assertEquals("Incorrect APC class",
				AspectJAwareAdvisorAutoProxyCreator.class.getName(), definition.getBeanClassName());
	}

}
