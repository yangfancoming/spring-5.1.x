

package org.springframework.test.context.junit4;

import java.util.ArrayList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assume.*;

/**
 * Simple unit test to verify the expected functionality of standard JUnit 4.4+
 * testing features.
 *
 * Currently testing: {@link Test @Test} (including expected exceptions and
 * timeouts), {@link BeforeClass @BeforeClass}, {@link Before @Before}, and
 * <em>assumptions</em>.
 * </p>
 *
 * Due to the fact that JUnit does not guarantee a particular ordering of test
 * method execution, the following are currently not tested:
 * {@link org.junit.AfterClass @AfterClass} and {@link org.junit.After @After}.
 * </p>
 *
 * @author Sam Brannen
 * @since 2.5
 * @see StandardJUnit4FeaturesSpringRunnerTests
 */
public class StandardJUnit4FeaturesTests {

	private static int staticBeforeCounter = 0;


	@BeforeClass
	public static void incrementStaticBeforeCounter() {
		StandardJUnit4FeaturesTests.staticBeforeCounter++;
	}


	private int beforeCounter = 0;


	@Test
	@Ignore
	public void alwaysFailsButShouldBeIgnored() {
		fail("The body of an ignored test should never be executed!");
	}

	@Test
	public void alwaysSucceeds() {
		assertTrue(true);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void expectingAnIndexOutOfBoundsException() {
		new ArrayList<>().get(1);
	}

	@Test
	public void failedAssumptionShouldPrecludeImminentFailure() {
		assumeTrue(false);
		fail("A failed assumption should preclude imminent failure!");
	}

	@Before
	public void incrementBeforeCounter() {
		this.beforeCounter++;
	}

	@Test(timeout = 10000)
	public void noOpShouldNotTimeOut() {
		/* no-op */
	}

	@Test
	public void verifyBeforeAnnotation() {
		assertEquals(1, this.beforeCounter);
	}

	@Test
	public void verifyBeforeClassAnnotation() {
		// Instead of testing for equality to 1, we just assert that the value
		// was incremented at least once, since this test class may serve as a
		// parent class to other tests in a suite, etc.
		assertTrue(StandardJUnit4FeaturesTests.staticBeforeCounter > 0);
	}

}
