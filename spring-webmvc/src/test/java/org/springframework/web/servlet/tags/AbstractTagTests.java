

package org.springframework.web.servlet.tags;

import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.mock.web.test.MockPageContext;
import org.springframework.mock.web.test.MockServletContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.SimpleWebApplicationContext;
import org.springframework.web.servlet.ThemeResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.theme.FixedThemeResolver;

/**
 * Abstract base class for testing tags; provides {@link #createPageContext()}.
 *
 * @author Alef Arendsen

 * @author Sam Brannen
 */
public abstract class AbstractTagTests {

	protected MockPageContext createPageContext() {
		MockServletContext sc = new MockServletContext();
		SimpleWebApplicationContext wac = new SimpleWebApplicationContext();
		wac.setServletContext(sc);
		wac.setNamespace("test");
		wac.refresh();

		MockHttpServletRequest request = new MockHttpServletRequest(sc);
		MockHttpServletResponse response = new MockHttpServletResponse();
		if (inDispatcherServlet()) {
			request.setAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
			LocaleResolver lr = new AcceptHeaderLocaleResolver();
			request.setAttribute(DispatcherServlet.LOCALE_RESOLVER_ATTRIBUTE, lr);
			ThemeResolver tr = new FixedThemeResolver();
			request.setAttribute(DispatcherServlet.THEME_RESOLVER_ATTRIBUTE, tr);
			request.setAttribute(DispatcherServlet.THEME_SOURCE_ATTRIBUTE, wac);
		}
		else {
			sc.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);
		}

		return new MockPageContext(sc, request, response);
	}

	protected boolean inDispatcherServlet() {
		return true;
	}

}
