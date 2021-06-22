

package org.springframework.core.type.filter;

import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.lang.Nullable;

/**
 * Type filter that is aware of traversing over hierarchy.
 * This filter is useful when matching needs to be made based on potentially the whole class/interface hierarchy.
 * The algorithm employed uses a succeed-fast strategy: if at any time a match is declared, no further processing is carried out.
 * 用来处理那些存在继承关系的接口和类，根据配置，会扫描所有的父类和所有实现的接口。此类是抽象类，如果有具体的实现类不满足要求，可以自行继承实现。
 * 知道遍历层次结构的类型筛选器。
 * 当需要基于整个类/接口层次结构进行匹配时，此筛选器非常有用。
 * 所采用的算法使用了一种快速成功的策略：如果在任何时候声明匹配，则不执行进一步的处理。
 * @since 2.5
 */
public abstract class AbstractTypeHierarchyTraversingFilter implements TypeFilter {

	protected final Log logger = LogFactory.getLog(getClass());
	// 是否考虑父类匹配
	private final boolean considerInherited;
	// 是否考虑接口匹配
	private final boolean considerInterfaces;

	protected AbstractTypeHierarchyTraversingFilter(boolean considerInherited, boolean considerInterfaces) {
		this.considerInherited = considerInherited;
		this.considerInterfaces = considerInterfaces;
	}

	@Override
	public boolean match(MetadataReader metadataReader, MetadataReaderFactory metadataReaderFactory) throws IOException {
		// This method optimizes avoiding unnecessary creation of ClassReaders as well as visiting over those readers.
		// 提供了一个快速匹配的通道，可由子类重写
		// 此方法优化了避免不必要地创建类读取器以及访问这些读取器。
		if (matchSelf(metadataReader)) {
			return true;
		}
		ClassMetadata metadata = metadataReader.getClassMetadata();
		// 类匹配，子类复写
		if (matchClassName(metadata.getClassName())) {
			return true;
		}
		// 如果考虑 父类匹配
		if (this.considerInherited) {
			String superClassName = metadata.getSuperClassName();
			if (superClassName != null) {
				// Optimization to avoid creating ClassReader for super class.
				Boolean superClassMatch = matchSuperClass(superClassName);
				if (superClassMatch != null) {
					if (superClassMatch.booleanValue()) {
						return true;
					}
				}else {
					// Need to read super class to determine a match...
					try {
						if (match(metadata.getSuperClassName(), metadataReaderFactory)) {
							return true;
						}
					}catch (IOException ex) {
						if (logger.isDebugEnabled()) {
							logger.debug("Could not read super class [" + metadata.getSuperClassName() + "] of type-filtered class [" + metadata.getClassName() + "]");
						}
					}
				}
			}
		}
		// 如果考虑 接口匹配
		if (this.considerInterfaces) {
			for (String ifc : metadata.getInterfaceNames()) {
				// Optimization to avoid creating ClassReader for super class
				Boolean interfaceMatch = matchInterface(ifc);
				if (interfaceMatch != null) {
					if (interfaceMatch.booleanValue()) {
						return true;
					}
				}else {
					// Need to read interface to determine a match...
					try {
						if (match(ifc, metadataReaderFactory)) {
							return true;
						}
					}catch (IOException ex) {
						if (logger.isDebugEnabled()) {
							logger.debug("Could not read interface [" + ifc + "] for type-filtered class [" + metadata.getClassName() + "]");
						}
					}
				}
			}
		}
		return false;
	}

	private boolean match(String className, MetadataReaderFactory metadataReaderFactory) throws IOException {
		return match(metadataReaderFactory.getMetadataReader(className), metadataReaderFactory);
	}

	/**
	 * Override this to match self characteristics alone. Typically, the implementation will use a visitor to extract information to perform matching.
	 * 重写此项以单独匹配自身特征。通常，实现将使用访问者提取信息以执行匹配。
	 */
	protected boolean matchSelf(MetadataReader metadataReader) {
		return false;
	}

	/**
	 * 类名匹配
	 * Override this to match on type name.
	 */
	protected boolean matchClassName(String className) {
		return false;
	}

	/**
	 * Override this to match on super type name.
	 */
	@Nullable
	protected Boolean matchSuperClass(String superClassName) {
		return null;
	}

	/**
	 * Override this to match on interface type name.
	 */
	@Nullable
	protected Boolean matchInterface(String interfaceName) {
		return null;
	}
}
