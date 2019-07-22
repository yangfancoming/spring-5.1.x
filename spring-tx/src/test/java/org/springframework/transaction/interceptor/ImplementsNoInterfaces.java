

package org.springframework.transaction.interceptor;

import org.springframework.tests.sample.beans.TestBean;

/**
 * Test for CGLIB proxying that implements no interfaces
 * and has one dependency.
 *
 * @author Rod Johnson
 */
public class ImplementsNoInterfaces {

	private TestBean testBean;

	public void setDependency(TestBean testBean) {
		this.testBean = testBean;
	}

	public String getName() {
		return testBean.getName();
	}

	public void setName(String name) {
		testBean.setName(name);
	}

}
