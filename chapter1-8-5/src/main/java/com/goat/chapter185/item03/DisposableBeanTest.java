package com.goat.chapter185.item03;

import org.springframework.beans.factory.DisposableBean;

/**
 * Created by Administrator on 2020/4/21.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/4/21---14:01
 */
public class DisposableBeanTest implements DisposableBean {
	@Override
	public void destroy()  {
		System.out.println("destroy...");
	}
}
