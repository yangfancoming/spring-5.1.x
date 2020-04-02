

package org.springframework.web.reactive.result.view.freemarker;

import freemarker.template.Configuration;

/**
 * Interface to be implemented by objects that configure and manage a
 * FreeMarker Configuration object in a web environment. Detected and
 * used by {@link FreeMarkerView}.
 *
 * @author Rossen Stoyanchev
 * @since 5.0
 */
public interface FreeMarkerConfig {

	/**
	 * Return the FreeMarker Configuration object for the current
	 * web application context.
	 * A FreeMarker Configuration object may be used to set FreeMarker
	 * properties and shared objects, and allows to retrieve templates.
	 * @return the FreeMarker Configuration
	 */
	Configuration getConfiguration();

}
