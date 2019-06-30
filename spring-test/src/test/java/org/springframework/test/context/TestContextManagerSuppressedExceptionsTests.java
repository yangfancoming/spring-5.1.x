

package org.springframework.test.context;

import java.lang.reflect.Method;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * JUnit 4 based unit tests for {@link TestContextManager}, which verify proper
 * support for <em>suppressed exceptions</em> thrown by {@link TestExecutionListener
 * TestExecutionListeners}.
 *
 * @author Sam Brannen
 * @since 5.0
 * @see Throwable#getSuppressed()
 */
public class TestContextManagerSuppressedExceptionsTests {

	@Test
	public void afterTestExecution() throws Exception {
		test("afterTestExecution", FailingAfterTestExecutionTestCase.class,
			(tcm, c, m) -> tcm.afterTestExecution(this, m, null));
	}

	@Test
	public void afterTestMethod() throws Exception {
		test("afterTestMethod", FailingAfterTestMethodTestCase.class,
			(tcm, c, m) -> tcm.afterTestMethod(this, m, null));
	}

	@Test
	public void afterTestClass() throws Exception {
		test("afterTestClass", FailingAfterTestClassTestCase.class, (tcm, c, m) -> tcm.afterTestClass());
	}

	private void test(String useCase, Class<?> testClass, Callback callback) throws Exception {
		TestContextManager testContextManager = new TestContextManager(testClass);
		assertEquals("Registered TestExecutionListeners", 2, testContextManager.getTestExecutionListeners().size());

		try {
			Method testMethod = getClass().getMethod("toString");
			callback.invoke(testContextManager, testClass, testMethod);
			fail("should have thrown an AssertionError");
		}
		catch (AssertionError err) {
			// 'after' callbacks are reversed, so 2 comes before 1.
			assertEquals(useCase + "-2", err.getMessage());
			Throwable[] suppressed = err.getSuppressed();
			assertEquals(1, suppressed.length);
			assertEquals(useCase + "-1", suppressed[0].getMessage());
		}
	}


	// -------------------------------------------------------------------

	@FunctionalInterface
	private interface Callback {

		void invoke(TestContextManager tcm, Class<?> clazz, Method method) throws Exception;
	}

	private static class FailingAfterTestClassListener1 implements TestExecutionListener {

		@Override
		public void afterTestClass(TestContext testContext) {
			fail("afterTestClass-1");
		}
	}

	private static class FailingAfterTestClassListener2 implements TestExecutionListener {

		@Override
		public void afterTestClass(TestContext testContext) {
			fail("afterTestClass-2");
		}
	}

	private static class FailingAfterTestMethodListener1 implements TestExecutionListener {

		@Override
		public void afterTestMethod(TestContext testContext) {
			fail("afterTestMethod-1");
		}
	}

	private static class FailingAfterTestMethodListener2 implements TestExecutionListener {

		@Override
		public void afterTestMethod(TestContext testContext) {
			fail("afterTestMethod-2");
		}
	}

	private static class FailingAfterTestExecutionListener1 implements TestExecutionListener {

		@Override
		public void afterTestExecution(TestContext testContext) {
			fail("afterTestExecution-1");
		}
	}

	private static class FailingAfterTestExecutionListener2 implements TestExecutionListener {

		@Override
		public void afterTestExecution(TestContext testContext) {
			fail("afterTestExecution-2");
		}
	}

	@TestExecutionListeners({ FailingAfterTestExecutionListener1.class, FailingAfterTestExecutionListener2.class })
	private static class FailingAfterTestExecutionTestCase {
	}

	@TestExecutionListeners({ FailingAfterTestMethodListener1.class, FailingAfterTestMethodListener2.class })
	private static class FailingAfterTestMethodTestCase {
	}

	@TestExecutionListeners({ FailingAfterTestClassListener1.class, FailingAfterTestClassListener2.class })
	private static class FailingAfterTestClassTestCase {
	}

}
