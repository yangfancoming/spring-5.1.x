

package org.springframework.test.context.junit;

import org.junit.platform.runner.JUnitPlatform;
import org.junit.platform.suite.api.ExcludeTags;
import org.junit.platform.suite.api.IncludeClassNamePatterns;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectPackages;
import org.junit.platform.suite.api.UseTechnicalNames;
import org.junit.runner.RunWith;

/**
 * JUnit 4 based test suite for tests that involve the Spring TestContext
 * Framework and JUnit Jupiter (i.e., JUnit 5's programming model).
 *
 * This class intentionally does not reside in the "jupiter" package
 * so that the entire "jupiter" package can be excluded from the Gradle
 * build. This class is therefore responsible for executing all JUnit
 * Jupiter based tests in Spring's official test suite.
 *
 * <h3>Logging Configuration</h3>
 *
 * In order for our log4j2 configuration to be used in an IDE, you must
 * set the following system property before running any tests ; for
 * example, in <em>Run Configurations</em> in Eclipse.
 *
 * <pre style="code">
 * -Djava.util.logging.manager=org.apache.logging.log4j.jul.LogManager
 * </pre>
 *
 * @author Sam Brannen
 * @since 5.0
 */
@RunWith(JUnitPlatform.class)
@IncludeEngines("junit-jupiter")
@SelectPackages("org.springframework.test.context.junit.jupiter")
@IncludeClassNamePatterns(".*Tests$")
@ExcludeTags("failing-test-case")
@UseTechnicalNames
public class SpringJUnitJupiterTestSuite {
}
