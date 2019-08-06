package com.goat.chapter100.knight.impl;


import com.goat.chapter100.knight.Knight;
import com.goat.chapter100.quest.impl.RescueDamselQuest;

/**

正如你所见，DamselRescuingKnight 在它的构造函数中自行创建了RescueDamselQuest，
这使得DamselRescuingKnight和RescueDamselQuest紧密地耦合到了一起，
因此极大地限制了这个骑士的执行能力。
如果一个少女需要救援，这个骑士能够召之即来。
但是如果一条恶龙需要杀掉，那么这个骑士只能爱莫能助了
     * @Date:   2018/7/24
*/
public class DamselRescuingKnight implements Knight {

  private RescueDamselQuest quest;

  public DamselRescuingKnight() {
    this.quest = new RescueDamselQuest(); // 与 RescueDamselQuest 紧耦合  因为没能实现多态！！！
  }

    @Override
    public void embarkOnQuest() {
        quest.embark();
    }

}
