

package org.springframework.remoting.caucho;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.HttpRequestHandler;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.util.NestedServletException;

/**
 * Servlet-API-based HTTP request handler that exports the specified service bean
 * as Hessian service endpoint, accessible via a Hessian proxy.
 *
 * <p>Hessian is a slim, binary RPC protocol.
 * For information on Hessian, see the
 * <a href="http://hessian.caucho.com">Hessian website</a>.
 * <b>Note: As of Spring 4.0, this exporter requires Hessian 4.0 or above.</b>
 *
 * <p>Hessian services exported with this class can be accessed by
 * any Hessian client, as there isn't any special handling involved.
 *

 * @since 13.05.2003
 * @see HessianClientInterceptor
 * @see HessianProxyFactoryBean
 * @see org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter
 * @see org.springframework.remoting.rmi.RmiServiceExporter
 */
public class HessianServiceExporter extends HessianExporter implements HttpRequestHandler {

	/**
	 * Processes the incoming Hessian request and creates a Hessian response.
	 */
	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		if (!"POST".equals(request.getMethod())) {
			throw new HttpRequestMethodNotSupportedException(request.getMethod(),
					new String[] {"POST"}, "HessianServiceExporter only supports POST requests");
		}

		response.setContentType(CONTENT_TYPE_HESSIAN);
		try {
			invoke(request.getInputStream(), response.getOutputStream());
		}
		catch (Throwable ex) {
			throw new NestedServletException("Hessian skeleton invocation failed", ex);
		}
	}

}
