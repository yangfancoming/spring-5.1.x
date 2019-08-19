

package javax.servlet.http;

import java.util.EventListener;


/**
 * Causes an object to be notified when it is bound to
 * or unbound from a session. The object is notified
 * by an {@link HttpSessionBindingEvent} object. This may be as a result
 * of a servlet programmer explicitly unbinding an attribute from a session,
 * due to a session being invalidated, or due to a session timing out.
 *
 *
 * @author		Various
 *
 * @see HttpSession
 * @see HttpSessionBindingEvent
 *
 */

public interface HttpSessionBindingListener extends EventListener {

    /**
     *
     * Notifies the object that it is being bound to
     * a session and identifies the session.
     *
     * @implSpec
     * The default implementation takes no action.
     * 
     * @param event		the event that identifies the
     *				session 
     *
     * @see #valueUnbound
     *
     */ 
    default public void valueBound(HttpSessionBindingEvent event) {}

    /**
     *
     * Notifies the object that it is being unbound
     * from a session and identifies the session.
     *
     * @implSpec
     * The default implementation takes no action.
     *
     * @param event		the event that identifies
     *				the session 
     *	
     * @see #valueBound
     *
     */
    default public void valueUnbound(HttpSessionBindingEvent event) {}
}
