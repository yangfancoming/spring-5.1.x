

package org.springframework.web.reactive.result.view.script;

import org.junit.Assert;
import org.junit.Test;

import org.springframework.beans.DirectFieldAccessor;

/**
 * Unit tests for {@link ScriptTemplateViewResolver}.
 *
 * @author Sebastien Deleuze
 */
public class ScriptTemplateViewResolverTests {

	@Test
	public void viewClass() throws Exception {
		ScriptTemplateViewResolver resolver = new ScriptTemplateViewResolver();
		Assert.assertEquals(ScriptTemplateView.class, resolver.requiredViewClass());
		DirectFieldAccessor viewAccessor = new DirectFieldAccessor(resolver);
		Class<?> viewClass = (Class<?>) viewAccessor.getPropertyValue("viewClass");
		Assert.assertEquals(ScriptTemplateView.class, viewClass);
	}

}
