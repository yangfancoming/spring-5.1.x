

package org.springframework.web.servlet.mvc.method.annotation;

import com.fasterxml.jackson.annotation.JsonView;

import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.Assert;

/**
 * A {@link ResponseBodyAdvice} implementation that adds support for Jackson's
 * {@code @JsonView} annotation declared on a Spring MVC {@code @RequestMapping}
 * or {@code @ExceptionHandler} method.
 *
 * <p>The serialization view specified in the annotation will be passed in to the
 * {@link org.springframework.http.converter.json.MappingJackson2HttpMessageConverter}
 * which will then use it to serialize the response body.
 *
 * <p>Note that despite {@code @JsonView} allowing for more than one class to
 * be specified, the use for a response body advice is only supported with
 * exactly one class argument. Consider the use of a composite interface.
 *
 * @author Rossen Stoyanchev
 * @since 4.1
 * @see com.fasterxml.jackson.annotation.JsonView
 * @see com.fasterxml.jackson.databind.ObjectMapper#writerWithView(Class)
 */
public class JsonViewResponseBodyAdvice extends AbstractMappingJacksonResponseBodyAdvice {

	@Override
	public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
		return super.supports(returnType, converterType) && returnType.hasMethodAnnotation(JsonView.class);
	}

	@Override
	protected void beforeBodyWriteInternal(MappingJacksonValue bodyContainer, MediaType contentType,
			MethodParameter returnType, ServerHttpRequest request, ServerHttpResponse response) {

		JsonView ann = returnType.getMethodAnnotation(JsonView.class);
		Assert.state(ann != null, "No JsonView annotation");

		Class<?>[] classes = ann.value();
		if (classes.length != 1) {
			throw new IllegalArgumentException(
					"@JsonView only supported for response body advice with exactly 1 class argument: " + returnType);
		}

		bodyContainer.setSerializationView(classes[0]);
	}

}
