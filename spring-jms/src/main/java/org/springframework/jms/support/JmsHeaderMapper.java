

package org.springframework.jms.support;

import javax.jms.Message;

import org.springframework.messaging.support.HeaderMapper;

/**
 * Strategy interface for mapping {@link org.springframework.messaging.Message}
 * headers to an outbound JMS {@link javax.jms.Message} (e.g. to configure JMS
 * properties) or extracting messaging header values from an inbound JMS Message.
 *
 * @author Mark Fisher
 * @author Oleg Zhurakousky
 * @author Gary Russell
 * @since 4.1
 */
public interface JmsHeaderMapper extends HeaderMapper<Message> {

	/**
	 * The JMS-compliant {@code content_type} property.
	 */
	String CONTENT_TYPE_PROPERTY = "content_type";

}

