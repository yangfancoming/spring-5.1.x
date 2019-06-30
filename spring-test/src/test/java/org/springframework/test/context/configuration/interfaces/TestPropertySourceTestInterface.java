

package org.springframework.test.context.configuration.interfaces;

import org.springframework.test.context.TestPropertySource;

/**
 * @author Sam Brannen
 * @since 4.3
 */
@TestPropertySource(properties = { "foo = bar", "enigma: 42" })
interface TestPropertySourceTestInterface {
}
