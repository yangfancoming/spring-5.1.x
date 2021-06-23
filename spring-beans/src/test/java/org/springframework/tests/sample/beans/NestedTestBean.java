

package org.springframework.tests.sample.beans;

/**
 * Simple nested test bean used for testing bean factories, AOP framework etc.
 * @since 30.09.2003
 */
public class NestedTestBean implements INestedTestBean {

	private String company = "";

	public NestedTestBean() {
		System.out.println("NestedTestBean 无参构造函数 执行");
	}

	public NestedTestBean(String company) {
		System.out.println("NestedTestBean 单参构造函数 执行");
		setCompany(company);
	}

	public void setCompany(String company) {
		this.company = (company != null ? company : "");
	}

	@Override
	public String getCompany() {
		return company;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof NestedTestBean)) {
			return false;
		}
		NestedTestBean ntb = (NestedTestBean) obj;
		return this.company.equals(ntb.company);
	}

	@Override
	public int hashCode() {
		return this.company.hashCode();
	}

	@Override
	public String toString() {
		return "NestedTestBean: " + this.company;
	}
}
