

package org.springframework.ui.freemarker;

import java.io.IOException;

import freemarker.template.Configuration;
import freemarker.template.TemplateException;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ResourceLoaderAware;
import org.springframework.lang.Nullable;

/**
 * Factory bean that creates a FreeMarker Configuration and provides it as
 * bean reference. This bean is intended for any kind of usage of FreeMarker
 * in application code, e.g. for generating email content. For web views,
 * FreeMarkerConfigurer is used to set up a FreeMarkerConfigurationFactory.
 *
 * The simplest way to use this class is to specify just a "templateLoaderPath";
 * you do not need any further configuration then. For example, in a web
 * application context:
 *
 * <pre class="code"> &lt;bean id="freemarkerConfiguration" class="org.springframework.ui.freemarker.FreeMarkerConfigurationFactoryBean"&gt;
 *   &lt;property name="templateLoaderPath" value="/WEB-INF/freemarker/"/&gt;
 * &lt;/bean&gt;</pre>

 * See the base class FreeMarkerConfigurationFactory for configuration details.
 *
 * xmlBeanDefinitionReaderNote: Spring's FreeMarker support requires FreeMarker 2.3 or higher.
 *
 * @author Darren Davison
 * @since 03.03.2004
 * @see #setConfigLocation
 * @see #setFreemarkerSettings
 * @see #setTemplateLoaderPath
 * @see org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer
 */
public class FreeMarkerConfigurationFactoryBean extends FreeMarkerConfigurationFactory
		implements FactoryBean<Configuration>, InitializingBean, ResourceLoaderAware {

	@Nullable
	private Configuration configuration;


	@Override
	public void afterPropertiesSet() throws IOException, TemplateException {
		this.configuration = createConfiguration();
	}


	@Override
	@Nullable
	public Configuration getObject() {
		return this.configuration;
	}

	@Override
	public Class<? extends Configuration> getObjectType() {
		return Configuration.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
