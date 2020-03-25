package com.goat.chapter650.dao;



import java.util.List;
import java.util.Map;

/**
 * Created by 64274 on 2019/10/7.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/10/7---19:29
 */
public interface BookMapper extends IFoo{
	public List<Map> selectById();
}
