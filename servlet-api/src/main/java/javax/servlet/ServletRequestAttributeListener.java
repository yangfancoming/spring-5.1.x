

package javax.servlet;

import java.util.EventListener;

/**
 * Interface for receiving notification events about ServletRequest
 * attribute changes.
 *
 * <p>Notifications will be generated while the request
 * is within the scope of the web application. A ServletRequest
 * is defined as coming into scope of a web application when it
 * is about to enter the first servlet or filter of the web
 * application, and as going out of scope when it exits the last
 * servlet or the first filter in the chain.
 *
 * <p>In order to receive these notification events, the implementation
 * class must be either declared in the deployment descriptor of the web
 * application, annotated with {@link javax.servlet.annotation.WebListener},
 * or registered via one of the addListener methods defined on
 * {@link ServletContext}.
 *
 * <p>The order in which implementations of this interface are invoked is
 * unspecified.
 *
 * @since Servlet 2.4
 */

public interface ServletRequestAttributeListener extends EventListener {

    /**
     * Receives notification that an attribute has been added to the
     * ServletRequest.
     *
     * @param srae the ServletRequestAttributeEvent containing the 
     * ServletRequest and the name and value of the attribute that was
     * added
     *
     * @implSpec
     * The default implementation takes no action.
     */
    default public void attributeAdded(ServletRequestAttributeEvent srae) {}

    /**
     * Receives notification that an attribute has been removed from the
     * ServletRequest.
     *
     * @param srae the ServletRequestAttributeEvent containing the 
     * ServletRequest and the name and value of the attribute that was
     * removed
     *
     * @implSpec
     * The default implementation takes no action.
     */
    default public void attributeRemoved(ServletRequestAttributeEvent srae) {}

    /**
     * Receives notification that an attribute has been replaced on the
     * ServletRequest.
     *
     * @param srae the ServletRequestAttributeEvent containing the 
     * ServletRequest and the name and (old) value of the attribute
     * that was replaced
     *
     * @implSpec
     * The default implementation takes no action.
     */
    default public void attributeReplaced(ServletRequestAttributeEvent srae) {}
}

