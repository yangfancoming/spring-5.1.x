

package org.springframework.web.reactive.result.condition;

import org.springframework.http.MediaType;

/**
 * A contract for media type expressions (e.g. "text/plain", "!text/plain") as
 * defined in the {@code @RequestMapping} annotation for "consumes" and
 * "produces" conditions.
 *
 *
 * @since 5.0
 */
public interface MediaTypeExpression {

	MediaType getMediaType();

	boolean isNegated();

}
