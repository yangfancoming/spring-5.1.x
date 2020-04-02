

package org.springframework.jmx;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;

import org.junit.After;
import org.junit.Before;

import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.jmx.export.MBeanExporter;
import org.springframework.tests.TestGroup;
import org.springframework.util.MBeanTestUtils;

import static org.junit.Assert.*;

/**
 * <strong>Note:</strong> certain tests throughout this hierarchy require the presence of
 * the {@code jmxremote_optional.jar} in your classpath. For this reason, these tests are
 * run only if {@link TestGroup#JMXMP} is enabled.
 *
 * If you wish to run these tests, follow the instructions in the TestGroup class to
 * enable JMXMP tests (i.e., set the following Java system property:
 * {@code -DtestGroups=jmxmp}).
 *
 * If you run into the <em>"Unsupported protocol: jmxmp"</em> error, you will need to
 * download the <a href="https://www.oracle.com/technetwork/java/javase/tech/download-jsp-141676.html">JMX
 * Remote API 1.0.1_04 Reference Implementation</a> from Oracle and extract
 * {@code jmxremote_optional.jar} into your classpath, for example in the {@code lib/ext}
 * folder of your JVM.
 *
 * See also:
 * <ul>
 * <li>
 * <li><a href="https://jira.spring.io/browse/SPR-8093">SPR-8093</a></li>
 * <li><a href="https://issuetracker.springsource.com/browse/EBR-349">EBR-349</a></li>
 * </ul>
 *
 * @author Rob Harrop

 * @author Sam Brannen

 * @author Stephane Nicoll
 */
public abstract class AbstractMBeanServerTests {

	protected MBeanServer server;


	@Before
	public final void setUp() throws Exception {
		this.server = MBeanServerFactory.createMBeanServer();
		try {
			onSetUp();
		}
		catch (Exception ex) {
			releaseServer();
			throw ex;
		}
	}

	protected ConfigurableApplicationContext loadContext(String configLocation) {
		GenericApplicationContext ctx = new GenericApplicationContext();
		new XmlBeanDefinitionReader(ctx).loadBeanDefinitions(configLocation);
		ctx.getDefaultListableBeanFactory().registerSingleton("server", this.server);
		ctx.refresh();
		return ctx;
	}

	@After
	public void tearDown() throws Exception {
		releaseServer();
		onTearDown();
	}

	private void releaseServer() throws Exception {
		MBeanServerFactory.releaseMBeanServer(getServer());
		MBeanTestUtils.resetMBeanServers();
	}

	protected void onTearDown() throws Exception {
	}

	protected void onSetUp() throws Exception {
	}

	public MBeanServer getServer() {
		return this.server;
	}

	/**
	 * Start the specified {@link MBeanExporter}.
	 */
	protected void start(MBeanExporter exporter) {
		exporter.afterPropertiesSet();
		exporter.afterSingletonsInstantiated();
	}

	protected void assertIsRegistered(String message, ObjectName objectName) {
		assertTrue(message, getServer().isRegistered(objectName));
	}

	protected void assertIsNotRegistered(String message, ObjectName objectName) {
		assertFalse(message, getServer().isRegistered(objectName));
	}

}
