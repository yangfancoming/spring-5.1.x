

package org.springframework.messaging.handler.invocation;

import reactor.core.publisher.Mono;

import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.util.Assert;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.MonoToListenableFutureAdapter;

/**
 * Support for single-value reactive types (like {@code Mono} or {@code Single})
 * as a return value type.
 *
 * @author Sebastien Deleuze
 * @since 5.1
 */
public class ReactiveReturnValueHandler extends AbstractAsyncReturnValueHandler {

	private final ReactiveAdapterRegistry adapterRegistry;


	public ReactiveReturnValueHandler() {
		this(ReactiveAdapterRegistry.getSharedInstance());
	}

	public ReactiveReturnValueHandler(ReactiveAdapterRegistry adapterRegistry) {
		this.adapterRegistry = adapterRegistry;
	}


	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		return (this.adapterRegistry.getAdapter(returnType.getParameterType()) != null);
	}

	@Override
	public boolean isAsyncReturnValue(Object returnValue, MethodParameter returnType) {
		ReactiveAdapter adapter = this.adapterRegistry.getAdapter(returnType.getParameterType(), returnValue);
		return (adapter != null && !adapter.isMultiValue() && !adapter.isNoValue());
	}

	@Override
	public ListenableFuture<?> toListenableFuture(Object returnValue, MethodParameter returnType) {
		ReactiveAdapter adapter = this.adapterRegistry.getAdapter(returnType.getParameterType(), returnValue);
		Assert.state(adapter != null, () -> "No ReactiveAdapter found for " + returnType.getParameterType());
		return new MonoToListenableFutureAdapter<>(Mono.from(adapter.toPublisher(returnValue)));
	}

}
