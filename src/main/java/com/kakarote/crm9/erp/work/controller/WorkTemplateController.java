package com.kakarote.crm9.erp.work.controller;

import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.core.paragetter.Para;
import com.kakarote.crm9.common.config.paragetter.BasePageRequest;
import com.kakarote.crm9.erp.work.service.WorkTemplateService;

/**
 * @Author: wangpeichuan
 * @Date: 2019/7/29 19:13
 * @Version 1.0
 */

public class WorkTemplateController extends Controller {

    @Inject
    WorkTemplateService workTemplateService;

    public void setWorkTemplate(){

        renderJson(workTemplateService.setWorkTemplate(getRawData()));
    }
    public void findById(@Para("id")Integer id){
        renderJson(workTemplateService.findById(id));
    }
    public void delById(@Para("id")Integer id){

        renderJson(workTemplateService.delById(id));
    }
    public void findList(){

        renderJson(workTemplateService.findList());
    }
    public void findPageList(BasePageRequest request){

        renderJson(workTemplateService.findPageList(request));
    }

    public void update(){

        renderJson(workTemplateService.update(getRawData()));
    }

    public void updateTemplateAndDoc(){
        renderJson(workTemplateService.updateTemplateAndDoc(getRawData()));
    }

}
