

package org.springframework.test.context.junit4.profile.annotation;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * JUnit test suite for <em>bean definition profile</em> support in the
 * Spring TestContext Framework with annotation-based configuration.
 *
 * @author Sam Brannen
 * @since 3.1
 */
@RunWith(Suite.class)
// Note: the following 'multi-line' layout is for enhanced code readability.
@SuiteClasses({//
DefaultProfileAnnotationConfigTests.class,//
	DevProfileAnnotationConfigTests.class,//
	DevProfileResolverAnnotationConfigTests.class //
})
public class ProfileAnnotationConfigTestSuite {
}
