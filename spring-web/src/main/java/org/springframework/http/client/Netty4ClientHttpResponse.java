

package org.springframework.http.client;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import io.netty.buffer.ByteBufInputStream;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.FullHttpResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * {@link ClientHttpResponse} implementation based on Netty 4.
 *
 * @author Arjen Poutsma
 * @since 4.1.2
 * @deprecated as of Spring 5.0, in favor of
 * {@link org.springframework.http.client.reactive.ReactorClientHttpConnector}
 */
@Deprecated
class Netty4ClientHttpResponse extends AbstractClientHttpResponse {

	private final ChannelHandlerContext context;

	private final FullHttpResponse nettyResponse;

	private final ByteBufInputStream body;

	@Nullable
	private volatile HttpHeaders headers;


	public Netty4ClientHttpResponse(ChannelHandlerContext context, FullHttpResponse nettyResponse) {
		Assert.notNull(context, "ChannelHandlerContext must not be null");
		Assert.notNull(nettyResponse, "FullHttpResponse must not be null");
		this.context = context;
		this.nettyResponse = nettyResponse;
		this.body = new ByteBufInputStream(this.nettyResponse.content());
		this.nettyResponse.retain();
	}


	@Override
	public int getRawStatusCode() throws IOException {
		return this.nettyResponse.getStatus().code();
	}

	@Override
	public String getStatusText() throws IOException {
		return this.nettyResponse.getStatus().reasonPhrase();
	}

	@Override
	public HttpHeaders getHeaders() {
		HttpHeaders headers = this.headers;
		if (headers == null) {
			headers = new HttpHeaders();
			for (Map.Entry<String, String> entry : this.nettyResponse.headers()) {
				headers.add(entry.getKey(), entry.getValue());
			}
			this.headers = headers;
		}
		return headers;
	}

	@Override
	public InputStream getBody() throws IOException {
		return this.body;
	}

	@Override
	public void close() {
		this.nettyResponse.release();
		this.context.close();
	}

}
