package com.kakarote.crm9.swagger.controller;

import com.feizhou.swagger.annotation.Api;
import com.feizhou.swagger.annotation.ApiOperation;
import com.feizhou.swagger.annotation.Param;
import com.feizhou.swagger.annotation.Params;
import com.feizhou.swagger.model.SwaggerDoc;
import com.feizhou.swagger.model.SwaggerGlobalPara;
import com.feizhou.swagger.model.SwaggerPath;
import com.feizhou.swagger.utils.ClassHelper;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.jfinal.core.Controller;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @ClassName SwaggerController
 * @Coder lindy
 * @Date 2019/8/22 下午6:42
 * @Version 1.0
 **/
public class SwaggerController extends Controller {
    public SwaggerController() {
    }

    public void index() {
        this.render("index.html");
    }

    public void api() {
        SwaggerDoc doc = new SwaggerDoc();
        Map<String, Map<String, SwaggerPath.ApiMethod>> paths = new HashMap();
        Map<String, String> classMap = Maps.newHashMap();
        Set<Class<?>> classSet = ClassHelper.getBeanClassSet();
        Iterator var5 = classSet.iterator();

        while(true) {
            Class cls;
            do {
                if (!var5.hasNext()) {
                    if (classMap.size() > 0) {
                        var5 = classMap.keySet().iterator();

                        while(var5.hasNext()) {
                            String key = (String)var5.next();
                            doc.addTags(new SwaggerDoc.TagBean(key, (String)classMap.get(key)));
                        }
                    }

                    doc.setPaths(paths);
                    Gson gson = new Gson();
                    this.renderText(gson.toJson(doc).replaceAll("\"defaultValue\"", "\"default\""));
                    return;
                }

                cls = (Class)var5.next();
            } while(!cls.isAnnotationPresent(Api.class));

            Api api = (Api)cls.getAnnotation(Api.class);
            if (!classMap.containsKey(api.tag())) {
                classMap.put(api.tag(), api.description());
            }

            Method[] methods = cls.getMethods();
            Method[] var9 = methods;
            int var10 = methods.length;

            for(int var11 = 0; var11 < var10; ++var11) {
                Method method = var9[var11];
                Annotation[] annotations = method.getAnnotations();
                SwaggerPath.ApiMethod apiMethod = new SwaggerPath.ApiMethod();
                apiMethod.setOperationId("");
                apiMethod.addProduce("application/json");
                List<SwaggerPath.Parameter> parameterList = SwaggerGlobalPara.getParameterList();
                if (parameterList != null && parameterList.size() > 0) {
                    Iterator var16 = parameterList.iterator();

                    while(var16.hasNext()) {
                        SwaggerPath.Parameter parameter = (SwaggerPath.Parameter)var16.next();
                        apiMethod.addParameter(parameter);
                    }
                }

                Annotation[] var29 = annotations;
                int var30 = annotations.length;

                for(int var18 = 0; var18 < var30; ++var18) {
                    Annotation annotation = var29[var18];
                    Class<? extends Annotation> annotationType = annotation.annotationType();
                    if (ApiOperation.class == annotationType) {
                        ApiOperation apiOperation = (ApiOperation)annotation;
                        Map<String, SwaggerPath.ApiMethod> methodMap = new HashMap();
                        apiMethod.setSummary(apiOperation.description());
                        apiMethod.setDescription(apiOperation.description());
                        apiMethod.addTag(apiOperation.tag());
                        apiMethod.addConsume(apiOperation.consumes());
                        methodMap.put(apiOperation.httpMethod(), apiMethod);
                        paths.put(apiOperation.url(), methodMap);
                    } else if (Params.class == annotationType) {
                        Params apiOperation = (Params)annotation;
                        Param[] params = apiOperation.value();
                        Param[] var23 = params;
                        int var24 = params.length;

                        for(int var25 = 0; var25 < var24; ++var25) {
                            Param apiParam = var23[var25];
                            if (apiParam.dataType().equals("file")) {
                                apiMethod.addParameter(new SwaggerPath.Parameter(apiParam.name(), "formData", apiParam.description(), apiParam.required(), apiParam.dataType(), apiParam.format(), apiParam.defaultValue()));
                            } else {
                                apiMethod.addParameter(new SwaggerPath.Parameter(apiParam.name(), apiParam.description(), apiParam.required(), apiParam.dataType(), apiParam.format(), apiParam.defaultValue()));
                            }
                        }
                    } else if (Param.class == annotationType) {
                        Param apiParam = (Param)annotation;
                        apiMethod.addParameter(new SwaggerPath.Parameter(apiParam.name(), apiParam.description(), apiParam.required(), apiParam.dataType(), apiParam.format(), apiParam.defaultValue()));
                    }
                }
            }
        }
    }
}
