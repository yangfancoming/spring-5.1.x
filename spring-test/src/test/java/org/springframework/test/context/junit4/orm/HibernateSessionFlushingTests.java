

package org.springframework.test.context.junit4.orm;

import javax.persistence.PersistenceException;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.exception.ConstraintViolationException;
import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.orm.domain.DriversLicense;
import org.springframework.test.context.junit4.orm.domain.Person;
import org.springframework.test.context.junit4.orm.service.PersonService;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;
import static org.springframework.test.transaction.TransactionTestUtils.*;

/**
 * Transactional integration tests regarding <i>manual</i> session flushing with
 * Hibernate.
 *
 * @author Sam Brannen

 * @author Vlad Mihalcea
 * @since 3.0
 */
@ContextConfiguration
public class HibernateSessionFlushingTests extends AbstractTransactionalJUnit4SpringContextTests {

	private static final String SAM = "Sam";
	private static final String JUERGEN = "Juergen";

	@Autowired
	private PersonService personService;

	@Autowired
	private SessionFactory sessionFactory;


	@Before
	public void setup() {
		assertInTransaction(true);
		assertNotNull("PersonService should have been autowired.", personService);
		assertNotNull("SessionFactory should have been autowired.", sessionFactory);
	}


	@Test
	public void findSam() {
		Person sam = personService.findByName(SAM);
		assertNotNull("Should be able to find Sam", sam);
		DriversLicense driversLicense = sam.getDriversLicense();
		assertNotNull("Sam's driver's license should not be null", driversLicense);
		assertEquals("Verifying Sam's driver's license number", Long.valueOf(1234), driversLicense.getNumber());
	}

	@Test  // SPR-16956
	@Transactional(readOnly = true)
	public void findSamWithReadOnlySession() {
		Person sam = personService.findByName(SAM);
		sam.setName("Vlad");
		// By setting setDefaultReadOnly(true), the user can no longer modify any entity...
		Session session = sessionFactory.getCurrentSession();
		session.flush();
		session.refresh(sam);
		assertEquals("Sam", sam.getName());
	}

	@Test
	public void saveJuergenWithDriversLicense() {
		DriversLicense driversLicense = new DriversLicense(2L, 2222L);
		Person juergen = new Person(JUERGEN, driversLicense);
		int numRows = countRowsInTable("person");
		personService.save(juergen);
		assertEquals("Verifying number of rows in the 'person' table.", numRows + 1, countRowsInTable("person"));
		assertNotNull("Should be able to save and retrieve Juergen", personService.findByName(JUERGEN));
		assertNotNull("Juergen's ID should have been set", juergen.getId());
	}

	@Test(expected = ConstraintViolationException.class)
	public void saveJuergenWithNullDriversLicense() {
		personService.save(new Person(JUERGEN));
	}

	@Test
	// no expected exception!
	public void updateSamWithNullDriversLicenseWithoutSessionFlush() {
		updateSamWithNullDriversLicense();
		// False positive, since an exception will be thrown once the session is
		// finally flushed (i.e., in production code)
	}

	@Test(expected = ConstraintViolationException.class)
	public void updateSamWithNullDriversLicenseWithSessionFlush() throws Throwable {
		updateSamWithNullDriversLicense();
		// Manual flush is required to avoid false positive in test
		try {
			sessionFactory.getCurrentSession().flush();
		}
		catch (PersistenceException ex) {
			// Wrapped in Hibernate 5.2, with the constraint violation as cause
			throw ex.getCause();
		}
	}

	private void updateSamWithNullDriversLicense() {
		Person sam = personService.findByName(SAM);
		assertNotNull("Should be able to find Sam", sam);
		sam.setDriversLicense(null);
		personService.save(sam);
	}

}
