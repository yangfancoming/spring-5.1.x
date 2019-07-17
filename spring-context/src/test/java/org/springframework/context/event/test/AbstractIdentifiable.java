

package org.springframework.context.event.test;

import java.util.UUID;


public abstract class AbstractIdentifiable implements Identifiable {

	private final String id;

	public AbstractIdentifiable() {
		this.id = UUID.randomUUID().toString();
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		AbstractIdentifiable that = (AbstractIdentifiable) o;

		return this.id.equals(that.id);
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

}
