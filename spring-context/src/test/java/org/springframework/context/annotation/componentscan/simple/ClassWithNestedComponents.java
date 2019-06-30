

package org.springframework.context.annotation.componentscan.simple;

import org.springframework.stereotype.Component;

public class ClassWithNestedComponents {

	@Component
	public static class NestedComponent extends ClassWithNestedComponents {
	}

	@Component
	public static class OtherNestedComponent extends ClassWithNestedComponents {
	}

}
