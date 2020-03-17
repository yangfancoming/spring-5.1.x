package com.goat.chapter105;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Created by 64274 on 2019/8/10.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/10---21:38
 */
public class EventManager {

	private final List<Consumer<String>> listeners = new ArrayList<>();

	private EventManager() {}

	private static class SingletonHolder {
		private static final EventManager INSTANCE = new EventManager();
	}

	public static EventManager getInstance() {
		return SingletonHolder.INSTANCE;
	}

	public void publish(final String message) {
		listeners.forEach(l -> l.accept(message));
	}

	public void addListener(Consumer<String> eventConsumer) {
		listeners.add(eventConsumer);
	}
}
