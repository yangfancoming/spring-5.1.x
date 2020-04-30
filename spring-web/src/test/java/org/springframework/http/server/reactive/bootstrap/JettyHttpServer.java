

package org.springframework.http.server.reactive.bootstrap;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import org.springframework.http.server.reactive.JettyHttpHandlerAdapter;
import org.springframework.http.server.reactive.ServletHttpHandlerAdapter;

/**
 *
 */
public class JettyHttpServer extends AbstractHttpServer {

	private Server jettyServer;

	private ServletContextHandler contextHandler;


	@Override
	protected void initServer() throws Exception {

		this.jettyServer = new Server();

		ServletHttpHandlerAdapter servlet = createServletAdapter();
		ServletHolder servletHolder = new ServletHolder(servlet);
		servletHolder.setAsyncSupported(true);

		this.contextHandler = new ServletContextHandler(this.jettyServer, "", false, false);
		this.contextHandler.addServlet(servletHolder, "/");
		this.contextHandler.start();

		ServerConnector connector = new ServerConnector(this.jettyServer);
		connector.setHost(getHost());
		connector.setPort(getPort());
		this.jettyServer.addConnector(connector);
	}

	private ServletHttpHandlerAdapter createServletAdapter() {
		return new JettyHttpHandlerAdapter(resolveHttpHandler());
	}

	@Override
	protected void startInternal() throws Exception {
		this.jettyServer.start();
		setPort(((ServerConnector) this.jettyServer.getConnectors()[0]).getLocalPort());
	}

	@Override
	protected void stopInternal() throws Exception {
		try {
			if (this.contextHandler.isRunning()) {
				this.contextHandler.stop();
			}
		}
		finally {
			try {
				if (this.jettyServer.isRunning()) {
					this.jettyServer.setStopTimeout(5000);
					this.jettyServer.stop();
					this.jettyServer.destroy();
				}
			}
			catch (Exception ex) {
				// ignore
			}
		}
	}

	@Override
	protected void resetInternal() {
		try {
			if (this.jettyServer.isRunning()) {
				this.jettyServer.setStopTimeout(5000);
				this.jettyServer.stop();
				this.jettyServer.destroy();
			}
		}
		catch (Exception ex) {
			throw new IllegalStateException(ex);
		}
		finally {
			this.jettyServer = null;
			this.contextHandler = null;
		}
	}

}
