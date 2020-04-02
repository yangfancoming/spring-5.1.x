

package org.springframework.web.servlet.view;

import java.util.Map;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

/**
 * Wrapper for a JSP or other resource within the same web application.
 * Exposes model objects as request attributes and forwards the request to
 * the specified resource URL using a {@link javax.servlet.RequestDispatcher}.
 *
 * A URL for this view is supposed to specify a resource within the web
 * application, suitable for RequestDispatcher's {@code forward} or
 * {@code include} method.
 *
 * If operating within an already included request or within a response that
 * has already been committed, this view will fall back to an include instead of
 * a forward. This can be enforced by calling {@code response.flushBuffer()}
 * (which will commit the response) before rendering the view.
 *
 * Typical usage with {@link InternalResourceViewResolver} looks as follows,
 * from the perspective of the DispatcherServlet context definition:
 *
 * <pre class="code">&lt;bean id="viewResolver" class="org.springframework.web.servlet.view.InternalResourceViewResolver"&gt;
 *   &lt;property name="prefix" value="/WEB-INF/jsp/"/&gt;
 *   &lt;property name="suffix" value=".jsp"/&gt;
 * &lt;/bean&gt;</pre>
 *
 * Every view name returned from a handler will be translated to a JSP
 * resource (for example: "myView" -> "/WEB-INF/jsp/myView.jsp"), using
 * this view class by default.

 * @see javax.servlet.RequestDispatcher#forward
 * @see javax.servlet.RequestDispatcher#include
 * @see javax.servlet.ServletResponse#flushBuffer
 * @see InternalResourceViewResolver
 * @see JstlView
 */
public class InternalResourceView extends AbstractUrlBasedView {

	private boolean alwaysInclude = false;

	private boolean preventDispatchLoop = false;

	/**
	 * Constructor for use as a bean.
	 * @see #setUrl
	 * @see #setAlwaysInclude
	 */
	public InternalResourceView() {
	}

	/**
	 * Create a new InternalResourceView with the given URL.
	 * @param url the URL to forward to
	 * @see #setAlwaysInclude
	 */
	public InternalResourceView(String url) {
		super(url);
	}

	/**
	 * Create a new InternalResourceView with the given URL.
	 * @param url the URL to forward to
	 * @param alwaysInclude whether to always include the view rather than forward to it
	 */
	public InternalResourceView(String url, boolean alwaysInclude) {
		super(url);
		this.alwaysInclude = alwaysInclude;
	}


	/**
	 * Specify whether to always include the view rather than forward to it.
	 * Default is "false". Switch this flag on to enforce the use of a
	 * Servlet include, even if a forward would be possible.
	 * @see javax.servlet.RequestDispatcher#forward
	 * @see javax.servlet.RequestDispatcher#include
	 * @see #useInclude(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	public void setAlwaysInclude(boolean alwaysInclude) {
		this.alwaysInclude = alwaysInclude;
	}

	/**
	 * Set whether to explicitly prevent dispatching back to the
	 * current handler path.
	 * Default is "false". Switch this to "true" for convention-based
	 * views where a dispatch back to the current handler path is a
	 * definitive error.
	 */
	public void setPreventDispatchLoop(boolean preventDispatchLoop) {
		this.preventDispatchLoop = preventDispatchLoop;
	}

	/**
	 * An ApplicationContext is not strictly required for InternalResourceView.
	 */
	@Override
	protected boolean isContextRequired() {
		return false;
	}


	/**
	 * Render the internal resource given the specified model.
	 * This includes setting the model as request attributes.
	 */
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		// Expose the model object as request attributes.
		// 这里主要是对model进行遍历，将其key和value设置到request中，当做request的一个属性供给页面调用
		exposeModelAsRequestAttributes(model, request);

		// Expose helpers as request attributes, if any.// 提供的一个hook方法，默认是空实现，用于用户进行request属性的自定义使用
		exposeHelpers(request);

		// Determine the path for the request dispatcher. // 检查当前是否存在循环类型的视图名称解析，主要是根据相对路径进行判断视图名是无法解析的
		String dispatcherPath = prepareForRendering(request, response);

		// 获取当前request的RequestDispatcher对象，该对象有两个方法：include()和forward()，
		// 用于对当前的request进行转发，其实也就是将当前的request转发到另一个url，这里的另一个
		// url就是要解析的视图地址，也就是说进行视图解析的时候请求的对于文件的解析实际上相当于
		// 构造了另一个（文件）请求，在该请求中对文件内容进行渲染，从而得到最终的文件。这里的
		// include()方法表示将目标文件引入到当前文件中，与jsp中的include标签作用相同；
		// forward()请求则表示将当前请求转发到另一个请求中，也就是目标文件路径，这种转发并不会
		// 改变用户浏览器地址栏的请求地址。
		// Obtain a RequestDispatcher for the target resource (typically a JSP).
		RequestDispatcher rd = getRequestDispatcher(request, dispatcherPath);
		if (rd == null) {
			throw new ServletException("Could not get RequestDispatcher for [" + getUrl() + "]: Check that the corresponding file exists within your web application archive!");
		}
		// 判断当前是否为include请求，如果是，则调用RequestDispatcher.include()方法进行文件引入
		// If already included or response already committed, perform include, else forward.
		if (useInclude(request, response)) {
			response.setContentType(getContentType());
			if (logger.isDebugEnabled()) {
				logger.debug("Including [" + getUrl() + "]");
			}
			rd.include(request, response);
		}

		else {
			// Note: The forwarded resource is supposed to determine the content type itself.
			if (logger.isDebugEnabled()) {
				logger.debug("Forwarding to [" + getUrl() + "]");
			}
			// 如果当前不是include()请求，则直接使用forward请求将当前请求转发到目标文件路径中，从而渲染该视图
			rd.forward(request, response);
		}
	}

	/**
	 * Expose helpers unique to each rendering operation. This is necessary so that
	 * different rendering operations can't overwrite each other's contexts etc.
	 * Called by {@link #renderMergedOutputModel(Map, HttpServletRequest, HttpServletResponse)}.
	 * The default implementation is empty. This method can be overridden to add
	 * custom helpers as request attributes.
	 * @param request current HTTP request
	 * @throws Exception if there's a fatal error while we're adding attributes
	 * @see #renderMergedOutputModel
	 * @see JstlView#exposeHelpers
	 */
	protected void exposeHelpers(HttpServletRequest request) throws Exception {
	}

	/**
	 * Prepare for rendering, and determine the request dispatcher path
	 * to forward to (or to include).
	 * This implementation simply returns the configured URL.
	 * Subclasses can override this to determine a resource to render,
	 * typically interpreting the URL in a different manner.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @return the request dispatcher path to use
	 * @throws Exception if preparations failed
	 * @see #getUrl()
	 */
	protected String prepareForRendering(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String path = getUrl();
		Assert.state(path != null, "'url' not set");

		if (this.preventDispatchLoop) {
			String uri = request.getRequestURI();
			if (path.startsWith("/") ? uri.equals(path) : uri.equals(StringUtils.applyRelativePath(uri, path))) {
				throw new ServletException("Circular view path [" + path + "]: would dispatch back " +
						"to the current handler URL [" + uri + "] again. Check your ViewResolver setup! " +
						"(Hint: This may be the result of an unspecified view, due to default view name generation.)");
			}
		}
		return path;
	}

	/**
	 * Obtain the RequestDispatcher to use for the forward/include.
	 * The default implementation simply calls
	 * {@link HttpServletRequest#getRequestDispatcher(String)}.
	 * Can be overridden in subclasses.
	 * @param request current HTTP request
	 * @param path the target URL (as returned from {@link #prepareForRendering})
	 * @return a corresponding RequestDispatcher
	 */
	@Nullable
	protected RequestDispatcher getRequestDispatcher(HttpServletRequest request, String path) {
		return request.getRequestDispatcher(path);
	}

	/**
	 * Determine whether to use RequestDispatcher's {@code include} or
	 * {@code forward} method.
	 * Performs a check whether an include URI attribute is found in the request,
	 * indicating an include request, and whether the response has already been committed.
	 * In both cases, an include will be performed, as a forward is not possible anymore.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @return {@code true} for include, {@code false} for forward
	 * @see javax.servlet.RequestDispatcher#forward
	 * @see javax.servlet.RequestDispatcher#include
	 * @see javax.servlet.ServletResponse#isCommitted
	 * @see org.springframework.web.util.WebUtils#isIncludeRequest
	 */
	protected boolean useInclude(HttpServletRequest request, HttpServletResponse response) {
		return (this.alwaysInclude || WebUtils.isIncludeRequest(request) || response.isCommitted());
	}

}
