

package org.springframework.test.context.junit4;

import org.junit.runner.RunWith;

import org.springframework.test.context.TestExecutionListeners;

/**
 * <p>
 * Simple unit test to verify that {@link SpringRunner} does not
 * hinder correct functionality of standard JUnit 4.4+ testing features.
 * </p>
 * <p>
 * Note that {@link TestExecutionListeners @TestExecutionListeners} is
 * explicitly configured with an empty list, thus disabling all default
 * listeners.
 * </p>
 *
 * @author Sam Brannen
 * @since 2.5
 * @see StandardJUnit4FeaturesTests
 */
@RunWith(SpringRunner.class)
@TestExecutionListeners({})
public class StandardJUnit4FeaturesSpringRunnerTests extends StandardJUnit4FeaturesTests {

	/* All tests are in the parent class... */

}
