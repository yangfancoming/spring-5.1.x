

package org.springframework.web.reactive.result.condition;

import org.springframework.lang.Nullable;

/**
 * A contract for {@code "name!=value"} style expression used to specify request
 * parameters and request header conditions in {@code @RequestMapping}.
 *
 * @author Rossen Stoyanchev
 * @since 5.0
 * @param <T> the value type
 */
public interface NameValueExpression<T> {

	String getName();

	@Nullable
	T getValue();

	boolean isNegated();

}
