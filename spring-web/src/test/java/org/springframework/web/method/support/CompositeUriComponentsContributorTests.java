

package org.springframework.web.method.support;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import org.springframework.core.MethodParameter;
import org.springframework.util.ClassUtils;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.annotation.RequestHeaderMethodArgumentResolver;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;

import static org.junit.Assert.*;

/**
 * Unit tests for
 * {@link org.springframework.web.method.support.CompositeUriComponentsContributor}.
 *
 *
 */
public class CompositeUriComponentsContributorTests {


	@Test
	public void supportsParameter() {

		List<HandlerMethodArgumentResolver> resolvers = new ArrayList<>();
		resolvers.add(new RequestParamMethodArgumentResolver(false));
		resolvers.add(new RequestHeaderMethodArgumentResolver(null));
		resolvers.add(new RequestParamMethodArgumentResolver(true));

		Method method = ClassUtils.getMethod(this.getClass(), "handleRequest", String.class, String.class, String.class);

		CompositeUriComponentsContributor contributor = new CompositeUriComponentsContributor(resolvers);
		assertTrue(contributor.supportsParameter(new MethodParameter(method, 0)));
		assertTrue(contributor.supportsParameter(new MethodParameter(method, 1)));
		assertFalse(contributor.supportsParameter(new MethodParameter(method, 2)));
	}


	public void handleRequest(@RequestParam String p1, String p2, @RequestHeader String h) {
	}

}
