package com.goat.chapter200.xmlconfig;

import com.goat.chapter200.base.CompactDisc;
import com.goat.chapter200.base.MediaPlayer;

public class CDPlayer implements MediaPlayer {

	private CompactDisc cd;

	public CDPlayer(CompactDisc cd) {
		this.cd = cd;
	}

	@Override
	public void insert() {
		cd.play();
	}
}
