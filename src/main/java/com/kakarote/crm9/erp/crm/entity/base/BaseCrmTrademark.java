package com.kakarote.crm9.erp.crm.entity.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * @Description
 * @Author guyanyang
 * @Date 2019/8/2 10:48
 * @Version 1.0
 */
public class BaseCrmTrademark<M extends BaseCrmTrademark<M>> extends Model<M> implements IBean {

    public void setId(String id) { set("id", id); }

    public String getId() {
        return getStr("id");
    }

    public void setCustomerId(Integer customerId) {
        set("customer_id", customerId);
    }

    public Integer getCustomerId() {
        return getInt("customer_id");
    }

    public void setSolrId(String solrId) {
        set("solr_id", solrId);
    }

    public String getSolrId() {
        return getStr("solr_id");
    }

    public void setImage(String image) {
        set("image", image);
    }

    public String getImage() {
        return getStr("image");
    }

    public void setName(String name) {
        set("name", name);
    }

    public String getName() {
        return getStr("name");
    }

    public void setApplyNo(String applyNo) {
        set("apply_no", applyNo);
    }

    public String getApplyNo() {
        return getStr("apply_no");
    }

    public void setCategory(String category) {
        set("category", category);
    }

    public String getCategory() {
        return getStr("category");
    }

    public void setStatus(String status) {
        set("status", status);
    }

    public String getStatus() {
        return getStr("status");
    }

    public void setApplyDate(String applyDate) {
        set("apply_date", applyDate);
    }

    public String getApplyDate() {
        return getStr("apply_date");
    }

    public void setDeadlineBegin(String deadlineBegin) {
        set("deadline_begin", deadlineBegin);
    }

    public String getDeadlineBegin() {
        return getStr("deadline_begin");
    }

    public void setDeadlineEnd(String deadlineEnd) {
        set("deadline_end", deadlineEnd);
    }

    public String getDeadlineEnd() {
        return getStr("deadline_end");
    }

    public void setAgency(String agency) {
        set("agency", agency);
    }

    public String getAgency() {
        return getStr("agency");
    }

    public void setServiceItems(String serviceItems) {
        set("service_items", serviceItems);
    }

    public String getServiceItems() {
        return getStr("service_items");
    }

    public void setAppAddress(String appAddress) {
        set("app_address", appAddress);
    }

    public String getAppAddress() {
        return getStr("app_address");
    }

    public void setAppFlow(String appFlow) {
        set("app_flow", appFlow);
    }

    public String getAppFlow() {
        return getStr("app_flow");
    }

    public void setAnnouncement(String announcement) {
        set("announcement", announcement);
    }

    public String getAnnouncement() {
        return getStr("announcement");
    }

    public void setRegisterDate(String registerDate) {
        set("register_date", registerDate);
    }

    public String getRegisterDate() {
        return getStr("register_date");
    }
}
