

package org.springframework.util.concurrent;

/**
 * Failure callback for a {@link ListenableFuture}.
 *
 * @author Sebastien Deleuze
 * @since 4.1
 */
@FunctionalInterface
public interface FailureCallback {

	/**
	 * Called when the {@link ListenableFuture} completes with failure.
	 * Note that Exceptions raised by this method are ignored.
	 * @param ex the failure
	 */
	void onFailure(Throwable ex);

}
