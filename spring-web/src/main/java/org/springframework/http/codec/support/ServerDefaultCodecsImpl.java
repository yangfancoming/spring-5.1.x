
package org.springframework.http.codec.support;

import java.util.List;

import org.springframework.core.codec.Encoder;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.ServerSentEventHttpMessageWriter;
import org.springframework.http.codec.multipart.MultipartHttpMessageReader;
import org.springframework.http.codec.multipart.SynchronossPartHttpMessageReader;
import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/**
 * Default implementation of {@link ServerCodecConfigurer.ServerDefaultCodecs}.
 *
 * @author Rossen Stoyanchev
 */
class ServerDefaultCodecsImpl extends BaseDefaultCodecs implements ServerCodecConfigurer.ServerDefaultCodecs {

	private static final boolean synchronossMultipartPresent =
			ClassUtils.isPresent("org.synchronoss.cloud.nio.multipart.NioMultipartParser",
					DefaultServerCodecConfigurer.class.getClassLoader());


	@Nullable
	private Encoder<?> sseEncoder;


	@Override
	public void serverSentEventEncoder(Encoder<?> encoder) {
		this.sseEncoder = encoder;
	}


	@Override
	protected void extendTypedReaders(List<HttpMessageReader<?>> typedReaders) {
		if (synchronossMultipartPresent) {
			boolean enable = isEnableLoggingRequestDetails();

			SynchronossPartHttpMessageReader partReader = new SynchronossPartHttpMessageReader();
			partReader.setEnableLoggingRequestDetails(enable);
			typedReaders.add(partReader);

			MultipartHttpMessageReader reader = new MultipartHttpMessageReader(partReader);
			reader.setEnableLoggingRequestDetails(enable);
			typedReaders.add(reader);
		}
	}

	@Override
	protected void extendObjectWriters(List<HttpMessageWriter<?>> objectWriters) {
		objectWriters.add(new ServerSentEventHttpMessageWriter(getSseEncoder()));
	}

	@Nullable
	private Encoder<?> getSseEncoder() {
		return this.sseEncoder != null ? this.sseEncoder : jackson2Present ? getJackson2JsonEncoder() : null;
	}

}
