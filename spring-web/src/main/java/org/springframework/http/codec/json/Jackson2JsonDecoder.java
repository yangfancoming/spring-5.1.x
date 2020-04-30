

package org.springframework.http.codec.json;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.util.MimeType;

/**
 * Decode a byte stream into JSON and convert to Object's with Jackson 2.9,
 * leveraging non-blocking parsing.
 *
 * @author Sebastien Deleuze
 *
 * @since 5.0
 * @see Jackson2JsonEncoder
 */
public class Jackson2JsonDecoder extends AbstractJackson2Decoder {

	public Jackson2JsonDecoder() {
		super(Jackson2ObjectMapperBuilder.json().build());
	}

	public Jackson2JsonDecoder(ObjectMapper mapper, MimeType... mimeTypes) {
		super(mapper, mimeTypes);
	}

}
