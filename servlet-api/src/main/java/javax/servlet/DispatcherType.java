package javax.servlet;

/**
 * Enumeration of filter dispatcher types.
 *
 * @since Servlet 3.0
 */
public enum DispatcherType {
    FORWARD, //转发时过滤器生效
    INCLUDE, //
    REQUEST, //请求时生效 默认的
    ASYNC,
    ERROR  //错误时生效
}
