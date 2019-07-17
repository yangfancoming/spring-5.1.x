

package org.springframework.jmx.support;

/**
 * Indicates registration behavior when attempting to register an MBean that already
 * exists.
 *
 * @author Phillip Webb

 * @since 3.2
 */
public enum RegistrationPolicy {

	/**
	 * Registration should fail when attempting to register an MBean under a name that
	 * already exists.
	 */
	FAIL_ON_EXISTING,

	/**
	 * Registration should ignore the affected MBean when attempting to register an MBean
	 * under a name that already exists.
	 */
	IGNORE_EXISTING,

	/**
	 * Registration should replace the affected MBean when attempting to register an MBean
	 * under a name that already exists.
	 */
	REPLACE_EXISTING;

}
