
package org.mybatis.spring.annotation;

import org.mybatis.logging.Logger;
import org.mybatis.logging.LoggerFactory;
import org.mybatis.spring.mapper.ClassPathMapperScanner;
import org.mybatis.spring.mapper.MapperFactoryBean;
import org.mybatis.spring.mapper.MapperScannerConfigurer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanNameGenerator;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A {@link ImportBeanDefinitionRegistrar} to allow annotation configuration of MyBatis mapper scanning.
 * Using  an @Enable annotation allows beans to be registered via @Component configuration, whereas implementing
 * {@code BeanDefinitionRegistryPostProcessor} will work for XML configuration.
 * @see MapperFactoryBean
 * @see ClassPathMapperScanner
 * @since 1.2.0
 */
public class MapperScannerRegistrar implements ImportBeanDefinitionRegistrar {

	private static final Logger LOGGER = LoggerFactory.getLogger(MapperScannerRegistrar.class);

  @Override
  public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
  	// 拿到 @MapperScan 注解上配置的所有属性值
    AnnotationAttributes mapperScanAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(MapperScan.class.getName()));
    if (mapperScanAttrs != null) {
    	// 准备向容器中注册 MapperScannerConfigurer 的bean定义
		LOGGER.warn(() -> "【mybatis 处理 @MapperScan 注解  ---   】" );
      registerBeanDefinitions(mapperScanAttrs, registry, generateBaseBeanName(importingClassMetadata, 0));
    }
  }

  void registerBeanDefinitions(AnnotationAttributes annoAttrs, BeanDefinitionRegistry registry, String beanName) {
  	// 生成 MapperScannerConfigurer 的bean定义
    BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(MapperScannerConfigurer.class);
    builder.addPropertyValue("processPropertyPlaceHolders", true);

    //  为 MapperScannerConfigurer的bean定义所有属性挨个赋值(都是@MapperScan注解对应的属性)
    Class<? extends Annotation> annotationClass = annoAttrs.getClass("annotationClass");
    if (!Annotation.class.equals(annotationClass)) {
      builder.addPropertyValue("annotationClass", annotationClass);
    }

    Class<?> markerInterface = annoAttrs.getClass("markerInterface");
    if (!Class.class.equals(markerInterface)) {
      builder.addPropertyValue("markerInterface", markerInterface);
    }

    Class<? extends BeanNameGenerator> generatorClass = annoAttrs.getClass("nameGenerator");
    if (!BeanNameGenerator.class.equals(generatorClass)) {
      builder.addPropertyValue("nameGenerator", BeanUtils.instantiateClass(generatorClass));
    }

    Class<? extends MapperFactoryBean> mapperFactoryBeanClass = annoAttrs.getClass("factoryBean");
    if (!MapperFactoryBean.class.equals(mapperFactoryBeanClass)) {
      builder.addPropertyValue("mapperFactoryBeanClass", mapperFactoryBeanClass);
    }

    String sqlSessionTemplateRef = annoAttrs.getString("sqlSessionTemplateRef");
    if (StringUtils.hasText(sqlSessionTemplateRef)) {
      builder.addPropertyValue("sqlSessionTemplateBeanName", annoAttrs.getString("sqlSessionTemplateRef"));
    }

    String sqlSessionFactoryRef = annoAttrs.getString("sqlSessionFactoryRef");
    if (StringUtils.hasText(sqlSessionFactoryRef)) {
      builder.addPropertyValue("sqlSessionFactoryBeanName", annoAttrs.getString("sqlSessionFactoryRef"));
    }

    // basePackages 以下三种情况，都会转换并存入 basePackage
    List<String> basePackages = new ArrayList<>();
    // @MapperScan("org.mybatis.spring.mapper")
    basePackages.addAll( Arrays.stream(annoAttrs.getStringArray("value")).filter(StringUtils::hasText).collect(Collectors.toList()));
    basePackages.addAll(Arrays.stream(annoAttrs.getStringArray("basePackages")).filter(StringUtils::hasText).collect(Collectors.toList()));
    // @MapperScan(basePackageClasses = MapperInterface.class)
    basePackages.addAll(Arrays.stream(annoAttrs.getClassArray("basePackageClasses")).map(ClassUtils::getPackageName).collect(Collectors.toList()));
    String lazyInitialization = annoAttrs.getString("lazyInitialization");
    if (StringUtils.hasText(lazyInitialization)) {
      builder.addPropertyValue("lazyInitialization", lazyInitialization);
    }
    builder.addPropertyValue("basePackage", StringUtils.collectionToCommaDelimitedString(basePackages)); // basePackageClasses 转换并存入 basePackage
    /**
	 *  经过以上的属性赋值后，将 MapperScannerConfigurer 对象注入到容器中
	 * 	由于 MapperScannerConfigurer 实现了 BeanDefinitionRegistryPostProcessor 接口，
	 * 	容器还会调用其 postProcessBeanDefinitionRegistry 方法
    */
    registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
  }

  private static String generateBaseBeanName(AnnotationMetadata importingClassMetadata, int index) {
    return importingClassMetadata.getClassName() + "#" + MapperScannerRegistrar.class.getSimpleName() + "#" + index;
  }

  /**
   * A {@link MapperScannerRegistrar} for {@link MapperScans}.
   * @since 2.0.0
   */
  static class RepeatingRegistrar extends MapperScannerRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
      AnnotationAttributes mapperScansAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(MapperScans.class.getName()));
      if (mapperScansAttrs != null) {
        AnnotationAttributes[] annotations = mapperScansAttrs.getAnnotationArray("value");
        for (int i = 0; i < annotations.length; i++) {
          registerBeanDefinitions(annotations[i], registry, generateBaseBeanName(importingClassMetadata, i));
        }
      }
    }
  }

}
