

package org.springframework.orm.jpa;

import java.lang.reflect.Proxy;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TransactionRequiredException;

import org.junit.Test;

import org.springframework.orm.jpa.domain.Person;

import static org.junit.Assert.*;

/**
 * An application-managed entity manager can join an existing transaction,
 * but such joining must be made programmatically, not transactionally.
 *
 * @author Rod Johnson

 * @since 2.0
 */
public class ApplicationManagedEntityManagerIntegrationTests extends AbstractEntityManagerFactoryIntegrationTests {

	@Test
	@SuppressWarnings("unchecked")
	public void testEntityManagerProxyIsProxy() {
		EntityManager em = entityManagerFactory.createEntityManager();
		assertTrue(Proxy.isProxyClass(em.getClass()));
		Query q = em.createQuery("select p from Person as p");
		List<Person> people = q.getResultList();
		assertNotNull(people);

		assertTrue("Should be open to start with", em.isOpen());
		em.close();
		assertFalse("Close should work on application managed EM", em.isOpen());
	}

	@Test
	public void testEntityManagerProxyAcceptsProgrammaticTxJoining() {
		EntityManager em = entityManagerFactory.createEntityManager();
		em.joinTransaction();
	}

	@Test
	public void testInstantiateAndSave() {
		EntityManager em = entityManagerFactory.createEntityManager();
		em.joinTransaction();
		doInstantiateAndSave(em);
	}

	@Test
	public void testCannotFlushWithoutGettingTransaction() {
		EntityManager em = entityManagerFactory.createEntityManager();
		try {
			doInstantiateAndSave(em);
			fail("Should have thrown TransactionRequiredException");
		}
		catch (TransactionRequiredException ex) {
			// expected
		}

		// TODO following lines are a workaround for Hibernate bug
		// If Hibernate throws an exception due to flush(),
		// it actually HAS flushed, meaning that the database
		// was updated outside the transaction
		deleteAllPeopleUsingEntityManager(sharedEntityManager);
		setComplete();
	}

	protected void doInstantiateAndSave(EntityManager em) {
		testStateClean();
		Person p = new Person();

		p.setFirstName("Tony");
		p.setLastName("Blair");
		em.persist(p);

		em.flush();
		assertEquals("1 row must have been inserted", 1, countRowsInTable(em, "person"));
	}

	@Test
	public void testStateClean() {
		assertEquals("Should be no people from previous transactions", 0, countRowsInTable("person"));
	}

	@Test
	public void testReuseInNewTransaction() {
		EntityManager em = entityManagerFactory.createEntityManager();
		em.joinTransaction();

		doInstantiateAndSave(em);
		endTransaction();

		assertFalse(em.getTransaction().isActive());

		startNewTransaction();
		// Call any method: should cause automatic tx invocation
		assertFalse(em.contains(new Person()));

		assertFalse(em.getTransaction().isActive());
		em.joinTransaction();

		assertTrue(em.getTransaction().isActive());

		doInstantiateAndSave(em);
		setComplete();
		endTransaction();	// Should rollback
		assertEquals("Tx must have committed back", 1, countRowsInTable(em, "person"));

		// Now clean up the database
		startNewTransaction();
		em.joinTransaction();
		deleteAllPeopleUsingEntityManager(em);
		assertEquals("People have been killed", 0, countRowsInTable(em, "person"));
		setComplete();
	}

	protected void deleteAllPeopleUsingEntityManager(EntityManager em) {
		em.createQuery("delete from Person p").executeUpdate();
	}

	@Test
	public void testRollbackOccurs() {
		EntityManager em = entityManagerFactory.createEntityManager();
		em.joinTransaction();
		doInstantiateAndSave(em);
		endTransaction();	// Should rollback
		assertEquals("Tx must have been rolled back", 0, countRowsInTable(em, "person"));
	}

	@Test
	public void testCommitOccurs() {
		EntityManager em = entityManagerFactory.createEntityManager();
		em.joinTransaction();
		doInstantiateAndSave(em);

		setComplete();
		endTransaction();	// Should rollback
		assertEquals("Tx must have committed back", 1, countRowsInTable(em, "person"));

		// Now clean up the database
		deleteFromTables("person");
	}

}
