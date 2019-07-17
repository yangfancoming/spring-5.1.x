

package org.springframework.jmx.access;

import java.net.BindException;
import java.net.MalformedURLException;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import org.junit.After;

import org.springframework.tests.Assume;
import org.springframework.tests.TestGroup;
import org.springframework.util.SocketUtils;

/**
 * To run the tests in the class, set the following Java system property:
 * {@code -DtestGroups=jmxmp}.
 *
 * @author Rob Harrop

 * @author Sam Brannen
 */
public class RemoteMBeanClientInterceptorTests extends MBeanClientInterceptorTests {

	private static final int SERVICE_PORT;

	private static final String SERVICE_URL;

	static {
		SERVICE_PORT = SocketUtils.findAvailableTcpPort();
		SERVICE_URL = "service:jmx:jmxmp://localhost:" + SERVICE_PORT;
	}


	private JMXConnectorServer connectorServer;

	private JMXConnector connector;


	@Override
	public void onSetUp() throws Exception {
		runTests = false;
		Assume.group(TestGroup.JMXMP);
		runTests = true;
		super.onSetUp();
		this.connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(getServiceUrl(), null, getServer());
		try {
			this.connectorServer.start();
		}
		catch (BindException ex) {
			System.out.println("Skipping remote JMX tests because binding to local port ["
					+ SERVICE_PORT + "] failed: " + ex.getMessage());
			runTests = false;
		}
	}

	private JMXServiceURL getServiceUrl() throws MalformedURLException {
		return new JMXServiceURL(SERVICE_URL);
	}

	@Override
	protected MBeanServerConnection getServerConnection() throws Exception {
		this.connector = JMXConnectorFactory.connect(getServiceUrl());
		return this.connector.getMBeanServerConnection();
	}

	@After
	@Override
	public void tearDown() throws Exception {
		if (this.connector != null) {
			this.connector.close();
		}
		if (this.connectorServer != null) {
			this.connectorServer.stop();
		}
		if (runTests) {
			super.tearDown();
		}
	}

}
