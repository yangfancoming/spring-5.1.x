

package org.springframework.http.codec.multipart;

/**
 * Specialization of {@link Part} for a form field.
 *
 * @author Rossen Stoyanchev
 * @since 5.0
 */
public interface FormFieldPart extends Part {

	/**
	 * Return the form field value.
	 */
	String value();

}
