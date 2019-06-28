

package org.springframework.scripting;

/**
 * @author Juergen Hoeller
 */
public interface CallCounter {

	void before();

	int getCalls();

}
