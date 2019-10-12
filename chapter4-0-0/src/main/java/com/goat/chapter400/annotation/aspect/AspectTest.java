package com.goat.chapter400.annotation.aspect;



import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;


/**
    整个表达式可以分为五个部分：
    1、execution(): 表达式主体。 execution (* com.sample.service.impl..*.*(..))
    2、第一个* 号：表示返回类型，* 号表示所有的类型。
    3、包名：表示需要拦截的包名，后面的两个句点表示当前包和当前包的所有子包，com.sample.service.impl包、子孙包下所有类的方法。
    4、第二个* 号：表示类名，* 号表示所有的类。
    5、*(..): 最后这个星号表示方法名，* 号表示所有的方法，后面括弧里面表示方法的参数，两个句点表示任何参数。

private static final String aspect =  "execution(* com.goat.con123fig..*(..))";
"execution(* com.goat.test..*(..))"   拦截 test 包下   （.. 当前test包及其所有子包 ）  *(..)中  * 任意方法名  (..) 任意参数

（1）Before ---在所拦截方法执行前执行；
（2）After  ---在所拦截方法执行后执行；
（3）AfterRuturning   ---在所拦截方法返回值后，执行；
（4）AfterThrowing     ---当所拦截方法抛出异常时，执行；
（5）Around ---最为复杂的切入方式，刚方式可以包括上述4个方式。

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
    Object result;
    try {
    //sos @Before
    result = method.invoke(target, args);
    //sos @After
    return result;
    } catch (InvocationTargetException e) {
    Throwable targetException = e.getTargetException();
    //sos @AfterThrowing
    throw targetException;
    } finally {
    //sos @AfterReturning
    }
    }
*/
// fuck  各种增强方法的 JoinPoint 参数 必须方法第一个位置 否则spring报错
@Aspect  // 定义切面  告诉spring该类是一个切面类
@Component  // 定义组件
public class AspectTest {
    /**
     * sos 多个 @Before 的执行顺序 可以通过 方法名控制  myBefore2() 先于 myBefore3() 执行
    */

	/** 前置增强  无参数*/
    @Before("execution(* com.goat.chapter400.annotation.service..*(..))")
    public void myBefore3(){
        System.out.println("前置增强3  无参数。。。。。。。。。。。");
    }

	/** 前置增强  有参数*/
    @Before("execution(* com.goat.chapter400.annotation.service..*(..))")
    public void myBefore2(JoinPoint joinPoint){
        System.out.println("前置增强2  获取参数列表 开始 。。。。。。。。。。。");
        System.out.println(joinPoint.getSignature().getName());
		Object[] args = joinPoint.getArgs();
		System.out.println(Arrays.asList(args));
		System.out.println("前置增强2  获取参数列表 结束 。。。。。。。。。。。");
    }

	/** 后置增强*/
    @After("execution(* com.goat.chapter400.annotation.service.HelloServiceImpl.sayHiService1(..))")
    public void myAfter(JoinPoint joinPoint){
        System.out.println("后置增强。。。。。开始。。。。。。");
		System.out.println(joinPoint.getSignature().getName());
		System.out.println("后置增强。。。。。结束。。。。。。");
    }

    /** 正常返回增强  可以接收 切入方法的返回值  */
    @AfterReturning(returning="rtn", pointcut="execution(* com.goat.chapter400.annotation.service.HelloServiceImpl.sayHiService1(..))")
    public void afterExec(Object rtn){
        System.out.println(" 正常返回增强 开始。。。。。。。。。。。");
        System.out.println("返回值：" + rtn);
		System.out.println(" 正常返回增强 结束。。。。。。。。。。。");
    }

    /**  异常返回增强
     声明ex时指定的类型会限制目标方法必须抛出指定类型的异常
     此处将ex的类型声明为Throwable，意味着对目标方法抛出的异常不加限制
    */
    @AfterThrowing(throwing="ex",pointcut="execution(* com.goat.chapter400.annotation.service..*(..))")
    public void afterThrowing(JoinPoint joinPoint,Throwable ex){
        System.out.println(" 异常返回增强  开始 。。。。。。。。。。。" + ex);
		System.out.println(joinPoint.getSignature().getName());
		System.out.println(" 异常返回增强  结束 。。。。。。。。。。。" + ex);
    }

	/** 环绕增强*/
    @Around("execution(* com.goat.chapter400.annotation.service..*(..))")
    public void around(ProceedingJoinPoint pjp) throws Throwable {
        System.out.println(" 环绕增强 调用目标方法前执行。。。。。。。。。。。");
        pjp.proceed(); // 如果目标方法 出现异常 则 不会执行  System.out.println(" 环绕增强 调用目标方法后执行。。。。。。。。。。。");
        System.out.println(" 环绕增强 调用目标方法后执行。。。。。。。。。。。");
    }

}
