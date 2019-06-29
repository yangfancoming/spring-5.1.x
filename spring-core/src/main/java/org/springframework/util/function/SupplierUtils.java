

package org.springframework.util.function;

import java.util.function.Supplier;

import org.springframework.lang.Nullable;

/**
 * Convenience utilities for {@link java.util.function.Supplier} handling.
 *
 * @author Juergen Hoeller
 * @since 5.1
 * @see SingletonSupplier
 */
public abstract class SupplierUtils {

	/**
	 * Resolve the given {@code Supplier}, getting its result or immediately
	 * returning {@code null} if the supplier itself was {@code null}.
	 * @param supplier the supplier to resolve
	 * @return the supplier's result, or {@code null} if none
	 */
	@Nullable
	public static <T> T resolve(@Nullable Supplier<T> supplier) {
		return (supplier != null ? supplier.get() : null);
	}

}
