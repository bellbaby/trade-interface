package com.asiainfo.ti.dto;

import java.util.Date;

public class TradeCheckNotification extends BaseDto{

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	
	public static int STATE_TRADE_SUCCESS= 0;
	public static int STATE_TRADE_TIMEOUT = 1;
	public static int STATE_TRADE_FAILED = 2;
	public static int STATE_UNKNOW_FAILED = 3;
	private String id;
	private String responseMessageKey;
	private Date pretime;
	private Date createtime;
	private int checkcount;
	private Date lastchecktime;
	private int state;
	private String message;
	
	public int getCheckcount() {
		return checkcount;
	}
	public void setCheckcount(int checkcount) {
		this.checkcount = checkcount;
	}
	public Date getLastchecktime() {
		return lastchecktime;
	}
	public void setLastchecktime(Date lastchecktime) {
		this.lastchecktime = lastchecktime;
	}
	public String getResponseMessageKey() {
		return responseMessageKey;
	}
	public void setResponseMessageKey(String responseMessageKey) {
		this.responseMessageKey = responseMessageKey;
	}
	public Date getPretime() {
		return pretime;
	}
	public void setPretime(Date pretime) {
		this.pretime = pretime;
	}
	public Date getCreatetime() {
		return createtime;
	}
	public void setCreatetime(Date createtime) {
		this.createtime = createtime;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
}
