

package org.springframework.web.reactive.result.view.freemarker;

import java.io.IOException;
import java.util.List;

import freemarker.cache.ClassTemplateLoader;
import freemarker.cache.TemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.TemplateException;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.lang.Nullable;
import org.springframework.ui.freemarker.FreeMarkerConfigurationFactory;
import org.springframework.util.Assert;

/**
 * Configures FreeMarker for web usage via the "configLocation" and/or
 * "freemarkerSettings" and/or "templateLoaderPath" properties.
 * The simplest way to use this class is to specify just a "templateLoaderPath"
 * (e.g. "classpath:templates"); you do not need any further configuration then.
 *
 * This bean must be included in the application context of any application
 * using {@link FreeMarkerView}. It exists purely to configure FreeMarker.
 * It is not meant to be referenced by application components but just internally
 * by {@code FreeMarkerView}. Implements {@link FreeMarkerConfig} to be found by
 * {@code FreeMarkerView} without depending on the bean name the configurer.
 *
 * Note that you can also refer to a pre-configured FreeMarker Configuration
 * instance via the "configuration" property. This allows to share a FreeMarker
 * Configuration for web and email usage for example.
 *
 * TODO: macros
 *
 * This configurer registers a template loader for this package, allowing to
 * reference the "spring.ftl" macro library contained in this package:
 *
 * <pre class="code">
 * &lt;#import "/spring.ftl" as spring/&gt;
 * &lt;@spring.bind "person.age"/&gt;
 * age is ${spring.status.value}</pre>
 *
 * Note: Spring's FreeMarker support requires FreeMarker 2.3 or higher.
 *
 *
 * @since 5.0
 */
public class FreeMarkerConfigurer extends FreeMarkerConfigurationFactory
		implements FreeMarkerConfig, InitializingBean, ResourceLoaderAware {

	@Nullable
	private Configuration configuration;


	public FreeMarkerConfigurer() {
		setDefaultEncoding("UTF-8");
	}


	/**
	 * Set a pre-configured Configuration to use for the FreeMarker web config,
	 * e.g. a shared one for web and email usage. If this is not set,
	 * FreeMarkerConfigurationFactory's properties (inherited by this class)
	 * have to be specified.
	 */
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}


	/**
	 * Initialize FreeMarkerConfigurationFactory's Configuration
	 * if not overridden by a pre-configured FreeMarker Configuration.
	 * Sets up a ClassTemplateLoader to use for loading Spring macros.
	 * @see #createConfiguration
	 * @see #setConfiguration
	 */
	@Override
	public void afterPropertiesSet() throws IOException, TemplateException {
		if (this.configuration == null) {
			this.configuration = createConfiguration();
		}
	}

	/**
	 * This implementation registers an additional ClassTemplateLoader
	 * for the Spring-provided macros, added to the end of the list.
	 */
	@Override
	protected void postProcessTemplateLoaders(List<TemplateLoader> templateLoaders) {
		templateLoaders.add(new ClassTemplateLoader(FreeMarkerConfigurer.class, ""));
	}


	/**
	 * Return the Configuration object wrapped by this bean.
	 */
	@Override
	public Configuration getConfiguration() {
		Assert.state(this.configuration != null, "No Configuration available");
		return this.configuration;
	}

}
