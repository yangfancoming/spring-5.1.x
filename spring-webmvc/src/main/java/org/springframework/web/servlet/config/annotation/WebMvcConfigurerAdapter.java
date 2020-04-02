

package org.springframework.web.servlet.config.annotation;

import java.util.List;

import org.springframework.format.FormatterRegistry;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.lang.Nullable;
import org.springframework.validation.MessageCodesResolver;
import org.springframework.validation.Validator;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodReturnValueHandler;
import org.springframework.web.servlet.HandlerExceptionResolver;

/**
 * An implementation of {@link WebMvcConfigurer} with empty methods allowing
 * subclasses to override only the methods they're interested in.
 *
 * @author Rossen Stoyanchev
 * @since 3.1
 * @deprecated as of 5.0 {@link WebMvcConfigurer} has default methods (made
 * possible by a Java 8 baseline) and can be implemented directly without the
 * need for this adapter
 */
@Deprecated
public abstract class WebMvcConfigurerAdapter implements WebMvcConfigurer {

	/**
	 * {@inheritDoc}
	 * This implementation is empty.
	 */
	@Override
	public void configurePathMatch(PathMatchConfigurer configurer) {
	}

	/**
	 * {@inheritDoc}
	 * This implementation is empty.
	 */
	@Override
	public void configureContentNegotiation(ContentNegotiationConfigurer configurer) {
	}

	/**
	 * {@inheritDoc}
	 * This implementation is empty.
	 */
	@Override
	public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
	}

	/**
	 * {@inheritDoc}
	 * This implementation is empty.
	 */
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
	}

	/**
	 * {@inheritDoc}
	 * This implementation is empty.
	 */
	@Override
	public void addFormatters(FormatterRegistry registry) {
	}

	/**
	 * {@inheritDoc}
	 * This implementation is empty.
	 */
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
	}

	/**
	 * {@inheritDoc}
	 * This implementation is empty.
	 */
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
	}

	/**
	 * {@inheritDoc}
	 * This implementation is empty.
	 */
	@Override
	public void addCorsMappings(CorsRegistry registry) {
	}

	/**
	 * {@inheritDoc}
	 * This implementation is empty.
	 */
	@Override
	public void addViewControllers(ViewControllerRegistry registry) {
	}

	/**
	 * {@inheritDoc}
	 * This implementation is empty.
	 */
	@Override
	public void configureViewResolvers(ViewResolverRegistry registry) {
	}

	/**
	 * {@inheritDoc}
	 * This implementation is empty.
	 */
	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
	}

	/**
	 * {@inheritDoc}
	 * This implementation is empty.
	 */
	@Override
	public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
	}

	/**
	 * {@inheritDoc}
	 * This implementation is empty.
	 */
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
	}

	/**
	 * {@inheritDoc}
	 * This implementation is empty.
	 */
	@Override
	public void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
	}

	/**
	 * {@inheritDoc}
	 * This implementation is empty.
	 */
	@Override
	public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
	}

	/**
	 * {@inheritDoc}
	 * This implementation is empty.
	 */
	@Override
	public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
	}

	/**
	 * {@inheritDoc}
	 * This implementation returns {@code null}.
	 */
	@Override
	@Nullable
	public Validator getValidator() {
		return null;
	}

	/**
	 * {@inheritDoc}
	 * This implementation returns {@code null}.
	 */
	@Override
	@Nullable
	public MessageCodesResolver getMessageCodesResolver() {
		return null;
	}

}
