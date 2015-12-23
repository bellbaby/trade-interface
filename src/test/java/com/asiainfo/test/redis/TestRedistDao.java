package com.asiainfo.test.redis;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.asiainfo.ti.dto.TradeRequestMessage;
import com.asiainfo.ti.redis.dao.TradeMessageDao;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/spring/applicationContext.xml", "/spring/spring-redis.xml"})
public class TestRedistDao {
	
	@Autowired
	private TradeMessageDao  redisDao;
	
	@Test
	public void testGetObject(){
		
		TradeRequestMessage trm = new TradeRequestMessage();
		trm.setUuid(UUID.randomUUID().toString());
		trm.setPhonenum("18811151183");
		trm.setPackcode("003");
		trm.setOperid("001");
		
		SimpleDateFormat f = new SimpleDateFormat("hhmmss");
		String key = "order_"+f.format(new Date());
		redisDao.putReqeustMessage("order_01000", trm);
		redisDao.pushToQueue("flow_request_queue", "order_01000");
//		for(int i=0;i<1000;i++){
//			trm.setUuid(UUID.randomUUID().toString());
//			redisDao.pushToQueue("flow_request_queue", "order_"+i);
//			redisDao.putReqeustMessage("order_"+i, trm);
//		}
//		 trm = redisDao.getRequestMessage("order_1");
//		 System.out.println(new Gson().toJson(trm));
	}
}
