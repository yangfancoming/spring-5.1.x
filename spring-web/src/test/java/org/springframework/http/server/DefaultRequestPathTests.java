
package org.springframework.http.server;

import java.net.URI;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Unit tests for {@link DefaultRequestPath}.
 */
public class DefaultRequestPathTests {

	@Test
	public void requestPath()  {
		// basic
		testRequestPath("/app/a/b/c", "/app", "/a/b/c");

		// no context path
		testRequestPath("/a/b/c", "", "/a/b/c");

		// context path only
		testRequestPath("/a/b", "/a/b", "");

		// root path
		testRequestPath("/", "", "/");

		// empty path
		testRequestPath("", "", "");
		testRequestPath("", "/", "");

		// trailing slash
		testRequestPath("/app/a/", "/app", "/a/");
		testRequestPath("/app/a//", "/app", "/a//");
	}

	private void testRequestPath(String fullPath, String contextPath, String pathWithinApplication) {
		URI uri = URI.create("http://localhost:8080" + fullPath);
		RequestPath requestPath = RequestPath.parse(uri, contextPath);
		assertEquals(contextPath.equals("/") ? "" : contextPath, requestPath.contextPath().value());
		assertEquals(pathWithinApplication, requestPath.pathWithinApplication().value());
	}

	@Test
	public void updateRequestPath()  {

		URI uri = URI.create("http://localhost:8080/aA/bB/cC");
		RequestPath requestPath = RequestPath.parse(uri, null);

		assertEquals("", requestPath.contextPath().value());
		assertEquals("/aA/bB/cC", requestPath.pathWithinApplication().value());

		requestPath = requestPath.modifyContextPath("/aA");

		assertEquals("/aA", requestPath.contextPath().value());
		assertEquals("/bB/cC", requestPath.pathWithinApplication().value());
	}

}
