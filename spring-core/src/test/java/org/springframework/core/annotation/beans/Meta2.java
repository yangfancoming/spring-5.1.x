package org.springframework.core.annotation.beans;

import org.springframework.stereotype.Component;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


@Component("meta2")
@Transactional(readOnly = true)
@Retention(RetentionPolicy.RUNTIME)
public @interface Meta2 {
}
