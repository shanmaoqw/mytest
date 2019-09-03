package com.kakarote.crm9.erp.work.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.kakarote.crm9.common.config.paragetter.BasePageRequest;
import com.kakarote.crm9.erp.work.entity.TemplateDocument;
import com.kakarote.crm9.erp.work.entity.WorkDocument;
import com.kakarote.crm9.erp.work.entity.WorkTemplate;
import com.kakarote.crm9.utils.R;

import java.util.*;

/**
 * @Author: wangpeichuan
 * @Date: 2019/7/30 14:40
 * @Version 1.0
 */
public class WorkTemplateService {
    public R setWorkTemplate(String rawData) {

        JSONObject jsonObject = JSON.parseObject(rawData);
        WorkTemplate workTemplate = JSON.parseObject(jsonObject.getString("workTemplate"), WorkTemplate.class);
        workTemplate.setCreateTime(new Date());
        workTemplate.setCreateUser(3);
        boolean save = workTemplate.save();
        String ids = jsonObject.getString("ids");
        if (ids == null  || ids == ""){
            return R.ok()  ;
        }
        String strList = jsonObject.getString("ids");
        if (strList.isEmpty()){
          return R.ok();
        }
        List<String> iList = Arrays.asList(jsonObject.getString("ids").split(","));
        Integer id = workTemplate.getId();
        if (save){
            for (String str: iList) {
                TemplateDocument templateDoc = new TemplateDocument();
                templateDoc.setTemplateId(id);
                templateDoc.setDocumentId(Integer.parseInt(str));
                templateDoc.save();
            }
            return R.ok()  ;
        }else {
            return R.error(101,"！！！");
        }

    }

    public R findById(Integer id) {
        if (id==null){
            return R.error(101,"暂无数据");
        }
        Record workTemplate = Db.findById("72crm_work_template", id);
        List<Integer> ids = Db.query("select document_id from 72crm_work_template_document where template_id = "+ workTemplate.getInt("id"));
       // List<WorkTemplate> rusult =new ArrayList<WorkTemplate>();
        for (Integer ii:ids){
            WorkDocument query = WorkDocument.dao.findById(ii);
            if (query.get("sort")==null){
                Integer sum = Db.queryInt("select count(*) from 72crm_work_document where id in (select document_id FROM 72crm_work_template_document where template_id = "+id+") AND sort is not null");
                query.set("sort",sum+1);
                query.update();
            }

        }
        List<WorkDocument> result =WorkDocument.dao.find(" select * from 72crm_work_document where id in (select document_id FROM 72crm_work_template_document where template_id = "+id+")  order by sort asc");
        for (WorkDocument workDocument:result){
            workDocument.setHtmlValue("");
        }
        Map map = new HashMap();
        map.put("workTemplate",workTemplate);
        map.put("child",result);
        if (workTemplate!=null){
            return R.ok().put("data",map);
        }
        return R.error(101,"暂无数据");
    }

    public R delById(Integer id) {
        if (id==null){
            return R.error(101,"请选择要删除的数据");
        }
        boolean b = Db.deleteById("72crm_work_template", id);
        if (b){
            List<Record> list = Db.find("select * from 72crm_work_template_document where template_id = "+ id);
            if (list.isEmpty()){
                return R.ok();
            }else {
             for (Record record:list) {
                 String id1 = record.getStr("id");
                 Db.deleteById("72crm_work_template_document",id1);
                 String document_id = record.getStr("document_id");
                 Db.deleteById("72crm_work_document",document_id);
             }
                return R.ok();
            }

        }else {
            return R.error(101,"删除失败");
        }

    }

    public R findList() {
        return R.ok().put("data",Db.find("select id ,name from 72crm_work_template"));
    }

    public R findPageList(BasePageRequest request) {
        Page<Record> paginate = Db.paginate(request.getPage(), request.getLimit(), Db.getSqlPara("workTemplate.findPageList"));
        return R.ok().put("data",paginate);
    }

    public R update(String rawData) {
        JSONObject jsonObject = JSON.parseObject(rawData);
        WorkTemplate workTemplate = JSON.parseObject(jsonObject.getString("workTemplate"), WorkTemplate.class);
        workTemplate.update();
        return R.ok();
    }

    public R updateTemplateAndDoc(String rawData) {

        JSONObject jsonObject = JSON.parseObject(rawData);
        Integer documentId = jsonObject.getInteger("ids");
        Integer workTemplateId= jsonObject.getInteger("id");
        if (documentId == null  || workTemplateId==null){
            return R.error(101,"网络延迟请稍后再试")  ;
        }
            TemplateDocument templateDoc = new TemplateDocument();
            templateDoc.setTemplateId(workTemplateId);
            templateDoc.setDocumentId(documentId);
            templateDoc.save();
        return R.ok();
    }




}
