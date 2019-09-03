package com.kakarote.crm9.erp.editor.controller;

import com.alibaba.fastjson.JSON;
import com.jfinal.aop.Clear;
import com.kakarote.crm9.erp.editor.entity.InfoCode;
import com.kakarote.crm9.erp.editor.entity.RespInfo;
import com.kakarote.crm9.erp.editor.utils.SaveFile;
import org.apache.poi.hssf.converter.ExcelToHtmlConverter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.converter.PicturesManager;
import org.apache.poi.hwpf.converter.WordToHtmlConverter;
import org.apache.poi.hwpf.usermodel.Picture;
import org.apache.poi.hwpf.usermodel.PictureType;
import org.apache.poi.xwpf.converter.core.FileImageExtractor;
import org.apache.poi.xwpf.converter.core.FileURIResolver;
import org.apache.poi.xwpf.converter.xhtml.XHTMLConverter;
import org.apache.poi.xwpf.converter.xhtml.XHTMLOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;


/**
 * Created by haoxy on 2018/9/11.
 * E-mail:hxyHelloWorld@163.com
 * github:https://github.com/haoxiaoyong1014
// */
//@Controller
//@RequestMapping(value = "upload/word")
    @Clear
public class Word2htmlController {
    //  @Value("${word.to.html.path}")// /tmp/docker/doc/
    private String path = "/tmp/docker/doc/";
    final static String path1= "D:\\EclipseWorkspace\\ExcelToHtmlDemo\\ExcelToHtml\\";
    final static String file = "TestExcel.xlsx";
    private static final String EXCEL_XLS = "xls";
    private static final String EXCEL_XLSX = "xlsx";
    //    @RequestMapping("/template")
//    @ResponseBody
    public String parseDocToHtml(MultipartFile file) throws IOException, ParserConfigurationException, TransformerException {
        String fileName = file.getOriginalFilename();
        SaveFile.savePic(file.getInputStream(), fileName);
        InputStream input = new FileInputStream(path + fileName);
        String suffix = fileName.substring(fileName.indexOf(".") + 1);// //截取文件格式名
        if ("docx".equals(suffix)) {
            String content = parseDocxToHtml(fileName);
            return content;
        }
//        if ("xlsx".equals(suffix)||"xls".equals(suffix)) {
//
//            return  convertExcel2Html(file.get, "UTF-8");
//        }
        //实例化WordToHtmlConverter，为图片等资源文件做准备
        WordToHtmlConverter wordToHtmlConverter = new WordToHtmlConverter(
                DocumentBuilderFactory.newInstance().newDocumentBuilder()
                        .newDocument());
        wordToHtmlConverter.setPicturesManager(new PicturesManager() {
            public String savePicture(byte[] content, PictureType pictureType,
                                      String suggestedName, float widthInches, float heightInches) {
                return suggestedName;
            }
        });
        if ("doc".equals(suffix.toLowerCase())) {
            // doc
            HWPFDocument wordDocument = new HWPFDocument(input);
            wordToHtmlConverter.processDocument(wordDocument);
            //处理图片，会在同目录下生成并保存图片
            List pics = wordDocument.getPicturesTable().getAllPictures();
            if (pics != null) {
                for (int i = 0; i < pics.size(); i++) {
                    Picture pic = (Picture) pics.get(i);
                    try {
                        pic.writeImageContent(new FileOutputStream(path
                                + pic.suggestFullFileName()));
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        // 转换
        String content = conversion(wordToHtmlConverter);
        RespInfo respInfo = new RespInfo();
        if (content != null) {
            respInfo.setContent(content);
            respInfo.setMessage("success");
            respInfo.setStatus(InfoCode.SUCCESS);
        } else {
            respInfo.setMessage("error");
            respInfo.setStatus(InfoCode.ERROR);
        }
        return JSON.toJSONString(respInfo);
    }

    private String parseDocxToHtml(String fileName) throws IOException {
        RespInfo respInfo = new RespInfo();
        File file = new File(path + fileName);
        if (!file.exists()) {
            respInfo.setStatus(InfoCode.ERROR);
            respInfo.setMessage("Sorry File does not Exists!");
            return JSON.toJSONString(respInfo);
        }
        if (file.getName().endsWith(".docx") || file.getName().endsWith(".DOCX")) {
            // 1) 加载XWPFDocument及文件
            InputStream in = new FileInputStream(file);
            XWPFDocument document = new XWPFDocument(in);
            document.createNumbering();
            // 2) 实例化XHTML内容(这里将会把图片等文件放到同级目录下)
            File imageFolderFile = new File(path);
            XHTMLOptions options = XHTMLOptions.create().URIResolver(
                    new FileURIResolver(imageFolderFile));
            options.setExtractor(new FileImageExtractor(imageFolderFile));
            options.setIgnoreStylesIfUnused(false);
            options.setFragment(true);
            // 3) 将XWPFDocument转成XHTML并生成文件  --> 我此时不想让它生成文件,所以我注释掉了,按需求定
          /*  OutputStream out = new FileOutputStream(new File(
                    path, "result.html"));
            XHTMLConverter.getInstance().convert(document, out, null);*/
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            XHTMLConverter.getInstance().convert(document, baos, options);
            String content = baos.toString();
            //content = Pattern.compile("width:\\d*[.]\\d*pt;").matcher(content).replaceAll("592.0pt;");595.0pt
            content = compile(content);
            baos.close();
            respInfo.setMessage("success");
            respInfo.setStatus(InfoCode.SUCCESS);
            //respInfo.setContent("<div style=\"width: 595.0pt; margin: -72.0pt -90.0pt -72.0pt -90.0pt !important;\">"+content+"</div>");
            respInfo.setContent(content);
            return JSON.toJSONString(respInfo);
        } else {
            System.out.println("Enter only MS Office 2007+ files");
        }
        return null;
    }

    private String compile(String content) {
        content = Pattern.compile("width:595.0pt;").matcher(content).replaceAll("");
        content = Pattern.compile("width:593.0pt;").matcher(content).replaceAll("");
        content = Pattern.compile("margin-left:\\d*[.]\\d*pt;").matcher(content).replaceAll("");
        content = Pattern.compile("margin-right:\\d*[.]\\d*pt;").matcher(content).replaceAll("");
        return content;
    }

    private String conversion(WordToHtmlConverter wordToHtmlConverter) throws TransformerException, IOException {
        Document htmlDocument = wordToHtmlConverter.getDocument();
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        DOMSource domSource = new DOMSource(htmlDocument);
        StreamResult streamResult = new StreamResult(outStream);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer serializer = tf.newTransformer();
        serializer.setOutputProperty(OutputKeys.ENCODING, "utf-8");//编码格式
        serializer.setOutputProperty(OutputKeys.INDENT, "yes");//是否用空白分割
        serializer.setOutputProperty(OutputKeys.METHOD, "html");//输出类型
        serializer.transform(domSource, streamResult);
        outStream.close();
        String content = new String(outStream.toByteArray());
        //我此时不想让它生成文件,所以我注释掉了,按需求定
        /*FileUtils.writeStringToFile(new File(path, "interface.html"), content,
                "utf-8");*/
        return content;
    }

    /*public static void main(String[] args) throws Throwable {
        new Word2htmlController().parseDoc2Html();
    }*/
    /**
     *
     * @param encoding 编码格式
     */
    public String convertExcel2Html(File excelFile, String encoding)throws Exception {
        // 获取文件名
        String fileName = excelFile.getName();
        // 获取文件后缀
        String prefix = fileName.substring(fileName.lastIndexOf("."));
        // 用uuid作为文件名，防止生成的临时文件重复

        File htmlFile = new File("/" + UUID.randomUUID() + ".html");

        File htmlFileParent = new File(htmlFile.getParent());
        InputStream is = null;
        OutputStream out = null;
        StringWriter writer = null;
        ExcelToHtmlConverter converter =  null;
        String res="";
        try {
            if (excelFile.exists()) {
                if (!htmlFileParent.exists()) {
                    htmlFileParent.mkdirs();
                }
                is = new FileInputStream(excelFile);
                HSSFWorkbook workBook = new HSSFWorkbook(is);
                 converter = new ExcelToHtmlConverter(DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument());
                converter.setOutputColumnHeaders(false);
                converter.setOutputRowNumbers(false);
                converter.processWorkbook(workBook);
                deleteFile(excelFile);


                writer = new StringWriter();
                Transformer serializer = TransformerFactory.newInstance().newTransformer();
                serializer.setOutputProperty(OutputKeys.ENCODING, encoding);
                serializer.setOutputProperty(OutputKeys.INDENT, "yes");
                serializer.setOutputProperty(OutputKeys.METHOD, "html");
                serializer.transform(new DOMSource(converter.getDocument()), new StreamResult(writer));
                out = new FileOutputStream(htmlFile);
                out.write(writer.toString().getBytes(encoding));
                out.flush();
                res = writer.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                is = null;
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                out = null;
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                writer = null;
            }
            return res;
        }
    }
    /**
     * 删除
     *
     * @param files
     */
    public static void deleteFile(File... files) {
        for (File file : files) {
            if (file.exists()) {
                file.delete();
            }
        }
    }
}