package com.kakarote.crm9.erp.crm.entity.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * @Description
 * @Author guyanyang
 * @Date 2019/8/1 17:00
 * @Version 1.0
 */
public abstract class BaseCrmShareHolder<M extends BaseCrmShareHolder<M>> extends Model<M> implements IBean {
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

    public void setInvestor(String investor) {
        set("investor", investor);
    }

    public String getInvestor() {
        return getStr("investor");
    }

    public void setShareholdingRatio(String shareholdingRatio) {
        set("shareholding_ratio", shareholdingRatio);
    }

    public String getShareholdingRatio() {
        return getStr("shareholding_ratio");
    }

    public void setHold(String hold) {
        set("hold", hold);
    }

    public String getHold() {
        return getStr("hold");
    }

    public void setIfPerson(String ifPerson) {
        set("if_person", ifPerson);
    }

    public String getIfPerson() {
        return getStr("if_person");
    }

    public void setInvestmentAmount(String investmentAmount) {
        set("investment_amount", investmentAmount);
    }

    public String getInvestmentAmount() {
        return getStr("investment_amount");
    }
    public void setMoneyType(String moneyType) {
        set("moneyType", moneyType);
    }

    public String getMoneyType() {
        return getStr("moneyType");
    }

    public void setInvestmentDate(String investmentDate) {
        set("investment_date", investmentDate);
    }

    public String getInvestmentDate() {
        return getStr("investment_date");
    }

    public void setPlaceType(String placeType) {
        set("placeType", placeType);
    }

    public String getPlaceType() {
        return getStr("placeType");
    }
}
