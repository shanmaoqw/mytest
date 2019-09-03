package com.kakarote.crm9.erp.admin.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.kakarote.crm9.erp.admin.service.AdminParamsService;

import java.util.List;

/**
 * 系统参数设置
 * @ClassName AdminParamsController
 * @Coder lindy
 * @Date 2019/8/28 下午7:34
 * @Version 1.0
 **/
public class AdminParamsController extends Controller {

    @Inject
    AdminParamsService adminParamsService;

    /**
     * @author wyq
     * 查询对口部门设置
     */
    public void queryJointDeptOptions(){
        renderJson(adminParamsService.queryJointDeptOptions());
    }

    /**
     * @author wyq
     * 设置对口部门
     */
    public void setJointDeptOptions(){
        JSONObject jsonObject = JSONObject.parseObject(getRawData());
        JSONArray jsonArray = JSONArray.parseArray(jsonObject.getString("value"));
        List<String> list = jsonArray.toJavaList(String.class);
        renderJson(adminParamsService.setJointDeptOptions(list));
    }
}
