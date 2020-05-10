package com.goat.chapter185.item07.sample2;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by Administrator on 2020/5/10.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/5/10---20:40
 */
@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE) // 这里scope指定不是单例
public class B {

}