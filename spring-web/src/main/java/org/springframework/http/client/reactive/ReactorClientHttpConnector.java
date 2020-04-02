

package org.springframework.http.client.reactive;

import java.net.URI;
import java.util.function.Function;

import io.netty.buffer.ByteBufAllocator;
import reactor.core.publisher.Mono;
import reactor.netty.NettyInbound;
import reactor.netty.NettyOutbound;
import reactor.netty.http.client.HttpClient;
import reactor.netty.http.client.HttpClientRequest;
import reactor.netty.http.client.HttpClientResponse;
import reactor.netty.resources.ConnectionProvider;
import reactor.netty.resources.LoopResources;

import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;

/**
 * Reactor-Netty implementation of {@link ClientHttpConnector}.
 *
 * @author Brian Clozel
 * @since 5.0
 * @see reactor.netty.http.client.HttpClient
 */
public class ReactorClientHttpConnector implements ClientHttpConnector {

	private final static Function<HttpClient, HttpClient> defaultInitializer = client -> client.compress(true);


	private final HttpClient httpClient;


	/**
	 * Default constructor. Initializes {@link HttpClient} via:
	 * <pre class="code">
	 * HttpClient.create().compress()
	 * </pre>
	 */
	public ReactorClientHttpConnector() {
		this.httpClient = defaultInitializer.apply(HttpClient.create());
	}

	/**
	 * Constructor with externally managed Reactor Netty resources, including
	 * {@link LoopResources} for event loop threads, and {@link ConnectionProvider}
	 * for the connection pool.
	 * This constructor should be used only when you don't want the client
	 * to participate in the Reactor Netty global resources. By default the
	 * client participates in the Reactor Netty global resources held in
	 * {@link reactor.netty.http.HttpResources}, which is recommended since
	 * fixed, shared resources are favored for event loop concurrency. However,
	 * consider declaring a {@link ReactorResourceFactory} bean with
	 * {@code globalResources=true} in order to ensure the Reactor Netty global
	 * resources are shut down when the Spring ApplicationContext is closed.
	 * @param factory the resource factory to obtain the resources from
	 * @param mapper a mapper for further initialization of the created client
	 * @since 5.1
	 */
	public ReactorClientHttpConnector(ReactorResourceFactory factory, Function<HttpClient, HttpClient> mapper) {
		this.httpClient = defaultInitializer.andThen(mapper).apply(initHttpClient(factory));
	}

	private static HttpClient initHttpClient(ReactorResourceFactory resourceFactory) {
		ConnectionProvider provider = resourceFactory.getConnectionProvider();
		LoopResources resources = resourceFactory.getLoopResources();
		Assert.notNull(provider, "No ConnectionProvider: is ReactorResourceFactory not initialized yet?");
		Assert.notNull(resources, "No LoopResources: is ReactorResourceFactory not initialized yet?");
		return HttpClient.create(provider).tcpConfiguration(tcpClient -> tcpClient.runOn(resources));
	}

	/**
	 * Constructor with a pre-configured {@code HttpClient} instance.
	 * @param httpClient the client to use
	 * @since 5.1
	 */
	public ReactorClientHttpConnector(HttpClient httpClient) {
		Assert.notNull(httpClient, "HttpClient is required");
		this.httpClient = httpClient;
	}


	@Override
	public Mono<ClientHttpResponse> connect(HttpMethod method, URI uri,
			Function<? super ClientHttpRequest, Mono<Void>> requestCallback) {

		if (!uri.isAbsolute()) {
			return Mono.error(new IllegalArgumentException("URI is not absolute: " + uri));
		}

		return this.httpClient
				.request(io.netty.handler.codec.http.HttpMethod.valueOf(method.name()))
				.uri(uri.toString())
				.send((request, outbound) -> requestCallback.apply(adaptRequest(method, uri, request, outbound)))
				.responseConnection((res, con) -> Mono.just(adaptResponse(res, con.inbound(), con.outbound().alloc())))
				.next();
	}

	private ReactorClientHttpRequest adaptRequest(HttpMethod method, URI uri, HttpClientRequest request,
			NettyOutbound nettyOutbound) {

		return new ReactorClientHttpRequest(method, uri, request, nettyOutbound);
	}

	private ClientHttpResponse adaptResponse(HttpClientResponse response, NettyInbound nettyInbound,
			ByteBufAllocator allocator) {

		return new ReactorClientHttpResponse(response, nettyInbound, allocator);
	}

}
