

package org.springframework.core.env;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/**
 * Unit tests for {@link PropertySource} implementations.

 * @since 3.1
 */
public class PropertySourceTests {

	@Test
	@SuppressWarnings("serial")
	public void equals() {
		Map<String, Object> map1 = new HashMap<String, Object>() {{ put("a", "b"); }};
		Map<String, Object> map2 = new HashMap<String, Object>() {{ put("c", "d"); }};
		Properties props1 = new Properties() {{ setProperty("a", "b"); }};
		Properties props2 = new Properties() {{ setProperty("c", "d"); }};

		MapPropertySource mps = new MapPropertySource("mps", map1);
		assertThat(mps, equalTo(mps));

		assertThat(new MapPropertySource("x", map1).equals(new MapPropertySource("x", map1)), is(true));
		assertThat(new MapPropertySource("x", map1).equals(new MapPropertySource("x", map2)), is(true));
		assertThat(new MapPropertySource("x", map1).equals(new PropertiesPropertySource("x", props1)), is(true));
		assertThat(new MapPropertySource("x", map1).equals(new PropertiesPropertySource("x", props2)), is(true));

		assertThat(new MapPropertySource("x", map1).equals(new Object()), is(false));
		assertThat(new MapPropertySource("x", map1).equals("x"), is(false));
		assertThat(new MapPropertySource("x", map1).equals(new MapPropertySource("y", map1)), is(false));
		assertThat(new MapPropertySource("x", map1).equals(new MapPropertySource("y", map2)), is(false));
		assertThat(new MapPropertySource("x", map1).equals(new PropertiesPropertySource("y", props1)), is(false));
		assertThat(new MapPropertySource("x", map1).equals(new PropertiesPropertySource("y", props2)), is(false));
	}

	@Test
	@SuppressWarnings("serial")
	public void collectionsOperations() {
		Map<String, Object> map1 = new HashMap<String, Object>() {{ put("a", "b"); }};
		Map<String, Object> map2 = new HashMap<String, Object>() {{ put("c", "d"); }};

		PropertySource<?> ps1 = new MapPropertySource("ps1", map1);
		ps1.getSource();
		List<PropertySource<?>> propertySources = new ArrayList<>();
		assertThat(propertySources.add(ps1), equalTo(true));
		assertThat(propertySources.contains(ps1), is(true));
		assertThat(propertySources.contains(PropertySource.named("ps1")), is(true));

		PropertySource<?> ps1replacement = new MapPropertySource("ps1", map2); // notice - different map
		assertThat(propertySources.add(ps1replacement), is(true)); // true because linkedlist allows duplicates
		assertThat(propertySources.size(), is(2));
		assertThat(propertySources.remove(PropertySource.named("ps1")), is(true));
		assertThat(propertySources.size(), is(1));
		assertThat(propertySources.remove(PropertySource.named("ps1")), is(true));
		assertThat(propertySources.size(), is(0));

		PropertySource<?> ps2 = new MapPropertySource("ps2", map2);
		propertySources.add(ps1);
		propertySources.add(ps2);
		assertThat(propertySources.indexOf(PropertySource.named("ps1")), is(0));
		assertThat(propertySources.indexOf(PropertySource.named("ps2")), is(1));
		propertySources.clear();
	}

}
