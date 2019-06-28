

package org.springframework.context.index.processor;

import java.util.Set;
import javax.lang.model.element.Element;

/**
 * Provide the list of stereotypes that match an {@link Element}.
 * If an element has one more stereotypes, it is referenced in the index
 * of candidate components and each stereotype can be queried individually.
 *
 * @author Stephane Nicoll
 * @since 5.0
 */
interface StereotypesProvider {

	/**
	 * Return the stereotypes that are present on the given {@link Element}.
	 * @param element the element to handle
	 * @return the stereotypes or an empty set if none were found
	 */
	Set<String> getStereotypes(Element element);

}
