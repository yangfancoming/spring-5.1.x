package org.springframework.tests.sample.objects.goat;

/**
 * Created by Administrator on 2021/6/27.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/6/27---23:23
 * 1.接口中的所有方法的范围修饰符，都必须是public ，因此可以忽略不写
 * 2.接口中的方法类型 默认是  abstract  ，因此可以忽略不写。  类型分为：abstract、default 、static
 * 3.接口中若有 static 和 default 方法，必须要当即实现（默认实现）
 * 4.接口实现类中 abstract 必须实现， default 可选实现，static 不能实现。
 */
public interface GoatBaseInterface  {

	public abstract String interfaceAbstract();

	public default String interfaceDefault(){
		return "default-base";
	}

	public static String interfaceStatic(){
		return "static-base";
	}
}
