

package org.springframework.messaging.converter;

import org.springframework.lang.Nullable;
import org.springframework.messaging.MessageHeaders;
import org.springframework.util.MimeType;

/**
 * A default {@link ContentTypeResolver} that checks the
 * {@link MessageHeaders#CONTENT_TYPE} header or falls back to a default value.
 *
 * The header value is expected to be a {@link org.springframework.util.MimeType}
 * or a {@code String} that can be parsed into a {@code MimeType}.
 *
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public class DefaultContentTypeResolver implements ContentTypeResolver {

	@Nullable
	private MimeType defaultMimeType;


	/**
	 * Set the default MIME type to use when there is no
	 * {@link MessageHeaders#CONTENT_TYPE} header present.
	 * This property does not have a default value.
	 */
	public void setDefaultMimeType(@Nullable MimeType defaultMimeType) {
		this.defaultMimeType = defaultMimeType;
	}

	/**
	 * Return the default MIME type to use if no
	 * {@link MessageHeaders#CONTENT_TYPE} header is present.
	 */
	@Nullable
	public MimeType getDefaultMimeType() {
		return this.defaultMimeType;
	}


	@Override
	@Nullable
	public MimeType resolve(@Nullable MessageHeaders headers) {
		if (headers == null || headers.get(MessageHeaders.CONTENT_TYPE) == null) {
			return this.defaultMimeType;
		}
		Object value = headers.get(MessageHeaders.CONTENT_TYPE);
		if (value == null) {
			return null;
		}
		else if (value instanceof MimeType) {
			return (MimeType) value;
		}
		else if (value instanceof String) {
			return MimeType.valueOf((String) value);
		}
		else {
			throw new IllegalArgumentException(
					"Unknown type for contentType header value: " + value.getClass());
		}
	}

	@Override
	public String toString() {
		return "DefaultContentTypeResolver[" + "defaultMimeType=" + this.defaultMimeType + "]";
	}

}
