

package org.springframework.context.index.processor;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents one entry in the index. The type defines the identify of the target
 * candidate (usually fully qualified name) and the stereotypes are "markers" that can
 * be used to retrieve the candidates. A typical use case is the presence of a given
 * annotation on the candidate.
 *
 * @author Stephane Nicoll
 * @since 5.0
 */
class ItemMetadata {

	private final String type;

	private final Set<String> stereotypes;


	public ItemMetadata(String type, Set<String> stereotypes) {
		this.type = type;
		this.stereotypes = new HashSet<>(stereotypes);
	}


	public String getType() {
		return this.type;
	}

	public Set<String> getStereotypes() {
		return this.stereotypes;
	}

}
