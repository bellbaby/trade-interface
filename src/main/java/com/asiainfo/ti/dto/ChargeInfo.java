package com.asiainfo.ti.dto;
//"charge":"{error=false, errorCode=null, value=true, class=com.alicom.flow.domain.TopResultDO, errorMsg=null
public class ChargeInfo {
	private String error;
	private String errorCode;
	private String value;
	private String errorMsg;
	
	public ChargeInfo(){}
	
	public ChargeInfo(String s){
		s = s.substring(1,s.length()-2);
		String[] ss = s.split(",");
		for(String k_v:ss){
			String[] kv = k_v.trim().split("=");
//			switch(kv[0]){
//			case "error":
//				error =kv.length>=2?kv[1]:null;break;
//			case "errorCode":
//				errorCode=kv.length>=2?kv[1]:null;break;
//			case "value":
//				value=kv.length>=2?kv[1]:null;break;
//			case "errorMsg":
//				errorMsg =kv.length>=2?kv[1]:null;
//			}
			
			if("error".equalsIgnoreCase(kv[0])){
				error = kv.length>=2?kv[1]:null;
			}else if("errorCode".equalsIgnoreCase(kv[0])){
				errorCode=kv.length>=2?kv[1]:null;
			}else if("value".equalsIgnoreCase(kv[0])){
				value=kv.length>=2?kv[1]:null;
			}else if("errorMsg".equalsIgnoreCase(kv[0])){
				errorMsg =kv.length>=2?kv[1]:null;
			}
		}
	}
	
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	public String getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	
}
