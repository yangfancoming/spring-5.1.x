package javax.servlet;

import javax.servlet.annotation.MultipartConfig;

/**
 * Java Class represntation of an {@link MultipartConfig} annotation value.
 *
 * @since Servlet 3.0
 */
public class MultipartConfigElement {

    //配置文件位置
    private String location;
    //文件大小
    private long maxFileSize;
    private long maxRequestSize;
    private int fileSizeThreshold;

    /**
     * Constructs an instance with defaults for all but location.
     *
     * @param location defualts to "" if values is null.
     */
    public MultipartConfigElement(String location) {
        if (location == null) {
            this.location = "";
        } else {
            this.location = location;
        }
        this.maxFileSize = -1L;
        this.maxRequestSize = -1L;
        this.fileSizeThreshold = 0;
    }

    /**
     * Constructs an instance with all values specified.
     *
     * @param location the directory location where files will be stored
     * @param maxFileSize the maximum size allowed for uploaded files
     * @param maxRequestSize the maximum size allowed for
     * multipart/form-data requests
     * @param fileSizeThreshold the size threshold after which files will
     * be written to disk
     */
    public MultipartConfigElement(String location, long maxFileSize,
            long maxRequestSize, int fileSizeThreshold) {
        if (location == null) {
            this.location = "";
        } else {
            this.location = location;
        }
        this.maxFileSize = maxFileSize;
        this.maxRequestSize = maxRequestSize;
        this.fileSizeThreshold = fileSizeThreshold;
    }

    /**
     * Constructs an instance from a {@link MultipartConfig} annotation value.
     *
     * @param annotation the annotation value
     */
    public MultipartConfigElement(MultipartConfig annotation) {
        this.location = annotation.location();
        this.fileSizeThreshold = annotation.fileSizeThreshold();
        this.maxFileSize = annotation.maxFileSize();
        this.maxRequestSize = annotation.maxRequestSize();
    }

    /**
     * Gets the directory location where files will be stored.
     *
     * @return the directory location where files will be stored
     */
    public String getLocation() {
        return this.location;
    }

    /**
     * Gets the maximum size allowed for uploaded files.
     *
     * @return the maximum size allowed for uploaded files
     */
    public long getMaxFileSize() {
        return this.maxFileSize;
    }

    /**
     * Gets the maximum size allowed for multipart/form-data requests.
     *
     * @return the maximum size allowed for multipart/form-data requests
     */
    public long getMaxRequestSize() {
        return this.maxRequestSize;
    }

    /**
     * Gets the size threshold after which files will be written to disk.
     *
     * @return the size threshold after which files will be written to disk
     */
    public int getFileSizeThreshold() {
        return this.fileSizeThreshold;
    }
}
