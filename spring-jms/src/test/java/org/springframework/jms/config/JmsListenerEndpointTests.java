

package org.springframework.jms.config;

import javax.jms.MessageListener;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.beans.DirectFieldAccessor;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.jms.listener.MessageListenerContainer;
import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.springframework.jms.listener.endpoint.JmsActivationSpecConfig;
import org.springframework.jms.listener.endpoint.JmsMessageEndpointManager;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;


public class JmsListenerEndpointTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();


	@Test
	public void setupJmsMessageContainerFullConfig() {
		DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
		MessageListener messageListener = new MessageListenerAdapter();
		SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
		endpoint.setDestination("myQueue");
		endpoint.setSelector("foo = 'bar'");
		endpoint.setSubscription("mySubscription");
		endpoint.setConcurrency("5-10");
		endpoint.setMessageListener(messageListener);

		endpoint.setupListenerContainer(container);
		assertEquals("myQueue", container.getDestinationName());
		assertEquals("foo = 'bar'", container.getMessageSelector());
		assertEquals("mySubscription", container.getSubscriptionName());
		assertEquals(5, container.getConcurrentConsumers());
		assertEquals(10, container.getMaxConcurrentConsumers());
		assertEquals(messageListener, container.getMessageListener());
	}

	@Test
	public void setupJcaMessageContainerFullConfig() {
		JmsMessageEndpointManager container = new JmsMessageEndpointManager();
		MessageListener messageListener = new MessageListenerAdapter();
		SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
		endpoint.setDestination("myQueue");
		endpoint.setSelector("foo = 'bar'");
		endpoint.setSubscription("mySubscription");
		endpoint.setConcurrency("10");
		endpoint.setMessageListener(messageListener);

		endpoint.setupListenerContainer(container);
		JmsActivationSpecConfig config = container.getActivationSpecConfig();
		assertEquals("myQueue", config.getDestinationName());
		assertEquals("foo = 'bar'", config.getMessageSelector());
		assertEquals("mySubscription", config.getSubscriptionName());
		assertEquals(10, config.getMaxConcurrency());
		assertEquals(messageListener, container.getMessageListener());
	}

	@Test
	public void setupConcurrencySimpleContainer() {
		SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
		MessageListener messageListener = new MessageListenerAdapter();
		SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
		endpoint.setConcurrency("5-10"); // simple implementation only support max value
		endpoint.setMessageListener(messageListener);

		endpoint.setupListenerContainer(container);
		assertEquals(10, new DirectFieldAccessor(container).getPropertyValue("concurrentConsumers"));
	}

	@Test
	public void setupMessageContainerNoListener() {
		DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
		SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();

		thrown.expect(IllegalStateException.class);
		endpoint.setupListenerContainer(container);
	}

	@Test
	public void setupMessageContainerUnsupportedContainer() {
		MessageListenerContainer container = mock(MessageListenerContainer.class);
		SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
		endpoint.setMessageListener(new MessageListenerAdapter());

		thrown.expect(IllegalArgumentException.class);
		endpoint.setupListenerContainer(container);
	}

}
