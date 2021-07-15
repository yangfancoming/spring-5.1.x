package com.goat.chapter201.Import;

import org.springframework.context.annotation.Import;

/**
 * Created by Administrator on 2021/6/21.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2021/6/21---16:21
 */
@Import(MyDeferredImportSelector.class)
public class DeferredImportSelectorConfig {

}
