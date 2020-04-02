

package org.springframework.test.context.junit.jupiter;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.SpringJUnitJupiterTestSuite;
import org.springframework.test.context.junit.jupiter.comics.Person;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests which demonstrate the composability of annotations from
 * JUnit Jupiter and the Spring TestContext Framework.
 *
 * Note that {@link SpringJUnitConfig @SpringJUnitConfig} is meta-annotated
 * with JUnit Jupiter's {@link ExtendWith @ExtendWith} <b>and</b> Spring's
 * {@link ContextConfiguration @ContextConfiguration}.
 *
 * To run these tests in an IDE that does not have built-in support for the JUnit
 * Platform, simply run {@link SpringJUnitJupiterTestSuite} as a JUnit 4 test.
 *
 * @author Sam Brannen
 * @since 5.0
 * @see SpringExtension
 * @see SpringJUnitConfig
 * @see SpringExtensionTests
 */
@SpringJUnitConfig(TestConfig.class)
@DisplayName("@SpringJUnitConfig Tests")
class ComposedSpringExtensionTests {

	@Autowired
	Person dilbert;

	@Autowired
	List<Person> people;

	@Test
	@DisplayName("ApplicationContext injected into method")
	void applicationContextInjected(ApplicationContext applicationContext) {
		assertNotNull(applicationContext, "ApplicationContext should have been injected into method by Spring");
		assertEquals(dilbert, applicationContext.getBean("dilbert", Person.class));
	}

	@Test
	@DisplayName("Spring @Beans injected into fields")
	void springBeansInjected() {
		assertNotNull(dilbert, "Person should have been @Autowired by Spring");
		assertEquals("Dilbert", dilbert.getName(), "Person's name");
		assertEquals(2, people.size(), "Number of Person objects in context");
	}

}
