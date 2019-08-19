

package javax.servlet;

import java.util.Enumeration;

/**
 * A filter configuration object used by a servlet container
 * to pass information to a filter during initialization.
 * 初始化Filter的配置 容器传递这个类到filter
 *
 * @see Filter
 * @since Servlet 2.3
 */
public interface FilterConfig {

    /**
     * Returns the filter-name of this filter as defined in the deployment
     * descriptor.
     * 在容器中定义的名字
     *
     * @return the filter name of this filter
     */
    public String getFilterName();


    /**
     * Returns a reference to the {@link ServletContext} in which the caller
     * is executing.
     * 对ServletContext的引用，可以和ServletContext交互
     *
     * @return a {@link ServletContext} object, used by the caller to
     * interact with its servlet container
     */
    public ServletContext getServletContext();


    /**
     * 初始化参数
     * Returns a <code>String</code> containing the value of the
     * named initialization parameter, or <code>null</code> if
     * the initialization parameter does not exist.
     */
    public String getInitParameter(String name);


    /**
     * 返回所有参数key的枚举集合
     * Returns the names of the filter's initialization parameters
     * as an <code>Enumeration</code> of <code>String</code> objects,
     * or an empty <code>Enumeration</code> if the filter has
     * no initialization parameters.
     */
    public Enumeration<String> getInitParameterNames();

}
