package com.goat.chapter200.javaconfig;

import com.goat.chapter200.autoconfig.BlackPanther;
import com.goat.chapter200.autoconfig.CDPlayer;
import com.goat.chapter200.base.CompactDisc;
import com.goat.chapter200.base.MediaPlayer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * 注解表明这个类是一个配置类， 该类应该包含在Spring应用上下文中如何创建bean的细节。
 * 默认情况下， bean的ID与带有@Bean注解的方法名是一样的
 * 如果你想为其设置成一个不同的名字的话， 那么可以重命名该方法， 也可以通过name属性指定一个不同的名字
 * @Date:   2018/7/25
 */
@Configuration
public class CDPlayerConfig {
  
  @Bean
  public CompactDisc compactDisc() {
//    return new Beyond();
    return new BlackPanther();
  }
  
  @Bean
  public MediaPlayer cdPlayer(CompactDisc compactDisc) {
    return new CDPlayer(compactDisc);
  }

}
