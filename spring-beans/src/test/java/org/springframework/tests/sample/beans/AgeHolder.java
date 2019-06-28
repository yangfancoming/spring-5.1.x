

package org.springframework.tests.sample.beans;

public interface AgeHolder {

	default int age() {
		return getAge();
	}

	int getAge();

	void setAge(int age);

}
