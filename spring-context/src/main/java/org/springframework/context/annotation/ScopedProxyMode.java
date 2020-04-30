

package org.springframework.context.annotation;

/**
 * Enumerates the various scoped-proxy options.
 * For a more complete discussion of exactly what a scoped proxy is, see the section of the Spring reference documentation entitled '<em>Scoped beans as dependencies</em>'.
 * @since 2.5
 * @see ScopeMetadata
 */
public enum ScopedProxyMode {

	DEFAULT, // Default typically equals {@link #NO}, unless a different default has been configured at the component-scan instruction level.

	/**
	 * Do not create a scoped proxy.
	 * This proxy-mode is not typically useful when used with a non-singleton scoped instance,
	 * which should favor the use of the {@link #INTERFACES} or {@link #TARGET_CLASS} proxy-modes instead if it is to be used as a dependency.
	 */
	NO,

	INTERFACES, //  Create a JDK dynamic proxy implementing <i>all</i> interfaces exposed by the class of the target object.

	TARGET_CLASS // Create a class-based proxy (uses CGLIB).
}
