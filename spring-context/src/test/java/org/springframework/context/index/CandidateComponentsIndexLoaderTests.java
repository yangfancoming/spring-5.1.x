

package org.springframework.context.index;

import java.io.IOException;
import java.util.Set;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.springframework.core.io.ClassPathResource;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * Tests for {@link CandidateComponentsIndexLoader}.
 *
 * @author Stephane Nicoll
 */
public class CandidateComponentsIndexLoaderTests {

	@Rule
	public final ExpectedException thrown = ExpectedException.none();


	@Test
	public void validateIndexIsDisabledByDefault() {
		CandidateComponentsIndex index = CandidateComponentsIndexLoader.loadIndex(null);
		assertThat("No spring.components should be available at the default location", index, is(nullValue()));
	}

	@Test
	public void loadIndexSeveralMatches() {
		CandidateComponentsIndex index = CandidateComponentsIndexLoader.loadIndex(
				CandidateComponentsTestClassLoader.index(getClass().getClassLoader(),
						new ClassPathResource("spring.components", getClass())));
		Set<String> components = index.getCandidateTypes("org.springframework", "foo");
		assertThat(components, containsInAnyOrder(
				"org.springframework.context.index.Sample1",
				"org.springframework.context.index.Sample2"));
	}

	@Test
	public void loadIndexSingleMatch() {
		CandidateComponentsIndex index = CandidateComponentsIndexLoader.loadIndex(
				CandidateComponentsTestClassLoader.index(getClass().getClassLoader(),
						new ClassPathResource("spring.components", getClass())));
		Set<String> components = index.getCandidateTypes("org.springframework", "biz");
		assertThat(components, containsInAnyOrder(
				"org.springframework.context.index.Sample3"));
	}

	@Test
	public void loadIndexNoMatch() {
		CandidateComponentsIndex index = CandidateComponentsIndexLoader.loadIndex(
				CandidateComponentsTestClassLoader.index(getClass().getClassLoader(),
						new ClassPathResource("spring.components", getClass())));
		Set<String> components = index.getCandidateTypes("org.springframework", "none");
		assertThat(components, hasSize(0));
	}

	@Test
	public void loadIndexNoPackage() {
		CandidateComponentsIndex index = CandidateComponentsIndexLoader.loadIndex(
				CandidateComponentsTestClassLoader.index(getClass().getClassLoader(),
						new ClassPathResource("spring.components", getClass())));
		Set<String> components = index.getCandidateTypes("com.example", "foo");
		assertThat(components, hasSize(0));
	}

	@Test
	public void loadIndexNoSpringComponentsResource() {
		CandidateComponentsIndex index = CandidateComponentsIndexLoader.loadIndex(
				CandidateComponentsTestClassLoader.disableIndex(getClass().getClassLoader()));
		assertThat(index, is(nullValue()));
	}

	@Test
	public void loadIndexNoEntry() throws IOException {
		CandidateComponentsIndex index = CandidateComponentsIndexLoader.loadIndex(
				CandidateComponentsTestClassLoader.index(getClass().getClassLoader(),
						new ClassPathResource("empty-spring.components", getClass())));
		assertThat(index, is(nullValue()));
	}

	@Test
	public void loadIndexWithException() throws IOException {
		final IOException cause = new IOException("test exception");
		this.thrown.expect(IllegalStateException.class);
		this.thrown.expectMessage("Unable to load indexes");
		this.thrown.expectCause(is(cause));
		CandidateComponentsIndexLoader.loadIndex(new CandidateComponentsTestClassLoader(
				getClass().getClassLoader(), cause));
	}

}
