

package org.springframework.http.codec.support;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import org.springframework.core.codec.Decoder;
import org.springframework.core.codec.Encoder;
import org.springframework.http.codec.ClientCodecConfigurer;
import org.springframework.http.codec.EncoderHttpMessageWriter;
import org.springframework.http.codec.FormHttpMessageWriter;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ServerSentEventHttpMessageReader;
import org.springframework.http.codec.multipart.MultipartHttpMessageWriter;
import org.springframework.lang.Nullable;

/**
 * Default implementation of {@link ClientCodecConfigurer.ClientDefaultCodecs}.
 *
 * @author Rossen Stoyanchev
 */
class ClientDefaultCodecsImpl extends BaseDefaultCodecs implements ClientCodecConfigurer.ClientDefaultCodecs {

	@Nullable
	private DefaultMultipartCodecs multipartCodecs;

	@Nullable
	private Decoder<?> sseDecoder;

	@Nullable
	private Supplier<List<HttpMessageWriter<?>>> partWritersSupplier;


	/**
	 * Set a supplier for part writers to use when
	 * {@link #multipartCodecs()} are not explicitly configured.
	 * That's the same set of writers as for general except for the multipart
	 * writer itself.
	 */
	void setPartWritersSupplier(Supplier<List<HttpMessageWriter<?>>> supplier) {
		this.partWritersSupplier = supplier;
	}


	@Override
	public ClientCodecConfigurer.MultipartCodecs multipartCodecs() {
		if (this.multipartCodecs == null) {
			this.multipartCodecs = new DefaultMultipartCodecs();
		}
		return this.multipartCodecs;
	}

	@Override
	public void serverSentEventDecoder(Decoder<?> decoder) {
		this.sseDecoder = decoder;
	}


	@Override
	protected void extendObjectReaders(List<HttpMessageReader<?>> objectReaders) {
		objectReaders.add(new ServerSentEventHttpMessageReader(getSseDecoder()));
	}

	@Nullable
	private Decoder<?> getSseDecoder() {
		return (this.sseDecoder != null ? this.sseDecoder : jackson2Present ? getJackson2JsonDecoder() : null);
	}

	@Override
	protected void extendTypedWriters(List<HttpMessageWriter<?>> typedWriters) {

		FormHttpMessageWriter formWriter = new FormHttpMessageWriter();
		formWriter.setEnableLoggingRequestDetails(isEnableLoggingRequestDetails());

		MultipartHttpMessageWriter multipartWriter = new MultipartHttpMessageWriter(getPartWriters(), formWriter);
		multipartWriter.setEnableLoggingRequestDetails(isEnableLoggingRequestDetails());

		typedWriters.add(multipartWriter);
	}

	private List<HttpMessageWriter<?>> getPartWriters() {
		if (this.multipartCodecs != null) {
			return this.multipartCodecs.getWriters();
		}
		else if (this.partWritersSupplier != null) {
			return this.partWritersSupplier.get();
		}
		else {
			return Collections.emptyList();
		}
	}


	/**
	 * Default implementation of {@link ClientCodecConfigurer.MultipartCodecs}.
	 */
	private static class DefaultMultipartCodecs implements ClientCodecConfigurer.MultipartCodecs {

		private final List<HttpMessageWriter<?>> writers = new ArrayList<>();

		@Override
		public ClientCodecConfigurer.MultipartCodecs encoder(Encoder<?> encoder) {
			writer(new EncoderHttpMessageWriter<>(encoder));
			return this;
		}

		@Override
		public ClientCodecConfigurer.MultipartCodecs writer(HttpMessageWriter<?> writer) {
			this.writers.add(writer);
			return this;
		}

		List<HttpMessageWriter<?>> getWriters() {
			return this.writers;
		}
	}

}
