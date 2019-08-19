

package javax.servlet;

/** 
 * Event class for notifications about changes to the attributes of
 * the ServletContext of a web application.
 *
 * @see ServletContextAttributeListener
 *
 * @since Servlet 2.3
 */

public class ServletContextAttributeEvent extends ServletContextEvent { 

    private static final long serialVersionUID = -5804680734245618303L;

    private String name;
    private Object value;

    /**
     * Constructs a ServletContextAttributeEvent from the given 
     * ServletContext, attribute name, and attribute value.
     *
     * @param source the ServletContext whose attribute changed
     * @param name the name of the ServletContext attribute that changed
     * @param value the value of the ServletContext attribute that changed
     */
    public ServletContextAttributeEvent(ServletContext source,
            String name, Object value) {
        super(source);
        this.name = name;
        this.value = value;
    }
	
    /**
     * Gets the name of the ServletContext attribute that changed.
     *
     * @return the name of the ServletContext attribute that changed
     */
    public String getName() {
        return this.name;
    }
	
    /**
     * Gets the value of the ServletContext attribute that changed.
     *
     * <p>If the attribute was added, this is the value of the attribute.
     * If the attribute was removed, this is the value of the removed
     * attribute. If the attribute was replaced, this is the old value of
     * the attribute.
     *
     * @return the value of the ServletContext attribute that changed
     */
    public Object getValue() {
        return this.value;   
    }
}

