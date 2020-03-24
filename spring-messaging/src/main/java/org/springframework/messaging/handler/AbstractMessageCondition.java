

package org.springframework.messaging.handler;

import java.util.Collection;
import java.util.Iterator;

import org.springframework.lang.Nullable;

/**
 * Base class for {@code MessageCondition's} that pre-declares abstract methods
 * {@link #getContent()} and {@link #getToStringInfix()} in order to provide
 * implementations of {@link #equals(Object)}, {@link #hashCode()}, and
 * {@link #toString()}.
 *
 * @author Rossen Stoyanchev
 * @since 4.0
 * @param <T> the kind of condition that this condition can be combined with or compared to
 */
public abstract class AbstractMessageCondition<T extends AbstractMessageCondition<T>> implements MessageCondition<T> {

	@Override
	public boolean equals(@Nullable Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || getClass() != other.getClass()) {
			return false;
		}
		return getContent().equals(((AbstractMessageCondition<?>) other).getContent());
	}

	@Override
	public int hashCode() {
		return getContent().hashCode();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder("[");
		for (Iterator<?> iterator = getContent().iterator(); iterator.hasNext();) {
			Object expression = iterator.next();
			builder.append(expression.toString());
			if (iterator.hasNext()) {
				builder.append(getToStringInfix());
			}
		}
		builder.append("]");
		return builder.toString();
	}


	/**
	 * Return the collection of objects the message condition is composed of
	 * (e.g. destination patterns), never {@code null}.
	 */
	protected abstract Collection<?> getContent();

	/**
	 * The notation to use when printing discrete items of content.
	 * For example " || " for URL patterns or " && " for param expressions.
	 */
	protected abstract String getToStringInfix();

}
