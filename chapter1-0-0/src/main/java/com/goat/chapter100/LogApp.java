package com.goat.chapter100;


import org.apache.log4j.Logger;
import org.junit.Test;

/**
 * Created by Administrator on 2020/3/27.
 * @ Description: TODO
 * @ author  山羊来了
 * @ date 2020/3/27---16:52
 */
public class LogApp {

	private static final Logger logger = Logger.getLogger(LogApp.class);
	@Test
	public void testLog(){
		logger.trace("goat - trace");
		logger.info("goat - info");
		logger.debug("goat - debug");
		logger.error("goat - error");
	}

}


