

package org.springframework.core.type;

import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import org.apache.log4j.Logger;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

/**
 * {@link ClassMetadata} implementation that uses standard reflection to introspect a given {@code Class}.
 * @since 2.5
 */
public class StandardClassMetadata implements ClassMetadata {

	private static final Logger logger = Logger.getLogger(StandardClassMetadata.class);

	private final Class<?> introspectedClass;

	/**
	 * Create a new StandardClassMetadata wrapper for the given Class.
	 * @param introspectedClass the Class to introspect
	 */
	public StandardClassMetadata(Class<?> introspectedClass) {
		logger.warn("进入 【StandardClassMetadata】 构造函数 {}");
		Assert.notNull(introspectedClass, "Class must not be null");
		this.introspectedClass = introspectedClass;
	}

	/**
	 * Return the underlying Class.
	 */
	public final Class<?> getIntrospectedClass() {
		return introspectedClass;
	}

	@Override
	public String getClassName() {
		return introspectedClass.getName();
	}

	@Override
	public boolean isInterface() {
		return introspectedClass.isInterface();
	}

	@Override
	public boolean isAnnotation() {
		return introspectedClass.isAnnotation();
	}

	@Override
	public boolean isAbstract() {
		return Modifier.isAbstract(introspectedClass.getModifiers());
	}

	@Override
	public boolean isConcrete() {
		return !(isInterface() || isAbstract());
	}

	@Override
	public boolean isFinal() {
		return Modifier.isFinal(introspectedClass.getModifiers());
	}

	@Override
	public boolean isIndependent() {
		return (!hasEnclosingClass() || (introspectedClass.getDeclaringClass() != null && Modifier.isStatic(introspectedClass.getModifiers())));
	}

	@Override
	public boolean hasEnclosingClass() {
		return (introspectedClass.getEnclosingClass() != null);
	}

	@Override
	@Nullable
	public String getEnclosingClassName() {
		Class<?> enclosingClass = introspectedClass.getEnclosingClass();
		return (enclosingClass != null ? enclosingClass.getName() : null);
	}

	@Override
	public boolean hasSuperClass() {
		return (introspectedClass.getSuperclass() != null);
	}

	@Override
	@Nullable
	public String getSuperClassName() {
		Class<?> superClass = introspectedClass.getSuperclass();
		return (superClass != null ? superClass.getName() : null);
	}

	@Override
	public String[] getInterfaceNames() {
		Class<?>[] ifcs = introspectedClass.getInterfaces();
		String[] ifcNames = new String[ifcs.length];
		for (int i = 0; i < ifcs.length; i++) {
			ifcNames[i] = ifcs[i].getName();
		}
		return ifcNames;
	}

	@Override
	public String[] getMemberClassNames() {
		LinkedHashSet<String> memberClassNames = new LinkedHashSet<>(4);
		for (Class<?> nestedClass : introspectedClass.getDeclaredClasses()) {
			memberClassNames.add(nestedClass.getName());
		}
		return StringUtils.toStringArray(memberClassNames);
	}
}
