

package javax.servlet.http;

/**
 * Enumeration of Servlet mapping types.</p>
 *
 * @since 4.0
 */
public enum MappingMatch {
    /**
     * This is used when the mapping was achieved
     * with an exact match to the application's context root.</p>
     */
    CONTEXT_ROOT,
    /**
     * This is used when the mapping was achieved
     * with an exact match to the default servlet of the application, the '{@code /}'
     * character.</p>
     */
    DEFAULT,
    /**
     * This is used when the mapping was achieved
     * with an exact match to the incoming request.</p>
     */
    EXACT,
    /**
     * This is used when the mapping was achieved
     * using an extension, such as "{@code *.xhtml}".</p>
     */
    EXTENSION,
    /**
     * This is used when the mapping was achieved
     * using a path, such as "{@code /faces/*}".</p>
     */
    PATH
}
