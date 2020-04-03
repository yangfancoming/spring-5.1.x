

package org.springframework.jms.connection;

import javax.jms.Connection;
import javax.jms.ConnectionConsumer;
import javax.jms.ConnectionMetaData;
import javax.jms.Destination;
import javax.jms.ExceptionListener;
import javax.jms.JMSException;
import javax.jms.ServerSessionPool;
import javax.jms.Session;
import javax.jms.Topic;


public class TestConnection implements Connection {

	private ExceptionListener exceptionListener;

	private int startCount;

	private int closeCount;


	@Override
	public Session createSession(boolean b, int i) throws JMSException {
		return null;
	}

	@Override
	public Session createSession(int sessionMode) throws JMSException {
		return null;
	}

	@Override
	public Session createSession() throws JMSException {
		return null;
	}

	@Override
	public String getClientID() throws JMSException {
		return null;
	}

	@Override
	public void setClientID(String paramName) throws JMSException {
	}

	@Override
	public ConnectionMetaData getMetaData() throws JMSException {
		return null;
	}

	@Override
	public ExceptionListener getExceptionListener() throws JMSException {
		return exceptionListener;
	}

	@Override
	public void setExceptionListener(ExceptionListener exceptionListener) throws JMSException {
		this.exceptionListener = exceptionListener;
	}

	@Override
	public void start() throws JMSException {
		this.startCount++;
	}

	@Override
	public void stop() throws JMSException {
	}

	@Override
	public void close() throws JMSException {
		this.closeCount++;
	}

	@Override
	public ConnectionConsumer createConnectionConsumer(Destination destination, String paramName, ServerSessionPool serverSessionPool, int i) throws JMSException {
		return null;
	}

	@Override
	public ConnectionConsumer createDurableConnectionConsumer(Topic topic, String paramName, String paramName1, ServerSessionPool serverSessionPool, int i) throws JMSException {
		return null;
	}

	@Override
	public ConnectionConsumer createSharedConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
		return null;
	}

	@Override
	public ConnectionConsumer createSharedDurableConnectionConsumer(Topic topic, String subscriptionName, String messageSelector, ServerSessionPool sessionPool, int maxMessages) throws JMSException {
		return null;
	}


	public int getStartCount() {
		return startCount;
	}

	public int getCloseCount() {
		return closeCount;
	}

}
