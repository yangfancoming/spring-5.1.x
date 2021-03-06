

package org.springframework.aop.framework.adapter;

/**
 * Singleton to publish a shared DefaultAdvisorAdapterRegistry instance.
 * @see DefaultAdvisorAdapterRegistry
 */
public final class GlobalAdvisorAdapterRegistry {

	private GlobalAdvisorAdapterRegistry() {
	}

	/**
	 * Keep track of a single instance so we can return it to classes that request it.
	 */
	private static AdvisorAdapterRegistry instance = new DefaultAdvisorAdapterRegistry();

	/**
	 * Return the singleton {@link DefaultAdvisorAdapterRegistry} instance.
	 */
	public static AdvisorAdapterRegistry getInstance() {
		return instance;
	}

	/**
	 * Reset the singleton {@link DefaultAdvisorAdapterRegistry}, removing any
	 * {@link AdvisorAdapterRegistry#registerAdvisorAdapter(AdvisorAdapter) registered}
	 * adapters.
	 */
	static void reset() {
		instance = new DefaultAdvisorAdapterRegistry();
	}

}
