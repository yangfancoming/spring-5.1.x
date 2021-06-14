package com.goat.chapter200.autoconfig;


import com.goat.chapter200.base.CompactDisc;
import org.springframework.stereotype.Component;

/**
 *   Beyond 乐队
*/
@Component
public class Beyond implements CompactDisc {

  private String title = "不再犹豫";

  private String artist = "beyond 乐队";

  @Override
  public void play() {
    System.out.println("Playing " + title + " by " + artist);
  }

}
