

package org.springframework.context.index.processor;

import java.util.LinkedHashSet;
import java.util.Set;
import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;

/**
 * A {@link StereotypesProvider} that extract a stereotype for each
 * {@code javax.*} annotation placed on a class or interface.
 *
 * @author Stephane Nicoll
 * @since 5.0
 */
class StandardStereotypesProvider implements StereotypesProvider {

	private final TypeHelper typeHelper;


	StandardStereotypesProvider(TypeHelper typeHelper) {
		this.typeHelper = typeHelper;
	}


	@Override
	public Set<String> getStereotypes(Element element) {
		Set<String> stereotypes = new LinkedHashSet<>();
		ElementKind kind = element.getKind();
		if (kind != ElementKind.CLASS && kind != ElementKind.INTERFACE) {
			return stereotypes;
		}
		for (AnnotationMirror annotation : this.typeHelper.getAllAnnotationMirrors(element)) {
			String type = this.typeHelper.getType(annotation);
			if (type.startsWith("javax.")) {
				stereotypes.add(type);
			}
		}
		return stereotypes;
	}

}
