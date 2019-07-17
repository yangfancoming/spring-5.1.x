

package org.springframework.context.annotation.spr16756;

import org.junit.Test;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;


public class Spr16756Tests {

	@Test
	public void shouldNotFailOnNestedScopedComponent() {
		AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
		context.register(ScanningConfiguration.class);
		context.refresh();
		context.getBean(ScannedComponent.class);
		context.getBean(ScannedComponent.State.class);
	}

}
