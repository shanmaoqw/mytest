package com.kakarote.crm9.erp.work.controller;

import com.jfinal.aop.Clear;
import com.jfinal.aop.Inject;
import com.jfinal.core.Controller;
import com.jfinal.core.paragetter.Para;
import com.jfinal.upload.UploadFile;
import com.kakarote.crm9.erp.work.entity.WorkDocument;
import com.kakarote.crm9.erp.work.service.WorkDocumentService;
import com.kakarote.crm9.utils.BaseUtil;
import com.kakarote.crm9.utils.R;
import org.apache.http.entity.ContentType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;

/**
 * @Author: wangpeichuan
 * @Date: 2019/7/29 19:13
 * @Version 1.0
 */
@Clear
public class WorkDocumentController extends Controller {

    @Inject
    WorkDocumentService workDocumentService;
    
  public void save()throws Exception{
      UploadFile file = getFile("file", BaseUtil.getDate());
      String typeName=getPara("type");
      if (typeName==null){
          typeName="";
      }
      if (StringUtils.isEmpty(file)){
          renderJson(R.error(""));
      }
      File pdfFile = file.getFile();
      FileInputStream fileInputStream = new FileInputStream(pdfFile);
      MultipartFile multipartFile = new MockMultipartFile(pdfFile.getName(), pdfFile.getName(),
              ContentType.APPLICATION_OCTET_STREAM.toString(), fileInputStream);
      renderJson(workDocumentService.save(multipartFile,typeName));
  }

    /**
     * 查文档详情
     * @param id
     */
    public void findById(@Para("id")Integer id){
        renderJson(workDocumentService.findById(id));
    }

    /**
     * 删除文档
     * @param id
     */

    public void delById(@Para("id")Integer id){
        renderJson(workDocumentService.delById(id));
    }

    public void update(@Para("")WorkDocument workDocument){
        renderJson(workDocumentService.update(workDocument));
    }

    public void updateType(){renderJson(workDocumentService.updateType(getPara("id"),getPara("type")));}

    public void updateAsc(){renderJson(workDocumentService.updateAsc(getRawData()));}

    public void findByTaskId(){renderJson(workDocumentService.findByTaskId(getPara("id")));}


    public void htmlToPdf() throws Exception {
        UploadFile file = getFile("file", BaseUtil.getDate());

        File pdfFile = file.getFile();
        renderJson(workDocumentService.htmlToPdf(pdfFile));}
}
