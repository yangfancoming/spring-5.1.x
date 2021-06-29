

package test.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

@Aspect("perthis(execution(* getAge()))")
public class PerThisAspect {

	private int invocations = 0;

	public int getInvocations() {
		return this.invocations;
	}

	@Around("execution(* getAge())")
	public int changeAge(ProceedingJoinPoint pjp) throws Throwable {
		return invocations++;
	}

}
