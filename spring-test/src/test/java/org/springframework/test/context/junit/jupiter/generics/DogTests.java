

package org.springframework.test.context.junit.jupiter.generics;

import org.springframework.test.context.junit.SpringJUnitJupiterTestSuite;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.comics.Dog;

/**
 * Parameterized test class for integration tests that demonstrate support for
 * Java generics in JUnit Jupiter test classes when used with the Spring TestContext
 * Framework and the {@link SpringExtension}.
 *
 * To run these tests in an IDE that does not have built-in support for the JUnit
 * Platform, simply run {@link SpringJUnitJupiterTestSuite} as a JUnit 4 test.
 *
 * @author Sam Brannen
 * @since 5.0
 */
class DogTests extends GenericComicCharactersTests<Dog> {

	@Override
	int getExpectedNumCharacters() {
		return 1;
	}

	@Override
	String getExpectedName() {
		return "Dogbert";
	}

}
