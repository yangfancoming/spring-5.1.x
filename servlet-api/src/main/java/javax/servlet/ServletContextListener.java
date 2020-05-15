

package javax.servlet;

import java.util.EventListener;

/** 
 * Interface for receiving notification events about ServletContext lifecycle changes.
 * In order to receive these notification events, the implementation class must be either declared in the deployment descriptor of the web
 * application, annotated with {@link javax.servlet.annotation.WebListener},or registered via one of the addListener methods defined on {@link ServletContext}.
 * Implementations of this interface are invoked at their
 * {@link #contextInitialized} method in the order in which they have been declared, and at their {@link #contextDestroyed} method in reverse order.
 * @see ServletContextEvent
 * @since Servlet 2.3
 */
public interface ServletContextListener extends EventListener {

    /**
     * Receives notification that the web application initialization process is starting.
     * 接收Web应用程序初始化进程正在启动的通知
     * All ServletContextListeners are notified of context initialization before any filters or servlets in the web application are initialized.
     * 在初始化Web应用程序中的任何过滤器或servlet之前，将通知所有servletContextListener上下文初始化。
     * @param sce the ServletContextEvent containing the ServletContext that is being initialized
     * @implSpec The default implementation takes no action.
     */
    default public void contextInitialized(ServletContextEvent sce) {}

    /**
     * Receives notification that the ServletContext is about to be shut down.
     * All servlets and filters will have been destroyed before any ServletContextListeners are notified of context destruction.
     * @param sce the ServletContextEvent containing the ServletContext that is being destroyed
     * @implSpec The default implementation takes no action.
     */
    default public void contextDestroyed(ServletContextEvent sce) {}
}

