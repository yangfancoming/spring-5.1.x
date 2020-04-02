

package org.springframework.web.reactive.result.view;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import reactor.core.publisher.Mono;

import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.web.reactive.HandlerResult;
import org.springframework.web.server.ServerWebExchange;

/**
 * Contract to render {@link HandlerResult} to the HTTP response.
 *
 * In contrast to an {@link org.springframework.core.codec.Encoder Encoder}
 * which is a singleton and encodes any object of a given type, a {@code View}
 * is typically selected by name and resolved using a {@link ViewResolver}
 * which may for example match it to an HTML template. Furthermore a {@code View}
 * may render based on multiple attributes contained in the model.
 *
 * A {@code View} can also choose to select an attribute from the model use
 * any existing {@code Encoder} to render alternate media types.
 *
 * @author Rossen Stoyanchev
 * @since 5.0
 */
public interface View {

	/**
	 * The name of the exchange attribute that contains the
	 * {@link org.springframework.web.reactive.BindingContext BindingContext}
	 * for the request which can be used to create
	 * {@link org.springframework.validation.BindingResult BindingResult}
	 * instances for objects in to the model.
	 * Note: This attribute is not required and may not be present.
	 * @since 5.1.8
	 */
	String BINDING_CONTEXT_ATTRIBUTE = View.class.getName() + ".bindingContext";


	/**
	 * Return the list of media types this View supports, or an empty list.
	 */
	default List<MediaType> getSupportedMediaTypes() {
		return Collections.emptyList();
	}

	/**
	 * Whether this View does rendering by performing a redirect.
	 */
	default boolean isRedirectView() {
		return false;
	}

	/**
	 * Render the view based on the given {@link HandlerResult}. Implementations
	 * can access and use the model or only a specific attribute in it.
	 * @param model a Map with name Strings as keys and corresponding model
	 * objects as values (Map can also be {@code null} in case of empty model)
	 * @param contentType the content type selected to render with which should
	 * match one of the {@link #getSupportedMediaTypes() supported media types}.
	 * @param exchange the current exchange
	 * @return {@code Mono} to represent when and if rendering succeeds
	 */
	Mono<Void> render(@Nullable Map<String, ?> model, @Nullable MediaType contentType, ServerWebExchange exchange);

}
