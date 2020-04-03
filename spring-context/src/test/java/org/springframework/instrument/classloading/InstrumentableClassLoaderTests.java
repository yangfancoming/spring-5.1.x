

package org.springframework.instrument.classloading;

import org.junit.Test;

import org.springframework.util.ClassUtils;

import static org.junit.Assert.*;


public class InstrumentableClassLoaderTests {

	@Test
	public void testDefaultLoadTimeWeaver() {
		ClassLoader loader = new SimpleInstrumentableClassLoader(ClassUtils.getDefaultClassLoader());
		ReflectiveLoadTimeWeaver handler = new ReflectiveLoadTimeWeaver(loader);
		assertSame(loader, handler.getInstrumentableClassLoader());
	}

}
