

package org.springframework.beans.factory.support;

import java.security.AccessControlContext;
import java.security.AccessController;

import org.springframework.lang.Nullable;

/**
 * Simple {@link SecurityContextProvider} implementation.
 *
 * @author Costin Leau
 * @since 3.0
 */
public class SimpleSecurityContextProvider implements SecurityContextProvider {

	@Nullable
	private final AccessControlContext acc;


	/**
	 * Construct a new {@code SimpleSecurityContextProvider} instance.
	 * <p>The security context will be retrieved on each call from the current
	 * thread.
	 */
	public SimpleSecurityContextProvider() {
		this(null);
	}

	/**
	 * Construct a new {@code SimpleSecurityContextProvider} instance.
	 * <p>If the given control context is null, the security context will be
	 * retrieved on each call from the current thread.
	 * @param acc access control context (can be {@code null})
	 * @see AccessController#getContext()
	 */
	public SimpleSecurityContextProvider(@Nullable AccessControlContext acc) {
		this.acc = acc;
	}


	@Override
	public AccessControlContext getAccessControlContext() {
		return (this.acc != null ? this.acc : AccessController.getContext());
	}

}
