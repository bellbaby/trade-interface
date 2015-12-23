package com.asiainfo.test.redis;

import java.util.UUID;

import redis.clients.jedis.Jedis;

import com.asiainfo.ti.dto.TradeRequestMessage;
import com.google.gson.Gson;

public class TestJedis {
	public static void main(String[] args) {
//		Jedis j = new Jedis();
//		TradeRequestMessage trm = new TradeRequestMessage();
//		trm.setUuid(UUID.randomUUID().toString());
//		trm.setPhonenum("18811151183");
//		trm.setPackcode("003");
//		trm.setOperid("001");
//		Gson gson = new Gson();
//		
//		for(int i=0;i<1000;i++){
//			trm.setUuid(UUID.randomUUID().toString());
//			j.lpush("flow_request_queue", "order_"+i);
//			j.set("order_"+i, gson.toJson(trm));
//		}
	}
}
