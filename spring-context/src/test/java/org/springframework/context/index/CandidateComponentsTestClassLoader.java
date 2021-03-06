

package org.springframework.context.index;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;

/**
 * A test {@link ClassLoader} that can be used in testing context to control the
 * {@code spring.components} resource that should be loaded. Can also simulate a failure
 * by throwing a configurable {@link IOException}.
 */
public class CandidateComponentsTestClassLoader extends ClassLoader {

	/**
	 * Create a test {@link ClassLoader} that disable the use of the index, even if resources are present at the standard location.
	 * @param classLoader the classloader to use for all other operations
	 * @return a test {@link ClassLoader} that has no index
	 * @see CandidateComponentsIndexLoader#COMPONENTS_RESOURCE_LOCATION
	 */
	public static ClassLoader disableIndex(ClassLoader classLoader) {
		return new CandidateComponentsTestClassLoader(classLoader,Collections.enumeration(Collections.emptyList()));
	}

	/**
	 * Create a test {@link ClassLoader} that creates an index with the  specified {@link Resource} instances
	 * @param classLoader the classloader to use for all other operations
	 * @return a test {@link ClassLoader} with an index built based on the
	 * specified resources.
	 */
	public static ClassLoader index(ClassLoader classLoader, Resource... resources) {
		return new CandidateComponentsTestClassLoader(classLoader,
				Collections.enumeration(Stream.of(resources).map(r -> {
					try {
						return r.getURL();
					}catch (Exception ex) {
						throw new IllegalArgumentException("Invalid resource " + r, ex);
					}
				}).collect(Collectors.toList())));
	}


	private final Enumeration<URL> resourceUrls;

	private final IOException cause;

	public CandidateComponentsTestClassLoader(ClassLoader classLoader, Enumeration<URL> resourceUrls) {
		super(classLoader);
		this.resourceUrls = resourceUrls;
		this.cause = null;
	}

	public CandidateComponentsTestClassLoader(ClassLoader parent, IOException cause) {
		super(parent);
		this.resourceUrls = null;
		this.cause = cause;
	}

	@Override
	public Enumeration<URL> getResources(String name) throws IOException {
		if (CandidateComponentsIndexLoader.COMPONENTS_RESOURCE_LOCATION.equals(name)) {
			if (this.resourceUrls != null) {
				return this.resourceUrls;
			}
			throw this.cause;
		}
		return super.getResources(name);
	}

}
