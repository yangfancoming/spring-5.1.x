

package org.springframework.web.context.request;

import org.springframework.lang.Nullable;

/**
 * Request-backed {@link org.springframework.beans.factory.config.Scope}
 * implementation.
 *
 * Relies on a thread-bound {@link RequestAttributes} instance, which
 * can be exported through {@link RequestContextListener},
 * {@link org.springframework.web.filter.RequestContextFilter} or
 * {@link org.springframework.web.servlet.DispatcherServlet}.
 *
 * @author Rod Johnson

 * @author Rob Harrop
 * @since 2.0
 * @see RequestContextHolder#currentRequestAttributes()
 * @see RequestAttributes#SCOPE_REQUEST
 * @see RequestContextListener
 * @see org.springframework.web.filter.RequestContextFilter
 * @see org.springframework.web.servlet.DispatcherServlet
 */
public class RequestScope extends AbstractRequestAttributesScope {

	@Override
	protected int getScope() {
		return RequestAttributes.SCOPE_REQUEST;
	}

	/**
	 * There is no conversation id concept for a request, so this method
	 * returns {@code null}.
	 */
	@Override
	@Nullable
	public String getConversationId() {
		return null;
	}

}
