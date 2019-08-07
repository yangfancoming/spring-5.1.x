package com.goat.chapter200.item01;



public class CompactDiscImpl implements CompactDisc {

  private String title = "Sgt. Pepper's Lonely Hearts Club Band";

  private String artist = "The Beatles";
  
  public void play() {
    System.out.println("Playing " + title + " by " + artist);
  }

}
