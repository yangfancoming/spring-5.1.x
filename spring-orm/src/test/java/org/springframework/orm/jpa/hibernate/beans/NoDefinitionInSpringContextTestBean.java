

package org.springframework.orm.jpa.hibernate.beans;

public class NoDefinitionInSpringContextTestBean extends TestBean {

	private NoDefinitionInSpringContextTestBean() {
		throw new AssertionError("Unexpected call to the default constructor. " +
				"Is Spring trying to instantiate this class by itself, even though it should delegate to the fallback producer?"
		);
	}

	/*
	 * Expect instantiation through a non-default constructor, just to be sure that Spring will fail if it tries to instantiate it,
	 * and will subsequently delegate to the fallback bean instance producer.
 	 */
	public NoDefinitionInSpringContextTestBean(String name, BeanSource source) {
		setName(name);
		setSource(source);
	}

}
