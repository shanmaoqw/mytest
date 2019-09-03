package com.kakarote.crm9.swagger.common;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

/**
 * swagger
 * @ClassName SwaggerInterceptor
 * @Coder lindy
 * @Date 2019/8/22 下午6:28
 * @Version 1.0
 **/
public class SwaggerInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation invocation) {
        invocation.invoke();
    }
}
