

package org.springframework.messaging.simp.user;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Rossen Stoyanchev
 */
public class TestSimpSession implements SimpSession {

	private final String id;

	private TestSimpUser user;

	private final Set<SimpSubscription> subscriptions = new HashSet<>();


	public TestSimpSession(String id) {
		this.id = id;
	}


	@Override
	public String getId() {
		return id;
	}

	@Override
	public TestSimpUser getUser() {
		return user;
	}

	public void setUser(TestSimpUser user) {
		this.user = user;
	}

	@Override
	public Set<SimpSubscription> getSubscriptions() {
		return subscriptions;
	}

	public void addSubscriptions(TestSimpSubscription... subscriptions) {
		for (TestSimpSubscription subscription : subscriptions) {
			subscription.setSession(this);
			this.subscriptions.add(subscription);
		}
	}


	@Override
	public boolean equals(Object other) {
		return (this == other || (other instanceof SimpSession && this.id.equals(((SimpSession) other).getId())));
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

	@Override
	public String toString() {
		return "id=" + this.id + ", subscriptions=" + this.subscriptions;
	}

}
