

package org.springframework.scripting.support;

import java.util.Arrays;

import org.junit.Test;

import org.springframework.aop.support.AopUtils;
import org.springframework.aop.target.dynamic.Refreshable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.scripting.Messenger;

import static org.junit.Assert.*;

/**
 * {@link StandardScriptFactory} (lang:std) tests for JavaScript.
 *
 * @author Juergen Hoeller
 * @since 4.2
 */
public class StandardScriptFactoryTests {

	@Test
	public void testJsr223FromTagWithInterface() throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("jsr223-with-xsd.xml", getClass());
		assertTrue(Arrays.asList(ctx.getBeanNamesForType(Messenger.class)).contains("messengerWithInterface"));
		Messenger messenger = (Messenger) ctx.getBean("messengerWithInterface");
		assertFalse(AopUtils.isAopProxy(messenger));
		assertEquals("Hello World!", messenger.getMessage());
	}

	@Test
	public void testRefreshableJsr223FromTagWithInterface() throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("jsr223-with-xsd.xml", getClass());
		assertTrue(Arrays.asList(ctx.getBeanNamesForType(Messenger.class)).contains("refreshableMessengerWithInterface"));
		Messenger messenger = (Messenger) ctx.getBean("refreshableMessengerWithInterface");
		assertTrue(AopUtils.isAopProxy(messenger));
		assertTrue(messenger instanceof Refreshable);
		assertEquals("Hello World!", messenger.getMessage());
	}

	@Test
	public void testInlineJsr223FromTagWithInterface() throws Exception {
		ApplicationContext ctx = new ClassPathXmlApplicationContext("jsr223-with-xsd.xml", getClass());
		assertTrue(Arrays.asList(ctx.getBeanNamesForType(Messenger.class)).contains("inlineMessengerWithInterface"));
		Messenger messenger = (Messenger) ctx.getBean("inlineMessengerWithInterface");
		assertFalse(AopUtils.isAopProxy(messenger));
		assertEquals("Hello World!", messenger.getMessage());
	}

}
