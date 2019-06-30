

package org.springframework.test.context.junit.jupiter.comics;

/**
 * Demo class for tests.
 *
 * @author Sam Brannen
 * @since 5.0
 */
public abstract class Character {

	private final String name;

	Character(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}
}
