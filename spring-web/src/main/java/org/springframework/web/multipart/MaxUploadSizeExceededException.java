

package org.springframework.web.multipart;

import org.springframework.lang.Nullable;

/**
 * MultipartException subclass thrown when an upload exceeds the
 * maximum upload size allowed.
 *
 * @author Juergen Hoeller
 * @since 1.0.1
 */
@SuppressWarnings("serial")
public class MaxUploadSizeExceededException extends MultipartException {

	private final long maxUploadSize;


	/**
	 * Constructor for MaxUploadSizeExceededException.
	 * @param maxUploadSize the maximum upload size allowed,
	 * or -1 if the size limit isn't known
	 */
	public MaxUploadSizeExceededException(long maxUploadSize) {
		this(maxUploadSize, null);
	}

	/**
	 * Constructor for MaxUploadSizeExceededException.
	 * @param maxUploadSize the maximum upload size allowed,
	 * or -1 if the size limit isn't known
	 * @param ex root cause from multipart parsing API in use
	 */
	public MaxUploadSizeExceededException(long maxUploadSize, @Nullable Throwable ex) {
		super("Maximum upload size " + (maxUploadSize >= 0 ? "of " + maxUploadSize + " bytes " : "") + "exceeded", ex);
		this.maxUploadSize = maxUploadSize;
	}


	/**
	 * Return the maximum upload size allowed,
	 * or -1 if the size limit isn't known.
	 */
	public long getMaxUploadSize() {
		return this.maxUploadSize;
	}

}
