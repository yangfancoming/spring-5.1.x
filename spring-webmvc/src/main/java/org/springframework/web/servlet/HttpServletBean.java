

package org.springframework.web.servlet;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.PropertyAccessorFactory;
import org.springframework.beans.PropertyValue;
import org.springframework.beans.PropertyValues;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.EnvironmentCapable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceEditor;
import org.springframework.core.io.ResourceLoader;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.support.ServletContextResourceLoader;
import org.springframework.web.context.support.StandardServletEnvironment;

/**
 * Simple extension of {@link javax.servlet.http.HttpServlet} which treats
 * its config parameters ({@code init-param} entries within the {@code servlet} tag in {@code web.xml}) as bean properties.
 *
 * A handy superclass for any type of servlet. Type conversion of config parameters is automatic, with the corresponding setter method getting invoked with the converted value.
 * It is also possible for subclasses to specify required properties.
 * Parameters without matching bean property  setter will simply be ignored.
 * This servlet leaves request handling to subclasses, inheriting the default behavior of HttpServlet ({@code doGet}, {@code doPost}, etc).
 *
 * This generic servlet base class has no dependency on the Spring {@link org.springframework.context.ApplicationContext} concept.
 * Simple servlets usually don't load their own context but rather access service beans from the Spring root application context,
 * accessible via the filter's {@link #getServletContext() ServletContext} (see{@link org.springframework.web.context.support.WebApplicationContextUtils}).
 *
 * The {@link FrameworkServlet} class is a more specific servlet base
 * class which loads its own application context. FrameworkServlet serves as direct base class of Spring's full-fledged {@link DispatcherServlet}.
 * @see #addRequiredProperty
 * @see #initServletBean
 * @see #doGet
 * @see #doPost
 *
 * HttpServletBean主要配置servlet中初始化参数。继承HttpServlet，并实现无参的init()方法，
 * 用于设置在web.xml中配置的contextConfigLocation属性，此属性指定Spring MVC的配置文件地址，
 * 默认为WEB-INF/[servlet-name]-servlet.xml
 *
 * 总结HttpServletBean的作用：
 *
 * 1.获取web.xml的中配置DispatcherServlet的初始化参数，存放到一个参数容器ServletConfigPropertyValues中
 * 2.根据传进来的this创建BeanWrapper，本质上它就是DispatcherServlet
 * 3.通过bw.setPropertyValues(pvs, true)，把参数设置到bw(即DispatcherServlet)里面去
 * 4.最后调用子类的initServletBean()
 */
@SuppressWarnings("serial")
public abstract class HttpServletBean extends HttpServlet implements EnvironmentCapable, EnvironmentAware {

	/** Logger available to subclasses. */
	protected final Log logger = LogFactory.getLog(getClass());

	@Nullable
	private ConfigurableEnvironment environment;

	private final Set<String> requiredProperties = new HashSet<>(4);

	/**
	 * Subclasses can invoke this method to specify that this property
	 * (which must match a JavaBean property they expose) is mandatory,
	 * and must be supplied as a config parameter. This should be called
	 * from the constructor of a subclass.
	 * This method is only relevant in case of traditional initialization
	 * driven by a ServletConfig instance.
	 * @param property name of the required property
	 */
	protected final void addRequiredProperty(String property) {
		this.requiredProperties.add(property);
	}

	/**
	 * Set the {@code Environment} that this servlet runs in.
	 * Any environment set here overrides the {@link StandardServletEnvironment}
	 * provided by default.
	 * @throws IllegalArgumentException if environment is not assignable to
	 * {@code ConfigurableEnvironment}
	 */
	@Override
	public void setEnvironment(Environment environment) {
		Assert.isInstanceOf(ConfigurableEnvironment.class, environment, "ConfigurableEnvironment required");
		this.environment = (ConfigurableEnvironment) environment;
	}

	/**
	 * Return the {@link Environment} associated with this servlet.
	 * If none specified, a default environment will be initialized via
	 * {@link #createEnvironment()}.
	 */
	@Override
	public ConfigurableEnvironment getEnvironment() {
		if (this.environment == null) {
			this.environment = createEnvironment();
		}
		return this.environment;
	}

	/**
	 * Create and return a new {@link StandardServletEnvironment}.
	 * Subclasses may override this in order to configure the environment or
	 * specialize the environment type returned.
	 */
	protected ConfigurableEnvironment createEnvironment() {
		return new StandardServletEnvironment();
	}

	/**
	 * Map config parameters onto bean properties of this servlet, and invoke subclass initialization.
	 * 将配置参数映射到此servlet的bean属性上，并调用子类初始化。
	 * @throws ServletException if bean properties are invalid (or required properties are missing), or if subclass initialization fails.
	 * DispatcherServlet第一次加载时调用init方法
	 * 启动Tomcat后的程序入口 断点打在这里
	 */
	@Override
	public final void init() throws ServletException {
		// 获取 ServletConfig 中的配置信息
		// Set bean properties from init parameters. // ServletConfigPropertyValues 是静态内部类，使用 ServletConfig 获取 web.xml 中配置的参数
		// 获取在web.xml配置的初始化参数<init-param>，并将其设置到DispatcherServlet中
		// 读取在web.xml中通过init-param标签设置的属性，如果没有配置，这里pvs就会是empty的
		PropertyValues pvs = new ServletConfigPropertyValues(getServletConfig(), this.requiredProperties);
		if (!pvs.isEmpty()) {
			try {
				// 使用 BeanWrapper 来构造 DispatcherServlet   // 注册Resource对象对应的PropertyEditor
				// 为当前对象（比如 DispatcherServlet 对象）创建一个 BeanWrapper，方便读/写对象属性。
				BeanWrapper bw = PropertyAccessorFactory.forBeanPropertyAccess(this);
				ResourceLoader resourceLoader = new ServletContextResourceLoader(getServletContext());
				bw.registerCustomEditor(Resource.class, new ResourceEditor(resourceLoader, getEnvironment()));
				// 初始化BeanWrapper对象，这里是一个空方法，供给使用者对BeanWrapper进行自定义处理
				initBeanWrapper(bw);
				// 设置配置信息到目标对象中
				bw.setPropertyValues(pvs, true);
			}catch (BeansException ex) {
				if (logger.isErrorEnabled()) logger.error("Failed to set bean properties on servlet '" + getServletName() + "'", ex);
				throw ex;
			}
		}
		// 进行后续的初始化
		// 初始化当前DispatcherServlet的各项配置
		// Let subclasses do whatever initialization they like. // 让子类实现的方法，这种在父类定义在子类实现的方式叫做模版方法模式
		initServletBean();
	}

	/**
	 * Initialize the BeanWrapper for this HttpServletBean, possibly with custom editors.
	 * This default implementation is empty.
	 * @param bw the BeanWrapper to initialize
	 * @throws BeansException if thrown by BeanWrapper methods
	 * @see org.springframework.beans.BeanWrapper#registerCustomEditor
	 */
	protected void initBeanWrapper(BeanWrapper bw) throws BeansException {
	}

	/**
	 * Subclasses may override this to perform custom initialization.
	 * All bean properties of this servlet will have been set before this method is invoked.
	 * This default implementation is empty.
	 * @throws ServletException if subclass initialization fails
	 */
	protected void initServletBean() throws ServletException {
	}

	/**
	 * Overridden method that simply returns {@code null} when no
	 * ServletConfig set yet.
	 * @see #getServletConfig()
	 */
	@Override
	@Nullable
	public String getServletName() {
		return (getServletConfig() != null ? getServletConfig().getServletName() : null);
	}

	/**
	 * PropertyValues implementation created from ServletConfig init parameters.
	 */
	private static class ServletConfigPropertyValues extends MutablePropertyValues {
		/**
		 * Create new ServletConfigPropertyValues.
		 * @param config the ServletConfig we'll use to take PropertyValues from
		 * @param requiredProperties set of property names we need, where we can't accept default values
		 * @throws ServletException if any required properties are missing
		 */
		public ServletConfigPropertyValues(ServletConfig config, Set<String> requiredProperties) throws ServletException {
			Set<String> missingProps = (!CollectionUtils.isEmpty(requiredProperties) ? new HashSet<>(requiredProperties) : null);
			Enumeration<String> paramNames = config.getInitParameterNames();
			while (paramNames.hasMoreElements()) {
				String property = paramNames.nextElement();
				Object value = config.getInitParameter(property);
				addPropertyValue(new PropertyValue(property, value));
				if (missingProps != null) {
					missingProps.remove(property);
				}
			}
			// Fail if we are still missing properties.
			if (!CollectionUtils.isEmpty(missingProps)) {
				throw new ServletException(
						"Initialization from ServletConfig for servlet '" + config.getServletName() + "' failed; the following required properties were missing: " +
						StringUtils.collectionToDelimitedString(missingProps, ", "));
			}
		}
	}

}
