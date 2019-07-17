

package org.springframework.web.multipart;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.lang.Nullable;

/**
 * Provides additional methods for dealing with multipart content within a
 * servlet request, allowing to access uploaded files.
 * Implementations also need to override the standard
 * {@link javax.servlet.ServletRequest} methods for parameter access, making
 * multipart parameters available.
 *
 * <p>A concrete implementation is
 * {@link org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest}.
 * As an intermediate step,
 * {@link org.springframework.web.multipart.support.AbstractMultipartHttpServletRequest}
 * can be subclassed.
 *

 * @author Trevor D. Cook
 * @since 29.09.2003
 * @see MultipartResolver
 * @see MultipartFile
 * @see javax.servlet.http.HttpServletRequest#getParameter
 * @see javax.servlet.http.HttpServletRequest#getParameterNames
 * @see javax.servlet.http.HttpServletRequest#getParameterMap
 * @see org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest
 * @see org.springframework.web.multipart.support.AbstractMultipartHttpServletRequest
 */
public interface MultipartHttpServletRequest extends HttpServletRequest, MultipartRequest {

	/**
	 * Return this request's method as a convenient HttpMethod instance.
	 */
	@Nullable
	HttpMethod getRequestMethod();

	/**
	 * Return this request's headers as a convenient HttpHeaders instance.
	 */
	HttpHeaders getRequestHeaders();

	/**
	 * Return the headers associated with the specified part of the multipart request.
	 * <p>If the underlying implementation supports access to headers, then all headers are returned.
	 * Otherwise, the returned headers will include a 'Content-Type' header at the very least.
	 */
	@Nullable
	HttpHeaders getMultipartHeaders(String paramOrFileName);

}
