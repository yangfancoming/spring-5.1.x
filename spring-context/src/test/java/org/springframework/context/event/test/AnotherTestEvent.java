

package org.springframework.context.event.test;


@SuppressWarnings("serial")
public class AnotherTestEvent extends IdentifiableApplicationEvent {

	public final Object content;

	public AnotherTestEvent(Object source, Object content) {
		super(source);
		this.content = content;
	}

}
