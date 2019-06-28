

package org.springframework.http.codec.support;

import org.springframework.http.codec.ClientCodecConfigurer;

/**
 * Default implementation of {@link ClientCodecConfigurer}.
 *
 * @author Rossen Stoyanchev
 * @since 5.0
 */
public class DefaultClientCodecConfigurer extends BaseCodecConfigurer implements ClientCodecConfigurer {

	public DefaultClientCodecConfigurer() {
		super(new ClientDefaultCodecsImpl());
		((ClientDefaultCodecsImpl) defaultCodecs()).setPartWritersSupplier(() -> getWritersInternal(true));
	}

	@Override
	public ClientDefaultCodecs defaultCodecs() {
		return (ClientDefaultCodecs) super.defaultCodecs();
	}

}
