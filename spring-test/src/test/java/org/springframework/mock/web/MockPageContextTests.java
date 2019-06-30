

package org.springframework.mock.web;

import javax.servlet.jsp.PageContext;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@code MockPageContext} class.
 *
 * @author Rick Evans
 */
public class MockPageContextTests {

	private final String key = "foo";

	private final String value = "bar";

	private final MockPageContext ctx = new MockPageContext();

	@Test
	public void setAttributeWithNoScopeUsesPageScope() throws Exception {
		ctx.setAttribute(key, value);
		assertEquals(value, ctx.getAttribute(key, PageContext.PAGE_SCOPE));
		assertNull(ctx.getAttribute(key, PageContext.APPLICATION_SCOPE));
		assertNull(ctx.getAttribute(key, PageContext.REQUEST_SCOPE));
		assertNull(ctx.getAttribute(key, PageContext.SESSION_SCOPE));
	}

	@Test
	public void removeAttributeWithNoScopeSpecifiedRemovesValueFromAllScopes() throws Exception {
		ctx.setAttribute(key, value, PageContext.APPLICATION_SCOPE);
		ctx.removeAttribute(key);

		assertNull(ctx.getAttribute(key, PageContext.PAGE_SCOPE));
		assertNull(ctx.getAttribute(key, PageContext.APPLICATION_SCOPE));
		assertNull(ctx.getAttribute(key, PageContext.REQUEST_SCOPE));
		assertNull(ctx.getAttribute(key, PageContext.SESSION_SCOPE));
	}

}
