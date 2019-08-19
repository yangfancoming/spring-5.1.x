package com.goat.chapter606;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by 64274 on 2019/8/19.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/8/19---19:56
 */
@Transactional(propagation = Propagation.REQUIRED)
public interface AccountService {

	void save() throws RuntimeException;
}
