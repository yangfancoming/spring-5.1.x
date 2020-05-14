package javax.servlet.annotation;

import java.lang.annotation.*;

/**
 * Annotation used to declare a servlet.
 * This annotation is processed by the container at deployment time,and the corresponding servlet made available at the specified URL patterns.
 * @see javax.servlet.Servlet
 * @since Servlet 3.0
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WebServlet {
    
    /**
     * The name of the servlet
     * @return the name of the servlet
     */
    String name() default "";
    
    /**
     * The URL patterns of the servlet
     * @return the URL patterns of the servlet
     */
    String[] value() default {};

    /**
     * The URL patterns of the servlet
     * @return the URL patterns of the servlet
     */
    String[] urlPatterns() default {};
    
    /**
     * The load-on-startup order of the servlet 
     * @return the load-on-startup order of the servlet
     */
    int loadOnStartup() default -1;
    
    /**
     * The init parameters of the servlet
     * @return the init parameters of the servlet
     */
    WebInitParam [] initParams() default {};
    
    /**
     * Declares whether the servlet supports asynchronous operation mode.
     * @return {@code true} if the servlet supports asynchronous operation mode
     * @see javax.servlet.ServletRequest#startAsync
     * @see javax.servlet.ServletRequest#startAsync(ServletRequest,ServletResponse)
     */
    boolean asyncSupported() default false;
    
    /**
     * The small-icon of the servlet
     * @return the small-icon of the servlet
     */
    String smallIcon() default "";

    /**
     * The large-icon of the servlet
     * @return the large-icon of the servlet
     */
    String largeIcon() default "";

    /**
     * The description of the servlet
     * @return the description of the servlet
     */
    String description() default "";

    /**
     * The display name of the servlet
     * @return the display name of the servlet
     */
    String displayName() default "";
}
