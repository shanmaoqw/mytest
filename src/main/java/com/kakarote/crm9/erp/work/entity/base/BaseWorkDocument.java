package com.kakarote.crm9.erp.work.entity.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

import java.util.Date;

/**
 * @Author: wangpeichuan
 * @Date: 2019/7/30 14:18
 * @Version 1.0
 */
public abstract class BaseWorkDocument <M extends BaseWorkDocument<M>> extends Model<M> implements IBean {
    public void setId(Integer id) {
        set("id", id);
    }
    public Integer getId() {
        return getInt("id");
    }

    public void setName(String name) {
        set("name", name);
    }
    public String getName() {
        return getStr("name");
    }

    public void setHtmlValue(String htmlValue) { set("html_value", htmlValue); }
    public String getHtmlValue() {
        return getStr("html_value");
    }

    public void setWordUrl(String wordUrl) { set("word_url", wordUrl); }
    public String getWordUrl() {
        return getStr("word_url");
    }

    public void setCreateTime(Date createTime) {
        set("create_time", createTime);
    }
    public Date getCreateTime() {
        return get("create_time");
    }

    public void setCreateUser(Integer createUser){set("create_user",createUser);}
    public Integer getCreateUser(){return getInt("create_user");}

    public void setType(String type){set("type",type);}
    public String getType(){return getStr("type");}

    public void setSort(Integer sort){set("sort",sort);}
    public Integer getSort(){return getInt("sort");}

    public void setUpdateTime(Date updateTime) {
        set("update_time", updateTime);
    }
    public Date getUpdateTime() {
        return get("update_time");
    }

    public void setStatus(Integer status){set("status",status);}
    public Integer getStatus(){return getInt("status");}




}
