package com.kakarote.crm9.erp.crm.common;

/**
 * 文件名称： com.kakarote.crm9.erp.crm.common.CrmViewEnum.java</br>
 * 初始作者： WenBin</br>
 * 创建日期： 2019年8月16日</br>
 * 功能说明： 这里用一句话描述这个类的作用--此句话需删除 <br/>
 *
 * =================================================<br/>
 * 修改记录：<br/>
 * 修改作者        日期       修改内容<br/>
 *
 *
 * ================================================<br/>
 *  Copyright (c) 2010-2011 .All rights reserved.<br/>
 */
public enum CrmViewEnum {
	/**
	 * 线索视图
	 */
	LEADS_VIEW("线索视图", "1","leadsview"),
	/**
	 * 客户视图
	 */
	CUSTOMER_VIEW("客户视图", "2","customerview"),
	/**
	 * 联系人视图
	 */
	CONTACTS_VIEW("联系人视图", "3","contactsview"),
	/**
	 * 产品视图
	 */
	PRODUCT_VIEW("产品视图", "4","productview"),
	/**
	 * 商机视图
	 */
	BUSINESS_VIEW("商机视图","5","businessview"),
	/**
	 * 合同视图
	 */
	CONTRACT_VIEW("合同视图","6","contractview"),
	/**
	 * 回款视图
	 */
	RECEIVABLES_VIEW("回款视图","7","receivablesview");

	private  String name;
	private  String types;
	private  String sign;

	CrmViewEnum(String name, String types, String sign) {
		this.name = name;
		this.types = types;
		this.sign = sign;
	}


	public String getName() {

		return name;
	}


	public String getTypes() {

		return types;
	}


	public String getSign() {

		return sign;
	}
}
