

package org.springframework.context.event;

import org.springframework.context.ApplicationContext;

/**
 * Event raised when an {@code ApplicationContext} gets closed.
 * ApplicationContext 关闭时引发的事件
 * @since 12.08.2003
 * @see ContextRefreshedEvent
 */
@SuppressWarnings("serial")
public class ContextClosedEvent extends ApplicationContextEvent {

	/**
	 * Creates a new ContextClosedEvent.
	 * @param source the {@code ApplicationContext} that has been closed (must not be {@code null})
	 */
	public ContextClosedEvent(ApplicationContext source) {
		super(source);
	}
}
