

package org.springframework.web.context.request.async;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

/**
 * Utility methods related to processing asynchronous web requests.
 *
 * @author Rossen Stoyanchev

 * @since 3.2
 */
public abstract class WebAsyncUtils {

	/**
	 * The name attribute containing the {@link WebAsyncManager}.
	 */
	public static final String WEB_ASYNC_MANAGER_ATTRIBUTE =
			WebAsyncManager.class.getName() + ".WEB_ASYNC_MANAGER";


	/**
	 * Obtain the {@link WebAsyncManager} for the current request, or if not
	 * found, create and associate it with the request.
	 */
	public static WebAsyncManager getAsyncManager(ServletRequest servletRequest) {
		WebAsyncManager asyncManager = null;
		Object asyncManagerAttr = servletRequest.getAttribute(WEB_ASYNC_MANAGER_ATTRIBUTE);
		if (asyncManagerAttr instanceof WebAsyncManager) {
			asyncManager = (WebAsyncManager) asyncManagerAttr;
		}
		if (asyncManager == null) {
			asyncManager = new WebAsyncManager();
			servletRequest.setAttribute(WEB_ASYNC_MANAGER_ATTRIBUTE, asyncManager);
		}
		return asyncManager;
	}

	/**
	 * Obtain the {@link WebAsyncManager} for the current request, or if not
	 * found, create and associate it with the request.
	 */
	public static WebAsyncManager getAsyncManager(WebRequest webRequest) {
		int scope = RequestAttributes.SCOPE_REQUEST;
		WebAsyncManager asyncManager = null;
		Object asyncManagerAttr = webRequest.getAttribute(WEB_ASYNC_MANAGER_ATTRIBUTE, scope);
		if (asyncManagerAttr instanceof WebAsyncManager) {
			asyncManager = (WebAsyncManager) asyncManagerAttr;
		}
		if (asyncManager == null) {
			asyncManager = new WebAsyncManager();
			webRequest.setAttribute(WEB_ASYNC_MANAGER_ATTRIBUTE, asyncManager, scope);
		}
		return asyncManager;
	}

	/**
	 * Create an AsyncWebRequest instance. By default, an instance of
	 * {@link StandardServletAsyncWebRequest} gets created.
	 * @param request the current request
	 * @param response the current response
	 * @return an AsyncWebRequest instance (never {@code null})
	 */
	public static AsyncWebRequest createAsyncWebRequest(HttpServletRequest request, HttpServletResponse response) {
		return new StandardServletAsyncWebRequest(request, response);
	}

}
