

package org.springframework.context.annotation.componentscan.importing;

import org.springframework.context.annotation.ComponentScanAndImportAnnotationInteractionTests;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import(ComponentScanAndImportAnnotationInteractionTests.ImportedConfig.class)
public class ImportingConfig {
}
