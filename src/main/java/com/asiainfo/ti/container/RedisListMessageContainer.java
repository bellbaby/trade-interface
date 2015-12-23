package com.asiainfo.ti.container;

import java.io.IOException;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisConnectionException;

public class RedisListMessageContainer extends AbstractContainer{

	private final static Log logger= LogFactory.getLog(RedisListMessageContainer.class);
	
	private JedisPool pool;
	
	private String queue;
	private String errorQueue;
	
	private MessageListener listener;
	
	private int timeoutIntervel;

	public JedisPool getPool() {
		return pool;
	}

	public void setPool(JedisPool pool) {
		this.pool = pool;
	}

	public String getQueue() {
		return queue;
	}

	public void setQueue(String queue) {
		this.queue = queue;
	}

	public MessageListener getListener() {
		return listener;
	}

	public void setListener(MessageListener listener) {
		this.listener = listener;
	}

	public int getTimeoutIntervel() {
		return timeoutIntervel;
	}

	public void setTimeoutIntervel(int timeoutIntervel) {
		this.timeoutIntervel = timeoutIntervel;
	}
	public String getErrorQueue() {
		return errorQueue;
	}

	public void setErrorQueue(String errorQueue) {
		this.errorQueue = errorQueue;
	}

	@Override
	protected MessageConsumer getMessageConsumer() {
		return new RedisQueueMessageConsumer();
	}
	
	class RedisQueueMessageConsumer implements MessageConsumer{

		@Override
		public boolean getAndExecute() throws InterruptedException{
			Jedis jedis =null;
			try{
				jedis = pool.getResource();
				List<String> kv =jedis.blpop(timeoutIntervel, queue);
				if(kv.get(1)==null){
					return false;
				}
				
				try{
					listener.onMessage(kv.get(1));
				}catch(Exception e){
					logger.warn("处理队列"+queue+"消息失败，消息push到队列"+errorQueue, e);
					jedis.rpush(errorQueue, kv.get(1));
				}
				return true;
			}finally{
				if(jedis!=null){
					jedis.close();
				}
			}
		}

		@Override
		public void close() throws IOException {
			//nothing to close
		}
		
	}

}
