

package org.springframework.http;

import java.util.function.Supplier;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;

/**
 * A "reactive" HTTP output message that accepts output as a {@link Publisher}.
 *
 * Typically implemented by an HTTP request on the client-side or an
 * HTTP response on the server-side.
 *
 * @author Arjen Poutsma
 * @author Sebastien Deleuze
 * @since 5.0
 */
public interface ReactiveHttpOutputMessage extends HttpMessage {

	/**
	 * Return a {@link DataBufferFactory} that can be used to create the body.
	 * @return a buffer factory
	 * @see #writeWith(Publisher)
	 */
	DataBufferFactory bufferFactory();

	/**
	 * Register an action to apply just before the HttpOutputMessage is committed.
	 * <strong>Note:</strong> the supplied action must be properly deferred,
	 * e.g. via {@link Mono#defer} or {@link Mono#fromRunnable}, to ensure it's
	 * executed in the right order, relative to other actions.
	 * @param action the action to apply
	 */
	void beforeCommit(Supplier<? extends Mono<Void>> action);

	/**
	 * Whether the HttpOutputMessage is committed.
	 */
	boolean isCommitted();

	/**
	 * Use the given {@link Publisher} to write the body of the message to the
	 * underlying HTTP layer.
	 * @param body the body content publisher
	 * @return a {@link Mono} that indicates completion or error
	 */

	Mono<Void> writeWith(Publisher<? extends DataBuffer> body);

	/**
	 * Use the given {@link Publisher} of {@code Publishers} to write the body
	 * of the HttpOutputMessage to the underlying HTTP layer, flushing after
	 * each {@code Publisher<DataBuffer>}.
	 * @param body the body content publisher
	 * @return a {@link Mono} that indicates completion or error
	 */
	Mono<Void> writeAndFlushWith(Publisher<? extends Publisher<? extends DataBuffer>> body);

	/**
	 * Indicate that message handling is complete, allowing for any cleanup or
	 * end-of-processing tasks to be performed such as applying header changes
	 * made via {@link #getHeaders()} to the underlying HTTP message (if not
	 * applied already).
	 * This method should be automatically invoked at the end of message
	 * processing so typically applications should not have to invoke it.
	 * If invoked multiple times it should have no side effects.
	 * @return a {@link Mono} that indicates completion or error
	 */
	Mono<Void> setComplete();

}
