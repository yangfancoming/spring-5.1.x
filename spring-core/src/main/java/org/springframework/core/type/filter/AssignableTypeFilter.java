

package org.springframework.core.type.filter;

import org.springframework.lang.Nullable;
import org.springframework.util.ClassUtils;

/**
 * A simple filter which matches classes that are assignable to a given type.
 * @since 2.5
 */
public class AssignableTypeFilter extends AbstractTypeHierarchyTraversingFilter {

	private final Class<?> targetType;

	/**
	 * Create a new AssignableTypeFilter for the given type.
	 * @param targetType the type to match
	 */
	public AssignableTypeFilter(Class<?> targetType) {
		// 考虑 父类 和 接口
		super(true, true);
		this.targetType = targetType;
	}

	/**
	 * Return the {@code type} that this instance is using to filter candidates.
	 * @since 5.0
	 */
	public final Class<?> getTargetType() {
		return this.targetType;
	}

	@Override
	protected boolean matchClassName(String className) {
		return this.targetType.getName().equals(className);
	}

	@Override
	@Nullable
	protected Boolean matchSuperClass(String superClassName) {
		return matchTargetType(superClassName);
	}

	@Override
	@Nullable
	protected Boolean matchInterface(String interfaceName) {
		return matchTargetType(interfaceName);
	}

	// 父类匹配、接口匹配
	@Nullable
	protected Boolean matchTargetType(String typeName) {
		if (this.targetType.getName().equals(typeName)) {
			return true; // 类名相同匹配成功
		}else if (Object.class.getName().equals(typeName)) {
			return false; // 目标类是 Object 则匹配失败，其实就相当于其下所有子类都已经匹配失败了
		}else if (typeName.startsWith("java")) {
			// 如果给定类名以 java 打头，则加载该类，并判断是否 targetType 的子类
			try {
				Class<?> clazz = ClassUtils.forName(typeName, getClass().getClassLoader());
				return this.targetType.isAssignableFrom(clazz);
			}catch (Throwable ex) {
				// Class not regularly loadable - can't determine a match that way.
			}
		}
		return null;
	}
}
