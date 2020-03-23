

package org.springframework.web.reactive.resource;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.util.StringUtils;

/**
 * Abstract base class for filename suffix based {@link VersionStrategy}
 * implementations, e.g. "static/myresource-version.js"
 *
 * @author Rossen Stoyanchev
 * @author Brian Clozel
 * @since 5.0
 */
public abstract class AbstractFileNameVersionStrategy implements VersionStrategy {

	protected final Log logger = LogFactory.getLog(getClass());

	private static final Pattern pattern = Pattern.compile("-(\\S*)\\.");


	@Override
	public String extractVersion(String requestPath) {
		Matcher matcher = pattern.matcher(requestPath);
		if (matcher.find()) {
			String match = matcher.group(1);
			return (match.contains("-") ? match.substring(match.lastIndexOf('-') + 1) : match);
		}
		else {
			return null;
		}
	}

	@Override
	public String removeVersion(String requestPath, String version) {
		return StringUtils.delete(requestPath, "-" + version);
	}

	@Override
	public String addVersion(String requestPath, String version) {
		String baseFilename = StringUtils.stripFilenameExtension(requestPath);
		String extension = StringUtils.getFilenameExtension(requestPath);
		return (baseFilename + '-' + version + '.' + extension);
	}

}
