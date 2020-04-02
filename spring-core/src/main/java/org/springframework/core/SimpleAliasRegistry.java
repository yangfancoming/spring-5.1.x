
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
 * Simple implementation of the {@link AliasRegistry} interface.
 * Serves as base class for implementations
 * @since 2.5.2
 * AliasRegistry实现类  主要使用 map 作为 alias 的缓存
 */
public class SimpleAliasRegistry implements AliasRegistry {

	/** Logger available to subclasses. */
	protected final Log logger = LogFactory.getLog(getClass());

	/** Map from alias to canonical name. */
	private final Map<String, String> aliasMap = new ConcurrentHashMap<>(16);

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

	@Override
	public void registerAlias(String name, String alias) {
		// 参数校验
		Assert.hasText(name, "'name' must not be empty");
		Assert.hasText(alias, "'alias' must not be empty");
		synchronized (aliasMap) {
			// 如果真实的名字（beanName）和别名相同，则把别名移除点，因为真实的名字和别名相同没有意义
			if (alias.equals(name)) {
				// 移除别名
				aliasMap.remove(alias);
				if (logger.isDebugEnabled()) logger.debug("Alias definition '" + alias + "' ignored since it points to same name");
			}else {
				// 尝试从缓存中获取别名
				String registeredName = aliasMap.get(alias);
				 // 如果别名已经在缓存中存在
				if (registeredName != null) {
					// An existing alias - no need to re-register   缓存中的别名和beanName(注意:不是别名)相同,不做任何操作,没有必要再注册一次
					if (registeredName.equals(name)) return;
					// 如果别名不允许覆盖，则抛出异常  //缓存中存在别名,且不允许覆盖,抛出异常
					if (!allowAliasOverriding()) {
						throw new IllegalStateException("Cannot define alias '" + alias + "' for name '" + name + "': It is already registered for name '" + registeredName + "'.");
					}
					if (logger.isDebugEnabled()) logger.debug("Overriding alias '" + alias + "' definition for registered name '" + registeredName + "' with new target name '" + name + "'");
				}
				//确保添加的没有name和alias值相反的数据且alias和name不相等
				// 检查是否有循环别名注册 //检查给定名称是否已指向另一个方向的别名作为别名,预先捕获循环引用并抛出相应的IllegalStateException
				checkForAliasCircle(name, alias);
				// 注册别名 // 将别名作为key，目标bean名称作为值注册到存储别名的Map中
				// 缓存别名
				aliasMap.put(alias, name);
				if (logger.isTraceEnabled()) logger.trace("Alias definition '" + alias + "' registered for name '" + name + "'");
			}
		}
	}

	/**
	 * Return whether alias overriding is allowed.
	 * Default is {@code true}.
	 */
	protected boolean allowAliasOverriding() {
		return true;
	}

	/**
	 * Determine whether the given name has the given alias registered.
	 * @param name the name to check
	 * @param alias the alias to look for
	 * @since 4.2.1
	 */
	public boolean hasAlias(String name, String alias) {
		for (Map.Entry<String, String> entry : aliasMap.entrySet()) {
			String registeredName = entry.getValue();
			if (registeredName.equals(name)) {
				String registeredAlias = entry.getKey();
				if (registeredAlias.equals(alias) || hasAlias(registeredAlias, alias)) {
					return true;
				}
			}
		}
		return false;
	}


	/**
	 * Transitively retrieve all aliases for the given name.
	 * @param name the target name to find aliases for
	 * @param result the resulting aliases list
	 */
	private void retrieveAliases(String name, List<String> result) {
		aliasMap.forEach((alias, registeredName) -> {
			// 当找到name值和给定值相等是，会把这个alias作为name，再次对散列表进行遍历
			if (registeredName.equals(name)) {
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
	 * 确定原始名称，将别名解析为规范名称。
	 * @param name the user-specified name
	 * @return the transformed name
	 * 该方法用于转换别名
	 */
	public String canonicalName(String name) {
		String canonicalName = name;
		// Handle aliasing...
		String resolvedName;
		/*
		 * 这里使用 while 循环进行处理，原因是：可能会存在多重别名的问题，即别名指向别名。比如下面的配置：
		 *   <bean id="hello" class="service.Hello"/>
		 *   <alias name="hello" alias="aliasA"/>
		 *   <alias name="aliasA" alias="aliasB"/>
		 *
		 * 上面的别名指向关系为 aliasB -> aliasA -> hello，对于上面的别名配置，
		 * aliasMap 中数据视图为：aliasMap = [<aliasB, aliasA>, <aliasA, hello>]
		 * 通过下面的循环解析别名  aliasB 最终指向的 beanName
		 */
		do {
			//从别名缓存Map中获取对应beanName
			resolvedName = aliasMap.get(canonicalName);
			if (resolvedName != null) canonicalName = resolvedName;
		}
		while (resolvedName != null);
		return canonicalName;
	}

}
