package com.goat.chapter100.config;

import com.goat.chapter100.Minstrel;
import com.goat.chapter100.knight.Knight;
import com.goat.chapter100.knight.impl.BraveKnight1;
import com.goat.chapter100.quest.Quest;
import com.goat.chapter100.quest.impl.RescueDamselQuest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
     * @Description: 功能描述：如果XML配置不符合你的喜好的话， Spring还支持使用Java来描述配置
     * @author: 杨帆
     * @Param:
     * @Return:
     * @Date:   2018/7/24
*/
@Configuration
public class KnightConfig1 {

    @Bean
    public Quest quest() {
        return new RescueDamselQuest();
    }
    @Bean
    public Minstrel minstrel() {
        return new Minstrel();
    }
    @Bean
    public Knight knight() {
        return new BraveKnight1(quest(),minstrel());
    }
}
