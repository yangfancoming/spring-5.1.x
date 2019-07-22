

package org.springframework.transaction;

import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.parsing.BeanComponentDefinition;
import org.springframework.beans.factory.parsing.ComponentDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.tests.beans.CollectingReaderEventListener;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * @author Torsten Juergeleit

 */
public class TxNamespaceHandlerEventTests {

	private DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

	private CollectingReaderEventListener eventListener = new CollectingReaderEventListener();


	@Before
	public void setUp() throws Exception {
		XmlBeanDefinitionReader reader = new XmlBeanDefinitionReader(this.beanFactory);
		reader.setEventListener(this.eventListener);
		reader.loadBeanDefinitions(new ClassPathResource("txNamespaceHandlerTests.xml", getClass()));
	}

	@Test
	public void componentEventReceived() {
		ComponentDefinition component = this.eventListener.getComponentDefinition("txAdvice");
		assertThat(component, instanceOf(BeanComponentDefinition.class));
	}

}
