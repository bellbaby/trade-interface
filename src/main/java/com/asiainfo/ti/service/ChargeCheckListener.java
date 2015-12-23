package com.asiainfo.ti.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.asiainfo.ti.HotPropertyManager;
import com.asiainfo.ti.container.MessageListener;
import com.asiainfo.ti.dto.TradeResponseMessage;
import com.asiainfo.ti.dto.TradeResponseMessageAssembler;
import com.asiainfo.ti.redis.dao.TradeMessageDao;
import com.google.gson.Gson;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFlowWalletQueryChargeRequest;
import com.taobao.api.response.AlibabaAliqinFlowWalletQueryChargeResponse;

@Deprecated
public class ChargeCheckListener implements MessageListener{

	private final static Log logger = LogFactory.getLog(ChargeCheckListener.class);
	
	private TradeMessageDao tradeMessageDao;
	private HotPropertyManager propertyManager;
	private TaobaoClient taobaoClient;
	
	private static long  waitIntervel = 2*60*60*1000;
	
	@Override
	public void onMessage(String message) throws Exception {
		Gson gson = new Gson();
		logger.info("处理flow_checkcharge_queue队列消息:"+message);
		TradeResponseMessage trm = tradeMessageDao.getResponseMessage(message);
		logger.info("加载查询信息{"+message+":"+gson.toJson(trm)+"}");
		
		AlibabaAliqinFlowWalletQueryChargeRequest req = new AlibabaAliqinFlowWalletQueryChargeRequest();
		req.setOutRechargeId(trm.getUuid());
		req.setChannelId(propertyManager.getChannelId());
		AlibabaAliqinFlowWalletQueryChargeResponse rsp = taobaoClient.execute(req, propertyManager.getAccessToken());
	
		trm = TradeResponseMessageAssembler.assemble(trm, rsp);
		if("failed".equals(trm.getState())||"success".equals(trm.getState())){
			logger.info("充值请求查询完毕.发送请求结果到队列flow_aftercharge_queue");
			tradeMessageDao.putResponseMessage(message, trm);
			tradeMessageDao.pushToQueue("flow_aftercharge_queue",message);
		}else if(checkTimeout(trm.getStime())){
			logger.info("充值超时.发送请求结果到队列flow_aftercharge_queue");
			trm.setState("timeout");
			tradeMessageDao.putResponseMessage(message, trm);
			tradeMessageDao.pushToQueue("flow_aftercharge_queue",message);
		}else{
			//再次入检查队列
			logger.info("充值请求查询继续");
			tradeMessageDao.pushToQueue("flow_checkcharge_queue", message);
		}
	}
	
	private static boolean checkTimeout(String stime){
		Date d  = null;
		try {
			d = new SimpleDateFormat("yyyyMMddhhmmss").parse(stime);
		} catch (ParseException e) {
			return true;
		}
		
		return System.currentTimeMillis()-d.getTime()>waitIntervel;
	}
	
	public HotPropertyManager getPropertyManager() {
		return propertyManager;
	}
	public void setPropertyManager(HotPropertyManager propertyManager) {
		this.propertyManager = propertyManager;
	}
	public TaobaoClient getTaobaoClient() {
		return taobaoClient;
	}
	public void setTaobaoClient(TaobaoClient taobaoClient) {
		this.taobaoClient = taobaoClient;
	}
	public TradeMessageDao getTradeMessageDao() {
		return tradeMessageDao;
	}
	public void setTradeMessageDao(TradeMessageDao tradeMessageDao) {
		this.tradeMessageDao = tradeMessageDao;
	}

	public long getWaitIntervel() {
		return waitIntervel;
	}

	public void setWaitIntervel(long waitIntervel) {
		ChargeCheckListener.waitIntervel = waitIntervel;
	}

}
