package com.kakarote.crm9.erp.work.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Inject;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Record;
import com.kakarote.crm9.erp.editor.controller.ItextHtmlToPdfController;
import com.kakarote.crm9.erp.editor.controller.Word2htmlController;
import com.kakarote.crm9.erp.editor.entity.RespInfo;
import com.kakarote.crm9.erp.work.entity.WorkDocument;
import com.kakarote.crm9.utils.R;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: wangpeichuan
 * @Date: 2019/7/30 14:40
 * @Version 1.0
 */
public class WorkDocumentService {
    @Inject
    Word2htmlController word2htmlController;

    @Inject
    ItextHtmlToPdfController itextHtmlToPdfController;

    public R save(MultipartFile file,String type) throws Exception {
        String html = word2htmlController.parseDocToHtml(file);
        RespInfo respInfo = JSON.parseObject(html,RespInfo.class);
        String filename = file.getOriginalFilename();
        WorkDocument workDocument = new WorkDocument();
        workDocument.setCreateTime(new Date());
        workDocument.setCreateUser(3);
        workDocument.setHtmlValue(respInfo.getContent().toString());
        workDocument.setName(filename);
        workDocument.setWordUrl(filename);
        workDocument.setUpdateTime(new Date());
        workDocument.setStatus(1);
        workDocument.setType(type);
        boolean save = workDocument.save();
        if (save){
            Map map= new HashMap();
            map.put("id",workDocument.getId());
            map.put("name",workDocument.getName());
            map.put("type",workDocument.getType());
            return R.ok(map);
        }else {
            return R.error();
        }
    }

    public R findById(Integer id) {
        if (id==null){
            return R.error(101,"暂无数据");
        }
        Record workTemplate = Db.findById("72crm_work_document", id);
        if (workTemplate!=null){
            return R.ok().put("data",workTemplate);
        }
        return R.error(101,"暂无数据");
    }

    public R delById(Integer id) {
        if (id==null){
            return R.error(101,"请选择要删除数据");
        }
        Integer workDocumentId =  Db.queryInt("select id from 72crm_work_document where id=?", id);
        if (id==null){
            return R.error(101,"数据不存在");
        }
        List<Object> tempalteDoc =Db.query("select id from 72crm_work_template_document where document_id = "+id);
        if(tempalteDoc != null){
            for (Object o:tempalteDoc){
                Db.deleteById("72crm_work_template_document",o);
            }
        }
        if (workDocumentId!=null){
            Db.deleteById("72crm_work_document",id);
            return R.ok();
        }
        return R.error(101,"数据已删除");
    }

    public R update(WorkDocument workDocument) {
        if (workDocument.getName()==null||workDocument.getName()==""){
            return R.error(101,"文档名称不能为空");
        }
        workDocument.setUpdateTime(new Date());
        workDocument.update();
        return R.ok();
    }

    public R updateType(String id, String type) {

        if (id==null || type ==null){
            return R.error(101,"参数错误");
        }
        WorkDocument workDocument = WorkDocument.dao.findById(id);
        workDocument.setType(type);
        workDocument.update();
        return R.ok();
    }

    public R updateAsc(String str) {
        JSONObject jsonObject = JSON.parseObject(str);
        List<WorkDocument> list = JSON.parseArray(jsonObject.getString("list"), WorkDocument.class);
        if (list.size()!=2){
            return R.error(101,"参数有误");
        }
        WorkDocument workDocument0 = WorkDocument.dao.findById(list.get(0).getId());
        WorkDocument workDocument1 = WorkDocument.dao.findById(list.get(1).getId());
        workDocument0.setSort(list.get(1).getSort());
        workDocument1.setSort(list.get(0).getSort());
        workDocument0.update();
        workDocument1.update();
        return R.ok();
    }

    public R findByTaskId(String id) {

        List<WorkDocument> result =WorkDocument.dao.find(" select * from 72crm_work_document where id in (select document_id FROM 72crm_task_document where task_id = "+id+")");
        if (result==null){
            return R.error(101,"暂时无数据");
        }
//        for (WorkDocument workDocument:result){
//            workDocument.setHtmlValue("");
//        }

        return R.ok().put("data",result);
    }

    public R htmlToPdf(File file) throws Exception {
        String s = word2htmlController.convertExcel2Html(file, "UTF-8");

        return R.ok().put("data",s );
    }
}
