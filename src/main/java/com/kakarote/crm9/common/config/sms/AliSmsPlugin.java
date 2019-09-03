package com.kakarote.crm9.common.config.sms;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.profile.DefaultProfile;
import com.jfinal.plugin.IPlugin;

/**
 * 阿里短信服务
 * @ClassName AliSmsPlugin
 * @Coder lindy
 * @Date 2019/8/23 上午9:24
 * @Version 1.0
 **/
public class AliSmsPlugin implements IPlugin {

    private String regionId;

    private String accessKeyId;

    private String secret;

    private static IAcsClient acsClient;

    public AliSmsPlugin(String regionId,String accessKeyId,String secret){
        this.regionId = regionId;
        this.accessKeyId = accessKeyId;
        this.secret = secret;
    }

    public static IAcsClient getInstance(){
        return acsClient;
    }

    @Override
    public boolean start() {
        DefaultProfile profile = DefaultProfile.getProfile(regionId,
                accessKeyId, secret);
        acsClient = new DefaultAcsClient(profile);
        return true;
    }

    @Override
    public boolean stop() {
        acsClient.shutdown();
        return true;
    }
}
