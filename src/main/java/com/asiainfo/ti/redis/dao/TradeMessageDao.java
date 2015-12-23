package com.asiainfo.ti.redis.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

import com.asiainfo.ti.dto.TradeRequestMessage;
import com.asiainfo.ti.dto.TradeResponseMessage;
import com.asiainfo.ti.exception.RedisDaoException;
import com.google.gson.Gson;

@Component
public class TradeMessageDao {
	
	@Autowired
	private JedisPool jedisPool ;
	
	public JedisPool getJedisPool() {
		return jedisPool;
	}

	public void setJedisPool(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	public TradeRequestMessage getRequestMessage(String key){
		TradeRequestMessage trm = null;
		Jedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			String msg = jedis.get(key);
			
			Gson gson = new Gson();
			trm = gson.fromJson(msg, TradeRequestMessage.class);
			System.out.println(msg);
		}catch(JedisConnectionException e){
			throw new RedisDaoException("get Redis Resource failed.NumActive:"+jedisPool.getNumActive()
					+" NumIdle"+jedisPool.getNumIdle()+" NumWaiters"+jedisPool.getNumWaiters(),e);
		}catch (Exception e) {
			throw new RedisDaoException("get trade request message failed",e);
		}finally{
			if(jedis!=null){
				jedis.close();
			}
		}
		
		return trm;
	}
	
	public void putReqeustMessage(String key,TradeRequestMessage trm){
		Jedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			Gson gson = new Gson();
			jedis.set(key,gson.toJson(trm));
		}catch(JedisConnectionException e){
			throw new RedisDaoException("get Redis Resource failed.NumActive:"+jedisPool.getNumActive()
					+" NumIdle"+jedisPool.getNumIdle()+" NumWaiters"+jedisPool.getNumWaiters(),e);
		}catch(Exception e){
			throw new RedisDaoException("put trade request message failed!");
		}finally{
			if(jedis!=null){
				jedis.close();
			}
		}
	}
	
	public TradeResponseMessage getResponseMessage(String key){
		TradeResponseMessage trm = null;
		Jedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			String msg = jedis.get(key);
			Gson gson = new Gson();
			trm = gson.fromJson(msg, TradeResponseMessage.class);
			System.out.println(msg);
		}catch(JedisConnectionException e){
			throw new RedisDaoException("get Redis Resource failed.NumActive:"+jedisPool.getNumActive()
					+" NumIdle"+jedisPool.getNumIdle()+" NumWaiters"+jedisPool.getNumWaiters(),e);
		}catch (Exception e) {
			throw new RedisDaoException("get trade response message failed",e);
		}finally{
			if(jedis!=null){
				jedis.close();
			}
		}
		return trm;
	}
	
	public void putResponseMessage(String key,TradeResponseMessage trm){
		Jedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			Gson gson = new Gson();
			jedis.set(key, gson.toJson(trm));
		}catch(JedisConnectionException e){
			throw new RedisDaoException("get Redis Resource failed.NumActive:"+jedisPool.getNumActive()
					+" NumIdle"+jedisPool.getNumIdle()+" NumWaiters"+jedisPool.getNumWaiters(),e);
		}catch (Exception e) {
			throw new RedisDaoException("put trade result message failed.",e);
		}
	}
	
	public void pushToQueue(String key,String value){
		Jedis jedis = null;
		try{
			jedis = jedisPool.getResource();
			jedis.rpush(key, value);
		}catch(JedisConnectionException e){
			throw new RedisDaoException("get Redis Resource failed.NumActive:"+jedisPool.getNumActive()
					+" NumIdle"+jedisPool.getNumIdle()+" NumWaiters"+jedisPool.getNumWaiters(),e);
		}catch(Exception e){
			throw new RedisDaoException("push message in queue failed",e);
		}finally{
			if(jedis!=null){
				jedis.close();
			}
		}
	}
}
