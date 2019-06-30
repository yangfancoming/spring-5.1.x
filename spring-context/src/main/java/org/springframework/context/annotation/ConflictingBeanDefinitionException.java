

package org.springframework.context.annotation;

/**
 * Marker subclass of {@link IllegalStateException}, allowing for explicit
 * catch clauses in calling code.
 *
 * @author Chris Beams
 * @since 3.1
 */
@SuppressWarnings("serial")
class ConflictingBeanDefinitionException extends IllegalStateException {

	public ConflictingBeanDefinitionException(String message) {
		super(message);
	}

}
