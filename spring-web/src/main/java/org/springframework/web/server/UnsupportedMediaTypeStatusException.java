

package org.springframework.web.server;

import java.util.Collections;
import java.util.List;

import org.springframework.core.ResolvableType;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;

/**
 * Exception for errors that fit response status 415 (unsupported media type).
 *
 * @author Rossen Stoyanchev
 * @since 5.0
 */
@SuppressWarnings("serial")
public class UnsupportedMediaTypeStatusException extends ResponseStatusException {

	@Nullable
	private final MediaType contentType;

	private final List<MediaType> supportedMediaTypes;

	@Nullable
	private final ResolvableType bodyType;


	/**
	 * Constructor for when the specified Content-Type is invalid.
	 */
	public UnsupportedMediaTypeStatusException(@Nullable String reason) {
		super(HttpStatus.UNSUPPORTED_MEDIA_TYPE, reason);
		this.contentType = null;
		this.supportedMediaTypes = Collections.emptyList();
		this.bodyType = null;
	}

	/**
	 * Constructor for when the Content-Type can be parsed but is not supported.
	 */
	public UnsupportedMediaTypeStatusException(@Nullable MediaType contentType, List<MediaType> supportedTypes) {
		this(contentType, supportedTypes, null);
	}

	/**
	 * Constructor for when trying to encode from or decode to a specific Java type.
	 * @since 5.1
	 */
	public UnsupportedMediaTypeStatusException(@Nullable MediaType contentType, List<MediaType> supportedTypes,
			@Nullable ResolvableType bodyType) {

		super(HttpStatus.UNSUPPORTED_MEDIA_TYPE, initReason(contentType, bodyType));
		this.contentType = contentType;
		this.supportedMediaTypes = Collections.unmodifiableList(supportedTypes);
		this.bodyType = bodyType;
	}

	private static String initReason(@Nullable MediaType contentType, @Nullable ResolvableType bodyType) {
		return "Content type '" + (contentType != null ? contentType : "") + "' not supported" +
				(bodyType != null ? " for bodyType=" + bodyType.toString() : "");
	}


	/**
	 * Return the request Content-Type header if it was parsed successfully,
	 * or {@code null} otherwise.
	 */
	@Nullable
	public MediaType getContentType() {
		return this.contentType;
	}

	/**
	 * Return the list of supported content types in cases when the Content-Type
	 * header is parsed but not supported, or an empty list otherwise.
	 */
	public List<MediaType> getSupportedMediaTypes() {
		return this.supportedMediaTypes;
	}

	/**
	 * Return the body type in the context of which this exception was generated.
	 * This is applicable when the exception was raised as a result trying to
	 * encode from or decode to a specific Java type.
	 * @return the body type, or {@code null} if not available
	 * @since 5.1
	 */
	@Nullable
	public ResolvableType getBodyType() {
		return this.bodyType;
	}

}
