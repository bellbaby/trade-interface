package com.asiainfo.ti.service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.asiainfo.ti.HotPropertyManager;
import com.asiainfo.ti.dto.TradeCheckNotification;
import com.asiainfo.ti.dto.TradeResponseMessage;
import com.asiainfo.ti.dto.TradeResponseMessageAssembler;
import com.asiainfo.ti.redis.dao.TradeMessageDao;
import com.google.gson.Gson;
import com.taobao.api.ApiException;
import com.taobao.api.TaobaoClient;
import com.taobao.api.request.AlibabaAliqinFlowWalletQueryChargeRequest;
import com.taobao.api.response.AlibabaAliqinFlowWalletQueryChargeResponse;

public class ChargeCheckService {
	
	private final static Log logger = LogFactory.getLog(ChargeCheckListener.class);
	
	private TradeMessageDao tradeMessageDao;
	private HotPropertyManager propertyManager;
	private TaobaoClient taobaoClient;
	private TradeCheckNotificationService notificationService;
	private Executor executor;
	

	public void doTask(){
		
		List<TradeCheckNotification> notifications =notificationService.queryReadyNotification();
		//if(logger.isDebugEnabled()){
			logger.debug("执行自动任务。加载到"+notifications.size()+"条数据!");
		//}
		for(TradeCheckNotification notification:notifications){
			try{
				executor.execute(new NotificationConsumer(notification));
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	
	public Executor getExecutor() {
		return executor;
	}

	public void setExecutor(Executor executor) {
		this.executor = executor;
	}


	class NotificationConsumer implements Runnable{

		private TradeCheckNotification notification;
		public NotificationConsumer(TradeCheckNotification notification){
			this.notification = notification;
		}
		@Override
		public void run() {
			try{
				Gson gson = new Gson();
				
				TradeResponseMessage trm =tradeMessageDao.getResponseMessage(notification.getResponseMessageKey());
				logger.info("加载查询信息{"+notification.getResponseMessageKey()+":"+gson.toJson(trm)+"}");
				
				AlibabaAliqinFlowWalletQueryChargeRequest req = new AlibabaAliqinFlowWalletQueryChargeRequest();
				req.setOutRechargeId(trm.getUuid());
				req.setChannelId(propertyManager.getChannelId());
				AlibabaAliqinFlowWalletQueryChargeResponse rsp = null;
				try {
					rsp = taobaoClient.execute(req, propertyManager.getAccessToken());
					logger.info("充值查询返回结果:"+rsp.getBody());
					trm = TradeResponseMessageAssembler.assemble(trm, rsp);
				} catch (ApiException e) {
					trm.setError_code(e.getErrCode());
					trm.setErrorMsg(e.getErrMsg());
					logger.error("充值查询异常,"+e.getErrMsg());
				}
			
				/**------查询请求仿真-----start---------**/
				//rsp = ResponseSimulator.getQueryResponse();
				//logger.info("查询结果:"+rsp.getBody());
				//trm = TradeResponseMessageAssembler.assemble(trm, rsp);
				/**------查询请求仿真-----end---------**/
				
				notification.setCheckcount(notification.getCheckcount()+1);
				notification.setLastchecktime(new Date());
				if("success".equals(trm.getState())){
					logger.info("查询充值请求返回成功.发送请求结果到队列flow_aftercharge_queue");
					notification.setState(TradeCheckNotification.STATE_TRADE_SUCCESS);
					notification.setMessage("充值成功");
					notificationService.deleteAndBak(notification);
					tradeMessageDao.putResponseMessage(notification.getResponseMessageKey(), trm);
					tradeMessageDao.pushToQueue("flow_aftercharge_queue",notification.getResponseMessageKey());
				}else if("failed".equals(trm.getState())){
					logger.info("查询充值请求返回失败.发送请求结果到队列flow_aftercharge_queue");
					notification.setState(TradeCheckNotification.STATE_TRADE_FAILED);
					notification.setMessage("充值失败");
					notificationService.deleteAndBak(notification);
					tradeMessageDao.putResponseMessage(notification.getResponseMessageKey(), trm);
					tradeMessageDao.pushToQueue("flow_aftercharge_queue",notification.getResponseMessageKey());
				}else if(notificationService.isTimeout(notification)){
					logger.info("充值超时.");
					trm.setState("error");
					trm.setErrorMsg("充值超时!");
					
					notification.setState(TradeCheckNotification.STATE_TRADE_TIMEOUT);
					notification.setMessage("充值超时");
					notificationService.deleteAndBak(notification);
					tradeMessageDao.pushToQueue("flow_aftercharge_queue",notification.getResponseMessageKey());
				}else{
					logger.info("充值请求查询继续");
					notificationService.update(notification);
				}
			}catch(Exception e){
				notification.setState(TradeCheckNotification.STATE_UNKNOW_FAILED);
				notification.setMessage("未知异常,"+e.getMessage());
				notificationService.bakNotification(notification);
				logger.error("执行查询业务未知异常",e);
			}
			
		}
		
	}
	

	public TradeMessageDao getTradeMessageDao() {
		return tradeMessageDao;
	}

	public void setTradeMessageDao(TradeMessageDao tradeMessageDao) {
		this.tradeMessageDao = tradeMessageDao;
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

	public TradeCheckNotificationService getNotificationService() {
		return notificationService;
	}

	public void setNotificationService(TradeCheckNotificationService notificationService) {
		this.notificationService = notificationService;
	}
}
