package com.kakarote.crm9.erp.crm.entity;

import com.kakarote.crm9.erp.crm.entity.base.BaseCrmCustomer;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class CrmCustomer extends BaseCrmCustomer<CrmCustomer> {
	public static final CrmCustomer dao = new CrmCustomer().dao();
	//移出方式（1.移除2.转为团队成员）
	private Integer transferType;
	//同时变更（1.联系人2.商机3.合同）
	private String changeType;
	//权限（1.只读2.只写）
	private Integer power;
	//变更负责人
	private Integer newOwnerUserId;
	private String ids;
	private String memberIds;
	private String customerIds;
	private Integer checkstatus;
	private String businessName;

	public String getBusinessName(){
		return businessName;
	}

	public void setBusinessName(String businessName){
		this.businessName = businessName;
	}

	public Integer getTransferType() {
		return transferType;
	}

	public void setTransferType(Integer transferType) {
		this.transferType = transferType;
	}

	public String getChangeType() {
		return changeType;
	}

	public void setChangeType(String changeType) {
		this.changeType = changeType;
	}

	public Integer getPower() {
		return power;
	}

	public void setPower(Integer power) {
		this.power = power;
	}

	public Integer getNewOwnerUserId() {
		return newOwnerUserId;
	}

	public void setNewOwnerUserId(Integer newOwnerUserId) {
		this.newOwnerUserId = newOwnerUserId;
	}

	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public String getMemberIds() {
		return memberIds;
	}

	public void setMemberIds(String memberIds) {
		this.memberIds = memberIds;
	}

	public String getCustomerIds() {
		return customerIds;
	}

	public void setCustomerIds(String customerIds) {
		this.customerIds = customerIds;
	}

	public Integer getCheckstatus() {
		return checkstatus;
	}

	public void setCheckstatus(Integer checkstatus) {
		this.checkstatus = checkstatus;
	}

}
