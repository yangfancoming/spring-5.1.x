

package org.springframework.tests.sample.beans;

import java.io.Serializable;

import org.springframework.util.ObjectUtils;

/**
 * Serializable implementation of the Person interface.
 *
 * @author Rod Johnson
 */
@SuppressWarnings("serial")
public class SerializablePerson implements Person, Serializable {

	private String name;

	private int age;


	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public int getAge() {
		return age;
	}

	@Override
	public void setAge(int age) {
		this.age = age;
	}

	@Override
	public Object echo(Object o) throws Throwable {
		if (o instanceof Throwable) {
			throw (Throwable) o;
		}
		return o;
	}


	@Override
	public boolean equals(Object other) {
		if (!(other instanceof SerializablePerson)) {
			return false;
		}
		SerializablePerson p = (SerializablePerson) other;
		return p.age == age && ObjectUtils.nullSafeEquals(name, p.name);
	}

	@Override
	public int hashCode() {
		return SerializablePerson.class.hashCode();
	}

}
