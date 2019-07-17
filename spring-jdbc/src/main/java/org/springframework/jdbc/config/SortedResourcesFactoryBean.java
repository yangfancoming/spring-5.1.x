

package org.springframework.jdbc.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.config.AbstractFactoryBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternUtils;

/**
 * {@link FactoryBean} implementation that takes a list of location Strings
 * and creates a sorted array of {@link Resource} instances.
 *
 * @author Dave Syer

 * @author Christian Dupuis
 * @since 3.0
 */
public class SortedResourcesFactoryBean extends AbstractFactoryBean<Resource[]> implements ResourceLoaderAware {

	private final List<String> locations;

	private ResourcePatternResolver resourcePatternResolver;


	public SortedResourcesFactoryBean(List<String> locations) {
		this.locations = locations;
		this.resourcePatternResolver = new PathMatchingResourcePatternResolver();
	}

	public SortedResourcesFactoryBean(ResourceLoader resourceLoader, List<String> locations) {
		this.locations = locations;
		this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
	}


	@Override
	public void setResourceLoader(ResourceLoader resourceLoader) {
		this.resourcePatternResolver = ResourcePatternUtils.getResourcePatternResolver(resourceLoader);
	}


	@Override
	public Class<? extends Resource[]> getObjectType() {
		return Resource[].class;
	}

	@Override
	protected Resource[] createInstance() throws Exception {
		List<Resource> scripts = new ArrayList<>();
		for (String location : this.locations) {
			List<Resource> resources = new ArrayList<>(
					Arrays.asList(this.resourcePatternResolver.getResources(location)));
			resources.sort((r1, r2) -> {
				try {
					return r1.getURL().toString().compareTo(r2.getURL().toString());
				}
				catch (IOException ex) {
					return 0;
				}
			});
			scripts.addAll(resources);
		}
		return scripts.toArray(new Resource[0]);
	}

}
