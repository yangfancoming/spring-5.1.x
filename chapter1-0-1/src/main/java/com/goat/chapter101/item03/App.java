package com.goat.chapter101.item03;

import org.junit.Test;

/**
 * Created by 64274 on 2019/8/9.
 *
 * @ Description:  所有类都使用了构造函数注入的方式来解决传统的依赖问题
 * @ author  山羊来了
 * @ date 2019/8/9---15:15
 */
public class App {

	/**  新版写死的 size  使用方法  */
	@Test
	public void tst(){
		Tire tire = new Tire();
		Bottom bottom = new Bottom(tire);
		Framework framework = new Framework(bottom);
		Car car = new Car(framework);
		car.run();
	}

	/**  改成动态的 size 只需要修改轮胎类就行了，不用修改其他任何上层类 */
	@Test
	public void tst1(){
		Tire tire = new Tire(11);
		Bottom bottom = new Bottom(tire);
		Framework framework = new Framework(bottom);
		Car car = new Car(framework);
		car.run();
	}
}
