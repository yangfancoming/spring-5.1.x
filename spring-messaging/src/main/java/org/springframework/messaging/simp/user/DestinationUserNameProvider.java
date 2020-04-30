
package org.springframework.messaging.simp.user;

/**
 * A {@link java.security.Principal} can also implement this contract when
 * {@link java.security.Principal#getName() getName()} isn't globally unique
 * and therefore not suited for use with "user" destinations.
 *
 *
 * @since 4.0.1
 * @see org.springframework.messaging.simp.user.UserDestinationResolver
 */
public interface DestinationUserNameProvider {

	/**
	 * Return a globally unique user name for use with "user" destinations.
	 */
	String getDestinationUserName();

}
