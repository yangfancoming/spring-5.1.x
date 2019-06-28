

package org.springframework.validation.support;

import java.util.Map;

import org.springframework.ui.ConcurrentModel;
import org.springframework.validation.BindingResult;

/**
 * Subclass of {@link ConcurrentModel} that automatically removes
 * the {@link BindingResult} object when its corresponding
 * target attribute is replaced through regular {@link Map} operations.
 *
 * <p>This is the class exposed to handler methods by Spring WebFlux,
 * typically consumed through a declaration of the
 * {@link org.springframework.ui.Model} interface as a parameter type.
 * There is typically no need to create it within user code.
 * If necessary a handler method can return a regular {@code java.util.Map},
 * likely a {@code java.util.ConcurrentMap}, for a pre-determined model.
 *
 * @author Rossen Stoyanchev
 * @since 5.0
 * @see BindingResult
 */
@SuppressWarnings("serial")
public class BindingAwareConcurrentModel extends ConcurrentModel {

	@Override
	public Object put(String key, Object value) {
		removeBindingResultIfNecessary(key, value);
		return super.put(key, value);
	}

	private void removeBindingResultIfNecessary(String key, Object value) {
		if (!key.startsWith(BindingResult.MODEL_KEY_PREFIX)) {
			String resultKey = BindingResult.MODEL_KEY_PREFIX + key;
			BindingResult result = (BindingResult) get(resultKey);
			if (result != null && result.getTarget() != value) {
				remove(resultKey);
			}
		}
	}

}
