package com.kakarote.crm9.swagger.common;

import com.jfinal.config.Routes;
import com.kakarote.crm9.swagger.controller.SwaggerController;

/**
 * @ClassName SwaggerRouter
 * @Coder lindy
 * @Date 2019/8/22 下午6:27
 * @Version 1.0
 **/
public class SwaggerRouter extends Routes {
    @Override
    public void config() {
        addInterceptor(new SwaggerInterceptor());
        this.setBaseViewPath("/WEB-INF/views");
        add("/swagger", SwaggerController.class);
    }
}
