package com.kakarote.crm9.erp.editor.common;

import com.jfinal.config.Routes;
import com.kakarote.crm9.erp.editor.controller.IndexController;
import com.kakarote.crm9.erp.editor.controller.ItextHtmlToPdfController;
import com.kakarote.crm9.erp.editor.controller.Word2htmlController;

public class EditorRouter extends Routes {
    @Override
    public void config() {
        addInterceptor(new EditorInterceptor());
        add("/html", IndexController.class).
        add("itext/html", ItextHtmlToPdfController.class);

    }
}
