package com.asiainfo.ti.mybatis.mapper;

import java.util.Date;
import java.util.List;

import com.asiainfo.ti.dto.TradeCheckNotification;

public interface TradeCheckNotificationMapper {
	
	public List<TradeCheckNotification> queryReadyNotification(Date date);
	
	public void add(TradeCheckNotification notification);
	
	public void delete(TradeCheckNotification notification);
	
	public void update(TradeCheckNotification notification);
	
	public void addBak(TradeCheckNotification notification);
}
