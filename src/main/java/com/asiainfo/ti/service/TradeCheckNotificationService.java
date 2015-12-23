package com.asiainfo.ti.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.asiainfo.ti.dto.TradeCheckNotification;
import com.asiainfo.ti.dto.TradeResponseMessage;
import com.asiainfo.ti.mybatis.mapper.TradeCheckNotificationMapper;

public class TradeCheckNotificationService {
	
	private long checkIntervel;
	private long timeout;
	
	private TradeCheckNotificationMapper notificationMapper;
	
	public long getCheckIntervel() {
		return checkIntervel;
	}

	public void setCheckIntervel(long checkIntervel) {
		this.checkIntervel = checkIntervel;
	}

	public long getTimeout() {
		return timeout;
	}

	public void setTimeout(long timeout) {
		this.timeout = timeout;
	}

	public TradeCheckNotificationMapper getNotificationMapper() {
		return notificationMapper;
	}

	public void setNotificationMapper(TradeCheckNotificationMapper notificationMapper) {
		this.notificationMapper = notificationMapper;
	}

	public void create(String key,TradeResponseMessage trm){
		TradeCheckNotification notification = new TradeCheckNotification();
		notification.setId(UUID.randomUUID().toString());
		notification.setUuid(trm.getUuid());
		notification.setResponseMessageKey(key);
		notification.setCreatetime(new Date());
		notification.setPretime(new Date(System.currentTimeMillis()+checkIntervel));
		notification.setCheckcount(0);
		
		notificationMapper.add(notification);
	}
	
	public void update(TradeCheckNotification notification){
		notification.setPretime(new Date(System.currentTimeMillis()+checkIntervel));
		notificationMapper.update(notification);
	}
	
	public void deleteAndBak(TradeCheckNotification notification){
		notificationMapper.delete(notification);
		bakNotification(notification);
	}
	
	public List<TradeCheckNotification> queryReadyNotification(){
		return notificationMapper.queryReadyNotification(new Date());
	}
	
	public void bakNotification(TradeCheckNotification notification){
		 notificationMapper.addBak(notification);
	}
	
	public boolean isTimeout(TradeCheckNotification notification){
		if(System.currentTimeMillis()-notification.getCreatetime().getTime()>timeout){
			return true;
		}else{
			return false;
		}
	}
}
