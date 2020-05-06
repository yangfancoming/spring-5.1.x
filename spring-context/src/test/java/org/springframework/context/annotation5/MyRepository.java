

package org.springframework.context.annotation5;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Repository;


@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Repository
@Primary
@Lazy
public @interface MyRepository {
}
