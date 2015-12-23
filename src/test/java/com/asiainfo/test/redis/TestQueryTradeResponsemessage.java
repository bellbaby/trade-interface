package com.asiainfo.test.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.asiainfo.ti.dto.TradeResponseMessage;
import com.asiainfo.ti.redis.dao.TradeMessageDao;
import com.google.gson.Gson;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/spring/applicationContext.xml", "/spring/spring-redis.xml"})
public class TestQueryTradeResponsemessage {
	@Autowired
	private TradeMessageDao  redisDao;
	
	@Test
	public void test(){
		TradeResponseMessage trm = redisDao.getResponseMessage("order_201512180212");
		System.out.println(new Gson().toJson(trm));
	}
}
