package com.goat.chapter651.service;

import com.goat.chapter651.dao.BookMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created by 64274 on 2019/10/7.
 *
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2019/10/7---19:43
 */
@Service
public class BookService {

	@Autowired
	BookMapper bookMapper;

	public List<Map> test(){
		List<Map> maps = bookMapper.queryAll();
		return maps;
	}
}
