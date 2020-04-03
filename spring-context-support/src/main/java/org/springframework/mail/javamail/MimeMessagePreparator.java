

package org.springframework.mail.javamail;

import javax.mail.internet.MimeMessage;

/**
 * Callback interface for the preparation of JavaMail MIME messages.
 *
 * xmlBeanDefinitionReaderThe corresponding {@code send} methods of {@link JavaMailSender}
 * will take care of the actual creation of a {@link MimeMessage} instance,
 * and of proper exception conversion.
 *
 * xmlBeanDefinitionReaderIt is often convenient to use a {@link MimeMessageHelper} for populating
 * the passed-in MimeMessage, in particular when working with attachments or
 * special character encodings.
 * See {@link MimeMessageHelper MimeMessageHelper's javadoc} for an example.
 *

 * @since 07.10.2003
 * @see JavaMailSender#send(MimeMessagePreparator)
 * @see JavaMailSender#send(MimeMessagePreparator[])
 * @see MimeMessageHelper
 */
@FunctionalInterface
public interface MimeMessagePreparator {

	/**
	 * Prepare the given new MimeMessage instance.
	 * @param mimeMessage the message to prepare
	 * @throws javax.mail.MessagingException passing any exceptions thrown by MimeMessage
	 * methods through for automatic conversion to the MailException hierarchy
	 * @throws java.io.IOException passing any exceptions thrown by MimeMessage methods
	 * through for automatic conversion to the MailException hierarchy
	 * @throws Exception if mail preparation failed, for example when a
	 * FreeMarker template cannot be rendered for the mail text
	 */
	void prepare(MimeMessage mimeMessage) throws Exception;

}
