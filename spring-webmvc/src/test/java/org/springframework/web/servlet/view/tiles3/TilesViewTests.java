
package org.springframework.web.servlet.view.tiles3;

import org.apache.tiles.request.AbstractRequest;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.render.Renderer;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.mock.web.test.MockServletContext;
import org.springframework.web.context.support.StaticWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

/**
 * Test fixture for {@link TilesView}.
 *
 * @author mick semb wever
 * @author Sebastien Deleuze
 */
public class TilesViewTests {

	private static final String VIEW_PATH = "template.test";

	private TilesView view;

	private Renderer renderer;

	private MockHttpServletRequest request;

	private MockHttpServletResponse response;


	@Before
	public void setUp() throws Exception {
		MockServletContext servletContext = new MockServletContext();
		StaticWebApplicationContext wac = new StaticWebApplicationContext();
		wac.setServletContext(servletContext);
		wac.refresh();

		request = new MockHttpServletRequest();
		request.setAttribute(DispatcherServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE, wac);

		response = new MockHttpServletResponse();

		renderer = mock(Renderer.class);

		view = new TilesView();
		view.setServletContext(servletContext);
		view.setRenderer(renderer);
		view.setUrl(VIEW_PATH);
		view.afterPropertiesSet();
	}

	@Test
	public void render() throws Exception {
		Map<String, Object> model = new HashMap<>();
		model.put("modelAttribute", "modelValue");
		view.render(model, request, response);
		assertEquals("modelValue", request.getAttribute("modelAttribute"));
		verify(renderer).render(eq(VIEW_PATH), isA(Request.class));
	}

	@Test
	public void alwaysIncludeDefaults() throws Exception {
		view.render(new HashMap<>(), request, response);
		assertNull(request.getAttribute(AbstractRequest.FORCE_INCLUDE_ATTRIBUTE_NAME));
	}

	@Test
	public void alwaysIncludeEnabled() throws Exception {
		view.setAlwaysInclude(true);
		view.render(new HashMap<>(), request, response);
		assertTrue((Boolean)request.getAttribute(AbstractRequest.FORCE_INCLUDE_ATTRIBUTE_NAME));
	}

}
