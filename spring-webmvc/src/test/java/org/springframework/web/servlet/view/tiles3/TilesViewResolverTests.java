
package org.springframework.web.servlet.view.tiles3;

import java.util.Locale;

import org.apache.tiles.request.Request;
import org.apache.tiles.request.render.Renderer;
import org.junit.Before;
import org.junit.Test;

import org.springframework.mock.web.test.MockServletContext;
import org.springframework.web.context.support.StaticWebApplicationContext;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

/**
 * Test fixture for {@link TilesViewResolver}.
 *
 * @author mick semb wever
 */
public class TilesViewResolverTests {

	private TilesViewResolver viewResolver;

	private Renderer renderer;


	@Before
	public void setUp() {
		StaticWebApplicationContext wac = new StaticWebApplicationContext();
		wac.setServletContext(new MockServletContext());
		wac.refresh();

		this.renderer = mock(Renderer.class);

		this.viewResolver = new TilesViewResolver();
		this.viewResolver.setRenderer(this.renderer);
		this.viewResolver.setApplicationContext(wac);
	}

	@Test
	public void testResolve() throws Exception {
		given(this.renderer.isRenderable(eq("/template.test"), isA(Request.class))).willReturn(true);
		given(this.renderer.isRenderable(eq("/nonexistent.test"), isA(Request.class))).willReturn(false);

		assertTrue(this.viewResolver.resolveViewName("/template.test", Locale.ITALY) instanceof TilesView);
		assertNull(this.viewResolver.resolveViewName("/nonexistent.test", Locale.ITALY));

		verify(this.renderer).isRenderable(eq("/template.test"), isA(Request.class));
		verify(this.renderer).isRenderable(eq("/nonexistent.test"), isA(Request.class));
	}
}
