package javax.servlet.annotation;

import javax.servlet.DispatcherType;
import java.lang.annotation.*;

/**
 * Annotation used to declare a servlet filter.
 *
 * This annotation is processed by the container at deployment time,
 * and the corresponding filter applied to the specified URL patterns,
 * servlets, and dispatcher types.
 * 
 * @see javax.servlet.Filter
 *
 * @since Servlet 3.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebFilter {

    /**
     * The description of the filter
     * 
     * @return the description of the filter
     */
    String description() default "";
    
    /**
     * The display name of the filter
     *
     * @return the display name of the filter
     */
    String displayName() default "";
    
    /**
     * The init parameters of the filter
     *
     * @return the init parameters of the filter
     */
    WebInitParam[] initParams() default {};
    
    /**
     * The name of the filter
     *
     * @return the name of the filter
     */
    String filterName() default "";
    
    /**
     * The small-icon of the filter
     *
     * @return the small-icon of the filter
     */
    String smallIcon() default "";

    /**
     * The large-icon of the filter
     *
     * @return the large-icon of the filter
     */
    String largeIcon() default "";

    /**
     * The names of the servlets to which the filter applies.
     *
     * @return the names of the servlets to which the filter applies
     */
    String[] servletNames() default {};
    
    /**
     * The URL patterns to which the filter applies
     * The default value is an empty array.
     *
     * @return the URL patterns to which the filter applies
     */
    String[] value() default {};

    /**
     * The URL patterns to which the filter applies
     *
     * @return the URL patterns to which the filter applies
     */
    String[] urlPatterns() default {};

    /**
     * The dispatcher types to which the filter applies
     *
     * @return the dispatcher types to which the filter applies
     */
    DispatcherType[] dispatcherTypes() default {DispatcherType.REQUEST};
    
    /**
     * Declares whether the filter supports asynchronous operation mode.
     *
     * @return {@code true} if the filter supports asynchronous operation mode
     * @see javax.servlet.ServletRequest#startAsync
     * @see javax.servlet.ServletRequest#startAsync(ServletRequest,
     * ServletResponse)
     */
    boolean asyncSupported() default false;

}
