package com.kakarote.crm9.erp.work.entity;

import com.kakarote.crm9.erp.work.entity.base.BaseWorkDocument;

/**
 * @Author: wangpeichuan
 * @Date: 2019/7/30 14:19
 * @Version 1.0
 */
@SuppressWarnings("serial")
public class WorkDocument extends BaseWorkDocument<WorkDocument> {
    public static final WorkDocument dao = new WorkDocument().dao();
}
