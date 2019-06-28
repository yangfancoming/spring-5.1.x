

package org.springframework.context.event.test;

/**
 * A simple marker interface used to identify an event or an event listener
 *
 * @author Stephane Nicoll
 */
public interface Identifiable {

	/**
	 * Return a unique global id used to identify this instance.
	 */
	String getId();

}
