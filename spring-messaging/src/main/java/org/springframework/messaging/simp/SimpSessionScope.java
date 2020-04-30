

package org.springframework.messaging.simp;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.config.Scope;
import org.springframework.lang.Nullable;

/**
 * A {@link Scope} implementation exposing the attributes of a SiMP session
 * (e.g. WebSocket session).
 *
 * Relies on a thread-bound {@link SimpAttributes} instance exported by
 * {@link org.springframework.messaging.simp.annotation.support.SimpAnnotationMethodMessageHandler}.
 *
 *
 * @since 4.1
 */
public class SimpSessionScope implements Scope {

	@Override
	public Object get(String name, ObjectFactory<?> objectFactory) {
		SimpAttributes simpAttributes = SimpAttributesContextHolder.currentAttributes();
		Object scopedObject = simpAttributes.getAttribute(name);
		if (scopedObject != null) {
			return scopedObject;
		}
		synchronized (simpAttributes.getSessionMutex()) {
			scopedObject = simpAttributes.getAttribute(name);
			if (scopedObject == null) {
				scopedObject = objectFactory.getObject();
				simpAttributes.setAttribute(name, scopedObject);
			}
			return scopedObject;
		}
	}

	@Override
	@Nullable
	public Object remove(String name) {
		SimpAttributes simpAttributes = SimpAttributesContextHolder.currentAttributes();
		synchronized (simpAttributes.getSessionMutex()) {
			Object value = simpAttributes.getAttribute(name);
			if (value != null) {
				simpAttributes.removeAttribute(name);
				return value;
			}
			else {
				return null;
			}
		}
	}

	@Override
	public void registerDestructionCallback(String name, Runnable callback) {
		SimpAttributesContextHolder.currentAttributes().registerDestructionCallback(name, callback);
	}

	@Override
	@Nullable
	public Object resolveContextualObject(String key) {
		return null;
	}

	@Override
	public String getConversationId() {
		return SimpAttributesContextHolder.currentAttributes().getSessionId();
	}

}
