
package org.springframework.jms.core.support;

import java.util.ArrayList;
import java.util.List;
import javax.jms.ConnectionFactory;

import org.junit.Test;

import org.springframework.jms.core.JmsTemplate;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

/**
 * @author Mark Pollack
 * @since 24.9.2004
 */
public class JmsGatewaySupportTests {

	@Test
	public void testJmsGatewaySupportWithConnectionFactory() throws Exception {
		ConnectionFactory mockConnectionFactory = mock(ConnectionFactory.class);
		final List<String> test = new ArrayList<>(1);
		JmsGatewaySupport gateway = new JmsGatewaySupport() {
			@Override
			protected void initGateway() {
				test.add("test");
			}
		};
		gateway.setConnectionFactory(mockConnectionFactory);
		gateway.afterPropertiesSet();
		assertEquals("Correct ConnectionFactory", mockConnectionFactory, gateway.getConnectionFactory());
		assertEquals("Correct JmsTemplate", mockConnectionFactory, gateway.getJmsTemplate().getConnectionFactory());
		assertEquals("initGateway called", 1, test.size());
	}

	@Test
	public void testJmsGatewaySupportWithJmsTemplate() throws Exception {
		JmsTemplate template = new JmsTemplate();
		final List<String> test = new ArrayList<>(1);
		JmsGatewaySupport gateway = new JmsGatewaySupport() {
			@Override
			protected void initGateway() {
				test.add("test");
			}
		};
		gateway.setJmsTemplate(template);
		gateway.afterPropertiesSet();
		assertEquals("Correct JmsTemplate", template, gateway.getJmsTemplate());
		assertEquals("initGateway called", 1, test.size());
	}

}
