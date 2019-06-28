

package org.springframework.context.support;

import org.springframework.context.EmbeddedValueResolverAware;
import org.springframework.lang.Nullable;
import org.springframework.util.StringValueResolver;

/**
 * Convenient base class for components with a need for embedded value resolution
 * (i.e. {@link org.springframework.context.EmbeddedValueResolverAware} consumers).
 *
 * @author Juergen Hoeller
 * @since 4.1
 */
public class EmbeddedValueResolutionSupport implements EmbeddedValueResolverAware {

	@Nullable
	private StringValueResolver embeddedValueResolver;


	@Override
	public void setEmbeddedValueResolver(StringValueResolver resolver) {
		this.embeddedValueResolver = resolver;
	}

	/**
	 * Resolve the given embedded value through this instance's {@link StringValueResolver}.
	 * @param value the value to resolve
	 * @return the resolved value, or always the original value if no resolver is available
	 * @see #setEmbeddedValueResolver
	 */
	@Nullable
	protected String resolveEmbeddedValue(String value) {
		return (this.embeddedValueResolver != null ? this.embeddedValueResolver.resolveStringValue(value) : value);
	}


}
