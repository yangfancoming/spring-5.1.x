

package org.springframework.jmx;


public interface IJmxTestBean {

	int add(int x, int y);

	long myOperation();

	int getAge();

	void setAge(int age);

	void setName(String name) throws Exception;

	String getName();

	// used to test invalid methods that exist in the proxy interface
	void dontExposeMe();

}
