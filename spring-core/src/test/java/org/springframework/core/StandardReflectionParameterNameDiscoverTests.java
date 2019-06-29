
package org.springframework.core;

import java.lang.reflect.Method;

import org.junit.Before;
import org.junit.Test;

import org.springframework.util.ReflectionUtils;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.*;

/**
 * Tests for StandardReflectionParameterNameDiscoverer
 *
 * @author Rob Winch
 */
public class StandardReflectionParameterNameDiscoverTests {
	private ParameterNameDiscoverer parameterNameDiscoverer;

	@Before
	public void setup() {
		parameterNameDiscoverer = new StandardReflectionParameterNameDiscoverer();
	}

	@Test
	public void getParameterNamesOnInterface() {
		Method method = ReflectionUtils.findMethod(MessageService.class,"sendMessage", String.class);
		String[] actualParams = parameterNameDiscoverer.getParameterNames(method);
		assertThat(actualParams, is(new String[]{"message"}));
	}

	public interface MessageService {
		void sendMessage(String message);
	}
}
