

package org.springframework.web.reactive.result.method.annotation;

import java.util.List;

import reactor.core.publisher.Mono;

import org.springframework.core.MethodParameter;
import org.springframework.core.ReactiveAdapterRegistry;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.codec.HttpMessageWriter;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.reactive.HandlerResultHandler;
import org.springframework.web.reactive.accept.RequestedContentTypeResolver;
import org.springframework.web.server.ServerWebExchange;

/**
 * {@code HandlerResultHandler} that handles return values from methods annotated
 * with {@code @ResponseBody} writing to the body of the request or response with
 * an {@link HttpMessageWriter}.
 *
 * <p>By default the order for this result handler is set to 100. As it detects
 * the presence of {@code @ResponseBody} it should be ordered after result
 * handlers that look for a specific return type. Note however that this handler
 * does recognize and explicitly ignores the {@code ResponseEntity} return type.
 *
 * @author Rossen Stoyanchev
 * @author Stephane Maldini
 * @author Sebastien Deleuze
 * @author Arjen Poutsma
 * @since 5.0
 */
public class ResponseBodyResultHandler extends AbstractMessageWriterResultHandler implements HandlerResultHandler {

	/**
	 * Basic constructor with a default {@link ReactiveAdapterRegistry}.
	 * @param writers writers for serializing to the response body
	 * @param resolver to determine the requested content type
	 */
	public ResponseBodyResultHandler(List<HttpMessageWriter<?>> writers, RequestedContentTypeResolver resolver) {
		this(writers, resolver, ReactiveAdapterRegistry.getSharedInstance());
	}

	/**
	 * Constructor with an {@link ReactiveAdapterRegistry} instance.
	 * @param writers writers for serializing to the response body
	 * @param resolver to determine the requested content type
	 * @param registry for adaptation to reactive types
	 */
	public ResponseBodyResultHandler(List<HttpMessageWriter<?>> writers,
			RequestedContentTypeResolver resolver, ReactiveAdapterRegistry registry) {

		super(writers, resolver, registry);
		setOrder(100);
	}


	@Override
	public boolean supports(HandlerResult result) {
		MethodParameter returnType = result.getReturnTypeSource();
		Class<?> containingClass = returnType.getContainingClass();
		return (AnnotatedElementUtils.hasAnnotation(containingClass, ResponseBody.class) ||
				returnType.hasMethodAnnotation(ResponseBody.class));
	}

	@Override
	public Mono<Void> handleResult(ServerWebExchange exchange, HandlerResult result) {
		Object body = result.getReturnValue();
		MethodParameter bodyTypeParameter = result.getReturnTypeSource();
		return writeBody(body, bodyTypeParameter, exchange);
	}

}
