

package org.springframework.test.util.subpackage;

import org.springframework.core.style.ToStringCreator;

/**
 * Concrete subclass of {@link PersistentEntity} representing a <em>person</em>
 * entity; intended for use in unit tests.
 *
 * @author Sam Brannen
 * @since 2.5
 */
public class PersonEntity extends PersistentEntity implements Person {

	protected String name;

	private int age;

	String eyeColor;

	boolean likesPets = false;

	private Number favoriteNumber;


	public String getName() {
		return this.name;
	}

	@SuppressWarnings("unused")
	private void setName(final String name) {
		this.name = name;
	}

	public int getAge() {
		return this.age;
	}

	protected void setAge(final int age) {
		this.age = age;
	}

	public String getEyeColor() {
		return this.eyeColor;
	}

	void setEyeColor(final String eyeColor) {
		this.eyeColor = eyeColor;
	}

	public boolean likesPets() {
		return this.likesPets;
	}

	protected void setLikesPets(final boolean likesPets) {
		this.likesPets = likesPets;
	}

	public Number getFavoriteNumber() {
		return this.favoriteNumber;
	}

	protected void setFavoriteNumber(Number favoriteNumber) {
		this.favoriteNumber = favoriteNumber;
	}

	@Override
	public String toString() {
		// @formatter:off
		return new ToStringCreator(this)
			.append("id", this.getId())
			.append("name", this.name)
			.append("age", this.age)
			.append("eyeColor", this.eyeColor)
			.append("likesPets", this.likesPets)
			.append("favoriteNumber", this.favoriteNumber)
			.toString();
		// @formatter:on
	}

}
