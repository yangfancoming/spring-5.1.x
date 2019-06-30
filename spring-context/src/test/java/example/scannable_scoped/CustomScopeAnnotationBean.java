

package example.scannable_scoped;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.stereotype.Component;

@Component
@MyScope(BeanDefinition.SCOPE_PROTOTYPE)
public class CustomScopeAnnotationBean {
}
