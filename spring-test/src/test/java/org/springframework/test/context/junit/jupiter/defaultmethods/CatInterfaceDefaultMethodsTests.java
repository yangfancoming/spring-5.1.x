

package org.springframework.test.context.junit.jupiter.defaultmethods;

import org.springframework.test.context.junit.SpringJUnitJupiterTestSuite;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.junit.jupiter.comics.Cat;

/**
 * Parameterized test class for integration tests that demonstrate support for
 * interface default methods and Java generics in JUnit Jupiter test classes when used
 * with the Spring TestContext Framework and the {@link SpringExtension}.
 *
 * <p>To run these tests in an IDE that does not have built-in support for the JUnit
 * Platform, simply run {@link SpringJUnitJupiterTestSuite} as a JUnit 4 test.
 *
 * @author Sam Brannen
 * @since 5.0
 */
class CatInterfaceDefaultMethodsTests implements GenericComicCharactersInterfaceDefaultMethodsTests<Cat> {

	@Override
	public int getExpectedNumCharacters() {
		return 2;
	}

	@Override
	public String getExpectedName() {
		return "Catbert";
	}

}
