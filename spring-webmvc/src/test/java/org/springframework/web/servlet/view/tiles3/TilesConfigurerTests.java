

package org.springframework.web.servlet.view.tiles3;

import org.apache.tiles.access.TilesAccess;
import org.apache.tiles.impl.BasicTilesContainer;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.servlet.ServletRequest;
import org.apache.tiles.request.servlet.ServletUtil;
import org.junit.Test;

import org.springframework.context.annotation.Configuration;
import org.springframework.mock.web.test.MockHttpServletRequest;
import org.springframework.mock.web.test.MockHttpServletResponse;
import org.springframework.mock.web.test.MockServletContext;

import static org.junit.Assert.*;

/**
 * Test fixture for {@link TilesConfigurer}.
 *
 * @author Nicolas Le Bas
 */
public class TilesConfigurerTests {

	@Test
	public void simpleBootstrap() {
		MockServletContext servletContext = new MockServletContext();

		TilesConfigurer tc = new TilesConfigurer();
		tc.setDefinitions("/org/springframework/web/servlet/view/tiles3/tiles-definitions.xml");
		tc.setCheckRefresh(true);
		tc.setServletContext(servletContext);
		tc.afterPropertiesSet();

		ApplicationContext tilesContext = ServletUtil.getApplicationContext(servletContext);

		BasicTilesContainer container = (BasicTilesContainer) TilesAccess.getContainer(tilesContext);
		Request requestContext = new ServletRequest(container.getApplicationContext(),
				new MockHttpServletRequest(), new MockHttpServletResponse());
		assertNotNull(container.getDefinitionsFactory().getDefinition("test", requestContext));

		tc.destroy();
	}


	@Configuration
	public static class AppConfig {
	}

}
