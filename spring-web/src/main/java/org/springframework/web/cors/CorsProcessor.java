

package org.springframework.web.cors;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;

/**
 * A strategy that takes a request and a {@link CorsConfiguration} and updates
 * the response.
 *
 * This component is not concerned with how a {@code CorsConfiguration} is
 * selected but rather takes follow-up actions such as applying CORS validation
 * checks and either rejecting the response or adding CORS headers to the
 * response.
 *
 * @author Sebastien Deleuze
 * @author Rossen Stoyanchev
 * @since 4.2
 * @see <a href="https://www.w3.org/TR/cors/">CORS W3C recommendation</a>
 * @see org.springframework.web.servlet.handler.AbstractHandlerMapping#setCorsProcessor
 */
public interface CorsProcessor {

	/**
	 * Process a request given a {@code CorsConfiguration}.
	 * @param configuration the applicable CORS configuration (possibly {@code null})
	 * @param request the current request
	 * @param response the current response
	 * @return {@code false} if the request is rejected, {@code true} otherwise
	 */
	boolean processRequest(@Nullable CorsConfiguration configuration, HttpServletRequest request,
			HttpServletResponse response) throws IOException;

}
