

package org.springframework.web.method.support;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import org.springframework.core.MethodParameter;

import static org.junit.Assert.*;

/**
 * Test fixture with {@link HandlerMethodArgumentResolverComposite}.
 *
 * @author Rossen Stoyanchev
 */
public class HandlerMethodArgumentResolverCompositeTests {

	private HandlerMethodArgumentResolverComposite resolverComposite;

	private MethodParameter paramInt;

	private MethodParameter paramStr;


	@Before
	public void setUp() throws Exception {
		this.resolverComposite = new HandlerMethodArgumentResolverComposite();

		Method method = getClass().getDeclaredMethod("handle", Integer.class, String.class);
		paramInt = new MethodParameter(method, 0);
		paramStr = new MethodParameter(method, 1);
	}


	@Test
	public void supportsParameter() {
		this.resolverComposite.addResolver(new StubArgumentResolver(Integer.class));

		assertTrue(this.resolverComposite.supportsParameter(paramInt));
		assertFalse(this.resolverComposite.supportsParameter(paramStr));
	}

	@Test
	public void resolveArgument() throws Exception {
		this.resolverComposite.addResolver(new StubArgumentResolver(55));
		Object resolvedValue = this.resolverComposite.resolveArgument(paramInt, null, null, null);

		assertEquals(55, resolvedValue);
	}

	@Test
	public void checkArgumentResolverOrder() throws Exception {
		this.resolverComposite.addResolver(new StubArgumentResolver(1));
		this.resolverComposite.addResolver(new StubArgumentResolver(2));
		Object resolvedValue = this.resolverComposite.resolveArgument(paramInt, null, null, null);

		assertEquals("Didn't use the first registered resolver", 1, resolvedValue);
	}

	@Test(expected = IllegalArgumentException.class)
	public void noSuitableArgumentResolver() throws Exception {
		this.resolverComposite.resolveArgument(paramStr, null, null, null);
	}


	@SuppressWarnings("unused")
	private void handle(Integer arg1, String arg2) {
	}

}
