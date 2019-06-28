

package example.scannable_implicitbasepackage;

import org.springframework.stereotype.Component;

/**
 * @author Juergen Hoeller
 */
@Component
public class ConfigurableComponent {

	private final boolean flag;

	public ConfigurableComponent() {
		this(false);
	}

	public ConfigurableComponent(boolean flag) {
		this.flag = flag;
	}

	public boolean isFlag() {
		return this.flag;
	}

}
