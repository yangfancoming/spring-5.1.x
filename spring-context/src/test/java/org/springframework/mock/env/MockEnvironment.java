

package org.springframework.mock.env;

import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * Simple {@link ConfigurableEnvironment} implementation exposing
 * {@link #setProperty} and {@link #withProperty} methods for testing purposes.

 * @author Sam Brannen
 * @since 3.2
 * @see org.springframework.mock.env.MockPropertySource
 */
public class MockEnvironment extends AbstractEnvironment {

	private final MockPropertySource propertySource = new MockPropertySource();


	/**
	 * Create a new {@code MockEnvironment} with a single {@link MockPropertySource}.
	 */
	public MockEnvironment() {
		getPropertySources().addLast(this.propertySource);
	}


	/**
	 * Set a property on the underlying {@link MockPropertySource} for this environment.
	 */
	public void setProperty(String key, String value) {
		this.propertySource.setProperty(key, value);
	}

	/**
	 * Convenient synonym for {@link #setProperty} that returns the current instance.
	 * Useful for method chaining and fluent-style use.
	 * @return this {@link MockEnvironment} instance
	 * @see MockPropertySource#withProperty
	 */
	public MockEnvironment withProperty(String key, String value) {
		setProperty(key, value);
		return this;
	}

}
