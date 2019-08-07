package com.goat.chapter200.xmlconfig;

import com.goat.chapter200.base.CompactDisc;
import com.goat.chapter200.base.MediaPlayer;

public class CDPlayer2 implements MediaPlayer {

	private CompactDisc cd;

	@Override
	public void insert() {
		cd.play();
	}

	public CompactDisc getCd() {
		return cd;
	}

	public void setCd(CompactDisc cd) {
		this.cd = cd;
	}
}
