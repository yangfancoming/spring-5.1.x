

package org.springframework.web.socket.config.annotation;

import java.util.List;

import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;

/**
 * A convenient abstract base class for {@link WebSocketMessageBrokerConfigurer}
 * implementations providing empty method implementations for optional methods.
 *
 *
 * @since 4.0.1
 * @deprecated as of 5.0 in favor of simply using {@link WebSocketMessageBrokerConfigurer}
 * which has default methods, made possible by a Java 8 baseline.
 */
@Deprecated
public abstract class AbstractWebSocketMessageBrokerConfigurer implements WebSocketMessageBrokerConfigurer {

	@Override
	public void configureWebSocketTransport(WebSocketTransportRegistration registration) {
	}

	@Override
	public void configureClientInboundChannel(ChannelRegistration registration) {
	}

	@Override
	public void configureClientOutboundChannel(ChannelRegistration registration) {
	}

	@Override
	public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
		return true;
	}

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
	}

	@Override
	public void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
	}

	@Override
	public void configureMessageBroker(MessageBrokerRegistry registry) {
	}

}
