package com.kakarote.crm9.erp.crm.entity.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * @Description
 * @Author guyanyang
 * @Date 2019/8/2 9:49
 * @Version 1.0
 */
public class BaseCrmPatent<M extends BaseCrmPatent<M>> extends Model<M> implements IBean {
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

    public void setTitle(String title) {
        set("title", title);
    }

    public String getTitle() {
        return getStr("title");
    }

    public void setApplyNo(String applyNo) {
        set("apply_no", applyNo);
    }

    public String getApplyNo() {
        return getStr("apply_no");
    }

    public void setApplyDate(String applyDate) {
        set("apply_date", applyDate);
    }

    public String getApplyDate() {
        return getStr("apply_date");
    }

    public void setPubNo(String pubNo) {
        set("pub_no", pubNo);
    }

    public String getPubNo() {
        return getStr("pub_no");
    }

    public void setPubDate(String pubDate) {
        set("pub_date", pubDate);
    }

    public String getPubDate() {
        return getStr("pub_date");
    }

    public void setInventor(String inventor) {
        set("inventor", inventor);
    }

    public String getInventor() {
        return getStr("inventor");
    }

    public void setType(String type) {
        set("type", type);
    }

    public String getType() {
        return getStr("type");
    }

    public void setAgency(String agency) {
        set("agency", agency);
    }

    public String getAgency() {
        return getStr("agency");
    }

    public void setLawStatus(String lawStatus) {
        set("law_status", lawStatus);
    }

    public String getLawStatus() {
        return getStr("law_status");
    }

    public void setLawHisStatus(String lawHisStatus) {
        set("law_his_status", lawHisStatus);
    }

    public String getLawHisStatus() {
        return getStr("law_his_status");
    }

    public void setDetail(String detail) {
        set("detail", detail);
    }

    public String getDetail() {
        return getStr("detail");
    }

}
