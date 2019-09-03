package com.kakarote.crm9.erp.crm.entity.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * @Description
 * @Author guyanyang
 * @Date 2019/8/2 11:51
 * @Version 1.0
 */
public class BaseCrmCopyright<M extends BaseCrmCopyright<M>> extends Model<M> implements IBean {
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

    public void setName(String name) {
        set("name", name);
    }

    public String getName() {
        return getStr("name");
    }

    public void setReleaseTime(String releaseTime) {
        set("release_time", releaseTime);
    }

    public String getReleaseTime() {
        return getStr("release_time");
    }

    public void setFinishTime(String finishTime) {
        set("finish_time", finishTime);
    }

    public String getFinishTime() {
        return getStr("finish_time");
    }

    public void setRegisterNo(String registerNo) {
        set("register_no", registerNo);
    }

    public String getRegisterNo() {
        return getStr("register_no");
    }

    public void setRegisterDate(String registerDate) {
        set("register_date", registerDate);
    }

    public String getRegisterDate() {
        return getStr("register_date");
    }

    public void setCategory(String category) {
        set("category", category);
    }

    public String getCategory() {
        return getStr("category");
    }

}
