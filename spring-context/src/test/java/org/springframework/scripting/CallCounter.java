

package org.springframework.scripting;


public interface CallCounter {

	void before();

	int getCalls();

}
