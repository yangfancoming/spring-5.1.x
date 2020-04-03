

package org.springframework.tests.sample.beans;

/**
 * @author Rob Harrop
 * @since 2.0
 */
public class Pet {

	private String name;

	public Pet(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		final Pet pet = (Pet) o;

		if (name != null ? !name.equals(pet.name) : pet.name != null) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return (name != null ? name.hashCode() : 0);
	}

}
