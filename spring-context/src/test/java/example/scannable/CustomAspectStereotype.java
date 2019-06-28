

package example.scannable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.stereotype.Component;

/**
 * @author Juergen Hoeller
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Component
public @interface CustomAspectStereotype {

	/**
	 * Not a plain String value - needs to be ignored during name detection.
	 */
	String[] value() default {};

}
