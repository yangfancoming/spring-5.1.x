

package org.springframework.test.context.testng;

import javax.sql.DataSource;

import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.transaction.AfterTransaction;
import org.springframework.test.context.transaction.BeforeTransaction;
import org.springframework.tests.sample.beans.Employee;
import org.springframework.tests.sample.beans.Pet;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.transaction.TransactionTestUtils.*;
import static org.testng.Assert.*;

/**
 * Integration tests that verify support for
 * {@link org.springframework.context.annotation.Configuration @Configuration} classes
 * with transactional TestNG-based tests.
 *
 * Configuration will be loaded from
 * {@link AnnotationConfigTransactionalTestNGSpringContextTests.ContextConfiguration}.
 *
 * @author Sam Brannen
 * @since 3.1
 */
@ContextConfiguration
public class AnnotationConfigTransactionalTestNGSpringContextTests
		extends AbstractTransactionalTestNGSpringContextTests {

	private static final String JANE = "jane";
	private static final String SUE = "sue";
	private static final String YODA = "yoda";

	private static final int NUM_TESTS = 2;
	private static final int NUM_TX_TESTS = 1;

	private static int numSetUpCalls = 0;
	private static int numSetUpCallsInTransaction = 0;
	private static int numTearDownCalls = 0;
	private static int numTearDownCallsInTransaction = 0;

	@Autowired
	private Employee employee;

	@Autowired
	private Pet pet;


	private int createPerson(String name) {
		return jdbcTemplate.update("INSERT INTO person VALUES(?)", name);
	}

	private int deletePerson(String name) {
		return jdbcTemplate.update("DELETE FROM person WHERE name=?", name);
	}

	private void assertNumRowsInPersonTable(int expectedNumRows, String testState) {
		assertEquals(countRowsInTable("person"), expectedNumRows, "the number of rows in the person table ("
				+ testState + ").");
	}

	private void assertAddPerson(final String name) {
		assertEquals(createPerson(name), 1, "Adding '" + name + "'");
	}

	@BeforeClass
	void beforeClass() {
		numSetUpCalls = 0;
		numSetUpCallsInTransaction = 0;
		numTearDownCalls = 0;
		numTearDownCallsInTransaction = 0;
	}

	@AfterClass
	void afterClass() {
		assertEquals(numSetUpCalls, NUM_TESTS, "number of calls to setUp().");
		assertEquals(numSetUpCallsInTransaction, NUM_TX_TESTS, "number of calls to setUp() within a transaction.");
		assertEquals(numTearDownCalls, NUM_TESTS, "number of calls to tearDown().");
		assertEquals(numTearDownCallsInTransaction, NUM_TX_TESTS, "number of calls to tearDown() within a transaction.");
	}

	@Test
	@Transactional(propagation = Propagation.NOT_SUPPORTED)
	void autowiringFromConfigClass() {
		assertNotNull(employee, "The employee should have been autowired.");
		assertEquals(employee.getName(), "John Smith");

		assertNotNull(pet, "The pet should have been autowired.");
		assertEquals(pet.getName(), "Fido");
	}

	@BeforeTransaction
	void beforeTransaction() {
		assertNumRowsInPersonTable(1, "before a transactional test method");
		assertAddPerson(YODA);
	}

	@BeforeMethod
	void setUp() throws Exception {
		numSetUpCalls++;
		if (inTransaction()) {
			numSetUpCallsInTransaction++;
		}
		assertNumRowsInPersonTable((inTransaction() ? 2 : 1), "before a test method");
	}

	@Test
	void modifyTestDataWithinTransaction() {
		assertInTransaction(true);
		assertAddPerson(JANE);
		assertAddPerson(SUE);
		assertNumRowsInPersonTable(4, "in modifyTestDataWithinTransaction()");
	}

	@AfterMethod
	void tearDown() throws Exception {
		numTearDownCalls++;
		if (inTransaction()) {
			numTearDownCallsInTransaction++;
		}
		assertNumRowsInPersonTable((inTransaction() ? 4 : 1), "after a test method");
	}

	@AfterTransaction
	void afterTransaction() {
		assertEquals(deletePerson(YODA), 1, "Deleting yoda");
		assertNumRowsInPersonTable(1, "after a transactional test method");
	}


	@Configuration
	static class ContextConfiguration {

		@Bean
		Employee employee() {
			Employee employee = new Employee();
			employee.setName("John Smith");
			employee.setAge(42);
			employee.setCompany("Acme Widgets, Inc.");
			return employee;
		}

		@Bean
		Pet pet() {
			return new Pet("Fido");
		}

		@Bean
		PlatformTransactionManager transactionManager() {
			return new DataSourceTransactionManager(dataSource());
		}

		@Bean
		DataSource dataSource() {
			return new EmbeddedDatabaseBuilder()//
			.addScript("classpath:/org/springframework/test/jdbc/schema.sql")//
			.addScript("classpath:/org/springframework/test/jdbc/data.sql")//
			.build();
		}

	}

}
