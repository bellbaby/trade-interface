package com.asiainfo.ti.simulation;

import com.taobao.api.response.AlibabaAliqinFlowWalletChargeResponse;
import com.taobao.api.response.AlibabaAliqinFlowWalletQueryChargeResponse;

public class ResponseSimulator {
	public static AlibabaAliqinFlowWalletChargeResponse getChargeResponse(){
		
		AlibabaAliqinFlowWalletChargeResponse response = new AlibabaAliqinFlowWalletChargeResponse();
		
		double random = Math.random();
		if(random<0.67){
			//模拟充值请求成功
			response.setBody("{\"alibaba_aliqin_flow_wallet_charge_response\":{\"charge\":\"{error=false, errorCode=null, value=true, class=com.alicom.flow.domain.TopResultDO, errorMsg=null}\",\"request_id\":\"12ckm6kp1d2eq\"}}");
			response.setCharge("{error=false, errorCode=null, value=true, class=com.alicom.flow.domain.TopResultDO, errorMsg=null}");
		}else{
			// 模拟充值请求失败
			response.setBody("{\"error_response\":{\"code\":27,\"msg\":\"Invalid session\",\"sub_code\":\"invalid-sessionkey\",\"request_id\":\"r4m1kwo2v1s5\"}}");
			response.setCharge(null);
			response.setErrorCode("27");
			response.setMsg("invalid-sessionkey");
		}
		
		return response;
	}
	
	public static AlibabaAliqinFlowWalletQueryChargeResponse getQueryResponse(){
		AlibabaAliqinFlowWalletQueryChargeResponse response = new AlibabaAliqinFlowWalletQueryChargeResponse();
		
		double random = Math.random();
		if(random<0.25){
			//模拟充值成功
			response.setBody("{\"alibaba_aliqin_flow_wallet_query_charge_response\":{\"charge\":\"{error=false, errorCode=null, value=3, class=com.alicom.flow.domain.TopResultDO, errorMsg=}\",\"request_id\":\"z2a0cjbaosyw\"}}");
			response.setCharge("{error=false, errorCode=null, value=3, class=com.alicom.flow.domain.TopResultDO, errorMsg=}");
		}else if(random<0.5){
			//模拟充值中
			response.setBody("{\"alibaba_aliqin_flow_wallet_query_charge_response\":{\"charge\":\"{error=false, errorCode=null, value=1, class=com.alicom.flow.domain.TopResultDO, errorMsg=}\",\"request_id\":\"z2a0cjbaosyw\"}}");
			response.setCharge("{error=false, errorCode=null, value=1, class=com.alicom.flow.domain.TopResultDO, errorMsg=}");
		}else if(random<0.75){
			//模拟充值失败
			response.setBody("{\"alibaba_aliqin_flow_wallet_query_charge_response\":{\"charge\":\"{error=false, errorCode=null, value=4, class=com.alicom.flow.domain.TopResultDO, errorMsg=}\",\"request_id\":\"z2a0cjbaosyw\"}}");
			response.setCharge("{error=false, errorCode=null, value=4, class=com.alicom.flow.domain.TopResultDO, errorMsg=}");
		}else{
			//模拟查询失败
			response.setBody("{\"error_response\":{\"code\":27,\"msg\":\"Invalid session\",\"sub_code\":\"invalid-sessionkey\",\"request_id\":\"r4m1kwo2v1s5\"}}");
			response.setCharge(null);
			response.setErrorCode("27");
			response.setMsg("error message");
		}
		return response;
	}
}
