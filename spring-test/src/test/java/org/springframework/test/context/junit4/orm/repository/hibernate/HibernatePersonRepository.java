

package org.springframework.test.context.junit4.orm.repository.hibernate;

import org.hibernate.SessionFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.test.context.junit4.orm.domain.Person;
import org.springframework.test.context.junit4.orm.repository.PersonRepository;

/**
 * Hibernate implementation of the {@link PersonRepository} API.
 *
 * @author Sam Brannen
 * @since 3.0
 */
@Repository
public class HibernatePersonRepository implements PersonRepository {

	private final SessionFactory sessionFactory;


	@Autowired
	public HibernatePersonRepository(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	@Override
	public Person save(Person person) {
		this.sessionFactory.getCurrentSession().save(person);
		return person;
	}

	@Override
	public Person findByName(String name) {
		return (Person) this.sessionFactory.getCurrentSession().createQuery(
			"from Person person where person.name = :name").setParameter("name", name).getSingleResult();
	}

}
