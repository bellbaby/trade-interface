package com.asiainfo.ti.dto;

import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;

import com.google.gson.Gson;
import com.taobao.api.ApiException;
import com.taobao.api.response.AlibabaAliqinFlowWalletChargeResponse;
import com.taobao.api.response.AlibabaAliqinFlowWalletQueryChargeResponse;

public class TradeResponseMessageAssembler {
	
	private static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
	private static SimpleDateFormat datef = new SimpleDateFormat("yyyyMMdd");
	private static volatile int seq = 0;
	private static String getTime(){
		return sdf.format(new Date());
	}
	
	private synchronized static String getSeqNum(){
		if(seq<999){
			seq++;
		}else{
			seq = 1;
		}
		
		return datef.format(new Date())+StringUtils.leftPad(""+seq, 4, "0");
	}
	
	public static String genTradeResponseMessageKey(){
		return "order_"+getSeqNum();
	}
	
	public static TradeResponseMessage assemble(TradeRequestMessage trm, AlibabaAliqinFlowWalletChargeResponse response,String order_id){
		TradeResponseMessage resMsg = new TradeResponseMessage();
		ChargeInfo charge = null;
		if(!StringUtils.isEmpty(response.getCharge())){
			charge = new ChargeInfo(response.getCharge());
		}
		try {
			PropertyUtils.copyProperties(resMsg, trm);
		} catch (Exception e) {
			
		}
		resMsg.setStime(getTime());
		resMsg.setInt_order_id(order_id);
		resMsg.setStime(getTime());
		if(charge!=null){
			resMsg.setError_code(charge.getErrorCode());
			if("false".equals(charge.getError())){
				resMsg.setState("sended");
			}else{
				resMsg.setState("failed");
			}
		}else{
			resMsg.setError_code(response.getErrorCode());
			resMsg.setState("failed");
			resMsg.setErrorMsg(response.getMsg());
		}
		
		return resMsg;
	}
	
	public static TradeResponseMessage assemble(TradeRequestMessage requestMsg,ApiException apie,String order_id){
		TradeResponseMessage resMsg = new TradeResponseMessage();
		try {
			PropertyUtils.copyProperties(resMsg, requestMsg);
		} catch (Exception e) {
			
		}
		resMsg.setState("failed");
		resMsg.setError_code(apie.getErrCode());
		resMsg.setErrorMsg(apie.getErrMsg());
		resMsg.setInt_order_id(order_id);
		resMsg.setStime(getTime());
		
		return resMsg;
	}
	
	public static TradeResponseMessage assemble(TradeResponseMessage trm,AlibabaAliqinFlowWalletQueryChargeResponse response){
		
		ChargeInfo charge = null;
		if(!StringUtils.isEmpty(response.getCharge())){
			charge = new ChargeInfo(response.getCharge());
		}
		if(charge!=null){
			trm.setError_code(charge.getErrorCode());
			if("false".equals(charge.getError())&&"3".equals(charge.getValue())){
				trm.setState("success");
				trm.setEtime(getTime());
			}else if("false".equals(charge.getError())&&"4".equals(charge.getValue())){
				trm.setState("failed");
				trm.setErrorMsg(charge.getErrorMsg());
				trm.setError_code(charge.getErrorCode());
				trm.setEtime(getTime());
			}else{
				trm.setState("sended");
			}
		}else{
			trm.setState("sended");
		}
		
		return trm;
	}
}
