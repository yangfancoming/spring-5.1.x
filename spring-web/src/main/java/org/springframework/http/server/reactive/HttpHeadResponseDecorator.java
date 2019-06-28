

package org.springframework.http.server.reactive;

import java.util.function.BiFunction;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;

/**
 * {@link ServerHttpResponse} decorator for HTTP HEAD requests.
 *
 * @author Rossen Stoyanchev
 * @since 5.0
 */
public class HttpHeadResponseDecorator extends ServerHttpResponseDecorator {


	public HttpHeadResponseDecorator(ServerHttpResponse delegate) {
		super(delegate);
	}


	/**
	 * Apply {@link Flux#reduce(Object, BiFunction) reduce} on the body, count
	 * the number of bytes produced, release data buffers without writing, and
	 * set the {@literal Content-Length} header.
	 */
	@Override
	public final Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
		return Flux.from(body)
				.reduce(0, (current, buffer) -> {
					int next = current + buffer.readableByteCount();
					DataBufferUtils.release(buffer);
					return next;
				})
				.doOnNext(count -> getHeaders().setContentLength(count))
				.then();
	}

	/**
	 * Invoke {@link #setComplete()} without writing.
	 * <p>RFC 7302 allows HTTP HEAD response without content-length and it's not
	 * something that can be computed on a streaming response.
	 */
	@Override
	public final Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body) {
		// Not feasible to count bytes on potentially streaming response.
		// RFC 7302 allows HEAD without content-length.
		return setComplete();
	}

}
