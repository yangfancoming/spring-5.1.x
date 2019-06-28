

package org.springframework.context.event;

import org.junit.Test;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.ResolvableType;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * @author Stephane Nicoll
 */
public class GenericApplicationListenerAdapterTests extends AbstractApplicationEventListenerTests {

	@Test
	public void supportsEventTypeWithSmartApplicationListener() {
		SmartApplicationListener smartListener = mock(SmartApplicationListener.class);
		GenericApplicationListenerAdapter listener = new GenericApplicationListenerAdapter(smartListener);
		ResolvableType type = ResolvableType.forClass(ApplicationEvent.class);
		listener.supportsEventType(type);
		verify(smartListener, times(1)).supportsEventType(ApplicationEvent.class);
	}

	@Test
	public void supportsSourceTypeWithSmartApplicationListener() {
		SmartApplicationListener smartListener = mock(SmartApplicationListener.class);
		GenericApplicationListenerAdapter listener = new GenericApplicationListenerAdapter(smartListener);
		listener.supportsSourceType(Object.class);
		verify(smartListener, times(1)).supportsSourceType(Object.class);
	}

	@Test
	public void genericListenerStrictType() {
		supportsEventType(true, StringEventListener.class, getGenericApplicationEventType("stringEvent"));
	}

	@Test // Demonstrates we can't inject that event because the generic type is lost
	public void genericListenerStrictTypeTypeErasure() {
		GenericTestEvent<String> stringEvent = createGenericTestEvent("test");
		ResolvableType eventType = ResolvableType.forType(stringEvent.getClass());
		supportsEventType(false, StringEventListener.class, eventType);
	}

	@Test // But it works if we specify the type properly
	public void genericListenerStrictTypeAndResolvableType() {
		ResolvableType eventType = ResolvableType
				.forClassWithGenerics(GenericTestEvent.class, String.class);
		supportsEventType(true, StringEventListener.class, eventType);
	}

	@Test // or if the event provides its precise type
	public void genericListenerStrictTypeAndResolvableTypeProvider() {
		ResolvableType eventType = new SmartGenericTestEvent<>(this, "foo").getResolvableType();
		supportsEventType(true, StringEventListener.class, eventType);
	}

	@Test // Demonstrates it works if we actually use the subtype
	public void genericListenerStrictTypeEventSubType() {
		StringEvent stringEvent = new StringEvent(this, "test");
		ResolvableType eventType = ResolvableType.forType(stringEvent.getClass());
		supportsEventType(true, StringEventListener.class, eventType);
	}

	@Test
	public void genericListenerStrictTypeNotMatching() {
		supportsEventType(false, StringEventListener.class, getGenericApplicationEventType("longEvent"));
	}

	@Test
	public void genericListenerStrictTypeEventSubTypeNotMatching() {
		LongEvent stringEvent = new LongEvent(this, 123L);
		ResolvableType eventType = ResolvableType.forType(stringEvent.getClass());
		supportsEventType(false, StringEventListener.class, eventType);
	}

	@Test
	public void genericListenerStrictTypeNotMatchTypeErasure() {
		GenericTestEvent<Long> longEvent = createGenericTestEvent(123L);
		ResolvableType eventType = ResolvableType.forType(longEvent.getClass());
		supportsEventType(false, StringEventListener.class, eventType);
	}

	@Test
	public void genericListenerStrictTypeSubClass() {
		supportsEventType(false, ObjectEventListener.class, getGenericApplicationEventType("longEvent"));
	}

	@Test
	public void genericListenerUpperBoundType() {
		supportsEventType(true, UpperBoundEventListener.class,
				getGenericApplicationEventType("illegalStateExceptionEvent"));
	}

	@Test
	public void genericListenerUpperBoundTypeNotMatching() {
		supportsEventType(false, UpperBoundEventListener.class,
				getGenericApplicationEventType("ioExceptionEvent"));
	}

	@Test
	public void genericListenerWildcardType() {
		supportsEventType(true, GenericEventListener.class,
				getGenericApplicationEventType("stringEvent"));
	}

	@Test  // Demonstrates we cant inject that event because the listener has a wildcard
	public void genericListenerWildcardTypeTypeErasure() {
		GenericTestEvent<String> stringEvent = createGenericTestEvent("test");
		ResolvableType eventType = ResolvableType.forType(stringEvent.getClass());
		supportsEventType(true, GenericEventListener.class, eventType);
	}

	@Test
	public void genericListenerRawType() {
		supportsEventType(true, RawApplicationListener.class,
				getGenericApplicationEventType("stringEvent"));
	}

	@Test  // Demonstrates we cant inject that event because the listener has a raw type
	public void genericListenerRawTypeTypeErasure() {
		GenericTestEvent<String> stringEvent = createGenericTestEvent("test");
		ResolvableType eventType = ResolvableType.forType(stringEvent.getClass());
		supportsEventType(true, RawApplicationListener.class, eventType);
	}


	private void supportsEventType(
			boolean match, Class<? extends ApplicationListener> listenerType, ResolvableType eventType) {

		ApplicationListener<?> listener = mock(listenerType);
		GenericApplicationListenerAdapter adapter = new GenericApplicationListenerAdapter(listener);
		assertEquals("Wrong match for event '" + eventType + "' on " + listenerType.getClass().getName(),
				match, adapter.supportsEventType(eventType));
	}

}
