package com.kakarote.crm9.erp.crm.service;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Before;
import com.jfinal.kit.Kv;
import com.jfinal.kit.StrKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.SqlPara;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.kakarote.crm9.common.config.paragetter.BasePageRequest;
import com.kakarote.crm9.erp.crm.entity.CrmTechnologyProject;
import com.kakarote.crm9.utils.R;

/**
 * 客户基本信息里面的科技项目列表
 * @ClassName CrmTechnologyProjectService
 * @Coder lindy
 * @Date 2019/8/9 下午3:00
 * @Version 1.0
 **/
public class CrmTechnologyProjectService {

    /**
     * 查询客户科技项目列表
     * @param basePageRequest
     * @return
     */
    public R queryCrmTechnologyProjects(BasePageRequest<CrmTechnologyProject> basePageRequest){
        Integer customerId = basePageRequest.getData().getCustomerId();
        Integer pageType = basePageRequest.getPageType();
        if (0 == pageType){
            return R.ok().put("data", Db.find("select * from 72crm_crm_technology_project where customer_id = ?",customerId));
        }else {
            return R.ok().put("data",Db.paginate(basePageRequest.getPage(),basePageRequest.getLimit(),new SqlPara()
                    .setSql("select * from 72crm_crm_technology_project where customer_id = ?").addPara(customerId)));
        }
    }

    /**
     * 新增或者编辑客户科技项目，文件上传到OSS操作由前端完成，后端接收前端传递过来的文件地址
     * @param jsonObject
     * @return
     */
    @Before(Tx.class)
    public R addOrUpdateCrmTechnologyProject(JSONObject jsonObject){
        CrmTechnologyProject crmTechnologyProject = jsonObject.getObject("entity", CrmTechnologyProject.class);
        if (crmTechnologyProject == null || crmTechnologyProject.getCustomerId() == null){
            return R.error("客户ID不能为空");
        }
        if (crmTechnologyProject.getProjectId() != null) {
            //更新科技项目
            return crmTechnologyProject.update() ? R.ok() : R.error();
        } else {
            //新增科技项目
            if(StrKit.isBlank(crmTechnologyProject.getProjectName())){
                return R.error("项目名称不能为空");
            }
            if(StrKit.isBlank(crmTechnologyProject.getApplyDate())){
                return R.error("申报时间不能为空");
            }
            return crmTechnologyProject.save() ? R.ok().put("data", Kv.by("projectId", crmTechnologyProject.getProjectId())) : R.error();
        }
    }

    /**
     * 删除某条客户的科技项目
     * @param projectId
     * @return
     */
    public R deleteCrmTechnologyProject(Integer projectId){
        return CrmTechnologyProject.dao.deleteById(projectId) ? R.ok() : R.error();
    }

}
