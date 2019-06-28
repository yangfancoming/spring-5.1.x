

package org.springframework.context.annotation.spr10546;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 *
 * @author Rob Winch
 */
@Configuration
@Import(ImportedConfig.class)
public class ParentWithImportConfig {

}
