

package org.springframework.http.server.reactive.bootstrap;

import java.net.InetSocketAddress;

import io.undertow.Undertow;

import org.springframework.http.server.reactive.UndertowHttpHandlerAdapter;

/**
 * @author Marek Hawrylczak
 */
public class UndertowHttpServer extends AbstractHttpServer {

	private Undertow server;


	@Override
	protected void initServer() throws Exception {
		this.server = Undertow.builder().addHttpListener(getPort(), getHost())
				.setHandler(initHttpHandlerAdapter())
				.build();
	}

	private UndertowHttpHandlerAdapter initHttpHandlerAdapter() {
		return new UndertowHttpHandlerAdapter(resolveHttpHandler());
	}

	@Override
	protected void startInternal() {
		this.server.start();
		Undertow.ListenerInfo info = this.server.getListenerInfo().get(0);
		setPort(((InetSocketAddress) info.getAddress()).getPort());
	}

	@Override
	protected void stopInternal() {
		this.server.stop();
	}

	@Override
	protected void resetInternal() {
		this.server = null;
	}

}
