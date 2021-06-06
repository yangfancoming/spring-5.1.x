
package example.scannable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;


@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Service
@Scope("prototype")
public @interface CustomStereotype {
	String value() default "thoreau";
}
