package com.asiainfo.ti.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.asiainfo.ti.HotPropertyManager;
import com.asiainfo.ti.container.MessageListener;
import com.asiainfo.ti.dto.ChargeInfo;
import com.asiainfo.ti.dto.TradeRequestMessage;
import com.asiainfo.ti.dto.TradeResponseMessage;
import com.asiainfo.ti.dto.TradeResponseMessageAssembler;
import com.asiainfo.ti.redis.dao.TradeMessageDao;
import com.asiainfo.ti.simulation.ResponseSimulator;
import com.google.gson.Gson;
import com.taobao.api.ApiException;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFlowWalletChargeRequest;
import com.taobao.api.response.AlibabaAliqinFlowWalletChargeResponse;
/**
 * 目前没有事务处理。。。
 * @author sunguihua
 *
 */
public class ChargeRequestListener implements MessageListener{

	private final static Log logger = LogFactory.getLog(ChargeRequestListener.class);
	private TradeMessageDao tradeMessageDao;
	private TaobaoClient taobaoClient;
	private HotPropertyManager propertyManager;
	private TradeCheckNotificationService notificationService;
	
	/**
	 * no transaction!!!!!
	 */
	@Override
	public void onMessage(String message) throws Exception {
		Gson gson = new Gson();
		logger.info("处理flow_request_queue队列消息:"+message);
		TradeRequestMessage reqMsg = tradeMessageDao.getRequestMessage(message);
		logger.info("加载请求信息{"+message+":"+gson.toJson(reqMsg)+"}");
		AlibabaAliqinFlowWalletChargeRequest req = new AlibabaAliqinFlowWalletChargeRequest();
		req.setPhoneNum(reqMsg.getPhonenum());
		req.setReason("充值");
		req.setGradeId(reqMsg.getPackcode());
		req.setOutRechargeId(reqMsg.getUuid());
		req.setChannelId(propertyManager.getChannelId());
		
		AlibabaAliqinFlowWalletChargeResponse rsp = null;
		TradeResponseMessage resMsg = null;
		try{
			rsp = taobaoClient.execute(req, propertyManager.getAccessToken());
			logger.info("充值请求返回:"+rsp.getBody());
			resMsg = TradeResponseMessageAssembler.assemble(reqMsg, rsp, message);
		}catch(ApiException e){
			logger.warn("充值请求失败", e);
			resMsg = TradeResponseMessageAssembler.assemble(reqMsg, e, message);
		}
		
		/**仿真代码---start--**/
		//rsp = ResponseSimulator.getChargeResponse();
		//resMsg = TradeResponseMessageAssembler.assemble(reqMsg, rsp, message);
		/**仿真代码---end--**/
		String rmKey = TradeResponseMessageAssembler.genTradeResponseMessageKey();
		tradeMessageDao.putResponseMessage(rmKey,resMsg);
		if("failed".equals(resMsg.getState())||"error".equals(resMsg.getState())){
			logger.info("充值请求失败，errorCode"+resMsg.getError_code()+"。发送请求结果到队列flow_aftercharge_queue");
			tradeMessageDao.pushToQueue("flow_aftercharge_queue", rmKey);
		}else{
			logger.info("充值请求成功。");
			notificationService.create(rmKey, resMsg);
		}
	}

	public TradeMessageDao getTradeMessageDao() {
		return tradeMessageDao;
	}

	public void setTradeMessageDao(TradeMessageDao tradeMessageDao) {
		this.tradeMessageDao = tradeMessageDao;
	}
	public TaobaoClient getTaobaoClient() {
		return taobaoClient;
	}

	public void setTaobaoClient(TaobaoClient taobaoClient) {
		this.taobaoClient = taobaoClient;
	}

	public HotPropertyManager getPropertyManager() {
		return propertyManager;
	}

	public void setPropertyManager(HotPropertyManager propertyManager) {
		this.propertyManager = propertyManager;
	}

	public TradeCheckNotificationService getNotificationService() {
		return notificationService;
	}

	public void setNotificationService(TradeCheckNotificationService notificationService) {
		this.notificationService = notificationService;
	}
}
