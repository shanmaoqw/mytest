package com.kakarote.crm9.common.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.jfinal.aop.Aop;
import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;
import com.kakarote.crm9.common.annotation.Permissions;
import com.kakarote.crm9.erp.admin.service.AdminRoleService;
import com.kakarote.crm9.utils.BaseUtil;
import com.kakarote.crm9.utils.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class AuthInterceptor implements Interceptor {

    @Override
    public void intercept(Invocation invocation) {
        //TODO 权限功能后台拦截
        Permissions permissions=invocation.getMethod().getAnnotation(Permissions.class);
        if(permissions!=null&&permissions.value().length>0){
            JSONObject jsonObject= Aop.get(AdminRoleService.class).auth(BaseUtil.getUserId());
            Integer userType = BaseUtil.getUserType();
            if(userType == 0){
                //普通用户
                //组装应有权限列表
                List<String> arr=queryAuth(jsonObject, "");
                boolean isRelease=false;
                for (String key : permissions.value()) {
                    if(!isRelease){
                        if(arr.contains(key)){
                            isRelease=true;
                        }
                    }
                }
                if(!isRelease){
                    invocation.getController().renderJson(R.error("无权访问"));
                    return;
                }
            }else if(userType == 1){
                //客户 ，客户只有查看项目和编辑项目的权限
                List<String> arr = new ArrayList<>();
                arr.add("work.taskClass.read");
                arr.add("work.task.read");
                arr.add("work.taskClass.update");
                boolean isRelease=false;
                for (String key : permissions.value()) {
                    if(!isRelease){
                        if(arr.contains(key)){
                            isRelease=true;
                        }
                    }
                }
                if(!isRelease){
                    invocation.getController().renderJson(R.error("无权访问"));
                    return;
                }
            }

        }
        invocation.invoke();
    }
    @SuppressWarnings("unchecked")
    private List<String> queryAuth(Map<String,Object> map,String key){
        List<String> permissions=new ArrayList<>();
        map.keySet().forEach(str->{
            if(map.get(str) instanceof Map){
                permissions.addAll(this.queryAuth((Map<String, Object>) map.get(str),key+str+":"));
            }else {
                permissions.add(key+str);
            }
        });
        return permissions;
    }
}
