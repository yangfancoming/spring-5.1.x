

package org.springframework.jms.config;

import javax.jms.MessageListener;

import org.junit.Test;

import org.springframework.jms.listener.SimpleMessageListenerContainer;
import org.springframework.jms.listener.adapter.MessageListenerAdapter;

import static org.junit.Assert.*;


public class SimpleJmsListenerEndpointTests {

	private final SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();


	@Test
	public void createListener() {
		SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
		MessageListener messageListener = new MessageListenerAdapter();
		endpoint.setMessageListener(messageListener);
		assertSame(messageListener, endpoint.createMessageListener(container));
	}

}
