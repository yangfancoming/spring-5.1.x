

package org.springframework.test.context.junit4;

import java.util.ArrayList;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import org.springframework.test.context.TestExecutionListeners;

import static org.springframework.test.context.junit4.JUnitTestingUtils.*;

/**
 * Verifies proper handling of JUnit's {@link Test#expected() &#064;Test(expected = ...)}
 * support in conjunction with the {@link SpringRunner}.
 *
 * @author Sam Brannen
 * @since 3.0
 */
@RunWith(JUnit4.class)
public class ExpectedExceptionSpringRunnerTests {

	@Test
	public void expectedExceptions() throws Exception {
		runTestsAndAssertCounters(SpringRunner.class, ExpectedExceptionSpringRunnerTestCase.class, 1, 0, 1, 0, 0);
	}


	@Ignore("TestCase classes are run manually by the enclosing test class")
	@TestExecutionListeners({})
	public static final class ExpectedExceptionSpringRunnerTestCase {

		// Should Pass.
		@Test(expected = IndexOutOfBoundsException.class)
		public void verifyJUnitExpectedException() {
			new ArrayList<>().get(1);
		}
	}

}
