package com.kakarote.crm9.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import com.jfinal.kit.Prop;
import com.jfinal.kit.PropKit;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.UUID;

/**
 * oss文件操作类
 * @ClassName FileUtil
 * @Coder lindy
 * @Date 2019/8/12 下午2:13
 * @Version 1.0
 **/
public class FileUtil {
    private static Prop p = PropKit.use("config/aliyun.txt");


    public static OSS getOSSClient() {
        return new OSSClientBuilder().build(p.get("ENDPOINT"), p.get("ACCESS_KEY_ID"), p.get("ACCESS_KEY_SECRET"));
    }


    public static String uploadImg(OSS ossClient, File file, String bucketName, String folder) {
        String url = null;
        try {
            // 以输入流的形式上传文件
            InputStream is = new FileInputStream(file);
            // 文件名
            String fileName = file.getName();
            fileName = UUID.randomUUID().toString().replaceAll("-", "") + fileName;
            // 文件大小
            Long fileSize = file.length();
            // 创建上传Object的Metadata
            ObjectMetadata metadata = new ObjectMetadata();
            // 上传的文件的长度
            metadata.setContentLength(is.available());
            // 指定该Object被下载时的网页的缓存行为
            metadata.setCacheControl("no-cache");
            // 指定该Object下设置Header
            metadata.setHeader("Pragma", "no-cache");
            // 指定该Object被下载时的内容编码格式
            metadata.setContentEncoding("utf-8");
            // 文件的MIME，定义文件的类型及网页编码，决定浏览器将以什么形式、什么编码读取文件。如果用户没有指定则根据Key或文件名的扩展名生成，
            // 如果没有扩展名则填默认值application/octet-stream
            metadata.setContentType(getContentType(fileName));
            // 指定该Object被下载时的名称（指示MINME用户代理如何显示附加的文件，打开或下载，及文件名称）
            metadata.setContentDisposition("filename/filesize=" + fileName + "/" + fileSize + "Byte.");
            // 上传文件 (上传文件流的形式)
            PutObjectResult putResult = ossClient.putObject(bucketName, folder + fileName, is, metadata);
            // 解析结果
            url = "https://" + p.get("BACKET_NAME") + "." + p.get("ENDPOINT") + "/" + p.get("FOLDER") + fileName;
            System.out.println("上传的文件路径：" + url);
            return url;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("上传阿里云异常");
        }
        return url;
    }

    public static String getContentType(String fileName) {
        // 文件的后缀名
        String fileExtension = fileName.substring(fileName.lastIndexOf("."));
        if (".bmp".equalsIgnoreCase(fileExtension)) {
            return "image/bmp";
        }
        if (".gif".equalsIgnoreCase(fileExtension)) {
            return "image/gif";
        }
        if (".jpeg".equalsIgnoreCase(fileExtension) || ".jpg".equalsIgnoreCase(fileExtension)
                || ".png".equalsIgnoreCase(fileExtension)) {
            return "image/jpeg";
        }
        if (".html".equalsIgnoreCase(fileExtension)) {
            return "text/html";
        }
        if (".txt".equalsIgnoreCase(fileExtension)) {
            return "text/plain";
        }
        if (".vsd".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.visio";
        }
        if (".ppt".equalsIgnoreCase(fileExtension) || "pptx".equalsIgnoreCase(fileExtension)) {
            return "application/vnd.ms-powerpoint";
        }
        if (".doc".equalsIgnoreCase(fileExtension) || "docx".equalsIgnoreCase(fileExtension)) {
            return "application/msword";
        }
        if (".xml".equalsIgnoreCase(fileExtension)) {
            return "text/xml";
        }
        // 默认返回类型
        return "image/jpeg";
    }
}
