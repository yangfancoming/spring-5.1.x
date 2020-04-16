

package org.springframework.tests.aop.interceptor;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * Trivial interceptor that can be introduced in a chain to display it.
 */
public class NopInterceptor implements MethodInterceptor {

	private int count;

	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		increment();
		return invocation.proceed();
	}

	protected void increment() {
		this.count++;
	}

	public int getCount() {
		return this.count;
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof NopInterceptor)) {
			return false;
		}
		if (this == other) {
			return true;
		}
		return this.count == ((NopInterceptor) other).count;
	}

	@Override
	public int hashCode() {
		return NopInterceptor.class.hashCode();
	}

}
