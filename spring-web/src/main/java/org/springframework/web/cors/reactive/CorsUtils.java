

package org.springframework.web.cors.reactive;

import java.net.URI;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Utility class for CORS reactive request handling based on the
 * <a href="https://www.w3.org/TR/cors/">CORS W3C recommendation</a>.
 *
 * @author Sebastien Deleuze
 * @since 5.0
 */
public abstract class CorsUtils {

	/**
	 * Returns {@code true} if the request is a valid CORS one.
	 */
	public static boolean isCorsRequest(ServerHttpRequest request) {
		return (request.getHeaders().get(HttpHeaders.ORIGIN) != null);
	}

	/**
	 * Returns {@code true} if the request is a valid CORS pre-flight one.
	 */
	public static boolean isPreFlightRequest(ServerHttpRequest request) {
		return (request.getMethod() == HttpMethod.OPTIONS && isCorsRequest(request) &&
				request.getHeaders().get(HttpHeaders.ACCESS_CONTROL_REQUEST_METHOD) != null);
	}

	/**
	 * Check if the request is a same-origin one, based on {@code Origin}, and
	 * {@code Host} headers.
	 *
	 * <strong>Note:</strong> as of 5.1 this method ignores
	 * {@code "Forwarded"} and {@code "X-Forwarded-*"} headers that specify the
	 * client-originated address. Consider using the {@code ForwardedHeaderFilter}
	 * to extract and use, or to discard such headers.
	 *
	 * @return {@code true} if the request is a same-origin one, {@code false} in case
	 * of a cross-origin request
	 */
	public static boolean isSameOrigin(ServerHttpRequest request) {
		String origin = request.getHeaders().getOrigin();
		if (origin == null) {
			return true;
		}

		URI uri = request.getURI();
		String actualScheme = uri.getScheme();
		String actualHost = uri.getHost();
		int actualPort = getPort(uri.getScheme(), uri.getPort());
		Assert.notNull(actualScheme, "Actual request scheme must not be null");
		Assert.notNull(actualHost, "Actual request host must not be null");
		Assert.isTrue(actualPort != -1, "Actual request port must not be undefined");

		UriComponents originUrl = UriComponentsBuilder.fromOriginHeader(origin).build();
		return (actualScheme.equals(originUrl.getScheme()) &&
				actualHost.equals(originUrl.getHost()) &&
				actualPort == getPort(originUrl.getScheme(), originUrl.getPort()));
	}

	private static int getPort(@Nullable String scheme, int port) {
		if (port == -1) {
			if ("http".equals(scheme) || "ws".equals(scheme)) {
				port = 80;
			}
			else if ("https".equals(scheme) || "wss".equals(scheme)) {
				port = 443;
			}
		}
		return port;
	}

}
