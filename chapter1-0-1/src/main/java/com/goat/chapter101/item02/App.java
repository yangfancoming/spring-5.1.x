package com.goat.chapter101.item02;

import org.junit.Test;

/**
 * Created by 64274 on 2019/8/9.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/9---15:15
 */
public class App {

	/**  改成动态的 size 需要更改所有的依赖类的构造函数。。。 */
	@Test
	public void tst(){
		int size = 44;
		Car car = new Car(size);
		car.run();
	}
}
