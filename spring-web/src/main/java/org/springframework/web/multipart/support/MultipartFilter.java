

package org.springframework.web.multipart.support;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;

/**
 * Servlet Filter that resolves multipart requests via a {@link MultipartResolver}.
 * in the root web application context.
 *
 * Looks up the MultipartResolver in Spring's root web application context.
 * Supports a "multipartResolverBeanName" filter init-param in {@code web.xml};
 * the default bean name is "filterMultipartResolver".
 *
 * If no MultipartResolver bean is found, this filter falls back to a default
 * MultipartResolver: {@link StandardServletMultipartResolver} for Servlet 3.0,
 * based on a multipart-config section in {@code web.xml}.
 * Note however that at present the Servlet specification only defines how to
 * enable multipart configuration on a Servlet and as a result multipart request
 * processing is likely not possible in a Filter unless the Servlet container
 * provides a workaround such as Tomcat's "allowCasualMultipartParsing" property.
 *
 * MultipartResolver lookup is customizable: Override this filter's
 * {@code lookupMultipartResolver} method to use a custom MultipartResolver
 * instance, for example if not using a Spring web application context.
 * Note that the lookup method should not create a new MultipartResolver instance
 * for each call but rather return a reference to a pre-built instance.
 *
 * Note: This filter is an <b>alternative</b> to using DispatcherServlet's
 * MultipartResolver support, for example for web applications with custom web views
 * which do not use Spring's web MVC, or for custom filters applied before a Spring MVC
 * DispatcherServlet (e.g. {@link org.springframework.web.filter.HiddenHttpMethodFilter}).
 * In any case, this filter should not be combined with servlet-specific multipart resolution.
 *

 * @since 08.10.2003
 * @see #setMultipartResolverBeanName
 * @see #lookupMultipartResolver
 * @see org.springframework.web.multipart.MultipartResolver
 * @see org.springframework.web.servlet.DispatcherServlet
 */
public class MultipartFilter extends OncePerRequestFilter {

	/**
	 * The default name for the multipart resolver bean.
	 */
	public static final String DEFAULT_MULTIPART_RESOLVER_BEAN_NAME = "filterMultipartResolver";

	private final MultipartResolver defaultMultipartResolver = new StandardServletMultipartResolver();

	private String multipartResolverBeanName = DEFAULT_MULTIPART_RESOLVER_BEAN_NAME;


	/**
	 * Set the bean name of the MultipartResolver to fetch from Spring's
	 * root application context. Default is "filterMultipartResolver".
	 */
	public void setMultipartResolverBeanName(String multipartResolverBeanName) {
		this.multipartResolverBeanName = multipartResolverBeanName;
	}

	/**
	 * Return the bean name of the MultipartResolver to fetch from Spring's
	 * root application context.
	 */
	protected String getMultipartResolverBeanName() {
		return this.multipartResolverBeanName;
	}


	/**
	 * Check for a multipart request via this filter's MultipartResolver,
	 * and wrap the original request with a MultipartHttpServletRequest if appropriate.
	 * All later elements in the filter chain, most importantly servlets, benefit
	 * from proper parameter extraction in the multipart case, and are able to cast to
	 * MultipartHttpServletRequest if they need to.
	 */
	@Override
	protected void doFilterInternal(
			HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		MultipartResolver multipartResolver = lookupMultipartResolver(request);

		HttpServletRequest processedRequest = request;
		if (multipartResolver.isMultipart(processedRequest)) {
			if (logger.isTraceEnabled()) {
				logger.trace("Resolving multipart request");
			}
			processedRequest = multipartResolver.resolveMultipart(processedRequest);
		}
		else {
			// A regular request...
			if (logger.isTraceEnabled()) {
				logger.trace("Not a multipart request");
			}
		}

		try {
			filterChain.doFilter(processedRequest, response);
		}
		finally {
			if (processedRequest instanceof MultipartHttpServletRequest) {
				multipartResolver.cleanupMultipart((MultipartHttpServletRequest) processedRequest);
			}
		}
	}

	/**
	 * Look up the MultipartResolver that this filter should use,
	 * taking the current HTTP request as argument.
	 * The default implementation delegates to the {@code lookupMultipartResolver}
	 * without arguments.
	 * @return the MultipartResolver to use
	 * @see #lookupMultipartResolver()
	 */
	protected MultipartResolver lookupMultipartResolver(HttpServletRequest request) {
		return lookupMultipartResolver();
	}

	/**
	 * Look for a MultipartResolver bean in the root web application context.
	 * Supports a "multipartResolverBeanName" filter init param; the default
	 * bean name is "filterMultipartResolver".
	 * This can be overridden to use a custom MultipartResolver instance,
	 * for example if not using a Spring web application context.
	 * @return the MultipartResolver instance
	 */
	protected MultipartResolver lookupMultipartResolver() {
		WebApplicationContext wac = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		String beanName = getMultipartResolverBeanName();
		if (wac != null && wac.containsBean(beanName)) {
			if (logger.isDebugEnabled()) {
				logger.debug("Using MultipartResolver '" + beanName + "' for MultipartFilter");
			}
			return wac.getBean(beanName, MultipartResolver.class);
		}
		else {
			return this.defaultMultipartResolver;
		}
	}

}
