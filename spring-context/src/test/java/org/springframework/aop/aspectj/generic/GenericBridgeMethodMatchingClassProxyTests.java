

package org.springframework.aop.aspectj.generic;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for AspectJ pointcut expression matching when working with bridge methods.
 *
 * This class focuses on class proxying.
 *
 * See GenericBridgeMethodMatchingTests for more details.
 *
 * @author Ramnivas Laddad

 */
public class GenericBridgeMethodMatchingClassProxyTests extends GenericBridgeMethodMatchingTests {

	@Test
	public void testGenericDerivedInterfaceMethodThroughClass() {
		((DerivedStringParameterizedClass) testBean).genericDerivedInterfaceMethod("");
		assertEquals(1, counterAspect.count);
	}

	@Test
	public void testGenericBaseInterfaceMethodThroughClass() {
		((DerivedStringParameterizedClass) testBean).genericBaseInterfaceMethod("");
		assertEquals(1, counterAspect.count);
	}

}
