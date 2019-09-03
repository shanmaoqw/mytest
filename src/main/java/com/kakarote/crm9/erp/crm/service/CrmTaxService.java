package com.kakarote.crm9.erp.crm.service;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.kit.Kv;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.kakarote.crm9.common.config.paragetter.BasePageRequest;
import com.kakarote.crm9.erp.crm.entity.CrmTax;
import com.kakarote.crm9.utils.R;

/**
 * 客户财税信息相关服务
 * @ClassName CrmTaxService
 * @Coder lindy
 * @Date 2019/8/8 下午4:58
 * @Version 1.0
 **/
public class CrmTaxService {
    /**
     * 根据客户id获取财税信息
     * @param basePageRequest
     * @return
     */
    public R queryCrmTax(BasePageRequest<CrmTax> basePageRequest){
        Integer customerId = basePageRequest.getData().getCustomerId();
        Integer pageType = basePageRequest.getPageType();
        if (0 == pageType){
            return R.ok().put("data", Db.findFirst("select * from 72crm_crm_tax where customer_id = ?" ,customerId));
        }else {
            //分页获取并按year倒序
            return R.ok().put("data",Db.paginate(basePageRequest.getPage(),basePageRequest.getLimit(),new SqlPara()
                    .setSql("select * from 72crm_crm_tax where customer_id = ? order by year desc").addPara(customerId)));
        }
    }

    /**
     * 新增或添加财税信息
     * @param jsonObject
     * @return
     */
    @Before(Tx.class)
    public R addOrUpdateTax(JSONObject jsonObject){
        CrmTax crmTax = jsonObject.getObject("entity", CrmTax.class);
        if (crmTax == null){
            return R.error("参数为空");
        }
        if (crmTax.getTaxId() != null) {
            return crmTax.update() ? R.ok() : R.error();
        } else {
            boolean save = crmTax.save();
            return save ? R.ok().put("data", Kv.by("taxId", crmTax.getTaxId())) : R.error();
        }
    }

    /**
     * 删除一条财务信息
     * @param id
     * @return
     */
    @Before(Tx.class)
    public R deleteById(Integer id){
        boolean isDeleted = Db.deleteById("72crm_crm_tax","tax_id",id);
        return isDeleted ? R.ok("删除成功") : R.error("删除失败");
    }

}
