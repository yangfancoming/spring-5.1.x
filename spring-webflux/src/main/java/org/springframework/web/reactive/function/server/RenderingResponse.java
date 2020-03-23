

package org.springframework.web.reactive.function.server;

import java.util.Collection;
import java.util.Map;
import java.util.function.Consumer;

import reactor.core.publisher.Mono;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.lang.Nullable;
import org.springframework.util.MultiValueMap;

/**
 * Rendering-specific subtype of {@link ServerResponse} that exposes model and template data.
 *
 * @author Arjen Poutsma

 * @since 5.0
 */
public interface RenderingResponse extends ServerResponse {

	/**
	 * Return the name of the template to be rendered.
	 */
	String name();

	/**
	 * Return the unmodifiable model map.
	 */
	Map<String, Object> model();


	// Builder

	/**
	 * Create a builder with the template name, status code, headers and model of the given response.
	 * @param other the response to copy the values from
	 * @return the created builder
	 */
	static Builder from(RenderingResponse other) {
		return new DefaultRenderingResponseBuilder(other);
	}

	/**
	 * Create a builder with the given template name.
	 * @param name the name of the template to render
	 * @return the created builder
	 */
	static Builder create(String name) {
		return new DefaultRenderingResponseBuilder(name);
	}


	/**
	 * Defines a builder for {@code RenderingResponse}.
	 */
	interface Builder {

		/**
		 * Add the supplied attribute to the model using a
		 * {@linkplain org.springframework.core.Conventions#getVariableName generated name}.
		 * <p><em>Note: Empty {@link Collection Collections} are not added to
		 * the model when using this method because we cannot correctly determine
		 * the true convention name. View code should check for {@code null} rather
		 * than for empty collections.</em>
		 * @param attribute the model attribute value (never {@code null})
		 */
		Builder modelAttribute(Object attribute);

		/**
		 * Add the supplied attribute value under the supplied name.
		 * @param name the name of the model attribute (never {@code null})
		 * @param value the model attribute value (can be {@code null})
		 */
		Builder modelAttribute(String name, @Nullable Object value);

		/**
		 * Copy all attributes in the supplied array into the model,
		 * using attribute name generation for each element.
		 * @see #modelAttribute(Object)
		 */
		Builder modelAttributes(Object... attributes);

		/**
		 * Copy all attributes in the supplied {@code Collection} into the model,
		 * using attribute name generation for each element.
		 * @see #modelAttribute(Object)
		 */
		Builder modelAttributes(Collection<?> attributes);

		/**
		 * Copy all attributes in the supplied {@code Map} into the model.
		 * @see #modelAttribute(String, Object)
		 */
		Builder modelAttributes(Map<String, ?> attributes);

		/**
		 * Add the given header value(s) under the given name.
		 * @param headerName the header name
		 * @param headerValues the header value(s)
		 * @return this builder
		 * @see HttpHeaders#add(String, String)
		 */
		Builder header(String headerName, String... headerValues);

		/**
		 * Copy the given headers into the entity's headers map.
		 * @param headers the existing HttpHeaders to copy from
		 * @return this builder
		 * @see HttpHeaders#add(String, String)
		 */
		Builder headers(HttpHeaders headers);

		/**
		 * Set the HTTP status.
		 * @param status the response status
		 * @return this builder
		 */
		Builder status(HttpStatus status);

		/**
		 * Set the HTTP status.
		 * @param status the response status
		 * @return this builder
		 * @since 5.0.3
		 */
		Builder status(int status);

		/**
		 * Add the given cookie to the response.
		 * @param cookie the cookie to add
		 * @return this builder
		 */
		Builder cookie(ResponseCookie cookie);

		/**
		 * Manipulate this response's cookies with the given consumer. The
		 * cookies provided to the consumer are "live", so that the consumer can be used to
		 * {@linkplain MultiValueMap#set(Object, Object) overwrite} existing cookies,
		 * {@linkplain MultiValueMap#remove(Object) remove} cookies, or use any of the other
		 * {@link MultiValueMap} methods.
		 * @param cookiesConsumer a function that consumes the cookies
		 * @return this builder
		 */
		Builder cookies(Consumer<MultiValueMap<String, ResponseCookie>> cookiesConsumer);

		/**
		 * Build the response.
		 * @return the built response
		 */
		Mono<RenderingResponse> build();
	}

}
