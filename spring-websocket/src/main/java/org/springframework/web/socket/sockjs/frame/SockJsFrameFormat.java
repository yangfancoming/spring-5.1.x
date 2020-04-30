

package org.springframework.web.socket.sockjs.frame;

/**
 * Applies a transport-specific format to the content of a SockJS frame resulting
 * in a content that can be written out. Primarily for use in HTTP server-side
 * transports that push data.
 *
 * xmlBeanDefinitionReaderFormatting may vary from simply appending a new line character for XHR
 * polling and streaming transports, to a jsonp-style callback function,
 * surrounding script tags, and more.
 *
 * xmlBeanDefinitionReaderFor the various SockJS frame formats in use, see implementations of
 * {@link  org.springframework.web.socket.sockjs.transport.handler.AbstractHttpSendingTransportHandler#getFrameFormat(org.springframework.http.server.ServerHttpRequest) AbstractHttpSendingTransportHandler.getFrameFormat}
 *
 *
 * @since 4.0
 */
public interface SockJsFrameFormat {

	String format(SockJsFrame frame);

}
