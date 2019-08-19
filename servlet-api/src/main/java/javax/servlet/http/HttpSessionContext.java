

package javax.servlet.http;

import java.util.Enumeration;

/**
 *
 * @author		Various
 *
 * @deprecated		As of Java(tm) Servlet API 2.1
 *			for security reasons, with no replacement.
 *			This interface will be removed in a future
 *			version of this API.
 *
 * @see			HttpSession
 * @see			HttpSessionBindingEvent
 * @see            HttpSessionBindingListener
 *
 */

@Deprecated
public interface HttpSessionContext {

    /**
     *
     * @deprecated 	As of Java Servlet API 2.1 with
     *			no replacement. This method must 
     *			return null and will be removed in
     *			a future version of this API.
     * @param sessionId the id of the session to be returned
     *
     * @return null in all cases
     */
    @Deprecated
    public HttpSession getSession(String sessionId);
    
    
    
  
    /**
     *
     * @deprecated	As of Java Servlet API 2.1 with
     *			no replacement. This method must return 
     *			an empty <code>Enumeration</code> and will be removed
     *			in a future version of this API.
     *
     * @return null 
     *
     */
    @Deprecated
    public Enumeration<String> getIds();
}





