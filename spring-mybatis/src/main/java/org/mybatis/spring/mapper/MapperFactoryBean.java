
package org.mybatis.spring.mapper;

import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.FactoryBean;

import static org.springframework.util.Assert.notNull;

/**
 * BeanFactory that enables injection of MyBatis mapper interfaces. It can be set up with a SqlSessionFactory or a pre-configured SqlSessionTemplate.
 * Sample configuration:
 *
 * <pre class="code">
 * {@code
 *   <bean id="baseMapper" class="org.mybatis.spring.mapper.MapperFactoryBean" abstract="true" lazy-init="true">
 *     <property name="sqlSessionFactory" ref="sqlSessionFactory" />
 *   </bean>
 *
 *   <bean id="oneMapper" parent="baseMapper">
 *     <property name="mapperInterface" value="my.package.MyMapperInterface" />
 *   </bean>
 *
 *   <bean id="anotherMapper" parent="baseMapper">
 *     <property name="mapperInterface" value="my.package.MyAnotherMapperInterface" />
 *   </bean>
 * }
 * </pre>
 * Note that this factory can only inject <em>interfaces</em>, not concrete classes.
 * @see SqlSessionTemplate
 */
public class MapperFactoryBean<T> extends SqlSessionDaoSupport implements FactoryBean<T> {

	private Class<T> mapperInterface;

	private boolean addToConfig = true;

	public MapperFactoryBean() {
		// intentionally empty
		logger.warn("【mybatis】 MapperFactoryBean 无参构造函数 执行");
	}

	public MapperFactoryBean(Class<T> mapperInterface) {
		logger.warn("【mybatis】 MapperFactoryBean 单参构造函数 执行");
		this.mapperInterface = mapperInterface;
	}

	@Override
	protected void checkDaoConfig() {
		super.checkDaoConfig(); /* 调用父类方法进行dao配置检查 */
		// 检查mapper接口不能为null
		notNull(mapperInterface, "Property 'mapperInterface' is required");
		Configuration configuration = getSqlSession().getConfiguration();
		if (addToConfig && !configuration.hasMapper(mapperInterface)) {
			try {
				// 如果Mybatis的Configuration配置中没有当前mapper，则添加
				configuration.addMapper(mapperInterface);
			} catch (Exception e) {
				logger.error("Error while adding the mapper '" + mapperInterface + "' to configuration.", e);
				throw new IllegalArgumentException(e);
			} finally {
				ErrorContext.instance().reset();
			}
		}
	}

	@Override
	public T getObject() throws Exception {
		/* 获取mapper ： 生成实例的时候，通过这个函数整合到了mybatis操作数据库的接口。*/
		return getSqlSession().getMapper(mapperInterface);
	}

	@Override
	public Class<T> getObjectType() {
		return mapperInterface;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	// ------------- mutators --------------
	/**
	 * Sets the mapper interface of the MyBatis mapper
	 * @param mapperInterface class of the interface
	 */
	public void setMapperInterface(Class<T> mapperInterface) {
		this.mapperInterface = mapperInterface;
	}

	/**
	 * Return the mapper interface of the MyBatis mapper
	 * @return class of the interface
	 */
	public Class<T> getMapperInterface() {
		return mapperInterface;
	}

	/**
	 * If addToConfig is false the mapper will not be added to MyBatis. This means it must have been included in mybatis-config.xml.
	 * If it is true, the mapper will be added to MyBatis in the case it is not already registered.
	 * By default addToConfig is true.
	 * @param addToConfig a flag that whether add mapper to MyBatis or not
	 */
	public void setAddToConfig(boolean addToConfig) {
		this.addToConfig = addToConfig;
	}

	/**
	 * Return the flag for addition into MyBatis config.
	 * @return true if the mapper will be added to MyBatis in the case it is not already registered.
	 */
	public boolean isAddToConfig() {
		return addToConfig;
	}
}
