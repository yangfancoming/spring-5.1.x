

package org.springframework.web.util;

import java.net.URI;
import java.util.Map;

/**
 * Defines methods for expanding a URI template with variables.
 *
 *
 * @since 4.2
 * @see org.springframework.web.client.RestTemplate#setUriTemplateHandler(UriTemplateHandler)
 */
public interface UriTemplateHandler {

	/**
	 * Expand the given URI template with a map of URI variables.
	 * @param uriTemplate the URI template
	 * @param uriVariables variable values
	 * @return the created URI instance
	 */
	URI expand(String uriTemplate, Map<String, ?> uriVariables);

	/**
	 * Expand the given URI template with an array of URI variables.
	 * @param uriTemplate the URI template
	 * @param uriVariables variable values
	 * @return the created URI instance
	 */
	URI expand(String uriTemplate, Object... uriVariables);

}
