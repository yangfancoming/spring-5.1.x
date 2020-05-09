package item01;

import org.junit.Test;

import java.util.Arrays;


/**
 *
 * 测试 注释掉 AbstractBeanFactory 类中的
 * @Override
 * public String[] getAliases(String name) {
 * 	return new String[]{"3","4"};
 * }
 * 则以后     调用的均是   new String[]{"1","2"};
 * 否则的话   调用的均是   new String[]{"3","4"};
 *
 * 直接 接口和 间接继承父类中的  相同接口方法， 再父类中实现 那么， 该实现方法 2个接口共用
 * 若是在子类中重写该方法，那么子类中的实现方法 2个接口共用。 此时想要使用父类中的接口实现方法，需要super.关键字
*/
public class App2 {


	@Test
	public void test1(){
		BeanFactory abstractBeanFactory = new AbstractBeanFactory();
		String[] aliases = abstractBeanFactory.getAliases("111");
		Arrays.stream(aliases).forEach(x->System.out.println(x));
	}

	@Test
	public void test2(){
		AliasRegistry abstractBeanFactory = new AbstractBeanFactory();
		String[] aliases = abstractBeanFactory.getAliases("111");
		Arrays.stream(aliases).forEach(x->System.out.println(x));
	}
}
