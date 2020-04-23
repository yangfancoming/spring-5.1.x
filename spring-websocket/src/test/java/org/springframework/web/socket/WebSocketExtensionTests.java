

package org.springframework.web.socket;

import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test fixture for {@link WebSocketExtension}
 */
public class WebSocketExtensionTests {

	@Test
	public void parseHeaderSingle() {
		List<WebSocketExtension> extensions = WebSocketExtension.parseExtensions("x-test-extension ; foo=bar ; bar=baz");
		assertThat(extensions, Matchers.hasSize(1));
		WebSocketExtension extension = extensions.get(0);

		assertEquals("x-test-extension", extension.getName());
		assertEquals(2, extension.getParameters().size());
		assertEquals("bar", extension.getParameters().get("foo"));
		assertEquals("baz", extension.getParameters().get("bar"));
	}

	@Test
	public void parseHeaderMultiple() {
		List<WebSocketExtension> extensions = WebSocketExtension.parseExtensions("x-foo-extension, x-bar-extension");
		assertThat(extensions, Matchers.hasSize(2));
		assertEquals("x-foo-extension", extensions.get(0).getName());
		assertEquals("x-bar-extension", extensions.get(1).getName());
	}

}
