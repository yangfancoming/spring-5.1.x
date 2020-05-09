package item01;

import org.junit.Test;

import java.util.Arrays;


/**
 * 测试一个类  继承类A 实现接口B  其中A 和B中 有同样的方法，C类同时继承和实现后
 * 该如何调用
*/
public class App1 {

	AbstractBeanFactory abstractBeanFactory = new AbstractBeanFactory();

	// 调用接口实现方法
	@Test
	public void test1(){
		String[] aliases = abstractBeanFactory.getAliases("111");
		Arrays.stream(aliases).forEach(x->System.out.println(x));
	}

	// 调用 继承父类中的方法
	@Test
	public void test2(){
		String[] aliases = abstractBeanFactory.test("111");
		Arrays.stream(aliases).forEach(x->System.out.println(x));
	}

}
