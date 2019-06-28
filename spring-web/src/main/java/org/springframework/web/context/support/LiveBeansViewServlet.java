

package org.springframework.web.context.support;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.support.LiveBeansView;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Servlet variant of {@link LiveBeansView}'s MBean exposure.
 *
 * <p>Generates a JSON snapshot for current beans and their dependencies in
 * all ApplicationContexts that live within the current web application.
 *
 * @author Juergen Hoeller
 * @since 3.2
 * @see org.springframework.context.support.LiveBeansView#getSnapshotAsJson()
 */
@SuppressWarnings("serial")
public class LiveBeansViewServlet extends HttpServlet {

	@Nullable
	private LiveBeansView liveBeansView;


	@Override
	public void init() throws ServletException {
		this.liveBeansView = buildLiveBeansView();
	}

	protected LiveBeansView buildLiveBeansView() {
		return new ServletContextLiveBeansView(getServletContext());
	}


	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		Assert.state(this.liveBeansView != null, "No LiveBeansView available");
		String content = this.liveBeansView.getSnapshotAsJson();
		response.setContentType("application/json");
		response.setContentLength(content.length());
		response.getWriter().write(content);
	}

}
