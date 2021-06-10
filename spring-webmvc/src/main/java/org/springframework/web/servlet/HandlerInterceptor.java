

package org.springframework.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.web.method.HandlerMethod;

/**
 * Workflow interface that allows for customized handler execution chains.
 * Applications can register any number of existing or custom interceptors
 * for certain groups of handlers, to add common preprocessing behavior without needing to modify each handler implementation.
 *
 * A HandlerInterceptor gets called before the appropriate HandlerAdapter
 * triggers the execution of the handler itself. This mechanism can be used
 * for a large field of preprocessing aspects, e.g. for authorization checks,or common handler behavior like locale or theme changes.
 * Its main purpose is to allow for factoring out repetitive handler code.
 *
 *
 * In an asynchronous processing scenario, the handler may be executed in a separate thread while the main thread exits without rendering or invoking the
 * {@code postHandle} and {@code afterCompletion} callbacks. When concurrent handler execution completes,
 * the request is dispatched back in order to proceed with rendering the model and all methods of this contract are invoked again.
 * For further options and details see {@code org.springframework.web.servlet.AsyncHandlerInterceptor}
 *
 * Typically an interceptor chain is defined per HandlerMapping bean,
 * sharing its granularity. To be able to apply a certain interceptor chain
 * to a group of handlers, one needs to map the desired handlers via one
 * HandlerMapping bean. The interceptors themselves are defined as beans
 * in the application context, referenced by the mapping bean definition
 * via its "interceptors" property (in XML: a &lt;list&gt; of &lt;ref&gt;).
 *
 * HandlerInterceptor is basically similar to a Servlet Filter, but in
 * contrast to the latter it just allows custom pre-processing with the option
 * of prohibiting the execution of the handler itself, and custom post-processing.
 * Filters are more powerful, for example they allow for exchanging the request
 * and response objects that are handed down the chain. Note that a filter
 * gets configured in web.xml, a HandlerInterceptor in the application context.
 *
 * As a basic guideline, fine-grained handler-related preprocessing tasks are
 * candidates for HandlerInterceptor implementations, especially factored-out
 * common handler code and authorization checks. On the other hand, a Filter
 * is well-suited for request content and view content handling, like multipart
 * forms and GZIP compression. This typically shows when one needs to map the
 * filter to certain content types (e.g. images), or to all requests.

 * @since 20.06.2003
 * @see HandlerExecutionChain#getInterceptors
 * @see org.springframework.web.servlet.handler.HandlerInterceptorAdapter
 * @see org.springframework.web.servlet.handler.AbstractHandlerMapping#setInterceptors
 * @see org.springframework.web.servlet.handler.UserRoleAuthorizationInterceptor
 * @see org.springframework.web.servlet.i18n.LocaleChangeInterceptor
 * @see org.springframework.web.servlet.theme.ThemeChangeInterceptor
 * @see javax.servlet.Filter
 */
public interface HandlerInterceptor {

	/**
	 * Intercept the execution of a handler. Called after HandlerMapping determined
	 * an appropriate handler object, but before HandlerAdapter invokes the handler.
	 * DispatcherServlet processes a handler in an execution chain, consisting of any number of interceptors, with the handler itself at the end.
	 * With this method, each interceptor can decide to abort the execution chain,typically sending a HTTP error or writing a custom response.
	 * <strong>Note:</strong> special considerations apply for asynchronous request processing. For more details see {@link org.springframework.web.servlet.AsyncHandlerInterceptor}.
	 * The default implementation returns {@code true}.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler chosen handler to execute, for type and/or instance evaluation
	 * @return {@code true} if the execution chain should proceed with the next interceptor or the handler itself.
	 * Else, DispatcherServlet assumes that this interceptor has already dealt with the response itself.
	 * @throws Exception in case of errors
	 * preHandle是调用Controller之前被调用，当返回false后，会跳过之后的拦截器，并且不会执行所有拦截器的postHandle，并调用返回true的拦截器的afterCompletion方法。
	 *
	 * preHandle方法是进行处理器拦截用的，顾名思义，该方法将在Controller处理之前进行调用，
	 * SpringMVC中的Interceptor拦截器是链式的，可以同时存在多个Interceptor，
	 * 然后SpringMVC会根据声明的前后顺序一个接一个的执行，
	 * 而且所有的Interceptor中的preHandle方法都会在Controller方法调用之前调用。
	 * SpringMVC的这种Interceptor链式结构也是可以进行中断的，
	 * 这种中断方式是令preHandle的返回值为false，当preHandle的返回值为false的时候整个请求就结束了。
	 */
	default boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)throws Exception {
		return true;
	}

	/**
	 * Intercept the execution of a handler. Called after HandlerAdapter actually
	 * invoked the handler, but before the DispatcherServlet renders the view.  Can expose additional model objects to the view via the given ModelAndView.
	 * DispatcherServlet processes a handler in an execution chain, consisting of any number of interceptors, with the handler itself at the end.
	 * With this method, each interceptor can post-process an execution, getting applied in inverse order of the execution chain.
	 * <strong>Note:</strong> special considerations apply for asynchronous request processing. For more details see {@link org.springframework.web.servlet.AsyncHandlerInterceptor}.
	 * The default implementation is empty.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler handler (or {@link HandlerMethod}) that started asynchronous execution, for type and/or instance examination
	 * @param modelAndView the {@code ModelAndView} that the handler returned (can also be {@code null})
	 * @throws Exception in case of errors
	 * postHandle是调用Controller之后被调用，但是在渲染View页面之前。
	 *
	 * 这个方法只会在当前这个Interceptor的preHandle方法返回值为true的时候才会执行。
	 * postHandle是进行处理器拦截用的，它的执行时间是在处理器进行处理之 后， 也就是在Controller的方法调用之后执行，
	 * 但是它会在DispatcherServlet进行视图的渲染之前执行，也就是说在这个方法中你可以对ModelAndView进行操作。
	 * 这个方法的链式结构跟正常访问的方向是相反的，也就是说先声明的Interceptor拦截器该方法反而会后调用，
	 * 这跟Struts2里面的拦截器的执行过程有点像，
	 * 只是Struts2里面的intercept方法中要手动的调用ActionInvocation的invoke方法，
	 * Struts2中调用ActionInvocation的invoke方法就是调用下一个Interceptor或者是调用action，
	 * 然后要在Interceptor之前调用的内容都写在调用invoke之前，要在Interceptor之后调用的内容都写在调用invoke方法之后。
	 */
	default void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,@Nullable ModelAndView modelAndView) throws Exception {
	}

	/**
	 * Callback after completion of request processing, that is, after rendering the view.
	 * Will be called on any outcome of handler execution, thus allows for proper resource cleanup.
	 * Note: Will only be called if this interceptor's {@code preHandle}
	 * method has successfully completed and returned {@code true}!
	 * As with the {@code postHandle} method, the method will be invoked on each
	 * interceptor in the chain in reverse order, so the first interceptor will be the last to be invoked.
	 * <strong>Note:</strong> special considerations apply for asynchronous  request processing. For more details see
	 * {@link org.springframework.web.servlet.AsyncHandlerInterceptor}. The default implementation is empty.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler handler (or {@link HandlerMethod}) that started asynchronous execution, for type and/or instance examination
	 * @param ex exception thrown on handler execution, if any
	 * @throws Exception in case of errors
	 * afterCompletion是调用完Controller接口，渲染View页面最后调用。返回true的拦截器都会调用该拦截器的afterCompletion方法，顺序相反。
	 * 该方法也是需要当前对应的Interceptor的preHandle方法的返回值为true时才会执行。
	 * 该方法将在整个请求完成之后，也就是DispatcherServlet渲染了视图执行， 这个方法的主要作用是用于清理资源的
	 */
	default void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,@Nullable Exception ex) throws Exception {
	}
}
