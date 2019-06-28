

package org.springframework.context.event;

import org.springframework.context.ApplicationEvent;

/**
 * Root object used during event listener expression evaluation.
 *
 * @author Stephane Nicoll
 * @since 4.2
 */
class EventExpressionRootObject {

	private final ApplicationEvent event;

	private final Object[] args;

	public EventExpressionRootObject(ApplicationEvent event, Object[] args) {
		this.event = event;
		this.args = args;
	}

	public ApplicationEvent getEvent() {
		return this.event;
	}

	public Object[] getArgs() {
		return this.args;
	}

}
