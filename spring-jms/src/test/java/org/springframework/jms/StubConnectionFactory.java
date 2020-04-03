

package org.springframework.jms;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSContext;
import javax.jms.JMSException;

/**
 * A stub implementation of the JMS ConnectionFactory for testing.
 *
 * @author Mark Fisher
 */
public class StubConnectionFactory implements ConnectionFactory {

	@Override
	public Connection createConnection() throws JMSException {
		return null;
	}

	@Override
	public Connection createConnection(String username, String password) throws JMSException {
		return null;
	}

	@Override
	public JMSContext createContext() {
		return null;
	}

	@Override
	public JMSContext createContext(String userName, String password) {
		return null;
	}

	@Override
	public JMSContext createContext(String userName, String password, int sessionMode) {
		return null;
	}

	@Override
	public JMSContext createContext(int sessionMode) {
		return null;
	}

}
