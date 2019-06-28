

package org.springframework.aop.config;

import org.junit.Test;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Juergen Hoeller
 * @author Chris Beams
 */
public class PrototypeProxyTests {

	@Test
	public void testInjectionBeforeWrappingCheckDoesNotKickInForPrototypeProxy() {
		new ClassPathXmlApplicationContext(getClass().getSimpleName() + "-context.xml", getClass());
	}

}
