

package org.springframework.web.servlet.view.groovy;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.DirectFieldAccessor;

import java.util.Locale;

/**
 * Unit tests for
 * {@link org.springframework.web.servlet.view.groovy.GroovyMarkupViewResolver}.
 *
 * @author Brian Clozel
 */
public class GroovyMarkupViewResolverTests {

	@Test
	public void viewClass() throws Exception {
		GroovyMarkupViewResolver resolver = new GroovyMarkupViewResolver();
		Assert.assertEquals(GroovyMarkupView.class, resolver.requiredViewClass());
		DirectFieldAccessor viewAccessor = new DirectFieldAccessor(resolver);
		Class<?> viewClass = (Class<?>) viewAccessor.getPropertyValue("viewClass");
		Assert.assertEquals(GroovyMarkupView.class, viewClass);
	}

	@Test
	public void cacheKey() throws Exception {
		GroovyMarkupViewResolver resolver = new GroovyMarkupViewResolver();
		String cacheKey = (String) resolver.getCacheKey("test", Locale.US);
		Assert.assertNotNull(cacheKey);
		Assert.assertEquals("test_en_US", cacheKey);
	}

}
