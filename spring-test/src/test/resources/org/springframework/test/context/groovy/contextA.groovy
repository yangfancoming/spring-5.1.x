

package org.springframework.test.context.groovy

/**
 * Groovy script for defining Spring beans for integration tests.
 *
 * @author Sam Brannen
 * @since 4.1
 */
beans {

	foo String, 'Groovy Foo'
	bar String, 'Groovy Bar'

}
