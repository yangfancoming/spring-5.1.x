

package org.springframework.jdbc.support.xml;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.lang.Nullable;

/**
 * Abstraction for handling XML object mapping to fields in a database.
 *
 * Provides accessor methods for XML fields unmarshalled to an Object,
 * and acts as factory for {@link SqlXmlValue} instances for marshalling
 * purposes.
 *
 * @author Thomas Risberg
 * @since 2.5.5
 * @see java.sql.ResultSet#getSQLXML
 * @see java.sql.SQLXML
 * @deprecated as of Spring Framework 5.1.5 since this class is only known to be used in spring-data-jdbc-ext project
 * which was never upgraded to Spring Framework 5.x and is no longer actively developed
 */
@Deprecated
public interface SqlXmlObjectMappingHandler extends SqlXmlHandler {

	/**
	 * Retrieve the given column as an object marshalled from the XML data retrieved
	 * from the given ResultSet.
	 * Works with an internal Object to XML Mapping implementation.
	 * @param rs the ResultSet to retrieve the content from
	 * @param columnName the column name to use
	 * @return the content as an Object, or {@code null} in case of SQL NULL
	 * @throws java.sql.SQLException if thrown by JDBC methods
	 * @see java.sql.ResultSet#getSQLXML
	 */
	@Nullable
	Object getXmlAsObject(ResultSet rs, String columnName) throws SQLException;

	/**
	 * Retrieve the given column as an object marshalled from the XML data retrieved
	 * from the given ResultSet.
	 * Works with an internal Object to XML Mapping implementation.
	 * @param rs the ResultSet to retrieve the content from
	 * @param columnIndex the column index to use
	 * @return the content as an Object, or {@code null} in case of SQL NULL
	 * @throws java.sql.SQLException if thrown by JDBC methods
	 * @see java.sql.ResultSet#getSQLXML
	 */
	@Nullable
	Object getXmlAsObject(ResultSet rs, int columnIndex) throws SQLException;

	/**
	 * Get an instance of an {@code SqlXmlValue} implementation to be used together
	 * with the database specific implementation of this {@code SqlXmlObjectMappingHandler}.
	 * @param value the Object to be marshalled to XML
	 * @return the implementation specific instance
	 * @see SqlXmlValue
	 */
	SqlXmlValue newMarshallingSqlXmlValue(Object value);

}
