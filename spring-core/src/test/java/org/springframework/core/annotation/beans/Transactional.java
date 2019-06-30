package org.springframework.core.annotation.beans;

import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by 64274 on 2019/6/30.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/6/30---15:32
 */
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface Transactional {

	boolean readOnly() default false;
}
