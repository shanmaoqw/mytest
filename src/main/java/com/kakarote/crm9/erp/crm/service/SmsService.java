package com.kakarote.crm9.erp.crm.service;

import com.aliyuncs.CommonRequest;
import com.aliyuncs.CommonResponse;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.http.MethodType;
import com.jfinal.kit.JsonKit;
import com.jfinal.kit.Kv;
import com.kakarote.crm9.common.config.sms.AliSmsPlugin;

/**
 * 短信服务
 * @ClassName SmsService
 * @Coder lindy
 * @Date 2019/8/23 下午1:55
 * @Version 1.0
 **/
public class SmsService {

    /**
     * 客户账号创建成功后，发送短信通知客户
     * @param service 服务商
     * @param phoneNumbers 手机号
     * @param saasId
     * @param pwd 初始密码
     */
    public void sendSmsCustomerAccount(String service,String phoneNumbers,String pwd,String saasId) {
        IAcsClient iAcsClient = AliSmsPlugin.getInstance();
        CommonRequest request = new CommonRequest();
        //request.setProtocol(ProtocolType.HTTPS);
        request.setSysMethod(MethodType.POST);
        request.setSysDomain("dysmsapi.aliyuncs.com");
        request.setSysVersion("2017-05-25");
        request.setSysAction("SendSms");
        request.putQueryParameter("PhoneNumbers", phoneNumbers);
        request.putQueryParameter("SignName", "禾禾贯文");
        request.putQueryParameter("TemplateCode", "SMS_173245903");
        request.putQueryParameter("TemplateParam", generateParams(service,phoneNumbers,pwd,saasId));
        CommonResponse response = null;
        try {
            response = iAcsClient.getCommonResponse(request);
            System.out.println(response.getData());
        } catch (ServerException e) {
            e.printStackTrace();
        } catch (ClientException e) {
            e.printStackTrace();
        }
    }

    /**
     * 生成模板json
     * @return
     */
    private String generateParams(String service,String phoneNumbers,String pwd,String saasId){
        String authJson = JsonKit.toJson(Kv.by("service",service).set("account",phoneNumbers).set("password",pwd).set("saasId",saasId));
        return authJson;
    }

}
