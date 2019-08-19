

package javax.servlet.http;

import java.util.EventListener;

/** Objects that are bound to a session may listen to container
 * events notifying them that sessions will be passivated and that
 * session will be activated. A container that migrates session
 * between VMs or persists sessions is required to notify all
 * attributes bound to sessions implementing
 * HttpSessionActivationListener.
 *
 * @since Servlet 2.3
 */

public interface HttpSessionActivationListener extends EventListener { 

    /**
     * Notification that the session is about to be passivated.
     *
     * @implSpec
     * The default implementation takes no action.
     * 
     * @param se the {@link HttpSessionEvent} indicating the passivation
     * of the session
     */
    default public void sessionWillPassivate(HttpSessionEvent se) {}

    /**
     * Notification that the session has just been activated.
     *
     * @implSpec
     * The default implementation takes no action.
     * 
     * @param se the {@link HttpSessionEvent} indicating the activation
     * of the session
     */
    default public void sessionDidActivate(HttpSessionEvent se) {}
} 

