

package javax.servlet;

/** 
 * Events of this kind indicate lifecycle events for a ServletRequest.
 * The source of the event is the ServletContext of this web application.
 *
 * @see ServletRequestListener
 * @since Servlet 2.4
 */
public class ServletRequestEvent extends java.util.EventObject { 

    private static final long serialVersionUID = -7467864054698729101L;

    private final transient ServletRequest request;

    /** Construct a ServletRequestEvent for the given ServletContext
      * and ServletRequest.
      *
      * @param sc       the ServletContext of the web application.
      * @param request  the ServletRequest that is sending the event.
      */
    public ServletRequestEvent(ServletContext sc, ServletRequest request) {
        super(sc);
        this.request = request;
    }

    /**
      * Returns the ServletRequest that is changing.

      * @return the {@link ServletRequest} corresponding to this event.
      */
    public ServletRequest getServletRequest () {
        return this.request;
    }

    /**
      * Returns the ServletContext of this web application.
      *
      * @return the {@link ServletContext} for this web application.
      */
    public ServletContext getServletContext () {
        return (ServletContext) super.getSource();
    }
}
