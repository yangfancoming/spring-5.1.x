

package org.springframework.tests.sample.beans;


public enum CustomEnum {

	VALUE_1, VALUE_2;

	@Override
	public String toString() {
		return "CustomEnum: " + name();
	}

}
