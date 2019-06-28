

package org.springframework.jmx.export.metadata;

import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;

/**
 * Metadata that indicates a JMX notification emitted by a bean.
 *
 * @author Rob Harrop
 * @since 2.0
 */
public class ManagedNotification {

	@Nullable
	private String[] notificationTypes;

	@Nullable
	private String name;

	@Nullable
	private String description;


	/**
	 * Set a single notification type, or a list of notification types
	 * as comma-delimited String.
	 */
	public void setNotificationType(String notificationType) {
		this.notificationTypes = StringUtils.commaDelimitedListToStringArray(notificationType);
	}

	/**
	 * Set a list of notification types.
	 */
	public void setNotificationTypes(@Nullable String... notificationTypes) {
		this.notificationTypes = notificationTypes;
	}

	/**
	 * Return the list of notification types.
	 */
	@Nullable
	public String[] getNotificationTypes() {
		return this.notificationTypes;
	}

	/**
	 * Set the name of this notification.
	 */
	public void setName(@Nullable String name) {
		this.name = name;
	}

	/**
	 * Return the name of this notification.
	 */
	@Nullable
	public String getName() {
		return this.name;
	}

	/**
	 * Set a description for this notification.
	 */
	public void setDescription(@Nullable String description) {
		this.description = description;
	}

	/**
	 * Return a description for this notification.
	 */
	@Nullable
	public String getDescription() {
		return this.description;
	}

}
