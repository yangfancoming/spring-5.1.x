

package org.springframework.beans;

import java.beans.BeanInfo;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;

import org.junit.Test;

import org.springframework.core.OverridingClassLoader;
import org.springframework.tests.sample.beans.TestBean;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;


public class CachedIntrospectionResultsTests {

	@Test
	public void acceptAndClearClassLoader() throws Exception {
		BeanWrapper bw = new BeanWrapperImpl(TestBean.class);
		assertTrue(bw.isWritableProperty("name"));
		assertTrue(bw.isWritableProperty("age"));
		assertTrue(CachedIntrospectionResults.strongClassCache.containsKey(TestBean.class));

		ClassLoader child = new OverridingClassLoader(getClass().getClassLoader());
		Class<?> tbClass = child.loadClass("org.springframework.tests.sample.beans.TestBean");
		assertFalse(CachedIntrospectionResults.strongClassCache.containsKey(tbClass));
		CachedIntrospectionResults.acceptClassLoader(child);
		bw = new BeanWrapperImpl(tbClass);
		assertTrue(bw.isWritableProperty("name"));
		assertTrue(bw.isWritableProperty("age"));
		assertTrue(CachedIntrospectionResults.strongClassCache.containsKey(tbClass));
		CachedIntrospectionResults.clearClassLoader(child);
		assertFalse(CachedIntrospectionResults.strongClassCache.containsKey(tbClass));

		assertTrue(CachedIntrospectionResults.strongClassCache.containsKey(TestBean.class));
	}

	@Test
	public void clearClassLoaderForSystemClassLoader() throws Exception {
		BeanUtils.getPropertyDescriptors(ArrayList.class);
		assertTrue(CachedIntrospectionResults.strongClassCache.containsKey(ArrayList.class));
		CachedIntrospectionResults.clearClassLoader(ArrayList.class.getClassLoader());
		assertFalse(CachedIntrospectionResults.strongClassCache.containsKey(ArrayList.class));
	}

	@Test
	public void shouldUseExtendedBeanInfoWhenApplicable() throws NoSuchMethodException, SecurityException {
		// given a class with a non-void returning setter method
		@SuppressWarnings("unused")
		class C {
			public Object setFoo(String s) { return this; }
			public String getFoo() { return null; }
		}

		// CachedIntrospectionResults should delegate to ExtendedBeanInfo
		CachedIntrospectionResults results = CachedIntrospectionResults.forClass(C.class);
		BeanInfo info = results.getBeanInfo();
		PropertyDescriptor pd = null;
		for (PropertyDescriptor candidate : info.getPropertyDescriptors()) {
			if (candidate.getName().equals("foo")) {
				pd = candidate;
			}
		}

		// resulting in a property descriptor including the non-standard setFoo method
		assertThat(pd, notNullValue());
		assertThat(pd.getReadMethod(), equalTo(C.class.getMethod("getFoo")));
		assertThat(
				"No write method found for non-void returning 'setFoo' method. " +
				"Check to see if CachedIntrospectionResults is delegating to " +
				"ExtendedBeanInfo as expected",
				pd.getWriteMethod(), equalTo(C.class.getMethod("setFoo", String.class)));
	}

}
