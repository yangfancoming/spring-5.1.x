

package org.springframework.web.servlet.view.tiles3;

import java.util.Locale;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.tiles.TilesContainer;
import org.apache.tiles.access.TilesAccess;
import org.apache.tiles.renderer.DefinitionRenderer;
import org.apache.tiles.request.AbstractRequest;
import org.apache.tiles.request.ApplicationContext;
import org.apache.tiles.request.Request;
import org.apache.tiles.request.render.Renderer;
import org.apache.tiles.request.servlet.ServletRequest;
import org.apache.tiles.request.servlet.ServletUtil;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.support.JstlUtils;
import org.springframework.web.servlet.support.RequestContext;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

/**
 * {@link org.springframework.web.servlet.View} implementation that renders
 * through the Tiles Request API. The "url" property is interpreted as name of a
 * Tiles definition.
 *
 * @author Nicolas Le Bas
 * @author mick semb wever
 *
 * @author Sebastien Deleuze
 * @since 3.2
 */
public class TilesView extends AbstractUrlBasedView {

	@Nullable
	private Renderer renderer;

	private boolean exposeJstlAttributes = true;

	private boolean alwaysInclude = false;

	@Nullable
	private ApplicationContext applicationContext;


	/**
	 * Set the {@link Renderer} to use.
	 * If not set, by default {@link DefinitionRenderer} is used.
	 */
	public void setRenderer(Renderer renderer) {
		this.renderer = renderer;
	}

	/**
	 * Whether to expose JSTL attributes. By default set to {@code true}.
	 * @see JstlUtils#exposeLocalizationContext(RequestContext)
	 */
	protected void setExposeJstlAttributes(boolean exposeJstlAttributes) {
		this.exposeJstlAttributes = exposeJstlAttributes;
	}

	/**
	 * Specify whether to always include the view rather than forward to it.
	 * Default is "false". Switch this flag on to enforce the use of a
	 * Servlet include, even if a forward would be possible.
	 * @since 4.1.2
	 * @see TilesViewResolver#setAlwaysInclude
	 */
	public void setAlwaysInclude(boolean alwaysInclude) {
		this.alwaysInclude = alwaysInclude;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		super.afterPropertiesSet();

		ServletContext servletContext = getServletContext();
		Assert.state(servletContext != null, "No ServletContext");
		this.applicationContext = ServletUtil.getApplicationContext(servletContext);

		if (this.renderer == null) {
			TilesContainer container = TilesAccess.getContainer(this.applicationContext);
			this.renderer = new DefinitionRenderer(container);
		}
	}


	@Override
	public boolean checkResource(final Locale locale) throws Exception {
		Assert.state(this.renderer != null, "No Renderer set");

		HttpServletRequest servletRequest = null;
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		if (requestAttributes instanceof ServletRequestAttributes) {
			servletRequest = ((ServletRequestAttributes) requestAttributes).getRequest();
		}

		Request request = new ServletRequest(this.applicationContext, servletRequest, null) {
			@Override
			public Locale getRequestLocale() {
				return locale;
			}
		};

		return this.renderer.isRenderable(getUrl(), request);
	}

	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request,
			HttpServletResponse response) throws Exception {

		Assert.state(this.renderer != null, "No Renderer set");

		exposeModelAsRequestAttributes(model, request);
		if (this.exposeJstlAttributes) {
			JstlUtils.exposeLocalizationContext(new RequestContext(request, getServletContext()));
		}
		if (this.alwaysInclude) {
			request.setAttribute(AbstractRequest.FORCE_INCLUDE_ATTRIBUTE_NAME, true);
		}

		Request tilesRequest = createTilesRequest(request, response);
		this.renderer.render(getUrl(), tilesRequest);
	}

	/**
	 * Create a Tiles {@link Request}.
	 * This implementation creates a {@link ServletRequest}.
	 * @param request the current request
	 * @param response the current response
	 * @return the Tiles request
	 */
	protected Request createTilesRequest(final HttpServletRequest request, HttpServletResponse response) {
		return new ServletRequest(this.applicationContext, request, response) {
			@Override
			public Locale getRequestLocale() {
				return RequestContextUtils.getLocale(request);
			}
		};
	}

}
