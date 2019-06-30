

package org.springframework.test.context.junit4.rules;

import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import org.springframework.test.context.web.BasicAnnotationConfigWacTests;

/**
 * This class is an extension of {@link BasicAnnotationConfigWacTests}
 * that has been modified to use {@link SpringClassRule} and
 * {@link SpringMethodRule}.
 *
 * @author Sam Brannen
 * @since 4.2
 */
@RunWith(JUnit4.class)
public class BasicAnnotationConfigWacSpringRuleTests extends BasicAnnotationConfigWacTests {

	// All tests are in superclass.

	@ClassRule
	public static final SpringClassRule springClassRule = new SpringClassRule();

	@Rule
	public final SpringMethodRule springMethodRule = new SpringMethodRule();

}
