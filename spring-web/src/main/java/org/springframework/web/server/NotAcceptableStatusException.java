

package org.springframework.web.server;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/**
 * Exception for errors that fit response status 406 (not acceptable).
 *
 * @author Rossen Stoyanchev
 * @since 5.0
 */
@SuppressWarnings("serial")
public class NotAcceptableStatusException extends ResponseStatusException {

	private final List<MediaType> supportedMediaTypes;


	/**
	 * Constructor for when the requested Content-Type is invalid.
	 */
	public NotAcceptableStatusException(String reason) {
		super(HttpStatus.NOT_ACCEPTABLE, reason);
		this.supportedMediaTypes = Collections.emptyList();
	}

	/**
	 * Constructor for when the requested Content-Type is not supported.
	 */
	public NotAcceptableStatusException(List<MediaType> supportedMediaTypes) {
		super(HttpStatus.NOT_ACCEPTABLE, "Could not find acceptable representation");
		this.supportedMediaTypes = Collections.unmodifiableList(supportedMediaTypes);
	}


	/**
	 * Return the list of supported content types in cases when the Accept
	 * header is parsed but not supported, or an empty list otherwise.
	 */
	public List<MediaType> getSupportedMediaTypes() {
		return this.supportedMediaTypes;
	}

}
