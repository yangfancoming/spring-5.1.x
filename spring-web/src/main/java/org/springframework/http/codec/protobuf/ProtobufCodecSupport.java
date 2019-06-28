

package org.springframework.http.codec.protobuf;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.lang.Nullable;
import org.springframework.util.MimeType;

/**
 * Base class providing support methods for Protobuf encoding and decoding.
 *
 * @author Sebastien Deleuze
 * @since 5.1
 */
public abstract class ProtobufCodecSupport {

	static final List<MimeType> MIME_TYPES = Collections.unmodifiableList(
			Arrays.asList(
					new MimeType("application", "x-protobuf"),
					new MimeType("application", "octet-stream")));

	static final String DELIMITED_KEY = "delimited";

	static final String DELIMITED_VALUE = "true";


	protected boolean supportsMimeType(@Nullable MimeType mimeType) {
		return (mimeType == null || MIME_TYPES.stream().anyMatch(m -> m.isCompatibleWith(mimeType)));
	}

	protected List<MimeType> getMimeTypes() {
		return MIME_TYPES;
	}

}
