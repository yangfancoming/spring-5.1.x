

package org.springframework.web.reactive.result.condition;

import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;
import org.springframework.web.server.ServerWebExchange;

/**
 * Supports "name=value" style expressions as described in:
 * {@link org.springframework.web.bind.annotation.RequestMapping#params()} and
 * {@link org.springframework.web.bind.annotation.RequestMapping#headers()}.
 *
 *
 * @since 5.0
 * @param <T> the value type
 */
abstract class AbstractNameValueExpression<T> implements NameValueExpression<T> {

	protected final String name;

	@Nullable
	protected final T value;

	protected final boolean isNegated;


	AbstractNameValueExpression(String expression) {
		int separator = expression.indexOf('=');
		if (separator == -1) {
			this.isNegated = expression.startsWith("!");
			this.name = (this.isNegated ? expression.substring(1) : expression);
			this.value = null;
		}
		else {
			this.isNegated = (separator > 0) && (expression.charAt(separator - 1) == '!');
			this.name = (this.isNegated ? expression.substring(0, separator - 1) : expression.substring(0, separator));
			this.value = parseValue(expression.substring(separator + 1));
		}
	}


	@Override
	public String getName() {
		return this.name;
	}

	@Override
	@Nullable
	public T getValue() {
		return this.value;
	}

	@Override
	public boolean isNegated() {
		return this.isNegated;
	}

	public final boolean match(ServerWebExchange exchange) {
		boolean isMatch;
		if (this.value != null) {
			isMatch = matchValue(exchange);
		}
		else {
			isMatch = matchName(exchange);
		}
		return this.isNegated != isMatch;
	}


	protected abstract boolean isCaseSensitiveName();

	protected abstract T parseValue(String valueExpression);

	protected abstract boolean matchName(ServerWebExchange exchange);

	protected abstract boolean matchValue(ServerWebExchange exchange);


	@Override
	public boolean equals(@Nullable Object other) {
		if (this == other) {
			return true;
		}
		if (other == null || getClass() != other.getClass()) {
			return false;
		}
		AbstractNameValueExpression<?> that = (AbstractNameValueExpression<?>) other;
		return ((isCaseSensitiveName() ? this.name.equals(that.name) : this.name.equalsIgnoreCase(that.name)) &&
				ObjectUtils.nullSafeEquals(this.value, that.value) && this.isNegated == that.isNegated);
	}

	@Override
	public int hashCode() {
		int result = (isCaseSensitiveName() ? this.name : this.name.toLowerCase()).hashCode();
		result = 31 * result + ObjectUtils.nullSafeHashCode(this.value);
		result = 31 * result + (this.isNegated ? 1 : 0);
		return result;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (this.value != null) {
			builder.append(this.name);
			if (this.isNegated) {
				builder.append('!');
			}
			builder.append('=');
			builder.append(this.value);
		}
		else {
			if (this.isNegated) {
				builder.append('!');
			}
			builder.append(this.name);
		}
		return builder.toString();
	}

}
