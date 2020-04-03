

package org.springframework.jms.listener.adapter;

import javax.jms.BytesMessage;
import javax.jms.MapMessage;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

/**
 * See the MessageListenerAdapterTests class for usage.
 *
 * @author Rick Evans
 */
public interface MessageDelegate {

	void handleMessage(TextMessage message);

	void handleMessage(MapMessage message);

	void handleMessage(BytesMessage message);

	void handleMessage(ObjectMessage message);

}
