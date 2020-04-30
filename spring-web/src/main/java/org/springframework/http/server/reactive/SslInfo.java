

package org.springframework.http.server.reactive;

import java.security.cert.X509Certificate;

import org.springframework.lang.Nullable;

/**
 * A holder for SSL session information.
 *
 *
 * @since 5.0.2
 */
public interface SslInfo {

	/**
	 * Return the SSL session id, if any.
	 */
	@Nullable
	String getSessionId();

	/**
	 * Return SSL certificates associated with the request, if any.
	 */
	@Nullable
	X509Certificate[] getPeerCertificates();

}
