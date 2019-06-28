

package org.springframework.context.event.test;

import org.springframework.core.ResolvableType;
import org.springframework.core.ResolvableTypeProvider;

/**
 * A simple POJO that implements {@link ResolvableTypeProvider}.
 *
 * @author Stephane Nicoll
 */
public class GenericEventPojo<T> implements ResolvableTypeProvider {
	private final T value;

	public GenericEventPojo(T value) {
		this.value = value;
	}

	@Override
	public ResolvableType getResolvableType() {
		return ResolvableType.forClassWithGenerics(getClass(), ResolvableType.forInstance(this.value));
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		GenericEventPojo<?> that = (GenericEventPojo<?>) o;

		return this.value.equals(that.value);
	}

	@Override
	public int hashCode() {
		return this.value.hashCode();
	}
}
