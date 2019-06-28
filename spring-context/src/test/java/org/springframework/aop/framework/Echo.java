

package org.springframework.aop.framework;

public class Echo implements IEcho {

	private int a;

	@Override
	public int echoException(int i, Throwable t) throws Throwable {
		if (t != null) {
			throw t;
		}
		return i;
	}

	@Override
	public void setA(int a) {
		this.a = a;
	}

	@Override
	public int getA() {
		return a;
	}

}
