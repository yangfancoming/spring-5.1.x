package com.goat.chapter534;



import org.springframework.core.convert.converter.Converter;
import org.springframework.expression.ParseException;

import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * Created by Administrator on 2020/1/10.
 *
 * @ Description: 字符串日期格式转换器  doit 为啥运行不到这个方法里？？？
 * @ author  山羊来了
 * @ date 2020/1/10---15:46
 */
public class CustomGlobalStrToDataConverter implements Converter<String, Date> {

	private String datePattern;//日期格式

	//创建对象,并传入构造参数
	public CustomGlobalStrToDataConverter(String datePattern){
		this.datePattern = datePattern;
	}

	@Override
	public Date convert(String source) {

		try {
			Date date = new SimpleDateFormat(datePattern).parse(source);
			return date;
		} catch (ParseException e) {
			e.printStackTrace();
		} catch (java.text.ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

}