package com.kakarote.crm9.erp.admin.entity.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseAdminFieldv<M extends BaseAdminFieldv<M>> extends Model<M> implements IBean {

	public void setId(Integer id) {
		set("id", id);
	}

	public Integer getId() {
		return getInt("id");
	}

	public void setFieldId(Integer fieldId) {
		set("field_id", fieldId);
	}

	public Integer getFieldId() {
		return getInt("field_id");
	}

	public void setName(String name) {
		set("name", name);
	}

	public String getName() {
		return getStr("name");
	}

	public void setValue(String value) {
		set("value", value);
	}

	public String getValue() {
		return getStr("value");
	}

	public void setCreateTime(java.util.Date createTime) {
		set("create_time", createTime);
	}

	public java.util.Date getCreateTime() {
		return get("create_time");
	}

	public void setBatchId(String batchId) {
		set("batch_id", batchId);
	}

	public String getBatchId() {
		return getStr("batch_id");
	}

}
