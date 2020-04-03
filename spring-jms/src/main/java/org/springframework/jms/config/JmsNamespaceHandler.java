

package org.springframework.jms.config;

import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * A {@link org.springframework.beans.factory.xml.NamespaceHandler}
 * for the JMS namespace.
 *
 * @author Mark Fisher

 * @author Stephane Nicoll
 * @since 2.5
 */
public class JmsNamespaceHandler extends NamespaceHandlerSupport {

	@Override
	public void init() {
		registerBeanDefinitionParser("listener-container", new JmsListenerContainerParser());
		registerBeanDefinitionParser("jca-listener-container", new JcaListenerContainerParser());
		registerBeanDefinitionParser("annotation-driven", new AnnotationDrivenJmsBeanDefinitionParser());
	}

}
