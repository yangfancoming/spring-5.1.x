

package org.springframework.web.socket.sockjs.transport.handler;

import java.io.IOException;

import org.springframework.http.HttpStatus;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.lang.Nullable;
import org.springframework.web.socket.sockjs.transport.TransportHandler;
import org.springframework.web.socket.sockjs.transport.TransportType;

/**
 * A {@link TransportHandler} that receives messages over HTTP.
 *
 *
 * @since 4.0
 */
public class XhrReceivingTransportHandler extends AbstractHttpReceivingTransportHandler {

	@Override
	public TransportType getTransportType() {
		return TransportType.XHR_SEND;
	}

	@Override
	@Nullable
	protected String[] readMessages(ServerHttpRequest request) throws IOException {
		return getServiceConfig().getMessageCodec().decodeInputStream(request.getBody());
	}

	@Override
	protected HttpStatus getResponseStatus() {
		return HttpStatus.NO_CONTENT;
	}

}
