

package org.springframework.web.servlet.handler;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.core.convert.ConversionService;
import org.springframework.util.Assert;

/**
 * Interceptor that places the configured {@link ConversionService} in request scope
 * so it's available during request processing. The request attribute name is
 * "org.springframework.core.convert.ConversionService", the value of {@code ConversionService.class.getName()}.
 * Mainly for use within JSP tags such as the spring:eval tag.
 * @since 3.0.1
 */
public class ConversionServiceExposingInterceptor extends HandlerInterceptorAdapter {

	private final ConversionService conversionService;

	/**
	 * Creates a new {@link ConversionServiceExposingInterceptor}.
	 * @param conversionService the conversion service to export to request scope when this interceptor is invoked
	 */
	public ConversionServiceExposingInterceptor(ConversionService conversionService) {
		Assert.notNull(conversionService, "The ConversionService may not be null");
		this.conversionService = conversionService;
	}

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException {
		request.setAttribute(ConversionService.class.getName(), this.conversionService);
		return true;
	}

}
