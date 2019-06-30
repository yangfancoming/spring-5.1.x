

package org.springframework.context.annotation.spr12111;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("test")
public class TestProfileBean {
}
