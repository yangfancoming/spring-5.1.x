

package org.springframework.web.context.support;

import javax.servlet.ServletConfig;

import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * {@link PropertySource} that reads init parameters from a {@link ServletConfig} object.
 *
 * @author Chris Beams
 * @since 3.1
 * @see ServletContextPropertySource
 */
public class ServletConfigPropertySource extends EnumerablePropertySource<ServletConfig> {

	public ServletConfigPropertySource(String name, ServletConfig servletConfig) {
		super(name, servletConfig);
	}

	@Override
	public String[] getPropertyNames() {
		return StringUtils.toStringArray(this.source.getInitParameterNames());
	}

	@Override
	@Nullable
	public String getProperty(String name) {
		return this.source.getInitParameter(name);
	}

}
