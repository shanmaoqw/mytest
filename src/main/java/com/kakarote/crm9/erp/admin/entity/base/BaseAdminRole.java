package com.kakarote.crm9.erp.admin.entity.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings("serial")
public abstract class BaseAdminRole<M extends BaseAdminRole<M>> extends Model<M> implements IBean {

	public void setRoleId(Integer roleId) {
		set("role_id", roleId);
	}

	public Integer getRoleId() {
		return getInt("role_id");
	}

	public void setRoleName(String roleName) {
		set("role_name", roleName);
	}

	public String getRoleName() {
		return getStr("role_name");
	}

	public void setRoleType(Integer roleType) {
		set("role_type", roleType);
	}

	public Integer getRoleType() {
		return getInt("role_type");
	}

	public void setRemark(String remark) {
		set("remark", remark);
	}

	public String getRemark() {
		return getStr("remark");
	}

	public void setStatus(Integer status) {
		set("status", status);
	}

	public Integer getStatus() {
		return getInt("status");
	}

	public void setDataType(Integer dataType) {
		set("data_type", dataType);
	}

	public Integer getDataType() {
		return getInt("data_type");
	}

}
