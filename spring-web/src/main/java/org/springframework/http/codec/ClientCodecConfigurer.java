

package org.springframework.http.codec;

import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;

/**
 * Extension of {@link CodecConfigurer} for HTTP message reader and writer
 * options relevant on the client side.
 *
 * <p>HTTP message readers for the following are registered by default:
 * <ul>{@code byte[]}
 * <li>{@link java.nio.ByteBuffer}
 * <li>{@link org.springframework.core.io.buffer.DataBuffer DataBuffer}
 * <li>{@link org.springframework.core.io.Resource Resource}
 * <li>{@link String}
 * <li>{@link org.springframework.util.MultiValueMap
 * MultiValueMap&lt;String,String&gt;} for form data
 * <li>JSON and Smile, if Jackson is present
 * <li>XML, if JAXB2 is present
 * <li>Server-Sent Events
 * </ul>
 *
 * <p>HTTP message writers registered by default:
 * <ul>{@code byte[]}
 * <li>{@link java.nio.ByteBuffer}
 * <li>{@link org.springframework.core.io.buffer.DataBuffer DataBuffer}
 * <li>{@link org.springframework.core.io.Resource Resource}
 * <li>{@link String}
 * <li>{@link org.springframework.util.MultiValueMap
 * MultiValueMap&lt;String,String&gt;} for form data
 * <li>{@link org.springframework.util.MultiValueMap
 * MultiValueMap&lt;String,Object&gt;} for multipart data
 * <li>JSON and Smile, if Jackson is present
 * <li>XML, if JAXB2 is present
 * </ul>
 *
 * @author Rossen Stoyanchev
 * @since 5.0
 */
public interface ClientCodecConfigurer extends CodecConfigurer {

	/**
	 * {@inheritDoc}
	 * <p>On the client side, built-in default also include customizations related
	 * to multipart readers and writers, as well as the decoder for SSE.
	 */
	@Override
	ClientDefaultCodecs defaultCodecs();


	/**
	 * Static factory method for a {@code ClientCodecConfigurer}.
	 */
	static ClientCodecConfigurer create() {
		return CodecConfigurerFactory.create(ClientCodecConfigurer.class);
	}


	/**
	 * {@link CodecConfigurer.DefaultCodecs} extension with extra client-side options.
	 */
	interface ClientDefaultCodecs extends DefaultCodecs {

		/**
		 * Configure encoders or writers for use with
		 * {@link org.springframework.http.codec.multipart.MultipartHttpMessageWriter
		 * MultipartHttpMessageWriter}.
		 */
		MultipartCodecs multipartCodecs();

		/**
		 * Configure the {@code Decoder} to use for Server-Sent Events.
		 * <p>By default if this is not set, and Jackson is available, the
		 * {@link #jackson2JsonDecoder} override is used instead. Use this property
		 * if you want to further customize the SSE decoder.
		 * @param decoder the decoder to use
		 */
		void serverSentEventDecoder(Decoder<?> decoder);
	}


	/**
	 * Registry and container for multipart HTTP message writers.
	 */
	interface MultipartCodecs {

		/**
		 * Add a Part {@code Encoder}, internally wrapped with
		 * {@link EncoderHttpMessageWriter}.
		 * @param encoder the encoder to add
		 */
		MultipartCodecs encoder(Encoder<?> encoder);

		/**
		 * Add a Part {@link HttpMessageWriter}. For writers of type
		 * {@link EncoderHttpMessageWriter} consider using the shortcut
		 * {@link #encoder(Encoder)} instead.
		 * @param writer the writer to add
		 */
		MultipartCodecs writer(HttpMessageWriter<?> writer);
	}

}
