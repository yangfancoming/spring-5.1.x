package com.goat.chapter200.autoconfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;


@Component
public class BoxPlayer implements MediaPlayer {

	private CompactDisc cd;

	/**
	 *  注入方式二：setter方法注入
	 * @Date:   2019/8/7
	*/

	@Autowired(required = false)
	public void setCd(@Qualifier("beyond") CompactDisc cd) {
		this.cd = cd;
	}

	@Override
	public void insert() {
		cd.play();
	}

}
