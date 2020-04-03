

package org.springframework.jms.listener.endpoint;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.jms.support.QosSettings;

import static org.junit.Assert.*;


public class JmsMessageEndpointManagerTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();

	@Test
	public void isPubSubDomainWithQueue() {
		JmsMessageEndpointManager endpoint = new JmsMessageEndpointManager();
		JmsActivationSpecConfig config = new JmsActivationSpecConfig();
		config.setPubSubDomain(false);
		endpoint.setActivationSpecConfig(config);
		assertEquals(false, endpoint.isPubSubDomain());
		assertEquals(false, endpoint.isReplyPubSubDomain());
	}

	@Test
	public void isPubSubDomainWithTopic() {
		JmsMessageEndpointManager endpoint = new JmsMessageEndpointManager();
		JmsActivationSpecConfig config = new JmsActivationSpecConfig();
		config.setPubSubDomain(true);
		endpoint.setActivationSpecConfig(config);
		assertEquals(true, endpoint.isPubSubDomain());
		assertEquals(true, endpoint.isReplyPubSubDomain());
	}

	@Test
	public void pubSubDomainCustomForReply() {
		JmsMessageEndpointManager endpoint = new JmsMessageEndpointManager();
		JmsActivationSpecConfig config = new JmsActivationSpecConfig();
		config.setPubSubDomain(true);
		config.setReplyPubSubDomain(false);
		endpoint.setActivationSpecConfig(config);
		assertEquals(true, endpoint.isPubSubDomain());
		assertEquals(false, endpoint.isReplyPubSubDomain());
	}

	@Test
	public void customReplyQosSettings() {
		JmsMessageEndpointManager endpoint = new JmsMessageEndpointManager();
		JmsActivationSpecConfig config = new JmsActivationSpecConfig();
		QosSettings settings = new QosSettings(1, 3, 5);
		config.setReplyQosSettings(settings);
		endpoint.setActivationSpecConfig(config);
		assertNotNull(endpoint.getReplyQosSettings());
		assertEquals(1, endpoint.getReplyQosSettings().getDeliveryMode());
		assertEquals(3, endpoint.getReplyQosSettings().getPriority());
		assertEquals(5, endpoint.getReplyQosSettings().getTimeToLive());
	}

	@Test
	public void isPubSubDomainWithNoConfig() {
		JmsMessageEndpointManager endpoint = new JmsMessageEndpointManager();

		this.thrown.expect(IllegalStateException.class); // far from ideal
		endpoint.isPubSubDomain();
	}

	@Test
	public void isReplyPubSubDomainWithNoConfig() {
		JmsMessageEndpointManager endpoint = new JmsMessageEndpointManager();

		this.thrown.expect(IllegalStateException.class); // far from ideal
		endpoint.isReplyPubSubDomain();
	}

	@Test
	public void getReplyQosSettingsWithNoConfig() {
		JmsMessageEndpointManager endpoint = new JmsMessageEndpointManager();

		this.thrown.expect(IllegalStateException.class); // far from ideal
		endpoint.getReplyQosSettings();
	}

	@Test
	public void getMessageConverterNoConfig() {
		JmsMessageEndpointManager endpoint = new JmsMessageEndpointManager();
		assertNull(endpoint.getMessageConverter());
	}

	@Test
	public void getDestinationResolverNoConfig() {
		JmsMessageEndpointManager endpoint = new JmsMessageEndpointManager();
		assertNull(endpoint.getDestinationResolver());
	}
}
