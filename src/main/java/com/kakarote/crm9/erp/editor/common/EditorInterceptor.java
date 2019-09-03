package com.kakarote.crm9.erp.editor.common;

import com.jfinal.aop.Interceptor;
import com.jfinal.aop.Invocation;

public class EditorInterceptor implements Interceptor {
    @Override
    public void intercept(Invocation invocation) {
        invocation.invoke();
    }
}
