package com.kakarote.crm9.erp.editor.controller;

import com.jfinal.core.Controller;
import com.jfinal.core.paragetter.Para;
import com.kakarote.crm9.erp.editor.entity.Editor;
import com.kakarote.crm9.erp.editor.utils.PdfService;

import java.io.File;
import java.io.IOException;

/**
 * Created by Haoxy on 2019-06-05.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
 */
public class ItextHtmlToPdfController extends Controller {

   // @RequestMapping(value = "pdf")
    public String converterTask(@Para("") Editor editor) {
        PdfService pdfService = new PdfService();
        String tempFile = PdfService.RESOURCE_PREFIX_INDEX + "/" + "pdf" + "/";
        createDirs(tempFile);
        File pdfFile = createFlawPdfFile(tempFile, System.currentTimeMillis() + "-itext");
        pdfService.createPdfFromHtml(pdfFile.getName(), editor.getContent(), tempFile);
        return pdfFile.getName();
    }


    public String converterTask2( String editor) throws IOException {
        PdfService pdfService = new PdfService();
        String tempFile = PdfService.RESOURCE_PREFIX_INDEX + "/" + "pdf" + "/";
        createDirs(tempFile);
        File pdfFile = createFlawPdfFile(tempFile, System.currentTimeMillis() + "-itext");
        pdfService.convertPageSpacing(pdfFile.getName(), editor, tempFile);
        return pdfFile.getName();
    }
    /**
     * 新建文件夹
     *
     * @param dirsPath
     */
    private static void createDirs(String dirsPath) {
        File temFile = new File(dirsPath);
        if (!temFile.exists()) {
            temFile.mkdirs();
        }
    }

    /**
     * 创建漏洞pdf版本空文件
     *
     * @param fileDir
     * @param fileName
     * @return
     */
    private static File createFlawPdfFile(String fileDir, String fileName) {
        File tempFile;
        do {
            tempFile = new File(fileDir + fileName + ".pdf");
        } while (tempFile.exists());
        return tempFile;
    }
}
