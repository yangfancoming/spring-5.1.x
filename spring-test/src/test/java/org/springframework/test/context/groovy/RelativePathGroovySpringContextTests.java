

package org.springframework.test.context.groovy;

import org.springframework.test.context.ContextConfiguration;

/**
 * Extension of {@link GroovySpringContextTests} that declares a Groovy
 * script using a relative path.
 *
 * @author Sam Brannen
 * @since 4.1
 * @see GroovySpringContextTests
 * @see AbsolutePathGroovySpringContextTests
 */
@ContextConfiguration(locations = "../groovy/context.groovy", inheritLocations = false)
public class RelativePathGroovySpringContextTests extends GroovySpringContextTests {

	/* all tests are in the superclass */

}
