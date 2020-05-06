

package org.springframework.aop.scope;

import org.springframework.aop.RawTargetAccess;

/**
 * An AOP introduction interface for scoped objects.
 * Objects created from the {@link ScopedProxyFactoryBean} can be cast
 * to this interface, enabling access to the raw target object  and programmatic removal of the target object.
 * @since 2.0
 * @see ScopedProxyFactoryBean
 */
public interface ScopedObject extends RawTargetAccess {

	/**
	 * Return the current target object behind this scoped object proxy,in its raw form (as stored in the target scope).
	 * The raw target object can for example be passed to persistence providers which would not be able to handle the scoped proxy object.
	 * @return the current target object behind this scoped object proxy
	 */
	Object getTargetObject();

	/**
	 * Remove this object from its target scope, for example from the backing session.
	 * Note that no further calls may be made to the scoped object afterwards (at least within the current thread, that is, with
	 * the exact same target object in the target scope).
	 */
	void removeFromScope();

}
