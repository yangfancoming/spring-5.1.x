

package org.springframework.core.io.support;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Enumeration;
import java.util.Properties;

import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.DefaultPropertiesPersister;
import org.springframework.util.PropertiesPersister;
import org.springframework.util.ResourceUtils;

/**
 * Convenient utility methods for loading of {@code java.util.Properties},performing standard handling of input streams.
 * For more configurable properties loading, including the option of a customized encoding, consider using the PropertiesLoaderSupport class.
 * @since 2.0
 * @see PropertiesLoaderSupport
 */
public abstract class PropertiesLoaderUtils {

	private static final String XML_FILE_EXTENSION = ".xml";

	/**
	 * 加载资源文件，传入的是提供了编码的资源类（EncodedResource）
	 * Load properties from the given EncodedResource,
	 * potentially defining a specific encoding for the properties file.
	 * @see #fillProperties(java.util.Properties, EncodedResource)
	 */
	public static Properties loadProperties(EncodedResource resource) throws IOException {
		Properties props = new Properties();
		fillProperties(props, resource);
		return props;
	}

	/**
	 * 从一个编码资源类中加载资源，并填充到指定的Properties对象中
	 * Fill the given properties from the given EncodedResource,potentially defining a specific encoding for the properties file.
	 * @param props the Properties instance to load into
	 * @param resource the resource to load from
	 * @throws IOException in case of I/O errors
	 */
	public static void fillProperties(Properties props, EncodedResource resource) throws IOException {
		fillProperties(props, resource, new DefaultPropertiesPersister());
	}

	/**
	 * 从一个编码资源类中加载资源，并填充到指定的Properties对象中
	 * Actually load properties from the given EncodedResource into the given Properties instance.
	 * @param props the Properties instance to load into
	 * @param resource the resource to load from
	 * @param persister the PropertiesPersister to use
	 * @throws IOException in case of I/O errors
	 */
	static void fillProperties(Properties props, EncodedResource resource, PropertiesPersister persister) throws IOException {
		InputStream stream = null;
		Reader reader = null;
		try {
			String filename = resource.getResource().getFilename();
			if (filename != null && filename.endsWith(XML_FILE_EXTENSION)) {
				stream = resource.getInputStream();
				persister.loadFromXml(props, stream);
			}else if (resource.requiresReader()) {
				reader = resource.getReader();
				persister.load(props, reader);
			}else {
				stream = resource.getInputStream();
				persister.load(props, stream);
			}
		}finally {
			if (stream != null) stream.close();
			if (reader != null) reader.close();
		}
	}

	/**
	 * 从一个资源文件加载Properties (支持 .properties文件 和 .xml文件 )
	 * Load properties from the given resource (in ISO-8859-1 encoding).
	 * @param resource the resource to load from
	 * @return the populated Properties instance
	 * @throws IOException if loading failed
	 * @see #fillProperties(java.util.Properties, Resource)
	 */
	public static Properties loadProperties(Resource resource) throws IOException {
		Properties props = new Properties();
		fillProperties(props, resource);
		return props;
	}

	/**
	 * 从一个资源类中加载资源，并填充到指定的Properties对象中  配置文件转Properties
	 * Fill the given properties from the given resource (in ISO-8859-1 encoding).
	 * @param props the Properties instance to fill
	 * @param resource the resource to load from
	 * @throws IOException if loading failed
	 */
	public static void fillProperties(Properties props, Resource resource) throws IOException {
		InputStream is = resource.getInputStream();
		try {
			String filename = resource.getFilename();
			if (filename != null && filename.endsWith(XML_FILE_EXTENSION)) {
				props.loadFromXML(is);
			}else {
				props.load(is);
			}
		}finally {
			is.close();
		}
	}

	/**
	 * 根据资源文件名称，加载并合并classpath中的所有资源文件；
	 * Load all properties from the specified class path resource (in ISO-8859-1 encoding), using the default class loader.
	 * Merges properties if more than one resource of the same name found in the class path.
	 * @param resourceName the name of the class path resource
	 * @return the populated Properties instance
	 * @throws IOException if loading failed
	 */
	public static Properties loadAllProperties(String resourceName) throws IOException {
		return loadAllProperties(resourceName, null);
	}

	/**
	 * 从指定的ClassLoader中，根据资源文件名称，加载并合并classpath中的所有资源文件；
	 * Load all properties from the specified class path resource (in ISO-8859-1 encoding), using the given class loader.
	 * Merges properties if more than one resource of the same name found in the class path.
	 * @param resourceName the name of the class path resource
	 * @param classLoader the ClassLoader to use for loading (or {@code null} to use the default class loader)
	 * @return the populated Properties instance
	 * @throws IOException if loading failed
	 */
	public static Properties loadAllProperties(String resourceName, @Nullable ClassLoader classLoader) throws IOException {
		Assert.notNull(resourceName, "Resource name must not be null");
		ClassLoader classLoaderToUse = classLoader;
		if (classLoaderToUse == null) classLoaderToUse = ClassUtils.getDefaultClassLoader();
		Enumeration<URL> urls = (classLoaderToUse != null ? classLoaderToUse.getResources(resourceName) : ClassLoader.getSystemResources(resourceName));
		Properties props = new Properties();
		while (urls.hasMoreElements()) {
			URL url = urls.nextElement();
			URLConnection con = url.openConnection();
			ResourceUtils.useCachesIfNecessary(con);
			InputStream is = con.getInputStream();
			try {
				if (resourceName.endsWith(XML_FILE_EXTENSION)) {
					props.loadFromXML(is);
				}else {
					props.load(is);
				}
			}finally {
				is.close();
			}
		}
		return props;
	}

}
