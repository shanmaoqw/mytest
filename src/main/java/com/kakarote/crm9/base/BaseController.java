package com.kakarote.crm9.base;

import com.jfinal.core.Controller;

/**
 * 自定义controller
 * @ClassName BaseController
 * @Coder lindy
 * @Date 2019/8/12 下午5:33
 * @Version 1.0
 **/
public class BaseController extends Controller {

    @Override
    public void renderJson(String jsonText) {
        render(new HhgwRenderJson(jsonText));
    }

}
