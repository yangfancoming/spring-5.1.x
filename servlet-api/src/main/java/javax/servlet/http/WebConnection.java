package javax.servlet.http;

import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import java.io.IOException;

/**
 * This interface encapsulates the connection for an upgrade request.
 * It allows the protocol handler to send service requests and status
 * queries to the container.
 *
 * @since Servlet 3.1
 */

public interface WebConnection extends AutoCloseable {
    /**
     * Returns an input stream for this web connection.
     *
     * @return a ServletInputStream for reading binary data
     *
     * @exception IOException if an I/O error occurs
     */
    public ServletInputStream getInputStream() throws IOException;

    /**
     * Returns an output stream for this web connection.
     *
     * @return a ServletOutputStream for writing binary data
     *
     * @exception IOException if an I/O error occurs
     */
    public ServletOutputStream getOutputStream() throws IOException;
}
