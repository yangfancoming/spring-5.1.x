package com.goat.chapter105.item03;

import org.springframework.context.annotation.Condition;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.type.AnnotatedTypeMetadata;

/**
 * Created by 64274 on 2019/2/4.
 * @ Description: 判断是否为windows系统
 * @ author  山羊来了
 * @ date 2019/2/4---17:28
 */
public class ConditionWindows implements Condition {

    @Override
    public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
        return context.getEnvironment().getProperty("os.name").contains("Windows");
    }

}