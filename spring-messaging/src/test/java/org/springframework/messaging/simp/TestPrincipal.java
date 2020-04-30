

package org.springframework.messaging.simp;

import java.security.Principal;

/**
 * An implementation of {@link Principal} for testing.
 *
 *
 */
public class TestPrincipal implements Principal {

	private String name;

	public TestPrincipal(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (!(obj instanceof TestPrincipal)) {
			return false;
		}
		TestPrincipal p = (TestPrincipal) obj;
		return this.name.equals(p.name);
	}

	@Override
	public int hashCode() {
		return this.name.hashCode();
	}

}
