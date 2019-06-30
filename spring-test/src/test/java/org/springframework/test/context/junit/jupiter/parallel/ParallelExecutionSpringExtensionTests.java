

package org.springframework.test.context.junit.jupiter.parallel;

import java.lang.reflect.Parameter;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.platform.engine.discovery.DiscoverySelectors.*;
import static org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder.*;

/**
 * Integration tests which verify that {@code @BeforeEach} and {@code @AfterEach} methods
 * that accept {@code @Autowired} arguments can be executed in parallel without issues
 * regarding concurrent access to the {@linkplain Parameter parameters} of such methods.
 *
 * @author Sam Brannen
 * @since 5.1.3
 */
class ParallelExecutionSpringExtensionTests {

	private static final int NUM_TESTS = 1000;

	@RepeatedTest(10)
	void runTestsInParallel() {
		Launcher launcher = LauncherFactory.create();
		SummaryGeneratingListener listener = new SummaryGeneratingListener();
		launcher.registerTestExecutionListeners(listener);

		LauncherDiscoveryRequest request = request()//
				.configurationParameter("junit.jupiter.execution.parallel.enabled", "true")//
				.configurationParameter("junit.jupiter.execution.parallel.config.dynamic.factor", "10")//
				.selectors(selectClass(TestCase.class))//
				.build();

		launcher.execute(request);

		assertEquals(NUM_TESTS, listener.getSummary().getTestsSucceededCount(),
				"number of tests executed successfully");
	}

	@SpringJUnitConfig
	static class TestCase {

		@BeforeEach
		void beforeEach(@Autowired ApplicationContext context) {
		}

		@RepeatedTest(NUM_TESTS)
		void repeatedTest(@Autowired ApplicationContext context) {
		}

		@AfterEach
		void afterEach(@Autowired ApplicationContext context) {
		}

		@Configuration
		static class Config {
		}
	}

}
