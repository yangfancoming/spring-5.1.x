

package org.springframework.web.servlet.mvc.condition;

import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * A contract for {@code "name!=value"} style expression used to specify request
 * parameters and request header conditions in {@code @RequestMapping}.
 * @since 3.1
 * @param <T> the value type
 * @see RequestMapping#params()
 * @see RequestMapping#headers()
 */
public interface NameValueExpression<T> {

	String getName();

	@Nullable
	T getValue();

	boolean isNegated();

}
