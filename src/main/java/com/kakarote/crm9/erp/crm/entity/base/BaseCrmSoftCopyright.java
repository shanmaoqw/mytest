package com.kakarote.crm9.erp.crm.entity.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * @Description
 * @Author guyanyang
 * @Date 2019/8/2 13:56
 * @Version 1.0
 */
public class BaseCrmSoftCopyright<M extends BaseCrmSoftCopyright<M>> extends Model<M> implements IBean {
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

    public void setVersion(String version) {
        set("version", version);
    }

    public String getVersion() {
        return getStr("version");
    }

    public void setPublishDate(String publishDate) {
        set("publish_date", publishDate);
    }

    public String getPublishDate() {
        return getStr("publish_date");
    }

    public void setBriefTitle(String briefTitle) {
        set("brief_title", briefTitle);
    }

    public String getBriefTitle() {
        return getStr("brief_title");
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

    public void setAbbreviation(String abbreviation) {
        set("abbreviation", abbreviation);
    }

    public String getAbbreviation() {
        return getStr("abbreviation");
    }

}
