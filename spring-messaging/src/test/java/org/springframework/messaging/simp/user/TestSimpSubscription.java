

package org.springframework.messaging.simp.user;

import org.springframework.util.ObjectUtils;

/**
 *
 */
public class TestSimpSubscription implements SimpSubscription {

	private final String destination;

	private final String id;

	private TestSimpSession session;


	public TestSimpSubscription(String id, String destination) {
		this.destination = destination;
		this.id = id;
	}


	@Override
	public String getId() {
		return id;
	}

	@Override
	public TestSimpSession getSession() {
		return this.session;
	}

	public void setSession(TestSimpSession session) {
		this.session = session;
	}

	@Override
	public String getDestination() {
		return destination;
	}


	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof SimpSubscription)) {
			return false;
		}
		SimpSubscription otherSubscription = (SimpSubscription) other;
		return (ObjectUtils.nullSafeEquals(getSession(), otherSubscription.getSession()) &&
				this.id.equals(otherSubscription.getId()));
	}

	@Override
	public int hashCode() {
		return this.id.hashCode() * 31 + ObjectUtils.nullSafeHashCode(getSession());
	}

	@Override
	public String toString() {
		return "destination=" + this.destination;
	}

}
