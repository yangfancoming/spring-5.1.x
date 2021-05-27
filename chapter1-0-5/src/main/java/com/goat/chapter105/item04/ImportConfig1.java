package com.goat.chapter105.item04;

import com.goat.chapter105.model.Blue;
import com.goat.chapter105.model.Red;
import org.springframework.context.annotation.Import;

/**
 * Created by Administrator on 2020/3/17.
 *
 * @ Description: @Import导入组件，id 默认是组件的全类名
 * @ author  山羊来了
 * @ date 2020/3/17---16:53
 */
@Import({Blue.class, Red.class})
public class ImportConfig1 {

}
