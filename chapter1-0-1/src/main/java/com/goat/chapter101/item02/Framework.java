package com.goat.chapter101.item02;

/**
 * Created by 64274 on 2019/8/9.
 *
 * @ Description: 车身
 * @ author  山羊来了
 * @ date 2019/8/9---15:18
 */
public class Framework {

	private Bottom bottom;

	public Framework(int size) {
		this.bottom = new Bottom(size);
	}

	public Bottom getBottom() {
		return bottom;
	}

	public void setBottom(Bottom bottom) {
		this.bottom = bottom;
	}
}
