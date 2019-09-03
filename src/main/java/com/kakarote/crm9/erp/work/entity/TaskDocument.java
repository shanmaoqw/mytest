package com.kakarote.crm9.erp.work.entity;

import com.kakarote.crm9.erp.work.entity.base.BaseTaskDocument;

/**
 * @Author: wangpeichuan
 * @Date: 2019/8/5 13:00
 * @Version 1.0
 */
@SuppressWarnings("serial")
public class TaskDocument extends BaseTaskDocument<TaskDocument> {
    public static final TaskDocument dao = new TaskDocument().dao();
}
