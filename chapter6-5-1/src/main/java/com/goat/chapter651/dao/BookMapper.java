package com.goat.chapter651.dao;

import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by 64274 on 2019/10/7.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/10/7---19:29
 */
@Repository
public interface BookMapper {

	@Select("select * from book")
	List<Map> queryAll();
}
