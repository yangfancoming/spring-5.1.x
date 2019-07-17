

package org.springframework.aop.aspectj;


interface ICounter {

	void increment();

	void decrement();

	int getCount();

	void setCount(int counter);

	void reset();

}
