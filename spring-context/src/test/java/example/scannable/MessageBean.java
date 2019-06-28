

package example.scannable;

/**
 * @author Mark Fisher
 */
@CustomComponent
@CustomAnnotations.SpecialAnnotation
public class MessageBean {

	private String message;

	public MessageBean() {
		this.message = "DEFAULT MESSAGE";
	}

	public MessageBean(String message) {
		this.message = message;
	}

	@CustomAnnotations.SpecialAnnotation
	public String getMessage() {
		return this.message;
	}

}
