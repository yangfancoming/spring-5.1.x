

package org.springframework.core.io.support;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;

/**
 * Base class for JavaBean-style components that need to load properties from one or more resources. Supports local properties as well, with configurable overriding.
 * @since 1.2.2
 */
public abstract class PropertiesLoaderSupport {

	/** Logger available to subclasses. */
	protected final Log logger = LogFactory.getLog(getClass());

	@Nullable
	protected Properties[] localProperties;

	protected boolean localOverride = false;

	@Nullable
	private Resource[] locations;

	private boolean ignoreResourceNotFound = false;

	// 资源文件编码格式设置
	@Nullable
	private String fileEncoding;

	private PropertiesPersister propertiesPersister = new DefaultPropertiesPersister();

	/**
	 * Set local properties, e.g. via the "props" tag in XML bean definitions.
	 * These can be considered defaults, to be overridden by properties loaded from files.
	 */
	public void setProperties(Properties properties) {
		localProperties = new Properties[] {properties};
	}

	/**
	 * Set local properties, e.g. via the "props" tag in XML bean definitions,allowing for merging multiple properties sets into one.
	 */
	public void setPropertiesArray(Properties... propertiesArray) {
		this.localProperties = propertiesArray;
	}

	/**
	 * Set a location of a properties file to be loaded.
	 * Can point to a classic properties file or to an XML file that follows JDK 1.5's properties XML format.
	 */
	public void setLocation(Resource location) {
		locations = new Resource[] {location};
	}

	/**
	 * Set locations of properties files to be loaded.
	 * Can point to classic properties files or to XML files that follow JDK 1.5's properties XML format.
	 * Note: Properties defined in later files will override properties defined earlier files, in case of overlapping keys.
	 * Hence, make sure that the most specific files are the last ones in the given list of locations.
	 */
	public void setLocations(Resource... locations) {
		this.locations = locations;
	}

	/**
	 * Set whether local properties override properties from files.
	 * Default is "false": Properties from files override local defaults.Can be switched to "true" to let local properties override defaults from files.
	 */
	public void setLocalOverride(boolean localOverride) {
		this.localOverride = localOverride;
	}

	/**
	 * Set if failure to find the property resource should be ignored."true" is appropriate if the properties file is completely optional.
	 * Default is "false".
	 */
	public void setIgnoreResourceNotFound(boolean ignoreResourceNotFound) {
		this.ignoreResourceNotFound = ignoreResourceNotFound;
	}

	/**
	 * Set the encoding to use for parsing properties files.
	 * Default is none, using the {@code java.util.Properties} default encoding.
	 * Only applies to classic properties files, not to XML files.
	 * @see org.springframework.util.PropertiesPersister#load
	 */
	public void setFileEncoding(String encoding) {
		fileEncoding = encoding;
	}

	/**
	 * Set the PropertiesPersister to use for parsing properties files.The default is DefaultPropertiesPersister.
	 * @see org.springframework.util.DefaultPropertiesPersister
	 */
	public void setPropertiesPersister(@Nullable PropertiesPersister propertiesPersister) {
		this.propertiesPersister = (propertiesPersister != null ? propertiesPersister : new DefaultPropertiesPersister());
	}

	/**
	 * Return a merged Properties instance containing both the loaded properties and properties set on this FactoryBean.
	 */
	protected Properties mergeProperties() throws IOException {
		Properties result = new Properties();
		if (localOverride) {
			// Load properties from file upfront, to let local properties override.
			loadProperties(result);
		}
		if (localProperties != null) {
			for (Properties localProp : localProperties) {
				CollectionUtils.mergePropertiesIntoMap(localProp, result);
			}
		}
		if (!localOverride) {
			// Load properties from file afterwards, to let those properties override.
			loadProperties(result);
		}
		return result;
	}

	/**
	 * Load properties into the given instance.
	 * @param props the Properties instance to load into
	 * @throws IOException in case of I/O errors
	 * @see #setLocations
	 */
	protected void loadProperties(Properties props) throws IOException {
		if (locations != null) {
			for (Resource location : locations) {
				if (logger.isTraceEnabled()) logger.trace("Loading properties file from " + location);
				try {
					PropertiesLoaderUtils.fillProperties(props, new EncodedResource(location, fileEncoding), propertiesPersister);
				}catch (FileNotFoundException | UnknownHostException ex) {
					if (ignoreResourceNotFound) {
						if (logger.isDebugEnabled()) logger.debug("Properties resource not found: " + ex.getMessage());
					}else {
						throw ex;
					}
				}
			}
		}
	}

}
