

package org.springframework.web.servlet.view.xslt;

import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.*;

import org.springframework.context.support.StaticApplicationContext;
import org.springframework.util.ClassUtils;

/**
 * @author Rob Harrop
 * @since 2.0
 */
public class XsltViewResolverTests {

	@Test
	public void resolveView() throws Exception {
		StaticApplicationContext ctx = new StaticApplicationContext();

		String prefix = ClassUtils.classPackageAsResourcePath(getClass());
		String suffix = ".xsl";
		String viewName = "products";

		XsltViewResolver viewResolver = new XsltViewResolver();
		viewResolver.setPrefix(prefix);
		viewResolver.setSuffix(suffix);
		viewResolver.setApplicationContext(ctx);

		XsltView view = (XsltView) viewResolver.resolveViewName(viewName, Locale.ENGLISH);
		assertNotNull("View should not be null", view);
		assertEquals("Incorrect URL", prefix + viewName + suffix, view.getUrl());
	}
}
