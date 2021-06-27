package org.springframework.cglib.common;

/**
 * Created by 64274 on 2019/4/9.
 *
 * @ Description: 被代理对象
 * @ author  山羊来了
 * @ date 2019/4/9---18:10
 */
public class MyDao {

    public void update() {
        System.out.println(" 执行 update() 方法");
    }

    public void select() {
		System.out.println(" 执行 select() 方法");
    }
}