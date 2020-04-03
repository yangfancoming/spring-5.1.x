

package org.springframework.web.socket.config.annotation;

import java.util.List;

import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.handler.invocation.HandlerMethodArgumentResolver;
import org.springframework.messaging.handler.invocation.HandlerMethodReturnValueHandler;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;

/**
 * Defines methods for configuring message handling with simple messaging
 * protocols (e.g. STOMP) from WebSocket clients.
 *
 * xmlBeanDefinitionReaderTypically used to customize the configuration provided via
 * {@link EnableWebSocketMessageBroker @EnableWebSocketMessageBroker}.
 *
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public interface WebSocketMessageBrokerConfigurer {

	/**
	 * Register STOMP endpoints mapping each to a specific URL and (optionally)
	 * enabling and configuring SockJS fallback options.
	 */
	default void registerStompEndpoints(StompEndpointRegistry registry) {
	}

	/**
	 * Configure options related to the processing of messages received from and
	 * sent to WebSocket clients.
	 */
	default void configureWebSocketTransport(WebSocketTransportRegistration registry) {
	}

	/**
	 * Configure the {@link org.springframework.messaging.MessageChannel} used for
	 * incoming messages from WebSocket clients. By default the channel is backed
	 * by a thread pool of size 1. It is recommended to customize thread pool
	 * settings for production use.
	 */
	default void configureClientInboundChannel(ChannelRegistration registration) {
	}

	/**
	 * Configure the {@link org.springframework.messaging.MessageChannel} used for
	 * outbound messages to WebSocket clients. By default the channel is backed
	 * by a thread pool of size 1. It is recommended to customize thread pool
	 * settings for production use.
	 */
	default void configureClientOutboundChannel(ChannelRegistration registration) {
	}

	/**
	 * Add resolvers to support custom controller method argument types.
	 * xmlBeanDefinitionReaderThis does not override the built-in support for resolving handler
	 * method arguments. To customize the built-in support for argument
	 * resolution, configure {@code SimpAnnotationMethodMessageHandler} directly.
	 * @param argumentResolvers the resolvers to register (initially an empty list)
	 * @since 4.1.1
	 */
	default void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
	}

	/**
	 * Add handlers to support custom controller method return value types.
	 * xmlBeanDefinitionReaderUsing this option does not override the built-in support for handling
	 * return values. To customize the built-in support for handling return
	 * values, configure  {@code SimpAnnotationMethodMessageHandler} directly.
	 * @param returnValueHandlers the handlers to register (initially an empty list)
	 * @since 4.1.1
	 */
	default void addReturnValueHandlers(List<HandlerMethodReturnValueHandler> returnValueHandlers) {
	}

	/**
	 * Configure the message converters to use when extracting the payload of
	 * messages in annotated methods and when sending messages (e.g. through the
	 * "broker" SimpMessagingTemplate).
	 * xmlBeanDefinitionReaderThe provided list, initially empty, can be used to add message converters
	 * while the boolean return value is used to determine if default message should
	 * be added as well.
	 * @param messageConverters the converters to configure (initially an empty list)
	 * @return whether to also add default converter or not
	 */
	default boolean configureMessageConverters(List<MessageConverter> messageConverters) {
		return true;
	}

	/**
	 * Configure message broker options.
	 */
	default void configureMessageBroker(MessageBrokerRegistry registry) {
	}

}
