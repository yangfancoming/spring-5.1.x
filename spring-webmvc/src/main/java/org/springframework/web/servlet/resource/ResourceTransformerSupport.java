

package org.springframework.web.servlet.resource;

import java.util.Collections;
import javax.servlet.http.HttpServletRequest;

import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * A base class for a {@code ResourceTransformer} with an optional helper method
 * for resolving public links within a transformed resource.
 *
 * @author Brian Clozel
 *

 * @since 4.1
 */
public abstract class ResourceTransformerSupport implements ResourceTransformer {

	@Nullable
	private ResourceUrlProvider resourceUrlProvider;


	/**
	 * Configure a {@link ResourceUrlProvider} to use when resolving the public
	 * URL of links in a transformed resource (e.g. import links in a CSS file).
	 * This is required only for links expressed as full paths and not for
	 * relative links.
	 */
	public void setResourceUrlProvider(@Nullable ResourceUrlProvider resourceUrlProvider) {
		this.resourceUrlProvider = resourceUrlProvider;
	}

	/**
	 * Return the configured {@code ResourceUrlProvider}.
	 */
	@Nullable
	public ResourceUrlProvider getResourceUrlProvider() {
		return this.resourceUrlProvider;
	}


	/**
	 * A transformer can use this method when a resource being transformed
	 * contains links to other resources. Such links need to be replaced with the
	 * public facing link as determined by the resource resolver chain (e.g. the
	 * public URL may have a version inserted).
	 * @param resourcePath the path to a resource that needs to be re-written
	 * @param request the current request
	 * @param resource the resource being transformed
	 * @param transformerChain the transformer chain
	 * @return the resolved URL, or {@code} if not resolvable
	 */
	@Nullable
	protected String resolveUrlPath(String resourcePath, HttpServletRequest request,
			Resource resource, ResourceTransformerChain transformerChain) {

		if (resourcePath.startsWith("/")) {
			// full resource path
			ResourceUrlProvider urlProvider = findResourceUrlProvider(request);
			return (urlProvider != null ? urlProvider.getForRequestUrl(request, resourcePath) : null);
		}
		else {
			// try resolving as relative path
			return transformerChain.getResolverChain().resolveUrlPath(
					resourcePath, Collections.singletonList(resource));
		}
	}

	/**
	 * Transform the given relative request path to an absolute path,
	 * taking the path of the given request as a point of reference.
	 * The resulting path is also cleaned from sequences like "path/..".
	 * @param path the relative path to transform
	 * @param request the referer request
	 * @return the absolute request path for the given resource path
	 */
	protected String toAbsolutePath(String path, HttpServletRequest request) {
		String absolutePath = path;
		if (!path.startsWith("/")) {
			ResourceUrlProvider urlProvider = findResourceUrlProvider(request);
			Assert.state(urlProvider != null, "No ResourceUrlProvider");
			String requestPath = urlProvider.getUrlPathHelper().getRequestUri(request);
			absolutePath = StringUtils.applyRelativePath(requestPath, path);
		}
		return StringUtils.cleanPath(absolutePath);
	}

	@Nullable
	private ResourceUrlProvider findResourceUrlProvider(HttpServletRequest request) {
		if (this.resourceUrlProvider != null) {
			return this.resourceUrlProvider;
		}
		return (ResourceUrlProvider) request.getAttribute(
				ResourceUrlProviderExposingInterceptor.RESOURCE_URL_PROVIDER_ATTR);
	}

}
