package com.goat.chapter404.bean;

/**
 * Created by 64274 on 2019/8/15.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/15---16:45
 */
public class Apple {

	private int id;

	public Apple(int id) {
		this.id = id;
	}

	public void eat() {
		System.out.println("eat apple, id: " + id);
	}
}