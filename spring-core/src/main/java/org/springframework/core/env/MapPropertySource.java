

package org.springframework.core.env;

import java.util.Map;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * {@link PropertySource} that reads keys and values from a {@code Map} object.
 * @since 3.1
 * @see PropertiesPropertySource
 */
public class MapPropertySource extends EnumerablePropertySource<Map<String, Object>> {

	public MapPropertySource(String name, Map<String, Object> source) {
		super(name, source);
	}

	//---------------------------------------------------------------------
	// Implementation of 【PropertySource】 class
	//---------------------------------------------------------------------
	@Override
	@Nullable
	public Object getProperty(String name) {
		return this.source.get(name);
	}

	//---------------------------------------------------------------------
	// Implementation of 【EnumerablePropertySource】 class
	//---------------------------------------------------------------------
	@Override
	public boolean containsProperty(String name) {
		return this.source.containsKey(name);
	}


	@Override
	public String[] getPropertyNames() {
		return StringUtils.toStringArray(this.source.keySet());
	}

}
