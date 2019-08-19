package com.goat.chapter602;

import org.junit.Test;

/**
 * Created by 64274 on 2019/8/19.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/19---19:31
 */
public class App {

	/**
	 * 在抛出异常之后手动回滚事物，所以数据库表中不会增加记录
	*/
	@Test
	public void test1() {
		MyTransaction myTransaction = new MyTransaction();
		myTransaction.save();
	}
}
