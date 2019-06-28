

package org.springframework.aop.framework;

import org.springframework.stereotype.Component;

@Component class Dependency {

	private int value = 0;

	public void method() {
		value++;
	}

	public int getValue() {
		return value;
	}
}
