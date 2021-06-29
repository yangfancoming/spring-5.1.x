package com.goat.chapter201.lookupmethod.item01;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by 64274 on 2019/8/16.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/16---12:32
 */

@Component
@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class News {

}
