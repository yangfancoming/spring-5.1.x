package javax.servlet.http;

/**
 * This interface encapsulates the upgrade protocol processing.
 * A HttpUpgradeHandler implementation would allow the servlet container
 * to communicate with it.
 *
 * @since Servlet 3.1
 */

public interface HttpUpgradeHandler {
    /**
     * It is called once the HTTP Upgrade process has been completed and
     * the upgraded connection is ready to start using the new protocol.
     *
     * @param wc the WebConnection object associated to this upgrade request
     */
    public void init(WebConnection wc);

    /**
     * It is called when the client is disconnected.
     */
    public void destroy();
}
