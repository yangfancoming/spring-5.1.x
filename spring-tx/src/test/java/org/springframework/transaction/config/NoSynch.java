

package org.springframework.transaction.config;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.springframework.beans.factory.annotation.Qualifier;


@Qualifier("noSynch")
@Retention(RetentionPolicy.RUNTIME)
public @interface NoSynch {

}
