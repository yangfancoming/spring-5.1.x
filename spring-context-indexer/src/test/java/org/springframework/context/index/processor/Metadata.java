

package org.springframework.context.index.processor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;

/**
 * Hamcrest {@link org.hamcrest.Matcher Matcher} to help test {@link CandidateComponentsMetadata}.
 *
 * @author Stephane Nicoll
 */
public class Metadata {

	public static ItemMetadataMatcher hasComponent(Class<?> type, Class<?>... stereotypes) {
		return new ItemMetadataMatcher(type.getName(), stereotypes);
	}

	public static ItemMetadataMatcher hasComponent(String type, String... stereotypes) {
		return new ItemMetadataMatcher(type, stereotypes);
	}


	private static class ItemMetadataMatcher extends BaseMatcher<CandidateComponentsMetadata> {

		private final String type;

		private final List<String> stereotypes;

		private ItemMetadataMatcher(String type, List<String> stereotypes) {
			this.type = type;
			this.stereotypes = stereotypes;
		}

		public ItemMetadataMatcher(String type, String... stereotypes) {
			this(type, Arrays.asList(stereotypes));
		}

		public ItemMetadataMatcher(String type, Class<?>... stereotypes) {
			this(type, Arrays.stream(stereotypes)
					.map(Class::getName).collect(Collectors.toList()));
		}

		@Override
		public boolean matches(Object value) {
			if (!(value instanceof CandidateComponentsMetadata)) {
				return false;
			}
			ItemMetadata itemMetadata = getFirstItemWithType((CandidateComponentsMetadata) value, this.type);
			if (itemMetadata == null) {
				return false;
			}
			if (this.type != null && !this.type.equals(itemMetadata.getType())) {
				return false;
			}
			if (this.stereotypes != null) {
				for (String stereotype : this.stereotypes) {
					if (!itemMetadata.getStereotypes().contains(stereotype)) {
						return false;
					}
				}
				if (this.stereotypes.size() != itemMetadata.getStereotypes().size()) {
					return false;
				}
			}
			return true;
		}

		private ItemMetadata getFirstItemWithType(CandidateComponentsMetadata metadata, String type) {
			for (ItemMetadata item : metadata.getItems()) {
				if (item.getType().equals(type)) {
					return item;
				}
			}
			return null;
		}

		@Override
		public void describeTo(Description description) {
			description.appendText("Candidates with type ").appendValue(this.type);
			description.appendText(" and stereotypes ").appendValue(this.stereotypes);
		}
	}

}
