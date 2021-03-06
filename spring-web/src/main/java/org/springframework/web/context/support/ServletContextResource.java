

package org.springframework.web.context.support;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import javax.servlet.ServletContext;

import org.springframework.core.io.AbstractFileResolvingResource;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ResourceUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.WebUtils;

/**
 * {@link org.springframework.core.io.Resource} implementation for {@link javax.servlet.ServletContext} resources,
 * interpreting relative paths within the web application root directory.
 * Always supports stream access and URL access, but only allows
 * {@code java.io.File} access when the web application archive is expanded.
 * @since 28.12.2003
 * @see javax.servlet.ServletContext#getResourceAsStream
 * @see javax.servlet.ServletContext#getResource
 * @see javax.servlet.ServletContext#getRealPath
 */
public class ServletContextResource extends AbstractFileResolvingResource implements ContextResource {

	private final ServletContext servletContext;

	private final String path;

	/**
	 * Create a new ServletContextResource.
	 * The Servlet spec requires that resource paths start with a slash,even if many containers accept paths without leading slash too.
	 * Consequently, the given path will be prepended with a slash if it doesn't already start with one.
	 * @param servletContext the ServletContext to load from
	 * @param path the path of the resource
	 */
	public ServletContextResource(ServletContext servletContext, String path) {
		// check ServletContext
		Assert.notNull(servletContext, "Cannot resolve ServletContextResource without ServletContext");
		this.servletContext = servletContext;
		// check path
		Assert.notNull(path, "Path is required");
		String pathToUse = StringUtils.cleanPath(path);
		if (!pathToUse.startsWith("/")) {
			pathToUse = "/" + pathToUse;
		}
		this.path = pathToUse;
	}

	//  Return the ServletContext for this resource.
	public final ServletContext getServletContext() {
		return servletContext;
	}

	// Return the path for this resource.
	public final String getPath() {
		return path;
	}

	/**
	 * This implementation checks {@code ServletContext.getResource}.
	 * @see javax.servlet.ServletContext#getResource(String)
	 */
	@Override
	public boolean exists() {
		try {
			URL url = servletContext.getResource(path);
			return (url != null);
		}catch (MalformedURLException ex) {
			return false;
		}
	}

	/**
	 * This implementation delegates to {@code ServletContext.getResourceAsStream},
	 * which returns {@code null} in case of a non-readable resource (e.g. a directory).
	 * @see javax.servlet.ServletContext#getResourceAsStream(String)
	 */
	@Override
	public boolean isReadable() {
		InputStream is = servletContext.getResourceAsStream(path);
		if (is != null) {
			try {
				is.close();
			}catch (IOException ex) {
				// ignore
			}
			return true;
		}else {
			return false;
		}
	}

	@Override
	public boolean isFile() {
		try {
			URL url = servletContext.getResource(path);
			if (url != null && ResourceUtils.isFileURL(url)) {
				return true;
			}else {
				return (servletContext.getRealPath(path) != null);
			}
		}catch (MalformedURLException ex) {
			return false;
		}
	}

	/**
	 * This implementation delegates to {@code ServletContext.getResourceAsStream},
	 * but throws a FileNotFoundException if no resource found.
	 * @see javax.servlet.ServletContext#getResourceAsStream(String)
	 */
	@Override
	public InputStream getInputStream() throws IOException {
		InputStream is = servletContext.getResourceAsStream(path);
		if (is == null) throw new FileNotFoundException("Could not open " + getDescription());
		return is;
	}

	/**
	 * This implementation delegates to {@code ServletContext.getResource},but throws a FileNotFoundException if no resource found.
	 * @see javax.servlet.ServletContext#getResource(String)
	 */
	@Override
	public URL getURL() throws IOException {
		URL url = servletContext.getResource(path);
		if (url == null) throw new FileNotFoundException(getDescription() + " cannot be resolved to URL because it does not exist");
		return url;
	}

	/**
	 * This implementation resolves "file:" URLs or alternatively delegates to
	 * {@code ServletContext.getRealPath}, throwing a FileNotFoundException
	 * if not found or not resolvable.
	 * @see javax.servlet.ServletContext#getResource(String)
	 * @see javax.servlet.ServletContext#getRealPath(String)
	 */
	@Override
	public File getFile() throws IOException {
		URL url = servletContext.getResource(path);
		if (url != null && ResourceUtils.isFileURL(url)) {
			// Proceed with file system resolution...
			return super.getFile();
		}else {
			String realPath = WebUtils.getRealPath(servletContext, path);
			return new File(realPath);
		}
	}

	/**
	 * This implementation creates a ServletContextResource, applying the given path
	 * relative to the path of the underlying file of this resource descriptor.
	 * @see org.springframework.util.StringUtils#applyRelativePath(String, String)
	 */
	@Override
	public Resource createRelative(String relativePath) {
		String pathToUse = StringUtils.applyRelativePath(path, relativePath);
		return new ServletContextResource(servletContext, pathToUse);
	}

	/**
	 * This implementation returns the name of the file that this ServletContext
	 * resource refers to.
	 * @see org.springframework.util.StringUtils#getFilename(String)
	 */
	@Override
	@Nullable
	public String getFilename() {
		return StringUtils.getFilename(path);
	}

	// This implementation returns a description that includes the ServletContext resource location.
	@Override
	public String getDescription() {
		return "ServletContext resource [" + path + "]";
	}

	@Override
	public String getPathWithinContext() {
		return path;
	}

	//  This implementation compares the underlying ServletContext resource locations.
	@Override
	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ServletContextResource)) {
			return false;
		}
		ServletContextResource otherRes = (ServletContextResource) other;
		return (servletContext.equals(otherRes.servletContext) && path.equals(otherRes.path));
	}

	// This implementation returns the hash code of the underlying ServletContext resource location.
	@Override
	public int hashCode() {
		return path.hashCode();
	}

}
