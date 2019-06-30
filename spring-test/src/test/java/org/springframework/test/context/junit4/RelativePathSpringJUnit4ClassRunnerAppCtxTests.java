

package org.springframework.test.context.junit4;

import org.springframework.test.context.ContextConfiguration;

/**
 * Extension of {@link SpringJUnit4ClassRunnerAppCtxTests}, which verifies that
 * we can specify an explicit, <em>relative path</em> location for our
 * application context.
 *
 * @author Sam Brannen
 * @since 2.5
 * @see SpringJUnit4ClassRunnerAppCtxTests
 * @see AbsolutePathSpringJUnit4ClassRunnerAppCtxTests
 */
@ContextConfiguration(locations = { "SpringJUnit4ClassRunnerAppCtxTests-context.xml" })
public class RelativePathSpringJUnit4ClassRunnerAppCtxTests extends SpringJUnit4ClassRunnerAppCtxTests {
	/* all tests are in the parent class. */
}
