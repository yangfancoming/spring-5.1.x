

package org.springframework.web.servlet.handler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;
import org.springframework.web.servlet.AsyncHandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

/**
 * Abstract adapter class for the {@link AsyncHandlerInterceptor} interface,for simplified implementation of pre-only/post-only interceptors.
 * @since 05.12.2003
 */
public abstract class HandlerInterceptorAdapter implements AsyncHandlerInterceptor {

	/**
	 * This implementation always returns {@code true}.
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)throws Exception {
		return true;
	}

	/** This implementation is empty.*/
	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,@Nullable ModelAndView modelAndView) throws Exception {
	}

	/** This implementation is empty.*/
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,@Nullable Exception ex) throws Exception {
	}

	/** This implementation is empty.*/
	@Override
	public void afterConcurrentHandlingStarted(HttpServletRequest request, HttpServletResponse response,Object handler) throws Exception {
	}

}
