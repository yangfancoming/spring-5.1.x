

package example.scannable;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class CustomAnnotations {

	@Retention(RetentionPolicy.RUNTIME)
	private @interface PrivateAnnotation {
		String value();
	}

	@Retention(RetentionPolicy.RUNTIME)
	@PrivateAnnotation("special")
	public @interface SpecialAnnotation {
	}

}
