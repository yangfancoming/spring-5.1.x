

package org.springframework.test.context.groovy

import org.springframework.tests.sample.beans.Employee
import org.springframework.tests.sample.beans.Pet

/**
 * Groovy script for defining Spring beans for integration tests.
 *
 * @author Sam Brannen
 * @since 4.1
 */
beans {

	foo String, 'Foo'
	bar String, 'Bar'

	employee(Employee) {
		name = "Dilbert"
		age = 42
		company = "???"
	}

	pet(Pet, 'Dogbert')
}
