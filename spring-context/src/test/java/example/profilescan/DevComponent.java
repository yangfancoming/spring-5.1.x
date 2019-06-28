

package example.profilescan;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Profile(DevComponent.PROFILE_NAME)
@Component
public @interface DevComponent {

	String PROFILE_NAME = "dev";

	String value() default "";

}
