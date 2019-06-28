

package org.springframework.tests.sample.beans;

public class Employee extends TestBean {

	private String co;

	public Employee() {
	}

	public Employee(String name) {
		super(name);
	}

	public String getCompany() {
		return co;
	}

	public void setCompany(String co) {
		this.co = co;
	}

}
