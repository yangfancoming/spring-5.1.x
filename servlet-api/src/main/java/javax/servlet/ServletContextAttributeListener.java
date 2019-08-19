

package javax.servlet;

import java.util.EventListener;

/**
 * Interface for receiving notification events about ServletContext
 * attribute changes.
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
 * @see ServletContextAttributeEvent
 *
 * @since Servlet 2.3
 */

public interface ServletContextAttributeListener extends EventListener {

    /**
     * Receives notification that an attribute has been added to the
     * ServletContext.
     *
     * @param event the ServletContextAttributeEvent containing the
     * ServletContext to which the attribute was added, along with the
     * attribute name and value
     *
     * @implSpec
     * The default implementation takes no action.
     */
    default public void attributeAdded(ServletContextAttributeEvent event) {}

    /**
     * Receives notification that an attribute has been removed
     * from the ServletContext.
     *
     * @param event the ServletContextAttributeEvent containing the
     * ServletContext from which the attribute was removed, along with
     * the attribute name and value
     *
     * @implSpec
     * The default implementation takes no action.
     */
    default public void attributeRemoved(ServletContextAttributeEvent event) {}

    /*
     * Receives notification that an attribute has been replaced
     * in the ServletContext.
     *
     * @param event the ServletContextAttributeEvent containing the
     * ServletContext in which the attribute was replaced, along with
     * the attribute name and its old value
     *
     * @implSpec
     * The default implementation takes no action.
     */
    default public void attributeReplaced(ServletContextAttributeEvent event) {}
}

