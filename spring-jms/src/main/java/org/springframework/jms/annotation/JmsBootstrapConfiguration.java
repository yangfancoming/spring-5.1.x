

package org.springframework.jms.annotation;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Role;
import org.springframework.jms.config.JmsListenerConfigUtils;
import org.springframework.jms.config.JmsListenerEndpointRegistry;

/**
 * {@code @Configuration} class that registers a {@link JmsListenerAnnotationBeanPostProcessor}
 * bean capable of processing Spring's @{@link JmsListener} annotation. Also register
 * a default {@link JmsListenerEndpointRegistry}.
 *
 * xmlBeanDefinitionReaderThis configuration class is automatically imported when using the @{@link EnableJms}
 * annotation. See the {@link EnableJms} javadocs for complete usage details.
 *
 * @author Stephane Nicoll
 * @since 4.1
 * @see JmsListenerAnnotationBeanPostProcessor
 * @see JmsListenerEndpointRegistry
 * @see EnableJms
 */
@Configuration
@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
public class JmsBootstrapConfiguration {

	@Bean(name = JmsListenerConfigUtils.JMS_LISTENER_ANNOTATION_PROCESSOR_BEAN_NAME)
	@Role(BeanDefinition.ROLE_INFRASTRUCTURE)
	public JmsListenerAnnotationBeanPostProcessor jmsListenerAnnotationProcessor() {
		return new JmsListenerAnnotationBeanPostProcessor();
	}

	@Bean(name = JmsListenerConfigUtils.JMS_LISTENER_ENDPOINT_REGISTRY_BEAN_NAME)
	public JmsListenerEndpointRegistry defaultJmsListenerEndpointRegistry() {
		return new JmsListenerEndpointRegistry();
	}

}
