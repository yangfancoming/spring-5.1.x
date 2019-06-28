

package org.springframework.aop.framework;

public interface IEcho {

	int echoException(int i, Throwable t) throws Throwable;

	int getA();

	void setA(int a);

}
