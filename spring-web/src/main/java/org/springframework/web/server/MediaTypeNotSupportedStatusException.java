

package org.springframework.web.server;

import java.util.Collections;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

/**
 * Exception for errors that fit response status 415 (unsupported media type).
 *
 * @author Rossen Stoyanchev
 * @since 5.0
 */
@SuppressWarnings("serial")
public class MediaTypeNotSupportedStatusException extends ResponseStatusException {

	private final List<MediaType> supportedMediaTypes;


	/**
	 * Constructor for when the Content-Type is invalid.
	 */
	public MediaTypeNotSupportedStatusException(String reason) {
		super(HttpStatus.UNSUPPORTED_MEDIA_TYPE, reason);
		this.supportedMediaTypes = Collections.emptyList();
	}

	/**
	 * Constructor for when the Content-Type is not supported.
	 */
	public MediaTypeNotSupportedStatusException(List<MediaType> supportedMediaTypes) {
		super(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Unsupported media type", null);
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
