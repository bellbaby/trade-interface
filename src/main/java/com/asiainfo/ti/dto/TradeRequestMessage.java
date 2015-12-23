package com.asiainfo.ti.dto;

public class TradeRequestMessage extends BaseDto{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7501115755289961528L;
	
	private String phonenum;
	private String packcode;
	private String operid;
	
	public String getPhonenum() {
		return phonenum;
	}
	public void setPhonenum(String phonenum) {
		this.phonenum = phonenum;
	}
	public String getPackcode() {
		return packcode;
	}
	public void setPackcode(String packcode) {
		this.packcode = packcode;
	}
	public String getOperid() {
		return operid;
	}
	public void setOperid(String operid) {
		this.operid = operid;
	}

}
