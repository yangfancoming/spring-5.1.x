

package org.springframework.web.servlet.mvc.method.annotation;

import java.io.IOException;
import java.lang.reflect.Type;

import com.fasterxml.jackson.annotation.JsonView;

import org.springframework.core.MethodParameter;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonInputMessage;
import org.springframework.util.Assert;

/**
 * A {@link RequestBodyAdvice} implementation that adds support for Jackson's
 * {@code @JsonView} annotation declared on a Spring MVC {@code @HttpEntity}
 * or {@code @RequestBody} method parameter.
 *
 * The deserialization view specified in the annotation will be passed in to the
 * {@link org.springframework.http.converter.json.MappingJackson2HttpMessageConverter}
 * which will then use it to deserialize the request body with.
 *
 * Note that despite {@code @JsonView} allowing for more than one class to
 * be specified, the use for a request body advice is only supported with
 * exactly one class argument. Consider the use of a composite interface.
 * @since 4.2
 * @see com.fasterxml.jackson.annotation.JsonView
 * @see com.fasterxml.jackson.databind.ObjectMapper#readerWithView(Class)
 */
public class JsonViewRequestBodyAdvice extends RequestBodyAdviceAdapter {

	@Override
	public boolean supports(MethodParameter methodParameter, Type targetType,Class<? extends HttpMessageConverter<?>> converterType) {
		return (AbstractJackson2HttpMessageConverter.class.isAssignableFrom(converterType) && methodParameter.getParameterAnnotation(JsonView.class) != null);
	}

	@Override
	public HttpInputMessage beforeBodyRead(HttpInputMessage inputMessage, MethodParameter methodParameter,Type targetType, Class<? extends HttpMessageConverter<?>> selectedConverterType) throws IOException {
		JsonView ann = methodParameter.getParameterAnnotation(JsonView.class);
		Assert.state(ann != null, "No JsonView annotation");
		Class<?>[] classes = ann.value();
		if (classes.length != 1) {
			throw new IllegalArgumentException("@JsonView only supported for request body advice with exactly 1 class argument: " + methodParameter);
		}
		return new MappingJacksonInputMessage(inputMessage.getBody(), inputMessage.getHeaders(), classes[0]);
	}

}
