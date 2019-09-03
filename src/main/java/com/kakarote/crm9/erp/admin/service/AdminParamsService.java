package com.kakarote.crm9.erp.admin.service;

import com.jfinal.aop.Before;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.kakarote.crm9.erp.admin.entity.AdminField;
import com.kakarote.crm9.utils.R;
import com.kakarote.crm9.utils.StringUtil;

import java.util.List;

/**
 * 系统参数设置
 * @ClassName AdminParamsService
 * @Coder lindy
 * @Date 2019/8/28 下午7:26
 * @Version 1.0
 **/
public class AdminParamsService {

    /**
     * @author wyq
     * 查询对口部门
     */
    public R queryJointDeptOptions(){
        String result = Db.queryStr("select options from 72crm_admin_field where field_id = 103");
        List<String> list = StringUtil.stringToList(result);
        return R.ok().put("data",list);
    }

    /**
     * @author wyq
     * 设置对口部门
     */
    @Before(Tx.class)
    public R setJointDeptOptions(List<String> list){
        String arr = StringUtil.join(list,",");
        AdminField adminField = new AdminField();
        adminField.setFieldId(103);
        adminField.setOptions(arr);
        boolean flag = adminField.update();
        return flag ? R.ok("操作成功") : R.error("操作失败");
    }
}
