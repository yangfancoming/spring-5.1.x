package javax.servlet.annotation;

import java.lang.annotation.*;

/**
 * This annotation is used on a Servlet or Filter implementation class
 * to specify an initialization parameter.
 * 
 * @since Servlet 3.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebInitParam {

    /**
     * Name of the initialization parameter
     *
     * @return name of the initialization parameter
     */
    String name();
    
    /**
     * Value of the initialization parameter
     *
     * @return value of the initialization parameter
     */    
    String value();
    
    /**
     * Description of the initialization parameter
     *
     * @return description of the initialization parameter
     */
    String description() default "";
}
