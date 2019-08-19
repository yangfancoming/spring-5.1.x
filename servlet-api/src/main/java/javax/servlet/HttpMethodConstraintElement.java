package javax.servlet;

import javax.servlet.annotation.HttpMethodConstraint;

/**
 * Java Class represntation of an {@link HttpMethodConstraint} annotation value.
 * 对方法访问控制
 *
 * @since Servlet 3.0
 */
public class HttpMethodConstraintElement extends HttpConstraintElement {

    //方法的名字
    private String methodName;

    /**
     * Constructs an instance with default {@link HttpConstraintElement}
     * value.
     *
     * @param methodName the name of an HTTP protocol method. The name must
     *                   not be null, or the empty string, and must be a legitimate HTTP
     *                   Method name as defined by RFC 2616
     */
    public HttpMethodConstraintElement(String methodName) {
        if (methodName == null || methodName.length() == 0) {
            throw new IllegalArgumentException("invalid HTTP method name");
        }
        this.methodName = methodName;
    }

    /**
     * Constructs an instance with specified {@link HttpConstraintElement}
     * value.
     *
     * @param methodName the name of an HTTP protocol method. The name must
     *                   not be null, or the empty string, and must be a legitimate HTTP
     *                   Method name as defined by RFC 2616
     * @param constraint the HTTPconstraintElement value to assign to the
     *                   named HTTP method
     */
    public HttpMethodConstraintElement(String methodName,
                                       HttpConstraintElement constraint) {
        super(constraint.getEmptyRoleSemantic(),
                constraint.getTransportGuarantee(),
                constraint.getRolesAllowed());
        if (methodName == null || methodName.length() == 0) {
            throw new IllegalArgumentException("invalid HTTP method name");
        }
        this.methodName = methodName;
    }

    /**
     * Gets the HTTP method name.
     *
     * @return the Http method name
     */
    public String getMethodName() {
        return this.methodName;
    }
}
