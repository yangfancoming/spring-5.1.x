

package org.springframework.messaging.simp.user;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Rossen Stoyanchev
 */
public class TestSimpUser implements SimpUser {

	private final String name;

	private final Map<String, SimpSession> sessions = new HashMap<>();


	public TestSimpUser(String name) {
		this.name = name;
	}


	@Override
	public String getName() {
		return name;
	}

	@Override
	public Set<SimpSession> getSessions() {
		return new HashSet<>(this.sessions.values());
	}

	@Override
	public boolean hasSessions() {
		return !this.sessions.isEmpty();
	}

	@Override
	public SimpSession getSession(String sessionId) {
		return this.sessions.get(sessionId);
	}

	public void addSessions(TestSimpSession... sessions) {
		for (TestSimpSession session : sessions) {
			session.setUser(this);
			this.sessions.put(session.getId(), session);
		}
	}


	@Override
	public boolean equals(Object other) {
		return (this == other || (other instanceof SimpUser && this.name.equals(((SimpUser) other).getName())));
	}

	@Override
	public int hashCode() {
		return this.name.hashCode();
	}

	@Override
	public String toString() {
		return "name=" + this.name + ", sessions=" + this.sessions;
	}

}
