

package org.springframework.core;

/**
 * Common interface for managing aliases. Serves as super-interface for {@link org.springframework.beans.factory.support.BeanDefinitionRegistry}.
 * @since 2.5.2
 */
public interface AliasRegistry { // 定义了对别名 alias 的简单增删改等操作

	/**
	 * Given a name, register an alias for it.
	 * @param name the canonical name
	 * @param alias the alias to be registered
	 * @throws IllegalStateException if the alias is already in use and may not be overridden
	 *  别名注册，需要说明两点：
	 * 1、需要避免循环别名和正名之间循环引用的问题。比如a->b   b->c   c->a这就循环引用了，是需要避免的，否则后续解析别名时会出现死循环
	 * 2、不能出现并发问题，若是并发注册，可能会出现如下情况。  a -> b   b->a (其实也是一种循环引用嘛)
	 */
	void registerAlias(String name, String alias);

	/**
	 * Remove the specified alias from this registry.
	 * @param alias the alias to remove
	 * @throws IllegalStateException if no such alias was found
	 */
	void removeAlias(String alias);

	/**
	 * Determine whether this given name is defines as an alias (as opposed to the name of an actually registered component).
	 * @param name the name to check
	 * @return whether the given name is an alias
	 */
	boolean isAlias(String name);

	/**
	 * Return the aliases for the given name, if defined. 根据给定的正名，获取该正名，所有已注册的所有别名
	 * @param name the name to check for aliases
	 * @return the aliases, or an empty array if none
	 */
	String[] getAliases(String name);

}
