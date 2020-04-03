

package org.springframework.web.socket.adapter.jetty;

import org.eclipse.jetty.websocket.api.extensions.ExtensionConfig;

import org.springframework.web.socket.WebSocketExtension;

/**
 * Adapter class to convert a {@link WebSocketExtension} to a Jetty
 * {@link ExtensionConfig}.
 *
 * @author Rossen Stoyanchev
 * @since 4.0
 */
public class WebSocketToJettyExtensionConfigAdapter extends ExtensionConfig {

	public WebSocketToJettyExtensionConfigAdapter(WebSocketExtension extension) {
		super(extension.getName());
		extension.getParameters().forEach(super::setParameter);
	}

}
