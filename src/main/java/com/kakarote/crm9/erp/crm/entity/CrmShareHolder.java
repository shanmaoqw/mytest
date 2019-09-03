package com.kakarote.crm9.erp.crm.entity;

import com.kakarote.crm9.erp.crm.entity.base.BaseCrmShareHolder;

/**
 * @Description
 * @Author guyanyang
 * @Date 2019/8/1 17:27
 * @Version 1.0
 */
public class CrmShareHolder extends BaseCrmShareHolder<CrmShareHolder> {
    public static final CrmShareHolder dao = new CrmShareHolder().dao();
}
