package com.kakarote.crm9.erp.crm.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.feizhou.swagger.annotation.ApiOperation;
import com.feizhou.swagger.annotation.Param;
import com.feizhou.swagger.annotation.Params;
import com.jfinal.aop.Before;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.core.paragetter.Para;
import com.jfinal.ext.interceptor.POST;
import com.kakarote.crm9.common.annotation.NotNullValidate;
import com.kakarote.crm9.common.annotation.Permissions;
import com.kakarote.crm9.common.config.paragetter.BasePageRequest;
import com.kakarote.crm9.erp.crm.entity.CrmTax;
import com.kakarote.crm9.erp.crm.service.CrmTaxService;

/**
 * 客户财税信息
 * @ClassName CrmTaxController
 * @Coder lindy
 * @Date 2019/8/8 下午5:09
 * @Version 1.0
 **/
public class CrmTaxController extends Controller {

    @Inject
    CrmTaxService crmTaxService;

    /**
     * 根据客户id和年限查询客户某一年的财税信息
     */
    @Permissions("crm:customer:read")
    public void queryByCustomerIdAndYear(BasePageRequest<CrmTax> basePageRequest){
        renderJson(crmTaxService.queryCrmTax(basePageRequest));
    }

    /**
     * 新增或更新财税信息
     */
    @Permissions({"crm:customer:save","crm:customer:update"})
    public void addOrUpdateTax(){
        JSONObject jsonObject= JSON.parseObject(getRawData());
        renderJson(crmTaxService.addOrUpdateTax(jsonObject));
    }

    /**
     * 删除客户财税财税信息
     */
    @ApiOperation(url = "/CrmTax/deleteTax", tag = "CrmTaxController【客户】", httpMethod = "post", description = "根据id删除财务信息")
    @Params({ @Param(name = "taxId", description = "ID", required = true, dataType = "Integer") })
    @Permissions("crm:customer:delete")
    @NotNullValidate(value = "taxId", message = "id不能为空")
    @Before(POST.class)
    public void deleteTax(@Para("taxId") Integer taxId){
        renderJson(crmTaxService.deleteById(taxId));
    }

}
