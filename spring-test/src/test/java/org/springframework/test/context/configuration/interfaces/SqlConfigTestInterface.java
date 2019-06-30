

package org.springframework.test.context.configuration.interfaces;

import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.EmptyDatabaseConfig;
import org.springframework.test.context.jdbc.SqlConfig;

/**
 * @author Sam Brannen
 * @since 4.3
 */
@ContextConfiguration(classes = EmptyDatabaseConfig.class)
@DirtiesContext
@SqlConfig(commentPrefix = "`", blockCommentStartDelimiter = "#$", blockCommentEndDelimiter = "$#", separator = "@@")
interface SqlConfigTestInterface {
}
