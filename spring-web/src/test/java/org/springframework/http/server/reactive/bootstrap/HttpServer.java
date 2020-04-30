

package org.springframework.http.server.reactive.bootstrap;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.Lifecycle;
import org.springframework.http.server.reactive.HttpHandler;

/**
 *
 */
public interface HttpServer extends InitializingBean, Lifecycle {

	void setHost(String host);

	void setPort(int port);

	int getPort();

	void setHandler(HttpHandler handler);

}
