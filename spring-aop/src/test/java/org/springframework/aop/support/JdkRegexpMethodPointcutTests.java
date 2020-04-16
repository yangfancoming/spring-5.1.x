
package org.springframework.aop.support;


public class JdkRegexpMethodPointcutTests extends AbstractRegexpMethodPointcutTests {

	@Override
	protected AbstractRegexpMethodPointcut getRegexpMethodPointcut() {
		return new JdkRegexpMethodPointcut();
	}

}
