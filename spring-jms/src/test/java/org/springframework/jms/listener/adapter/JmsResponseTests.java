

package org.springframework.jms.listener.adapter;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Session;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.jms.support.destination.DestinationResolver;

import static org.junit.Assert.assertSame;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;


public class JmsResponseTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Test
	public void destinationDoesNotUseDestinationResolver() throws JMSException {
		Destination destination = mock(Destination.class);
		Destination actual = JmsResponse.forDestination("foo", destination).resolveDestination(null, null);
		assertSame(destination, actual);
	}

	@Test
	public void resolveDestinationForQueue() throws JMSException {
		Session session = mock(Session.class);
		DestinationResolver destinationResolver = mock(DestinationResolver.class);
		Destination destination = mock(Destination.class);

		given(destinationResolver.resolveDestinationName(session, "myQueue", false)).willReturn(destination);
		JmsResponse<String> jmsResponse = JmsResponse.forQueue("foo", "myQueue");
		Destination actual = jmsResponse.resolveDestination(destinationResolver, session);
		assertSame(destination, actual);
	}

	@Test
	public void createWithNulResponse() {
		thrown.expect(IllegalArgumentException.class);
		JmsResponse.forQueue(null, "myQueue");
	}

	@Test
	public void createWithNullQueueName() {
		thrown.expect(IllegalArgumentException.class);
		JmsResponse.forQueue("foo", null);
	}

	@Test
	public void createWithNullTopicName() {
		thrown.expect(IllegalArgumentException.class);
		JmsResponse.forTopic("foo", null);
	}

	@Test
	public void createWithNulDestination() {
		thrown.expect(IllegalArgumentException.class);
		JmsResponse.forDestination("foo", null);
	}

}
