package javax.servlet;

import java.io.IOException;
import java.util.EventListener;

/**
 * http请求的资源可以被读取
 * <p>
 * This class represents a call-back mechanism that will notify implementations
 * as HTTP request data becomes available to be read without blocking.
 * </p>
 *
 * @since Servlet 3.1
 */
public interface ReadListener extends EventListener {

    /**
     * When an instance of the <code>ReadListener</code> is registered with a {@link ServletInputStream},
     * this method will be invoked by the container the first time when it is possible
     * to read data. Subsequently the container will invoke this method if and only
     * if the {@link javax.servlet.ServletInputStream#isReady()} method
     * has been called and has returned a value of <code>false</code> <em>and</em>
     * data has subsequently become available to read.
     *
     * @throws IOException if an I/O related error has occurred during processing
     */
    public void onDataAvailable() throws IOException;

    /**
     * Invoked when all data for the current request has been read.
     *
     * @throws IOException if an I/O related error has occurred during processing
     */

    public void onAllDataRead() throws IOException;

    /**
     * Invoked when an error occurs processing the request.
     *
     * @param t the throwable to indicate why the read operation failed
     */
    public void onError(Throwable t);


}
