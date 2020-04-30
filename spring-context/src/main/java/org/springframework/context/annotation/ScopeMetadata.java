
package org.springframework.context.annotation;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.util.Assert;

/**
 * Describes scope characteristics for a Spring-managed bean including the scope name and the scoped-proxy behavior.
 * The default scope is "singleton", and the default is to <i>not</i> create scoped-proxies.
 * @since 2.5
 * @see ScopeMetadataResolver
 * @see ScopedProxyMode
 */
public class ScopeMetadata {

	private String scopeName = BeanDefinition.SCOPE_SINGLETON;

	private ScopedProxyMode scopedProxyMode = ScopedProxyMode.NO;

	public void setScopeName(String scopeName) {
		Assert.notNull(scopeName, "'scopeName' must not be null");
		this.scopeName = scopeName;
	}

	public String getScopeName() {
		return this.scopeName;
	}

	/**
	 * Set the proxy-mode to be applied to the scoped instance.
	 */
	public void setScopedProxyMode(ScopedProxyMode scopedProxyMode) {
		Assert.notNull(scopedProxyMode, "'scopedProxyMode' must not be null");
		this.scopedProxyMode = scopedProxyMode;
	}

	/**
	 * Get the proxy-mode to be applied to the scoped instance.
	 */
	public ScopedProxyMode getScopedProxyMode() {
		return this.scopedProxyMode;
	}

}
