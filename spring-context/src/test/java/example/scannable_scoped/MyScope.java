

package example.scannable_scoped;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ScopedProxyMode;

public @interface MyScope {
	String value() default BeanDefinition.SCOPE_SINGLETON;
	ScopedProxyMode proxyMode() default ScopedProxyMode.DEFAULT;
}
