
package org.mybatis.spring;

import org.apache.ibatis.builder.xml.XMLConfigBuilder;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.cache.Cache;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.io.VFS;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.reflection.wrapper.ObjectWrapperFactory;
import org.apache.ibatis.scripting.LanguageDriver;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.mybatis.spring.transaction.SpringManagedTransactionFactory;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.core.NestedIOException;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.ClassMetadata;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;
import org.springframework.util.ClassUtils;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Stream;

import static org.springframework.util.Assert.notNull;
import static org.springframework.util.Assert.state;
import static org.springframework.util.ObjectUtils.isEmpty;
import static org.springframework.util.StringUtils.hasLength;
import static org.springframework.util.StringUtils.tokenizeToStringArray;

/**
 * {@code FactoryBean} that creates a MyBatis {@code SqlSessionFactory}.
 * This is the usual way to set up a shared MyBatis {@code SqlSessionFactory} in a Spring application context;
 * the SqlSessionFactory can then be passed to MyBatis-based DAOs via dependency injection.
 *
 * Either {@code DataSourceTransactionManager} or {@code JtaTransactionManager} can be used for transaction demarcation  in combination with a {@code SqlSessionFactory}.
 * JTA should be used for transactions which span multiple databases or when container managed transactions (CMT) are being used.
 * @see #setConfigLocation
 * @see #setDataSource
 * SqlSessionFactoryBean 实现了Spring的InitializingBean接口，其中的 afterPropertiesSet 方法中会调用 buildSqlSessionFactory 方法创建 SqlSessionFactory
 *  实现了FactoryBean接口，所以返回的不是 SqlSessionFactoryBean 的实例，而是它的 SqlSessionFactoryBean.getObject() 的返回值：
 *  我们知道，
 *  1.实现了FactoryBean的bean会调用它的getObject方法创建bean， （这里创建SqlSessionFactoryBean）
 *  2.实现了InitializingBean的bean会在属性填充完成之后调用它的afterPropertiesSet方法
 */
public class SqlSessionFactoryBean implements FactoryBean<SqlSessionFactory>, InitializingBean, ApplicationListener<ApplicationEvent> {

	private static final Logger LOGGER = LoggerFactory.getLogger(SqlSessionFactoryBean.class);

	private static final ResourcePatternResolver RESOURCE_PATTERN_RESOLVER = new PathMatchingResourcePatternResolver();
	private static final MetadataReaderFactory METADATA_READER_FACTORY = new CachingMetadataReaderFactory();

	// 全局xml配置
	private Resource configLocation;

	private Configuration configuration;

	// 局部xml配置
	private Resource[] mapperLocations;

	private DataSource dataSource;

	private TransactionFactory transactionFactory;

	private Properties configurationProperties;

	private SqlSessionFactoryBuilder sqlSessionFactoryBuilder = new SqlSessionFactoryBuilder();

	private SqlSessionFactory sqlSessionFactory;

	// EnvironmentAware requires spring 3.1
	private String environment = SqlSessionFactoryBean.class.getSimpleName();

	private boolean failFast;

	private Interceptor[] plugins;

	private TypeHandler<?>[] typeHandlers;

	private String typeHandlersPackage;

	private Class<?>[] typeAliases;

	private String typeAliasesPackage;

	private Class<?> typeAliasesSuperType;

	private LanguageDriver[] scriptingLanguageDrivers;

	private Class<? extends LanguageDriver> defaultScriptingLanguageDriver;

	// issue #19. No default provider.
	private DatabaseIdProvider databaseIdProvider;

	private Class<? extends VFS> vfs;

	private Cache cache;

	private ObjectFactory objectFactory;

	private ObjectWrapperFactory objectWrapperFactory;

	/**
	 * Sets the ObjectFactory.
	 * @since 1.1.2
	 * @param objectFactory a custom ObjectFactory
	 */
	public void setObjectFactory(ObjectFactory objectFactory) {
		this.objectFactory = objectFactory;
	}

	/**
	 * Sets the ObjectWrapperFactory.
	 * @since 1.1.2
	 * @param objectWrapperFactory a specified ObjectWrapperFactory
	 */
	public void setObjectWrapperFactory(ObjectWrapperFactory objectWrapperFactory) {
		this.objectWrapperFactory = objectWrapperFactory;
	}

	/**
	 * Gets the DatabaseIdProvider
	 * @since 1.1.0
	 * @return a specified DatabaseIdProvider
	 */
	public DatabaseIdProvider getDatabaseIdProvider() {
		return databaseIdProvider;
	}

	/**
	 * Sets the DatabaseIdProvider. As of version 1.2.2 this variable is not initialized by default.
	 * @since 1.1.0
	 * @param databaseIdProvider  a DatabaseIdProvider
	 */
	public void setDatabaseIdProvider(DatabaseIdProvider databaseIdProvider) {
		this.databaseIdProvider = databaseIdProvider;
	}

	/**
	 * Gets the VFS.
	 * @return a specified VFS
	 */
	public Class<? extends VFS> getVfs() {
		return this.vfs;
	}

	/**
	 * Sets the VFS.
	 * @param vfs  a VFS
	 */
	public void setVfs(Class<? extends VFS> vfs) {
		this.vfs = vfs;
	}

	/**
	 * Gets the Cache.
	 * @return a specified Cache
	 */
	public Cache getCache() {
		return this.cache;
	}

	/**
	 * Sets the Cache.
	 * @param cache  a Cache
	 */
	public void setCache(Cache cache) {
		this.cache = cache;
	}

	/**
	 * Mybatis plugin list.
	 * @since 1.0.1
	 * @param plugins   list of plugins
	 */
	public void setPlugins(Interceptor... plugins) {
		this.plugins = plugins;
	}

	/**
	 * Packages to search for type aliases.
	 * Since 2.0.1, allow to specify a wildcard such as {@code com.example.*.model}.
	 * @since 1.0.1
	 * @param typeAliasesPackage   package to scan for domain objects
	 */
	public void setTypeAliasesPackage(String typeAliasesPackage) {
		this.typeAliasesPackage = typeAliasesPackage;
	}

	/**
	 * Super class which domain objects have to extend to have a type alias created. No effect if there is no package to scan configured.
	 * @since 1.1.2
	 * @param typeAliasesSuperType super class for domain objects
	 */
	public void setTypeAliasesSuperType(Class<?> typeAliasesSuperType) {
		this.typeAliasesSuperType = typeAliasesSuperType;
	}

	/**
	 * Packages to search for type handlers.
	 * Since 2.0.1, allow to specify a wildcard such as {@code com.example.*.typehandler}.
	 * @since 1.0.1
	 * @param typeHandlersPackage package to scan for type handlers
	 */
	public void setTypeHandlersPackage(String typeHandlersPackage) {
		this.typeHandlersPackage = typeHandlersPackage;
	}

	/**
	 * Set type handlers. They must be annotated with {@code MappedTypes} and optionally with {@code MappedJdbcTypes}
	 * @since 1.0.1
	 * @param typeHandlers Type handler list
	 */
	public void setTypeHandlers(TypeHandler<?>... typeHandlers) {
		this.typeHandlers = typeHandlers;
	}

	/**
	 * List of type aliases to register. They can be annotated with {@code Alias}
	 * @since 1.0.1
	 * @param typeAliases Type aliases list
	 */
	public void setTypeAliases(Class<?>... typeAliases) {
		this.typeAliases = typeAliases;
	}

	/**
	 * If true, a final check is done on Configuration to assure that all mapped statements are fully loaded and there is
	 * no one still pending to resolve includes. Defaults to false.
	 * @since 1.0.1
	 * @param failFast  enable failFast
	 */
	public void setFailFast(boolean failFast) {
		this.failFast = failFast;
	}

	/**
	 * Set the location of the MyBatis {@code SqlSessionFactory} config file. A typical value is "WEB-INF/mybatis-configuration.xml".
	 * @param configLocation  a location the MyBatis config file
	 */
	public void setConfigLocation(Resource configLocation) {
		this.configLocation = configLocation;
	}

	/**
	 * Set a customized MyBatis configuration.
	 * @param configuration MyBatis configuration
	 * @since 1.3.0
	 */
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Set locations of MyBatis mapper files that are going to be merged into the {@code SqlSessionFactory} configuration t runtime.
	 * This is an alternative to specifying "&lt;sqlmapper&gt;" entries in an MyBatis config file. This property being
	 * based on Spring's resource abstraction also allows for specifying resource patterns here: e.g.
	 * "classpath*:sqlmap/*-mapper.xml".
	 * @param mapperLocations location of MyBatis mapper files
	 */
	public void setMapperLocations(Resource... mapperLocations) {
		this.mapperLocations = mapperLocations;
	}

	/**
	 * Set optional properties to be passed into the SqlSession configuration, as alternative to a Properties tag in the configuration xml file.
	 *  This will be used to resolve placeholders in the config file.
	 * @param sqlSessionFactoryProperties optional properties for the SqlSessionFactory
	 */
	public void setConfigurationProperties(Properties sqlSessionFactoryProperties) {
		this.configurationProperties = sqlSessionFactoryProperties;
	}

	/**
	 * Set the JDBC {@code DataSource} that this instance should manage transactions for. The {@code DataSource} should
	 * match the one used by the {@code SqlSessionFactory}: for example, you could specify the same JNDI DataSource for both.
	 * A transactional JDBC {@code Connection} for this {@code DataSource} will be provided to application code accessing
	 * this {@code DataSource} directly via {@code DataSourceUtils} or {@code DataSourceTransactionManager}.
	 * The {@code DataSource} specified here should be the target {@code DataSource} to manage transactions for, not a
	 * {@code TransactionAwareDataSourceProxy}. Only data access code may work with
	 * {@code TransactionAwareDataSourceProxy}, while the transaction manager needs to work on the underlying target
	 * {@code DataSource}. If there's nevertheless a {@code TransactionAwareDataSourceProxy} passed in, it will be
	 * unwrapped to extract its target {@code DataSource}.
	 * @param dataSource  a JDBC {@code DataSource}
	 */
	public void setDataSource(DataSource dataSource) {
		if (dataSource instanceof TransactionAwareDataSourceProxy) {
			// If we got a TransactionAwareDataSourceProxy, we need to perform
			// transactions for its underlying target DataSource, else data
			// access code won't see properly exposed transactions (i.e.
			// transactions for the target DataSource).
			this.dataSource = ((TransactionAwareDataSourceProxy) dataSource).getTargetDataSource();
		} else {
			this.dataSource = dataSource;
		}
	}

	/**
	 * Sets the {@code SqlSessionFactoryBuilder} to use when creating the {@code SqlSessionFactory}.
	 * This is mainly meant for testing so that mock SqlSessionFactory classes can be injected. By default,
	 * {@code SqlSessionFactoryBuilder} creates {@code DefaultSqlSessionFactory} instances.
	 * @param sqlSessionFactoryBuilder a SqlSessionFactoryBuilder
	 */
	public void setSqlSessionFactoryBuilder(SqlSessionFactoryBuilder sqlSessionFactoryBuilder) {
		this.sqlSessionFactoryBuilder = sqlSessionFactoryBuilder;
	}

	/**
	 * Set the MyBatis TransactionFactory to use. Default is {@code SpringManagedTransactionFactory}
	 * The default {@code SpringManagedTransactionFactory} should be appropriate for all cases: be it Spring transaction
	 * management, EJB CMT or plain JTA. If there is no active transaction, SqlSession operations will execute SQL
	 * statements non-transactionally.
	 * <b>It is strongly recommended to use the default {@code TransactionFactory}.</b> If not used, any attempt at
	 * getting an SqlSession through Spring's MyBatis framework will throw an exception if a transaction is active.
	 * @see SpringManagedTransactionFactory
	 * @param transactionFactory  the MyBatis TransactionFactory
	 */
	public void setTransactionFactory(TransactionFactory transactionFactory) {
		this.transactionFactory = transactionFactory;
	}

	/**
	 * <b>NOTE:</b> This class <em>overrides</em> any {@code Environment} you have set in the MyBatis config file. This is
	 * used only as a placeholder name. The default value is {@code SqlSessionFactoryBean.class.getSimpleName()}.
	 * @param environment  the environment name
	 */
	public void setEnvironment(String environment) {
		this.environment = environment;
	}

	/**
	 * Set scripting language drivers.
	 * @param scriptingLanguageDrivers scripting language drivers
	 * @since 2.0.2
	 */
	public void setScriptingLanguageDrivers(LanguageDriver... scriptingLanguageDrivers) {
		this.scriptingLanguageDrivers = scriptingLanguageDrivers;
	}

	/**
	 * Set a default scripting language driver class.
	 * @param defaultScriptingLanguageDriver  A default scripting language driver class
	 * @since 2.0.2
	 */
	public void setDefaultScriptingLanguageDriver(Class<? extends LanguageDriver> defaultScriptingLanguageDriver) {
		this.defaultScriptingLanguageDriver = defaultScriptingLanguageDriver;
	}


	//---------------------------------------------------------------------
	// Implementation of 【InitializingBean】 interface
	//---------------------------------------------------------------------
	@Override
	public void afterPropertiesSet() throws Exception {
		notNull(dataSource, "Property 'dataSource' is required");
		notNull(sqlSessionFactoryBuilder, "Property 'sqlSessionFactoryBuilder' is required");
		state((configuration == null && configLocation == null) || !(configuration != null && configLocation != null),"Property 'configuration' and 'configLocation' can not specified with together");
		sqlSessionFactory = buildSqlSessionFactory();
	}

	/**
	 * Build a {@code SqlSessionFactory} instance.
	 * The default implementation uses the standard MyBatis {@code XMLConfigBuilder} API to build a {@code SqlSessionFactory} instance based on an Reader.
	 * Since 1.3.0, it can be specified a {@link Configuration} instance directly(without config file).
	 * @return SqlSessionFactory
	 * @throws Exception  if configuration is failed
	 * buildSqlSessionFactory 方法内部会使用XMLConfigBuilder解析属性configLocation中配置的路径，还会使用 XMLMapperBuilder 属性解析mapperLocations属性中的各个xml文件。
	 */
	protected SqlSessionFactory buildSqlSessionFactory() throws Exception {
		final Configuration targetConfiguration;
		XMLConfigBuilder xmlConfigBuilder = null;
		if (configuration != null) {
			targetConfiguration = configuration;
			if (targetConfiguration.getVariables() == null) {
				targetConfiguration.setVariables(configurationProperties);
			} else if (configurationProperties != null) {
				targetConfiguration.getVariables().putAll(configurationProperties);
			}
		} else if (configLocation != null) { // 如果spring配置文件中指定了全局xml配置文件路径  则进行解析
			xmlConfigBuilder = new XMLConfigBuilder(configLocation.getInputStream(), null, configurationProperties);
			targetConfiguration = xmlConfigBuilder.getConfiguration();
		} else { // 如果 configuration 为null 并且又没有指定全局xml配置文件路径的情况下，使用mybatis的默认配置
			LOGGER.debug( () -> "Property 'configuration' or 'configLocation' not specified, using default MyBatis Configuration");
			targetConfiguration = new Configuration();
			Optional.ofNullable(configurationProperties).ifPresent(targetConfiguration::setVariables);
		}
		// 配置 objectFactory
		// 根据Spring配置文件，设置Configuration.objectWrapperFactory
		// 根据Spring配置文件，设置Configuration.objectFactory
		// 扫描typeAliasesPackage指定的包，并为其中的类注册别名
		// 为typeAliases集合中指定的类注册别名
		// 注册plugins集合中指定的插件
		// 扫描typeHandlersPackage指定的包，并注册其中的TypeHandler
		// 注册指定的typeHandlers
		// 配置databaseIdProvider
		// 配置缓存
		Optional.ofNullable(objectFactory).ifPresent(targetConfiguration::setObjectFactory);
		Optional.ofNullable(objectWrapperFactory).ifPresent(targetConfiguration::setObjectWrapperFactory);
		Optional.ofNullable(vfs).ifPresent(targetConfiguration::setVfsImpl);

		if (hasLength(typeAliasesPackage)) {
			scanClasses(typeAliasesPackage, typeAliasesSuperType).stream()
					// 过滤掉匿名类、接口、内部类
					.filter(clazz -> !clazz.isAnonymousClass()).filter(clazz -> !clazz.isInterface()).filter(clazz -> !clazz.isMemberClass())
					.forEach(targetConfiguration.getTypeAliasRegistry()::registerAlias);
		}

		if (!isEmpty(typeAliases)) {
			Stream.of(typeAliases).forEach(typeAlias -> {
				targetConfiguration.getTypeAliasRegistry().registerAlias(typeAlias);
				LOGGER.debug(() -> "Registered type alias: '" + typeAlias + "'");
			});
		}

		if (!isEmpty(plugins)) {
			Stream.of(plugins).forEach(plugin -> {
				targetConfiguration.addInterceptor(plugin);
				LOGGER.debug(() -> "Registered plugin: '" + plugin + "'");
			});
		}

		if (hasLength(typeHandlersPackage)) {
			scanClasses(typeHandlersPackage, TypeHandler.class).stream().filter(clazz -> !clazz.isAnonymousClass())
					.filter(clazz -> !clazz.isInterface()).filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
					.filter(clazz -> ClassUtils.getConstructorIfAvailable(clazz) != null)
					.forEach(targetConfiguration.getTypeHandlerRegistry()::register);
		}

		if (!isEmpty(typeHandlers)) {
			Stream.of(typeHandlers).forEach(typeHandler -> {
				targetConfiguration.getTypeHandlerRegistry().register(typeHandler);
				LOGGER.debug(() -> "Registered type handler: '" + typeHandler + "'");
			});
		}

		if (!isEmpty(scriptingLanguageDrivers)) {
			Stream.of(scriptingLanguageDrivers).forEach(languageDriver -> {
				targetConfiguration.getLanguageRegistry().register(languageDriver);
				LOGGER.debug(() -> "Registered scripting language driver: '" + languageDriver + "'");
			});
		}
		Optional.ofNullable(defaultScriptingLanguageDriver) .ifPresent(targetConfiguration::setDefaultScriptingLanguage);

		if (databaseIdProvider != null) {// fix #64 set databaseId before parse mapper xmls
			try {
				targetConfiguration.setDatabaseId(databaseIdProvider.getDatabaseId(dataSource));
			} catch (SQLException e) {
				throw new NestedIOException("Failed getting a databaseId", e);
			}
		}
		Optional.ofNullable(cache).ifPresent(targetConfiguration::addCache);
		// 调parse()方法解析配置文件
		if (xmlConfigBuilder != null) {
			try {
				xmlConfigBuilder.parse();
				LOGGER.debug(() -> "Parsed configuration file: '" + configLocation + "'");
			} catch (Exception ex) {
				throw new NestedIOException("Failed to parse config resource: " + configLocation, ex);
			} finally {
				ErrorContext.instance().reset();
			}
		}
		// 如果未配置transactionFactory，则默认使用SpringManagedTransactionFactory
		TransactionFactory transactionFactory = this.transactionFactory == null ? new SpringManagedTransactionFactory() : this.transactionFactory;
		// 设置environment
		targetConfiguration.setEnvironment(new Environment(environment,transactionFactory,dataSource));

		if (mapperLocations != null) {
			if (mapperLocations.length == 0) {
				LOGGER.warn(() -> "Property 'mapperLocations' was specified but matching resources are not found.");
			} else {
				// 遍历局部xml配置
				for (Resource mapperLocation : mapperLocations) {
					if (mapperLocation == null) continue;
					try {
						// 使用 XMLConfigBuilder解析 局部xml配置
						XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(mapperLocation.getInputStream(),targetConfiguration, mapperLocation.toString(), targetConfiguration.getSqlFragments());
						xmlMapperBuilder.parse();
					} catch (Exception e) {
						throw new NestedIOException("Failed to parse mapping resource: '" + mapperLocation + "'", e);
					} finally {
						ErrorContext.instance().reset();
					}
					LOGGER.debug(() -> "Parsed mapper file: '" + mapperLocation + "'");
				}
			}
		} else {
			LOGGER.debug(() -> "Property 'mapperLocations' was not specified.");
		}
		// 最终 调用SqlSessionFactoryBuilder.build()方法，创建SqlSessionFactory对象并返回
		return sqlSessionFactoryBuilder.build(targetConfiguration);
	}

	//---------------------------------------------------------------------
	// Implementation of 【FactoryBean<T>】 interface
	//---------------------------------------------------------------------
	@Override
	public SqlSessionFactory getObject() throws Exception {
		if (sqlSessionFactory == null) afterPropertiesSet();
		return sqlSessionFactory;
	}

	@Override
	public Class<? extends SqlSessionFactory> getObjectType() {
		return sqlSessionFactory == null ? SqlSessionFactory.class : sqlSessionFactory.getClass();
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

	//---------------------------------------------------------------------
	// Implementation of 【ApplicationListener<T>】 interface
	//---------------------------------------------------------------------
	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		if (failFast && event instanceof ContextRefreshedEvent) {
			// fail-fast -> check all statements are completed
			sqlSessionFactory.getConfiguration().getMappedStatementNames();
		}
	}

	private Set<Class<?>> scanClasses(String packagePatterns, Class<?> assignableType) throws IOException {
		Set<Class<?>> classes = new HashSet<>();
		String[] packagePatternArray = tokenizeToStringArray(packagePatterns,ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
		for (String packagePattern : packagePatternArray) {
			Resource[] resources = RESOURCE_PATTERN_RESOLVER.getResources(ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(packagePattern) + "/**/*.class");
			for (Resource resource : resources) {
				try {
					ClassMetadata classMetadata = METADATA_READER_FACTORY.getMetadataReader(resource).getClassMetadata();
					Class<?> clazz = Resources.classForName(classMetadata.getClassName());
					if (assignableType == null || assignableType.isAssignableFrom(clazz)) {
						classes.add(clazz);
					}
				} catch (Throwable e) {
					LOGGER.warn(() -> "Cannot load the '" + resource + "'. Cause by " + e.toString());
				}
			}
		}
		return classes;
	}

}
