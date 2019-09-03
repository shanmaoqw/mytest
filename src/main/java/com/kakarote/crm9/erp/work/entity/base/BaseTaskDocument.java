package com.kakarote.crm9.erp.work.entity.base;

import com.jfinal.plugin.activerecord.IBean;
import com.jfinal.plugin.activerecord.Model;

/**
 * @Author: wangpeichuan
 * @Date: 2019/8/5 13:01
 * @Version 1.0
 */
@SuppressWarnings("serial")
public class BaseTaskDocument <M extends BaseTaskDocument<M>> extends Model<M> implements IBean {
    public void setId(Integer id) {
        set("id", id);
    }
    public Integer getId() {
        return getInt("id");
    }

    public void setTaskId(Integer taskId) {
        set("task_id", taskId);
    }
    public Integer getTaskId() {
        return getInt("task_id");
    }

    public void setDocumentId(Integer documentId) {
        set("document_id", documentId);
    }
    public Integer getDocumentId() {
        return getInt("document_id");
    }
}
