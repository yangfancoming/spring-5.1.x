

package org.springframework.beans.factory.xml;

import java.io.IOException;

import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * {@link EntityResolver} implementation that delegates to a {@link BeansDtdResolver}  and a {@link PluggableSchemaResolver} for DTDs and XML schemas, respectively.
 * @since 2.0
 * @see BeansDtdResolver
 * @see PluggableSchemaResolver
 */
public class DelegatingEntityResolver implements EntityResolver {

	/** Suffix for DTD files. */
	public static final String DTD_SUFFIX = ".dtd";

	/** Suffix for schema definition files. */
	public static final String XSD_SUFFIX = ".xsd";

	private final EntityResolver dtdResolver;

	private final EntityResolver schemaResolver;

	/**
	 * Create a new DelegatingEntityResolver that delegates to a default {@link BeansDtdResolver} and a default {@link PluggableSchemaResolver}.
	 * Configures the {@link PluggableSchemaResolver} with the supplied {@link ClassLoader}.
	 * @param classLoader the ClassLoader to use for loading (can be {@code null}) to use the default ClassLoader)
	 */
	public DelegatingEntityResolver(@Nullable ClassLoader classLoader) {
		this.dtdResolver = new BeansDtdResolver(); // dtd 约束
		this.schemaResolver = new PluggableSchemaResolver(classLoader); // xsd 约束
	}

	/**
	 * Create a new DelegatingEntityResolver that delegates to the given {@link EntityResolver EntityResolvers}.
	 * @param dtdResolver the EntityResolver to resolve DTDs with
	 * @param schemaResolver the EntityResolver to resolve XML schemas with
	 */
	public DelegatingEntityResolver(EntityResolver dtdResolver, EntityResolver schemaResolver) {
		Assert.notNull(dtdResolver, "'dtdResolver' is required");
		Assert.notNull(schemaResolver, "'schemaResolver' is required");
		this.dtdResolver = dtdResolver;
		this.schemaResolver = schemaResolver;
	}

	@Override
	@Nullable
	public InputSource resolveEntity(@Nullable String publicId, @Nullable String systemId) throws SAXException, IOException {
		if (systemId != null) {
			if (systemId.endsWith(DTD_SUFFIX)) {
				return dtdResolver.resolveEntity(publicId, systemId);
			}else if (systemId.endsWith(XSD_SUFFIX)) {
				return schemaResolver.resolveEntity(publicId, systemId);
			}
		}
		// Fall back to the parser's default behavior.
		return null;
	}

	@Override
	public String toString() {
		return "EntityResolver delegating " + XSD_SUFFIX + " to " + this.schemaResolver + " and " + DTD_SUFFIX + " to " + this.dtdResolver;
	}

}
