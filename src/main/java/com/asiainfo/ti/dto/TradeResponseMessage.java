package com.asiainfo.ti.dto;

public class TradeResponseMessage extends TradeRequestMessage{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3895804018115373224L;

	private String orderid;
	private String int_order_id;
	private String state;
	private String error_code;
	private String errorMsg;
	private String stime;
	private String etime;
	
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getError_code() {
		return error_code;
	}
	public void setError_code(String error_code) {
		this.error_code = error_code;
	}
	public String getEtime() {
		return etime;
	}
	public void setEtime(String etime) {
		this.etime = etime;
	}
	public String getOrderid() {
		return orderid;
	}
	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}
	public String getStime() {
		return stime;
	}
	public void setStime(String stime) {
		this.stime = stime;
	}
	public String getInt_order_id() {
		return int_order_id;
	}
	public void setInt_order_id(String int_order_id) {
		this.int_order_id = int_order_id;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
}
