

package example.scannable_implicitbasepackage;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;


@Configuration
@ComponentScan
public class ComponentScanAnnotatedConfigWithImplicitBasePackage {

	@Bean  // override of scanned class
	public ConfigurableComponent configurableComponent() {
		return new ConfigurableComponent(true);
	}

}
