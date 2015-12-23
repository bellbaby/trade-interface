package com.asiainfo.test.redis;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"/spring/applicationContext.xml", "/spring/spring-redis.xml"})
public class TestClearDate {
	
	@Autowired
	private JedisPool pool;
	
	@Test
	public void test(){
		int count = 0;
		Jedis j = null;
		try{
			j = pool.getResource();
			while(true){
				String s = j.lpop("flow_aftercharge_queue");
				if(s!=null){
					count++;
				}else{
					break;
				}
			}
			System.out.println("清楚队列flow_aftercharge_queue:"+count);
		while(true){
			count = 0;
			String s = j.lpop("flow_request_queue");
			if(s!=null){
				count++;
			}else{
				break;
			}
		}
		System.out.println("清楚队列flow_request_queue:"+count);
		
		}finally{
			j.close();
		}
		
		
	}
}
