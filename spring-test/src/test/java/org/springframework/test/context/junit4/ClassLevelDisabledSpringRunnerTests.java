

package org.springframework.test.context.junit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.annotation.IfProfileValue;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;
import org.springframework.test.context.TestExecutionListeners;

import static org.junit.Assert.*;


@RunWith(SpringRunner.class)
@TestExecutionListeners(ClassLevelDisabledSpringRunnerTests.CustomTestExecutionListener.class)
@IfProfileValue(name = "ClassLevelDisabledSpringRunnerTests.profile_value.name", value = "enigmaX")
public class ClassLevelDisabledSpringRunnerTests {

	@Test
	public void testIfProfileValueDisabled() {
		fail("The body of a disabled test should never be executed!");
	}


	public static class CustomTestExecutionListener implements TestExecutionListener {

		@Override
		public void beforeTestClass(TestContext testContext) throws Exception {
			fail("A listener method for a disabled test should never be executed!");
		}

		@Override
		public void prepareTestInstance(TestContext testContext) throws Exception {
			fail("A listener method for a disabled test should never be executed!");
		}

		@Override
		public void beforeTestMethod(TestContext testContext) throws Exception {
			fail("A listener method for a disabled test should never be executed!");
		}

		@Override
		public void afterTestMethod(TestContext testContext) throws Exception {
			fail("A listener method for a disabled test should never be executed!");
		}

		@Override
		public void afterTestClass(TestContext testContext) throws Exception {
			fail("A listener method for a disabled test should never be executed!");
		}
	}
}
