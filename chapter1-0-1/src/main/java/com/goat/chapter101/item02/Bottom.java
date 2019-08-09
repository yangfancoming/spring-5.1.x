package com.goat.chapter101.item02;

/**
 * Created by 64274 on 2019/8/9.
 *
 * @ Description: 底盘
 * @ author  山羊来了
 * @ date 2019/8/9---15:16
 */
public class Bottom {

	private Tire tire;

	public Bottom(int size) {
		this.tire = new Tire(size);
	}

	public Tire getTire() {
		return tire;
	}

	public void setTire(Tire tire) {
		this.tire = tire;
	}
}
