

package org.springframework.context.event.test;


@SuppressWarnings("serial")
public class TestEvent extends IdentifiableApplicationEvent {

	public final String msg;

	public TestEvent(Object source, String id, String msg) {
		super(source, id);
		this.msg = msg;
	}

	public TestEvent(Object source, String msg) {
		super(source);
		this.msg = msg;
	}

	public TestEvent(Object source) {
		this(source, "test");
	}

	public TestEvent() {
		this(new Object());
	}

}
