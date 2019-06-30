

package org.springframework.test.context.junit4.spr3896;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * JUnit 4 based test suite for functionality proposed in <a
 * href="https://opensource.atlassian.com/projects/spring/browse/SPR-3896"
 * target="_blank">SPR-3896</a>.
 *
 * @author Sam Brannen
 * @since 2.5
 */
@RunWith(Suite.class)
// Note: the following 'multi-line' layout is for enhanced code readability.
@SuiteClasses({

DefaultLocationsBaseTests.class,

DefaultLocationsInheritedTests.class,

ExplicitLocationsBaseTests.class,

ExplicitLocationsInheritedTests.class,

BeanOverridingDefaultLocationsInheritedTests.class,

BeanOverridingExplicitLocationsInheritedTests.class

})
public class Spr3896SuiteTests {
}
