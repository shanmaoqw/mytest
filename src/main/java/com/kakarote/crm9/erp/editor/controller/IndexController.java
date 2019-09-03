package com.kakarote.crm9.erp.editor.controller;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Clear;
import com.jfinal.core.Controller;
import com.jfinal.core.paragetter.Para;
import com.kakarote.crm9.erp.editor.entity.Editor;
import com.kakarote.crm9.erp.editor.utils.Html2PDF;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Created by haoxy on 2018/9/11.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
@Clear
public class IndexController extends Controller {

  //  @RequestMapping(value = "pdf")
    public String getContent (@Para("editor") Editor editor) {
        Html2PDF html2PDF = new Html2PDF();
        html2PDF.html2pdf("/tmp/docker/doc/test2.pdf", editor.getContent());
        return JSON.toJSONString("success");
    }


}
