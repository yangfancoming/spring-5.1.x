

package org.springframework.test.context.junit4;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.util.ResourceUtils;

/**
 * Extension of {@link SpringJUnit4ClassRunnerAppCtxTests}, which verifies that
 * we can specify multiple resource locations for our application context, each
 * configured differently.
 *
 * As of Spring 3.0,
 * {@code MultipleResourcesSpringJUnit4ClassRunnerAppCtxTests} is also used
 * to verify support for the new {@code value} attribute alias for
 * {@code @ContextConfiguration}'s {@code locations} attribute.
 * </p>
 *
 * @author Sam Brannen
 * @since 2.5
 * @see SpringJUnit4ClassRunnerAppCtxTests
 */
@ContextConfiguration( { MultipleResourcesSpringJUnit4ClassRunnerAppCtxTests.CLASSPATH_RESOURCE_PATH,
	MultipleResourcesSpringJUnit4ClassRunnerAppCtxTests.LOCAL_RESOURCE_PATH,
	MultipleResourcesSpringJUnit4ClassRunnerAppCtxTests.ABSOLUTE_RESOURCE_PATH })
public class MultipleResourcesSpringJUnit4ClassRunnerAppCtxTests extends SpringJUnit4ClassRunnerAppCtxTests {

	public static final String CLASSPATH_RESOURCE_PATH = ResourceUtils.CLASSPATH_URL_PREFIX
			+ "/org/springframework/test/context/junit4/MultipleResourcesSpringJUnit4ClassRunnerAppCtxTests-context1.xml";
	public static final String LOCAL_RESOURCE_PATH = "MultipleResourcesSpringJUnit4ClassRunnerAppCtxTests-context2.xml";
	public static final String ABSOLUTE_RESOURCE_PATH = "/org/springframework/test/context/junit4/MultipleResourcesSpringJUnit4ClassRunnerAppCtxTests-context3.xml";

	/* all tests are in the parent class. */
}
