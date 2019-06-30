

package org.springframework.test.context.junit4.rules;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.Runner;
import org.junit.runners.JUnit4;
import org.junit.runners.Parameterized.Parameters;

import org.springframework.test.annotation.Repeat;
import org.springframework.test.annotation.Timed;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.RepeatedSpringRunnerTests;

/**
 * This class is an extension of {@link RepeatedSpringRunnerTests}
 * that has been modified to use {@link SpringClassRule} and
 * {@link SpringMethodRule}.
 *
 * @author Sam Brannen
 * @since 4.2
 */
public class RepeatedSpringRuleTests extends RepeatedSpringRunnerTests {

	@Parameters(name = "{0}")
	public static Object[][] repetitionData() {
		return new Object[][] {//
			{ NonAnnotatedRepeatedTestCase.class.getSimpleName(), 0, 1, 1, 1 },//
			{ DefaultRepeatValueRepeatedTestCase.class.getSimpleName(), 0, 1, 1, 1 },//
			{ NegativeRepeatValueRepeatedTestCase.class.getSimpleName(), 0, 1, 1, 1 },//
			{ RepeatedFiveTimesRepeatedTestCase.class.getSimpleName(), 0, 1, 1, 5 },//
			{ RepeatedFiveTimesViaMetaAnnotationRepeatedTestCase.class.getSimpleName(), 0, 1, 1, 5 },//
			{ TimedRepeatedTestCase.class.getSimpleName(), 3, 4, 4, (5 + 1 + 4 + 10) } //
		};
	}

	public RepeatedSpringRuleTests(String testClassName, int expectedFailureCount, int expectedTestStartedCount,
			int expectedTestFinishedCount, int expectedInvocationCount) throws Exception {

		super(testClassName, expectedFailureCount, expectedTestStartedCount, expectedTestFinishedCount,
			expectedInvocationCount);
	}

	@Override
	protected Class<? extends Runner> getRunnerClass() {
		return JUnit4.class;
	}

	// All tests are in superclass.

	@TestExecutionListeners({})
	public abstract static class AbstractRepeatedTestCase {

		@ClassRule
		public static final SpringClassRule springClassRule = new SpringClassRule();

		@Rule
		public final SpringMethodRule springMethodRule = new SpringMethodRule();


		protected void incrementInvocationCount() throws IOException {
			invocationCount.incrementAndGet();
		}
	}

	public static final class NonAnnotatedRepeatedTestCase extends AbstractRepeatedTestCase {

		@Test
		@Timed(millis = 10000)
		public void nonAnnotated() throws Exception {
			incrementInvocationCount();
		}
	}

	public static final class DefaultRepeatValueRepeatedTestCase extends AbstractRepeatedTestCase {

		@Test
		@Repeat
		@Timed(millis = 10000)
		public void defaultRepeatValue() throws Exception {
			incrementInvocationCount();
		}
	}

	public static final class NegativeRepeatValueRepeatedTestCase extends AbstractRepeatedTestCase {

		@Test
		@Repeat(-5)
		@Timed(millis = 10000)
		public void negativeRepeatValue() throws Exception {
			incrementInvocationCount();
		}
	}

	public static final class RepeatedFiveTimesRepeatedTestCase extends AbstractRepeatedTestCase {

		@Test
		@Repeat(5)
		public void repeatedFiveTimes() throws Exception {
			incrementInvocationCount();
		}
	}

	@Repeat(5)
	@Retention(RetentionPolicy.RUNTIME)
	private static @interface RepeatedFiveTimes {
	}

	public static final class RepeatedFiveTimesViaMetaAnnotationRepeatedTestCase extends AbstractRepeatedTestCase {

		@Test
		@RepeatedFiveTimes
		public void repeatedFiveTimes() throws Exception {
			incrementInvocationCount();
		}
	}

	/**
	 * Unit tests for claims raised in <a href="https://jira.spring.io/browse/SPR-6011" target="_blank">SPR-6011</a>.
	 */
	@Ignore("TestCase classes are run manually by the enclosing test class")
	public static final class TimedRepeatedTestCase extends AbstractRepeatedTestCase {

		@Test
		@Timed(millis = 1000)
		@Repeat(5)
		public void repeatedFiveTimesButDoesNotExceedTimeout() throws Exception {
			incrementInvocationCount();
		}

		@Test
		@Timed(millis = 10)
		@Repeat(1)
		public void singleRepetitionExceedsTimeout() throws Exception {
			incrementInvocationCount();
			Thread.sleep(15);
		}

		@Test
		@Timed(millis = 20)
		@Repeat(4)
		public void firstRepetitionOfManyExceedsTimeout() throws Exception {
			incrementInvocationCount();
			Thread.sleep(25);
		}

		@Test
		@Timed(millis = 100)
		@Repeat(10)
		public void collectiveRepetitionsExceedTimeout() throws Exception {
			incrementInvocationCount();
			Thread.sleep(11);
		}
	}

}
