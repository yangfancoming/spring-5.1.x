package com.goat.chapter105.item07.buzz05;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2020/3/19.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/3/19---8:54
 */

@Component
public class Boss2 {

	private Car car;

	@Autowired
	public Boss2(Car car) {
		this.car = car;
		System.out.println("Boss2 有参构造器调用");
	}

	public Car getCar() {
		return car;
	}

	public void setCar(Car car) {
		this.car = car;
	}

	@Override
	public String toString() {
		return "Boss{" + "car=" + car + '}';
	}
}
