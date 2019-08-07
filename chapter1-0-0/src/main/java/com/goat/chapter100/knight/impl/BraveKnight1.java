package com.goat.chapter100.knight.impl;


import com.goat.chapter100.Minstrel;
import com.goat.chapter100.knight.Knight;
import com.goat.chapter100.quest.Quest;


public class BraveKnight1 implements Knight {

  private Quest quest;
  private Minstrel minstrel;

  public BraveKnight1(Quest quest,Minstrel minstrel) {  //    构造器注入  这里松耦合 因为实现了多态！！！
    this.quest = quest;
    this.minstrel = minstrel;
  }

    @Override
    public void embarkOnQuest() {
        minstrel.singBeforeQuest();
        quest.embark();
        minstrel.singAfterQuest();
    }

}
