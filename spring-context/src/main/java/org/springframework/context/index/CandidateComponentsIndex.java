

package org.springframework.context.index;

import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.util.AntPathMatcher;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

/**
 * Provide access to the candidates that are defined in {@code META-INF/spring.components}.
 * An arbitrary number of stereotypes can be registered (and queried) on the index:
 * a typical example is the fully qualified name of an annotation that flags the class for a certain use case.
 * The following call returns all the {@code @Component} <b>candidate</b> types for the {@code com.example} package (and its sub-packages):
 * <pre class="code">
 * Set&lt;String&gt; candidates = index.getCandidateTypes("com.example", "org.springframework.stereotype.Component");
 * </pre>
 * The {@code type} is usually the fully qualified name of a class, though this is not a rule.
 * Similarly, the {@code stereotype} is usually the fully qualified name of a target type but it can be any marker really.
 * @since 5.0
 */
public class CandidateComponentsIndex {

	private static final AntPathMatcher pathMatcher = new AntPathMatcher(".");

	private final MultiValueMap<String, Entry> index;

	CandidateComponentsIndex(List<Properties> content) {
		this.index = parseIndex(content);
	}

	/**
	 * Return the candidate types that are associated with the specified stereotype.
	 * @param basePackage the package to check for candidates
	 * @param stereotype the stereotype to use
	 * @return the candidate types associated with the specified {@code stereotype} or an empty set if none has been found for the specified {@code basePackage}
	 */
	public Set<String> getCandidateTypes(String basePackage, String stereotype) {
		List<Entry> candidates = this.index.get(stereotype);
		if (candidates != null) {
			return candidates.parallelStream()
					.filter(t -> t.match(basePackage))
					.map(t -> t.type)
					.collect(Collectors.toSet());
		}
		return Collections.emptySet();
	}

	public static MultiValueMap<String, Entry> parseIndex(List<Properties> content) { // -modify
		MultiValueMap<String, Entry> index = new LinkedMultiValueMap<>();
		for (Properties entry : content) {
			entry.forEach((type, values) -> {
				String[] stereotypes = ((String) values).split(",");
				for (String stereotype : stereotypes) {
					index.add(stereotype, new Entry((String) type));
				}
			});
		}
		return index;
	}

	public static class Entry { // -modify
		private final String type;
		private final String packageName;
		Entry(String type) {
			this.type = type;
			this.packageName = ClassUtils.getPackageName(type);
		}
		public boolean match(String basePackage) {
			if (pathMatcher.isPattern(basePackage)) {
				return pathMatcher.match(basePackage, this.packageName);
			}else {
				return this.type.startsWith(basePackage);
			}
		}

	}

}
