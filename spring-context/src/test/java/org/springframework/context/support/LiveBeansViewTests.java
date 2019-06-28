

package org.springframework.context.support;

import java.lang.management.ManagementFactory;
import java.util.Set;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.mock.env.MockEnvironment;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

/**
 * Tests for {@link LiveBeansView}
 *
 * @author Stephane Nicoll
 */
public class LiveBeansViewTests {

	@Rule
	public TestName name = new TestName();

	private final MockEnvironment environment = new MockEnvironment();

	@Test
	public void registerIgnoredIfPropertyIsNotSet() throws MalformedObjectNameException {
		ConfigurableApplicationContext context = createApplicationContext("app");
		assertEquals(0, searchLiveBeansViewMeans().size());
		LiveBeansView.registerApplicationContext(context);
		assertEquals(0, searchLiveBeansViewMeans().size());
		LiveBeansView.unregisterApplicationContext(context);
	}

	@Test
	public void registerUnregisterSingleContext() throws MalformedObjectNameException {
		this.environment.setProperty(LiveBeansView.MBEAN_DOMAIN_PROPERTY_NAME, this.name.getMethodName());
		ConfigurableApplicationContext context = createApplicationContext("app");
		assertEquals(0, searchLiveBeansViewMeans().size());
		LiveBeansView.registerApplicationContext(context);
		assertSingleLiveBeansViewMbean("app");
		LiveBeansView.unregisterApplicationContext(context);
		assertEquals(0, searchLiveBeansViewMeans().size());
	}

	@Test
	public void registerUnregisterSeveralContexts() throws MalformedObjectNameException {
		this.environment.setProperty(LiveBeansView.MBEAN_DOMAIN_PROPERTY_NAME, this.name.getMethodName());
		ConfigurableApplicationContext context = createApplicationContext("app");
		ConfigurableApplicationContext childContext = createApplicationContext("child");
		assertEquals(0, searchLiveBeansViewMeans().size());
		LiveBeansView.registerApplicationContext(context);
		assertSingleLiveBeansViewMbean("app");
		LiveBeansView.registerApplicationContext(childContext);
		assertEquals(1, searchLiveBeansViewMeans().size()); // Only one MBean
		LiveBeansView.unregisterApplicationContext(childContext);
		assertSingleLiveBeansViewMbean("app"); // Root context removes it
		LiveBeansView.unregisterApplicationContext(context);
		assertEquals(0, searchLiveBeansViewMeans().size());
	}

	@Test
	public void registerUnregisterSeveralContextsDifferentOrder() throws MalformedObjectNameException {
		this.environment.setProperty(LiveBeansView.MBEAN_DOMAIN_PROPERTY_NAME, this.name.getMethodName());
		ConfigurableApplicationContext context = createApplicationContext("app");
		ConfigurableApplicationContext childContext = createApplicationContext("child");
		assertEquals(0, searchLiveBeansViewMeans().size());
		LiveBeansView.registerApplicationContext(context);
		assertSingleLiveBeansViewMbean("app");
		LiveBeansView.registerApplicationContext(childContext);
		assertSingleLiveBeansViewMbean("app"); // Only one MBean
		LiveBeansView.unregisterApplicationContext(context);
		LiveBeansView.unregisterApplicationContext(childContext);
		assertEquals(0, searchLiveBeansViewMeans().size());
	}

	private ConfigurableApplicationContext createApplicationContext(String applicationName) {
		ConfigurableApplicationContext context = mock(ConfigurableApplicationContext.class);
		given(context.getEnvironment()).willReturn(this.environment);
		given(context.getApplicationName()).willReturn(applicationName);
		return context;
	}

	public void assertSingleLiveBeansViewMbean(String applicationName) throws MalformedObjectNameException {
		Set<ObjectName> objectNames = searchLiveBeansViewMeans();
		assertEquals(1, objectNames.size());
		assertEquals("Wrong MBean name",
				String.format("%s:application=%s", this.name.getMethodName(), applicationName),
				objectNames.iterator().next().getCanonicalName());

	}

	private Set<ObjectName> searchLiveBeansViewMeans()
			throws MalformedObjectNameException {
		String objectName = String.format("%s:*,%s=*", this.name.getMethodName(),
				LiveBeansView.MBEAN_APPLICATION_KEY);
		return ManagementFactory.getPlatformMBeanServer().queryNames(new ObjectName(objectName), null);
	}

}
