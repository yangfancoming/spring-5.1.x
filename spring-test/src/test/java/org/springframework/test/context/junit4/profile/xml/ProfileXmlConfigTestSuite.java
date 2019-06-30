

package org.springframework.test.context.junit4.profile.xml;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

/**
 * JUnit test suite for <em>bean definition profile</em> support in the
 * Spring TestContext Framework with XML-based configuration.
 *
 * @author Sam Brannen
 * @since 3.1
 */
@RunWith(Suite.class)
// Note: the following 'multi-line' layout is for enhanced code readability.
@SuiteClasses({//
DefaultProfileXmlConfigTests.class,//
	DevProfileXmlConfigTests.class,//
	DevProfileResolverXmlConfigTests.class //
})
public class ProfileXmlConfigTestSuite {
}
