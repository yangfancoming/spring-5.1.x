

package org.springframework.jdbc.core.test;

/**
 * @author Juergen Hoeller
 */
public class ExtendedPerson extends ConcretePerson {

	private Object someField;


	public Object getSomeField() {
		return someField;
	}

	public void setSomeField(Object someField) {
		this.someField = someField;
	}

}
