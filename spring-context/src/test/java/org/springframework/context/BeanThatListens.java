

package org.springframework.context;

import java.util.Map;

/**
 * A stub {@link ApplicationListener}.
 */
public class BeanThatListens implements ApplicationListener<ApplicationEvent> {

	private BeanThatBroadcasts beanThatBroadcasts;

	private int eventCount;

	public BeanThatListens() {
	}

	public BeanThatListens(BeanThatBroadcasts beanThatBroadcasts) {
		this.beanThatBroadcasts = beanThatBroadcasts;
		Map<?, BeanThatListens> beans = beanThatBroadcasts.applicationContext.getBeansOfType(BeanThatListens.class);
		if (!beans.isEmpty()) {
			throw new IllegalStateException("Shouldn't have found any BeanThatListens instances");
		}
	}


	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		eventCount++;
		if (beanThatBroadcasts != null) {
			beanThatBroadcasts.receivedCount++;
		}
	}

	public int getEventCount() {
		return eventCount;
	}

	public void zero() {
		eventCount = 0;
	}

}
