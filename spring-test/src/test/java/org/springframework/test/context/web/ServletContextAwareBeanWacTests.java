

package org.springframework.test.context.web;

import org.junit.Test;

import static org.springframework.test.context.junit4.JUnitTestingUtils.*;

/**
 * Introduced to investigate claims in SPR-11145.
 *
 * Yes, this test class does in fact use JUnit to run JUnit. ;)
 *
 * @author Sam Brannen
 * @since 4.0.2
 */
public class ServletContextAwareBeanWacTests {

	@Test
	public void ensureServletContextAwareBeanIsProcessedProperlyWhenExecutingJUnitManually() throws Exception {
		runTestsAndAssertCounters(BasicAnnotationConfigWacTests.class, 3, 0, 3, 0, 0);
	}

}
