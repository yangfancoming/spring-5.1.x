

package org.springframework.web.servlet.handler;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.lang.Nullable;

/**
 * 实现了用户登录认证的拦截功能，如果当前用户没有通过认证，会报403错误。
 * Interceptor that checks the authorization of the current user via the user's roles, as evaluated by HttpServletRequest's isUserInRole method.
 * @since 20.06.2003
 * @see javax.servlet.http.HttpServletRequest#isUserInRole
 */
public class UserRoleAuthorizationInterceptor extends HandlerInterceptorAdapter {

	@Nullable
	private String[] authorizedRoles;

	/**
	 * Set the roles that this interceptor should treat as authorized.
	 * @param authorizedRoles array of role names
	 */
	public final void setAuthorizedRoles(String... authorizedRoles) {
		this.authorizedRoles = authorizedRoles;
	}

	@Override
	public final boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException {
		if (this.authorizedRoles != null) {
			for (String role : this.authorizedRoles) {
				if (request.isUserInRole(role)) {
					return true;
				}
			}
		}
		handleNotAuthorized(request, response, handler);
		return false;
	}

	/**
	 * Handle a request that is not authorized according to this interceptor.
	 * Default implementation sends HTTP status code 403 ("forbidden").
	 * This method can be overridden to write a custom message, forward or redirect to some error page or login page, or throw a ServletException.
	 * @param request current HTTP request
	 * @param response current HTTP response
	 * @param handler chosen handler to execute, for type and/or instance evaluation
	 * @throws javax.servlet.ServletException if there is an internal error
	 * @throws java.io.IOException in case of an I/O error when writing the response
	 */
	protected void handleNotAuthorized(HttpServletRequest request, HttpServletResponse response, Object handler) throws ServletException, IOException {
		response.sendError(HttpServletResponse.SC_FORBIDDEN);
	}
}
