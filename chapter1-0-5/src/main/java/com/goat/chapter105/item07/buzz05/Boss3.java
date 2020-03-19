package com.goat.chapter105.item07.buzz05;

import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2020/3/19.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/3/19---8:54
 */

@Component
public class Boss3 {

	private Car car;

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
