

package org.springframework.beans.factory.annotation;

import org.junit.Test;

import org.springframework.beans.factory.wiring.BeanWiringInfo;

import static org.junit.Assert.*;

/**
 * @author Rick Evans

 */
public class AnnotationBeanWiringInfoResolverTests {

	@Test
	public void testResolveWiringInfo() throws Exception {
		try {
			new AnnotationBeanWiringInfoResolver().resolveWiringInfo(null);
			fail("Must have thrown an IllegalArgumentException by this point (null argument)");
		}
		catch (IllegalArgumentException expected) {
		}
	}

	@Test
	public void testResolveWiringInfoWithAnInstanceOfANonAnnotatedClass() {
		AnnotationBeanWiringInfoResolver resolver = new AnnotationBeanWiringInfoResolver();
		BeanWiringInfo info = resolver.resolveWiringInfo("java.lang.String is not @Configurable");
		assertNull("Must be returning null for a non-@Configurable class instance", info);
	}

	@Test
	public void testResolveWiringInfoWithAnInstanceOfAnAnnotatedClass() {
		AnnotationBeanWiringInfoResolver resolver = new AnnotationBeanWiringInfoResolver();
		BeanWiringInfo info = resolver.resolveWiringInfo(new Soap());
		assertNotNull("Must *not* be returning null for a non-@Configurable class instance", info);
	}

	@Test
	public void testResolveWiringInfoWithAnInstanceOfAnAnnotatedClassWithAutowiringTurnedOffExplicitly() {
		AnnotationBeanWiringInfoResolver resolver = new AnnotationBeanWiringInfoResolver();
		BeanWiringInfo info = resolver.resolveWiringInfo(new WirelessSoap());
		assertNotNull("Must *not* be returning null for an @Configurable class instance even when autowiring is NO", info);
		assertFalse(info.indicatesAutowiring());
		assertEquals(WirelessSoap.class.getName(), info.getBeanName());
	}

	@Test
	public void testResolveWiringInfoWithAnInstanceOfAnAnnotatedClassWithAutowiringTurnedOffExplicitlyAndCustomBeanName() {
		AnnotationBeanWiringInfoResolver resolver = new AnnotationBeanWiringInfoResolver();
		BeanWiringInfo info = resolver.resolveWiringInfo(new NamedWirelessSoap());
		assertNotNull("Must *not* be returning null for an @Configurable class instance even when autowiring is NO", info);
		assertFalse(info.indicatesAutowiring());
		assertEquals("DerBigStick", info.getBeanName());
	}


	@Configurable()
	private static class Soap {
	}


	@Configurable(autowire = Autowire.NO)
	private static class WirelessSoap {
	}


	@Configurable(autowire = Autowire.NO, value = "DerBigStick")
	private static class NamedWirelessSoap {
	}

}
