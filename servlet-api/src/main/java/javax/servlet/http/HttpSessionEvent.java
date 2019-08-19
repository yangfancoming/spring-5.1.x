

package javax.servlet.http;

/**
 * This is the class representing event notifications for changes to
 * sessions within a web application.
 *
 * @since Servlet 2.3
 */
public class HttpSessionEvent extends java.util.EventObject {

    private static final long serialVersionUID = -7622791603672342895L;

    /**
     * Construct a session event from the given source.
     *
     * @param source the {@link HttpSession} corresponding to this event
     */
    public HttpSessionEvent(HttpSession source) {
        super(source);
    }

    /**
     * Return the session that changed.
     * @return the {@link HttpSession} for this event.
     */
    public HttpSession getSession () { 
        return (HttpSession) super.getSource();
    }
}

