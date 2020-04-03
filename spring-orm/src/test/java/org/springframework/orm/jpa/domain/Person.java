

package org.springframework.orm.jpa.domain;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;

import org.springframework.context.ApplicationContext;
import org.springframework.tests.sample.beans.TestBean;

/**
 * Simple JavaBean domain object representing an person.
 *
 * @author Rod Johnson
 */
@Entity
@EntityListeners(PersonListener.class)
public class Person {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer id;

	private transient TestBean testBean;

	// Lazy relationship to force use of instrumentation in JPA implementation.
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
	@JoinColumn(name = "DRIVERS_LICENSE_ID")
	private DriversLicense driversLicense;

	private String first_name;

	@Basic(fetch = FetchType.LAZY)
	private String last_name;

	public transient ApplicationContext postLoaded;


	public Integer getId() {
		return id;
	}

	public void setTestBean(TestBean testBean) {
		this.testBean = testBean;
	}

	public TestBean getTestBean() {
		return testBean;
	}

	public void setFirstName(String firstName) {
		this.first_name = firstName;
	}

	public String getFirstName() {
		return this.first_name;
	}

	public void setLastName(String lastName) {
		this.last_name = lastName;
	}

	public String getLastName() {
		return this.last_name;
	}

	public void setDriversLicense(DriversLicense driversLicense) {
		this.driversLicense = driversLicense;
	}

	public DriversLicense getDriversLicense() {
		return this.driversLicense;
	}

	@Override
	public String toString() {
		return getClass().getName() + ":(" + hashCode() + ") id=" + id + "; firstName=" + first_name +
				"; lastName=" + last_name + "; testBean=" + testBean;
	}

}
