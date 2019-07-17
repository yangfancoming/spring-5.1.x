

package org.springframework.tests.sample.beans;

/**

 * @since 07.03.2006
 */
public class FieldAccessBean {

	public String name;

	protected int age;

	private TestBean spouse;

	public String getName() {
		return name;
	}

	public int getAge() {
		return age;
	}

	public TestBean getSpouse() {
		return spouse;
	}

}
