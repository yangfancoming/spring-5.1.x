package com.goat.chapter200.item01;

public class MediaPlayerImpl implements MediaPlayer {

    private CompactDisc cd;

    public MediaPlayerImpl(CompactDisc cd) {
        this.cd = cd;
    }

    public void play() {
        cd.play();
    }

}
