package com.goat.chapter200.autoconfig;


import com.goat.chapter200.base.CompactDisc;
import org.springframework.stereotype.Component;

/**
 * 黑豹乐队
 *  手动显示指定 bean 的名称
*/
@Component(value = "bp")
public class BlackPanther implements CompactDisc {

  private String title = "无地自容";

  private String artist = "黑豹 乐队";

  @Override
  public void play() {
    System.out.println("Playing " + title + " by " + artist);
  }

}
