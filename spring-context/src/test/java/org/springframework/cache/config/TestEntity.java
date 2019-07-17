

package org.springframework.cache.config;

import org.springframework.util.ObjectUtils;

/**
 * Simple test entity for use with caching tests.

 */
public class TestEntity {

	private Long id;

	public Long getId() {
		return this.id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@Override
	public int hashCode() {
		return ObjectUtils.nullSafeHashCode(this.id);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (obj instanceof TestEntity) {
			return ObjectUtils.nullSafeEquals(this.id, ((TestEntity) obj).id);
		}
		return false;
	}
}
