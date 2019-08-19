package javax.servlet.annotation;

import java.lang.annotation.*;

/**
 * This annotation is used to declare a WebListener.
 *
 * Any class annotated with WebListener must implement one or more of
 * the {@link javax.servlet.ServletContextListener}, 
 * {@link javax.servlet.ServletContextAttributeListener},
 * {@link javax.servlet.ServletRequestListener},
 * {@link javax.servlet.ServletRequestAttributeListener}, 
 * {@link javax.servlet.http.HttpSessionListener}, or
 * {@link javax.servlet.http.HttpSessionAttributeListener}, or
 * {@link javax.servlet.http.HttpSessionIdListener} interfaces.
 * 
 * @since Servlet 3.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebListener {
    /**
     * Description of the listener
     *
     * @return description of the listener
     */
    String value() default "";
}
