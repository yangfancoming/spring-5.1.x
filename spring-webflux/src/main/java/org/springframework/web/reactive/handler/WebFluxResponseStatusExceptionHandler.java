

package org.springframework.web.reactive.handler;

import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.server.handler.ResponseStatusExceptionHandler;

/**
 * Common WebFlux exception handler that detects instances of
 * {@link org.springframework.web.server.ResponseStatusException}
 * (inherited from the base class) as well as exceptions annotated with
 * {@link ResponseStatus @ResponseStatus} by determining the HTTP status
 * for them and updating the status of the response accordingly.
 *
 * <p>If the response is already committed, the error remains unresolved
 * and is propagated.
 *

 * @author Rossen Stoyanchev
 * @since 5.0.5
 */
public class WebFluxResponseStatusExceptionHandler extends ResponseStatusExceptionHandler {

	@Override
	@Nullable
	protected HttpStatus determineStatus(Throwable ex) {
		HttpStatus status = super.determineStatus(ex);
		if (status == null) {
			ResponseStatus ann = AnnotatedElementUtils.findMergedAnnotation(ex.getClass(), ResponseStatus.class);
			if (ann != null) {
				status = ann.code();
			}
		}
		return status;
	}

}
