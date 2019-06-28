

package org.springframework.web.servlet.tags.form;

import org.springframework.tests.sample.beans.TestBean;

/**
 * @author Juergen Hoeller
 */
public class TestBeanWithRealCountry extends TestBean {

	private Country realCountry = Country.COUNTRY_AT;


	public void setRealCountry(Country realCountry) {
		this.realCountry = realCountry;
	}

	public Country getRealCountry() {
		return realCountry;
	}

}
