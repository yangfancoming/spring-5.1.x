
package org.springframework.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.util.StringValueResolver;

/**
 * Simple implementation of the {@link AliasRegistry} interface.Serves as base class for implementations
 * @since 2.5.2
 * AliasRegistry 根接口的实现类  主要使用map作为alias的缓存
 */
public class SimpleAliasRegistry implements AliasRegistry {

	/** Logger available to subclasses. */
	protected final Log logger = LogFactory.getLog(getClass());

	/** Map from alias to canonical name. 存储bean名称和bean别名的映射关系（key 别名，value 真实名 ）  -modify*/
	public final Map<String, String> aliasMap = new ConcurrentHashMap<>(16);

	// Return whether alias overriding is allowed. Default is true.
	protected boolean allowAliasOverriding() {
		return true;
	}

	/**
	 * Determine whether the given name has the given alias registered.
	 * 根据给定的正名和别名 判断两者之间是否已经注册过
	 * @param name the name to check
	 * @param alias the alias to look for
	 * @since 4.2.1
	 */
	public boolean hasAlias(String name, String alias) {
		for (Map.Entry<String, String> entry : aliasMap.entrySet()) {
			String registeredName = entry.getValue();
			if (registeredName.equals(name)) {
				String registeredAlias = entry.getKey();
				// 由于别名可以是链式的，因此这里需要记性递归遍历
				if (registeredAlias.equals(alias) || hasAlias(registeredAlias, alias)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Transitively retrieve all aliases for the given name.根据给定的正名，获取所有该正名已注册的所有别名
	 * @param canonicalName the target name to find aliases for
	 * @param result the resulting aliases list
	 */
	private void retrieveAliases(String canonicalName, List<String> result) {
		aliasMap.forEach((alias, registeredName) -> {
			// 当给定的正名与已注册正名相同时，会把其对应的别名作为正名，进行递归遍历
			if (registeredName.equals(canonicalName)) {
				result.add(alias);
				// 这也是在注册时限制注册的数据name和alias不能正好相反且name和alias不能相等的原因，否则回调函数将陷入无限循环中去。
				retrieveAliases(alias, result);
			}
		});
	}

	/**
	 * Resolve all alias target names and aliases registered in this factory, applying the given StringValueResolver to them.
	 * The value resolver may for example resolve placeholders in target bean names and even in alias names.
	 * @param valueResolver the StringValueResolver to apply
	 */
	public void resolveAliases(StringValueResolver valueResolver) {
		Assert.notNull(valueResolver, "StringValueResolver must not be null");
		synchronized (aliasMap) {
			Map<String, String> aliasCopy = new HashMap<>(aliasMap);
			aliasCopy.forEach((alias, registeredName) -> {
				String resolvedAlias = valueResolver.resolveStringValue(alias);
				String resolvedName = valueResolver.resolveStringValue(registeredName);
				if (resolvedAlias == null || resolvedName == null || resolvedAlias.equals(resolvedName)) {
					aliasMap.remove(alias);
				}else if (!resolvedAlias.equals(alias)) {
					String existingName = aliasMap.get(resolvedAlias);
					if (existingName != null) {
						if (existingName.equals(resolvedName)) {
							// Pointing to existing alias - just remove placeholder
							aliasMap.remove(alias);
							return;
						}
						throw new IllegalStateException("Cannot register resolved alias '" + resolvedAlias + "' (original: '" + alias + "') for name '" + resolvedName + "': It is already registered for name '" + registeredName + "'.");
					}
					checkForAliasCircle(resolvedName, resolvedAlias);
					aliasMap.remove(alias);
					aliasMap.put(resolvedAlias, resolvedName);
				}else if (!registeredName.equals(resolvedName)) {
					aliasMap.put(alias, resolvedName);
				}
			});
		}
	}

	/**
	 * Check whether the given name points back to the given alias as an alias in the other direction already,
	 * catching a circular reference upfront and throwing a corresponding IllegalStateException.
	 * @param name the candidate name
	 * @param alias the candidate alias
	 * @see #registerAlias
	 * @see #hasAlias
	 */
	protected void checkForAliasCircle(String name, String alias) {
		if (hasAlias(alias, name)) {
			throw new IllegalStateException("Cannot register alias '" + alias + "' for name '" + name + "': Circular reference - '" + name + "' is a direct or indirect alias for '" + alias + "' already");
		}
	}

	/**
	 * Determine the raw name, resolving aliases to canonical names.
	 * 根据别名解析出正名。( 链式别名解析到最根源的正名)
	 * @param name the user-specified name
	 * @return the transformed name
	 * 	registry.registerAlias("李彦伯", "李亮亮");
	 * 	registry.registerAlias("李亮亮", "老K");
	 * 	registry.registerAlias("老K", "JQK");
	 *
	 * 	assertSame("李彦伯", registry.canonicalName("李亮亮"));
	 * 	assertSame("李彦伯", registry.canonicalName("老K"));
	 * 	assertSame("李彦伯", registry.canonicalName("JQK"));
	 */
	public String canonicalName(String name) {
		String canonicalName = name; // doit  该行去掉 函数参数改成canonicalName 貌似也可以？
		String resolvedName; // Handle aliasing...
		// 这里使用 while 循环进行处理，原因是：可能会存在多重别名的问题，即别名指向别名。比如上面的配置
		do {
			// 根据别名获取正名
			resolvedName = aliasMap.get(canonicalName);
			if (resolvedName != null) canonicalName = resolvedName;
		}while (resolvedName != null);
		return canonicalName;
	}

	//---------------------------------------------------------------------
	// Implementation of 【AliasRegistry】 interface
	//---------------------------------------------------------------------
	@Override
	public void registerAlias(String name, String alias) {
		Assert.hasText(name, "'name' must not be empty");
		Assert.hasText(alias, "'alias' must not be empty");
		synchronized (aliasMap) {
			// 如果真实的beanName和要注册的别名相同，则直接删除，因为真实的名字和别名相同没有意义
			if (alias.equals(name)) {
				aliasMap.remove(alias);
				if (logger.isDebugEnabled()) logger.debug("Alias definition '" + alias + "' ignored since it points to same name");
			}else {
				// 尝试从缓存中获取 正名
				String registeredName = aliasMap.get(alias);
				// 如果 正名 已经在缓存中存在
				if (registeredName != null) {
					// An existing alias - no need to re-register  如果正名已经在缓存中存在，并且缓存中的正名和传入的正名相同,则直接返回,没有必要再注册一次
					if (registeredName.equals(name)) return;
					// 如果别名不允许覆盖，则抛出异常  //缓存中存在别名,且不允许覆盖,抛出异常
					if (!allowAliasOverriding()) {
						throw new IllegalStateException("Cannot define alias '" + alias + "' for name '" + name + "': It is already registered for name '" + registeredName + "'.");
					}
					if (logger.isDebugEnabled()) logger.debug("Overriding alias '" + alias + "' definition for registered name '" + registeredName + "' with new target name '" + name + "'");
				}
				// 检测别名和正名是否有循环引用注册，有则抛出异常。  eg："李彦伯" --- > "老K"  又 "老K" --- > "李彦伯"  则抛出异常
				checkForAliasCircle(name, alias);
				// 注册别名 // 将别名作为key，目标bean名称作为值注册到存储别名的Map中
				// 缓存别名
				aliasMap.put(alias, name);
				if (logger.isTraceEnabled()) logger.trace("Alias definition '" + alias + "' registered for name '" + name + "'");
			}
		}
	}

	@Override
	public void removeAlias(String alias) {
		synchronized (aliasMap) {
			String name = aliasMap.remove(alias);
			if (name == null) throw new IllegalStateException("No alias '" + alias + "' registered");
		}
	}

	@Override
	public boolean isAlias(String name) {
		return aliasMap.containsKey(name);
	}

	@Override
	public String[] getAliases(String name) {
		List<String> result = new ArrayList<>();
		synchronized (aliasMap) {
			retrieveAliases(name, result);
		}
		return StringUtils.toStringArray(result);
	}
}
