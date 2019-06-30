package org.springframework.core.annotation.beans;

import org.springframework.stereotype.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by 64274 on 2019/6/30.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/6/30---15:30
 */


@Component("meta2")
@Transactional(readOnly = true)
@Retention(RetentionPolicy.RUNTIME)
public @interface Meta2 {
}
