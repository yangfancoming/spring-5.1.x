

package org.springframework.context.annotation.componentscan.cycle.left;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("org.springframework.context.annotation.componentscan.cycle.right")
public class LeftConfig {

}
