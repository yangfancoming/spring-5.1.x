package com.goat.chapter100.knight;

import com.goat.chapter100.config.KnightConfig;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


/**
 * Created by 64274 on 2018/7/27.
 *
 * @author 山羊来了
 * @Description: TODO
 * @date 2018/7/27---14:26
     需要注意的是，这个类中完全不知道是由哪个Knight来执行何种Quest任务，
     只有knights.xml文件知道,在xml文件可以 用 任意实现了 Quest接口的类 进行替换 注入 后使用
 */


public class TestNG {
    @Test
    public void test0() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(KnightConfig.class);
        Knight knight = (Knight) ac.getBean("knight"); //
        knight.embarkOnQuest();
    }

    @Test
    public void test1() {
        ApplicationContext ac = new AnnotationConfigApplicationContext(KnightConfig.class);
        Knight knight = ac.getBean(Knight.class); // 这种方式  但这种方式  没能理解。。。
        knight.embarkOnQuest();
    }

    @Test
    public void test2() {
        ClassPathXmlApplicationContext context =  new ClassPathXmlApplicationContext("knight.xml");
         Knight knight = ((Knight) context.getBean("knight2")); // 这种方式 我可以理解
        knight.embarkOnQuest();
        context.close();
    }

}
