

package test.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class TwoAdviceAspect {

	private int totalCalls;

	@Around("execution(* org.springframework.tests.sample.beans.ITestBean.age())")
	public int returnCallCount(ProceedingJoinPoint pjp) throws Exception {
		return totalCalls;
	}

	@Before("execution(* org.springframework.tests.sample.beans.ITestBean.setAge(int)) && args(newAge)")
	public void countSet(int newAge) throws Exception {
		++totalCalls;
	}

}
