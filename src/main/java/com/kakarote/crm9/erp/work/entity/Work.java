package com.kakarote.crm9.erp.work.entity;

import com.kakarote.crm9.erp.work.entity.base.BaseWork;

/**
 * Generated by JFinal.
 */
@SuppressWarnings("serial")
public class Work extends BaseWork<Work> {
	public static final Work dao = new Work().dao();

	/**
	 * 客户名称
	 */
	private String customer;
	/**
	 * 合同名称
	 */
	private String contract;

	/**
	 * 创建人姓名
	 */
	private String createUser;

	public String getCustomer() {
		return customer;
	}

	public void setCustomer(String customer) {
		this.customer = customer;
	}

	public String getContract() {
		return contract;
	}

	public void setContract(String contract) {
		this.contract = contract;
	}

	public String getCreateUser() {
		return createUser;
	}

	public void setCreateUser(String createUser) {
		this.createUser = createUser;
	}
}
