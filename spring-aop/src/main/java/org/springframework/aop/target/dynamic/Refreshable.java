

package org.springframework.aop.target.dynamic;

/**
 * Interface to be implemented by dynamic target objects,
 * which support reloading and optionally polling for updates.
 * @since 2.0
 */
public interface Refreshable {

	/**
	 * Refresh the underlying target object.
	 */
	void refresh();

	/**
	 * Return the number of actual refreshes since startup.
	 */
	long getRefreshCount();

	/**
	 * Return the last time an actual refresh happened (as timestamp).
	 */
	long getLastRefreshTime();

}
