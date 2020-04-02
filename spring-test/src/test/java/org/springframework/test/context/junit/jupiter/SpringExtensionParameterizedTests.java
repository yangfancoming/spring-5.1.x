

package org.springframework.test.context.junit.jupiter;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.SpringJUnitJupiterTestSuite;
import org.springframework.test.context.junit.jupiter.comics.Cat;
import org.springframework.test.context.junit.jupiter.comics.Dog;
import org.springframework.test.context.junit.jupiter.comics.Person;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests which demonstrate that the Spring TestContext Framework
 * can be used with JUnit Jupiter's {@link ParameterizedTest @ParameterizedTest}
 * support in conjunction with the {@link SpringExtension}.
 *
 * To run these tests in an IDE that does not have built-in support for the
 * JUnit Platform, simply run {@link SpringJUnitJupiterTestSuite} as a JUnit 4 test.
 *
 * @author Sam Brannen
 * @since 5.0
 * @see SpringExtension
 * @see ParameterizedTest
 */
@SpringJUnitConfig(TestConfig.class)
class SpringExtensionParameterizedTests {

	@ParameterizedTest
	@ValueSource(strings = { "Dilbert", "Wally" })
	void people(String name, @Autowired List<Person> people) {
		assertTrue(people.stream().map(Person::getName).filter(str -> name.equals(str)).findFirst().isPresent());
	}

	@ParameterizedTest
	@CsvSource("dogbert, Dogbert")
	void dogs(String beanName, String dogName, ApplicationContext context) {
		assertEquals(dogName, context.getBean(beanName, Dog.class).getName());
	}

	@ParameterizedTest
	@CsvSource({ "garfield, Garfield", "catbert, Catbert" })
	void cats(String beanName, String catName, ApplicationContext context) {
		assertEquals(catName, context.getBean(beanName, Cat.class).getName());
	}

}
