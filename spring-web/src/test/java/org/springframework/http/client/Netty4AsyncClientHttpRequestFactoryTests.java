

package org.springframework.http.client;

import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import org.springframework.http.HttpMethod;

/**
 * @author Arjen Poutsma
 */
public class Netty4AsyncClientHttpRequestFactoryTests extends AbstractAsyncHttpRequestFactoryTestCase {

	private static EventLoopGroup eventLoopGroup;


	@BeforeClass
	public static void createEventLoopGroup() {
		eventLoopGroup = new NioEventLoopGroup();
	}

	@AfterClass
	public static void shutdownEventLoopGroup() throws InterruptedException {
		eventLoopGroup.shutdownGracefully().sync();
	}

	@SuppressWarnings("deprecation")
	@Override
	protected AsyncClientHttpRequestFactory createRequestFactory() {
		return new Netty4ClientHttpRequestFactory(eventLoopGroup);
	}

	@Override
	@Test
	public void httpMethods() throws Exception {
		super.httpMethods();
		assertHttpMethod("patch", HttpMethod.PATCH);
	}

}
