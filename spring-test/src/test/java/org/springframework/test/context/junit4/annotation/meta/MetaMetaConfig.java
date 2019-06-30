

package org.springframework.test.context.junit4.annotation.meta;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.test.context.ActiveProfiles;

/**
 * Custom configuration annotation that is itself meta-annotated with {@link
 * ConfigClassesAndProfilesWithCustomDefaultsMetaConfig} and {@link ActiveProfiles}.
 *
 * @author Sam Brannen
 * @since 4.0.3
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@ConfigClassesAndProfilesWithCustomDefaultsMetaConfig
// Override default "dev" profile from
// @ConfigClassesAndProfilesWithCustomDefaultsMetaConfig:
@ActiveProfiles("prod")
public @interface MetaMetaConfig {

}
