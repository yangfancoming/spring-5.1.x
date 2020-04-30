

package org.springframework.messaging.core;

import org.springframework.lang.Nullable;
import org.springframework.messaging.MessagingException;

/**
 * Thrown by a {@link DestinationResolver} when it cannot resolve a destination.
 *
 * @author Mark Fisher
 *
 * @since 4.0
 */
@SuppressWarnings("serial")
public class DestinationResolutionException extends MessagingException {

	public DestinationResolutionException(String description) {
		super(description);
	}

	public DestinationResolutionException(@Nullable String description, @Nullable Throwable cause) {
		super(description, cause);
	}

}
