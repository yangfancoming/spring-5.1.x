

package org.springframework.test.context.junit4;

import java.lang.annotation.Inherited;

import org.springframework.test.context.ContextConfiguration;

/**
 * Extension of {@link SpringJUnit4ClassRunnerAppCtxTests} which verifies that
 * the configuration of an application context and dependency injection of a
 * test instance function as expected within a class hierarchy, since
 * {@link ContextConfiguration configuration} is {@link Inherited inherited}.
 *
 * @author Sam Brannen
 * @since 2.5
 * @see SpringJUnit4ClassRunnerAppCtxTests
 */
public class InheritedConfigSpringJUnit4ClassRunnerAppCtxTests extends SpringJUnit4ClassRunnerAppCtxTests {
	/* all tests are in the parent class. */
}
