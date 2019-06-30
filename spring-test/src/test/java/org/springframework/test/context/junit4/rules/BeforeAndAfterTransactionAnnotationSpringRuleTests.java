

package org.springframework.test.context.junit4.rules;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import org.springframework.test.context.junit4.BeforeAndAfterTransactionAnnotationTests;

/**
 * This class is an extension of {@link BeforeAndAfterTransactionAnnotationTests}
 * that has been modified to use {@link SpringClassRule} and
 * {@link SpringMethodRule}.
 *
 * @author Sam Brannen
 * @since 4.2
 */
@RunWith(JUnit4.class)
public class BeforeAndAfterTransactionAnnotationSpringRuleTests extends BeforeAndAfterTransactionAnnotationTests {

	@ClassRule
	public static final SpringClassRule springClassRule = new SpringClassRule();

	@Rule
	public final SpringMethodRule springMethodRule = new SpringMethodRule();

	// All tests are in superclass.

}
