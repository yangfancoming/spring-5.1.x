

package org.springframework.aop.aspectj;

/**
 * @author Ramnivas Laddad
 */
interface ICounter {

	void increment();

	void decrement();

	int getCount();

	void setCount(int counter);

	void reset();

}
