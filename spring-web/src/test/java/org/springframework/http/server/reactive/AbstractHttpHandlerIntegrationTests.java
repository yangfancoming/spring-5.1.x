

package org.springframework.http.server.reactive;

import java.io.File;
import java.time.Duration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import reactor.core.publisher.Flux;

import org.springframework.http.server.reactive.bootstrap.HttpServer;
import org.springframework.http.server.reactive.bootstrap.JettyHttpServer;
import org.springframework.http.server.reactive.bootstrap.ReactorHttpServer;
import org.springframework.http.server.reactive.bootstrap.TomcatHttpServer;
import org.springframework.http.server.reactive.bootstrap.UndertowHttpServer;

@RunWith(Parameterized.class)
public abstract class AbstractHttpHandlerIntegrationTests {

	protected Log logger = LogFactory.getLog(getClass());

	protected int port;

	@Parameterized.Parameter(0)
	public HttpServer server;


	@Parameterized.Parameters(name = "server [{0}]")
	public static Object[][] arguments() {
		File base = new File(System.getProperty("java.io.tmpdir"));
		return new Object[][] {
				{new JettyHttpServer()},
				{new ReactorHttpServer()},
				{new TomcatHttpServer(base.getAbsolutePath())},
				{new UndertowHttpServer()}
		};
	}


	@Before
	public void setup() throws Exception {
		this.server.setHandler(createHttpHandler());
		this.server.afterPropertiesSet();
		this.server.start();

		// Set dynamically chosen port
		this.port = this.server.getPort();
	}

	@After
	public void tearDown() throws Exception {
		this.server.stop();
		this.port = 0;
	}


	protected abstract HttpHandler createHttpHandler();


	/**
	 * Return an interval stream of N number of ticks and buffer the emissions
	 * to avoid back pressure failures (e.g. on slow CI server).
	 *
	 * <p>Use this method as follows:
	 * <ul>
	 * <li>Tests that verify N number of items followed by verifyOnComplete()
	 * should set the number of emissions to N.
	 * <li>Tests that verify N number of items followed by thenCancel() should
	 * set the number of buffered to an arbitrary number greater than N.
	 * </ul>
	 */
	public static Flux<Long> testInterval(Duration period, int count) {
		return Flux.interval(period).take(count).onBackpressureBuffer(count);
	}

}
