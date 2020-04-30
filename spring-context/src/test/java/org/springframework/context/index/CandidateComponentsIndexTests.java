

package org.springframework.context.index;

import java.util.*;

import org.junit.Before;
import org.junit.Test;
import org.springframework.util.MultiValueMap;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Tests for {@link CandidateComponentsIndex}.
 */
public class CandidateComponentsIndexTests {

	List<Properties> content;

	CandidateComponentsIndex index ;

	private static Properties createProperties(String key, String stereotypes) {
		Properties properties = new Properties();
		properties.put(key, String.join(",", stereotypes));
		return properties;
	}

	private static Properties createSampleProperties() {
		Properties properties = new Properties();
		properties.put("com.example.service.One", "service");
		properties.put("com.example.service.sub.Two", "service");
		properties.put("com.example.service.Three", "service");
		properties.put("com.example.domain.Four", "entity");
		return properties;
	}

	@Before
	public void before(){
		content = Collections.singletonList(createSampleProperties());
		index = new CandidateComponentsIndex(content);
	}

	@Test
	public void testParseIndex() {
		MultiValueMap<String, CandidateComponentsIndex.Entry> temp = CandidateComponentsIndex.parseIndex(content);
		System.out.println(temp);
	}

	@Test
	public void getCandidateTypes() {
		Set<String> actual = index.getCandidateTypes("com.example.service", "service");
		assertThat(actual, containsInAnyOrder("com.example.service.One","com.example.service.sub.Two", "com.example.service.Three"));
	}

	@Test
	public void getCandidateTypesSubPackage() {
		Set<String> actual = index.getCandidateTypes("com.example.service.sub", "service");
		assertThat(actual, containsInAnyOrder("com.example.service.sub.Two"));
	}

	@Test
	public void getCandidateTypesSubPackageNoMatch() {
		Set<String> actual = index.getCandidateTypes("com.example.service.none", "service");
		assertThat(actual, hasSize(0));
	}

	@Test
	public void getCandidateTypesNoMatch() {
		Set<String> actual = index.getCandidateTypes("com.example.service", "entity");
		assertThat(actual, hasSize(0));
	}

	@Test
	public void mergeCandidateStereotypes() {
		CandidateComponentsIndex index = new CandidateComponentsIndex(Arrays.asList(createProperties("com.example.Foo", "service"),createProperties("com.example.Foo", "entity")));
		assertThat(index.getCandidateTypes("com.example", "service"),contains("com.example.Foo"));
		assertThat(index.getCandidateTypes("com.example", "entity"),contains("com.example.Foo"));
	}
}
