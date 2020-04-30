

package org.springframework.web.socket.config.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * Add this annotation to an {@code @Configuration} class to enable broker-backed
 * messaging over WebSocket using a higher-level messaging sub-protocol.
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableWebSocketMessageBroker
 * public class MyWebSocketConfig {
 *
 * }
 * </pre>
 *
 * xmlBeanDefinitionReaderCustomize the imported configuration by implementing the
 * {@link WebSocketMessageBrokerConfigurer} interface:
 *
 * <pre class="code">
 * &#064;Configuration
 * &#064;EnableWebSocketMessageBroker
 * public class MyConfiguration implements WebSocketMessageBrokerConfigurer {
 *
 *     &#064;Override
 *     public void registerStompEndpoints(StompEndpointRegistry registry) {
 *         registry.addEndpoint("/portfolio").withSockJS();
 *     }
 *
 *     &#064;Override
 *     public void configureMessageBroker(MessageBrokerRegistry registry) {
 *         registry.enableStompBrokerRelay("/queue/", "/topic/");
 *         registry.setApplicationDestinationPrefixes("/app/");
 *     }
 * }
 * </pre>
 *
 *
 * @since 4.0
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(DelegatingWebSocketMessageBrokerConfiguration.class)
public @interface EnableWebSocketMessageBroker {

}
