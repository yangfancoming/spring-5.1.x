

package org.springframework.web.servlet.config.annotation;

import java.util.Collections;
import javax.servlet.ServletContext;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.resource.DefaultServletHttpRequestHandler;

/**
 * Configures a request handler for serving static resources by forwarding
 * the request to the Servlet container's "default" Servlet. This is intended
 * to be used when the Spring MVC {@link DispatcherServlet} is mapped to "/"
 * thus overriding the Servlet container's default handling of static resources.
 *
 * <p>Since this handler is configured at the lowest precedence, effectively
 * it allows all other handler mappings to handle the request, and if none
 * of them do, this handler can forward it to the "default" Servlet.
 *
 * @author Rossen Stoyanchev

 * @since 3.1
 * @see DefaultServletHttpRequestHandler
 */
public class DefaultServletHandlerConfigurer {

	private final ServletContext servletContext;

	@Nullable
	private DefaultServletHttpRequestHandler handler;


	/**
	 * Create a {@link DefaultServletHandlerConfigurer} instance.
	 * @param servletContext the ServletContext to use.
	 */
	public DefaultServletHandlerConfigurer(ServletContext servletContext) {
		Assert.notNull(servletContext, "ServletContext is required");
		this.servletContext = servletContext;
	}


	/**
	 * Enable forwarding to the "default" Servlet.
	 * <p>When this method is used the {@link DefaultServletHttpRequestHandler}
	 * will try to autodetect the "default" Servlet name. Alternatively, you can
	 * specify the name of the default Servlet via {@link #enable(String)}.
	 * @see DefaultServletHttpRequestHandler
	 */
	public void enable() {
		enable(null);
	}

	/**
	 * Enable forwarding to the "default" Servlet identified by the given name.
	 * <p>This is useful when the default Servlet cannot be autodetected,
	 * for example when it has been manually configured.
	 * @see DefaultServletHttpRequestHandler
	 */
	public void enable(@Nullable String defaultServletName) {
		this.handler = new DefaultServletHttpRequestHandler();
		if (defaultServletName != null) {
			this.handler.setDefaultServletName(defaultServletName);
		}
		this.handler.setServletContext(this.servletContext);
	}


	/**
	 * Return a handler mapping instance ordered at {@link Integer#MAX_VALUE} containing the
	 * {@link DefaultServletHttpRequestHandler} instance mapped to {@code "/**"};
	 * or {@code null} if default servlet handling was not been enabled.
	 * @since 4.3.12
	 */
	@Nullable
	protected SimpleUrlHandlerMapping buildHandlerMapping() {
		if (this.handler == null) {
			return null;
		}

		SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
		handlerMapping.setUrlMap(Collections.singletonMap("/**", this.handler));
		handlerMapping.setOrder(Integer.MAX_VALUE);
		return handlerMapping;
	}

}
