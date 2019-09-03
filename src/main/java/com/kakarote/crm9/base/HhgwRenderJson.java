package com.kakarote.crm9.base;

import com.jfinal.render.JsonRender;
import com.jfinal.render.RenderException;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * 自定义render
 * @ClassName HhgwRenderJson
 * @Coder lindy
 * @Date 2019/8/12 下午5:36
 * @Version 1.0
 **/
public class HhgwRenderJson extends JsonRender {

    public HhgwRenderJson(String jsonText){
        this.jsonText = jsonText;
    }

    @Override
    public void render() {
        if (this.jsonText == null) {
            this.buildJsonText();
        }

        PrintWriter writer = null;

        try {
            this.response.setHeader("Pragma", "no-cache");
            this.response.setHeader("Cache-Control", "no-cache");
            this.response.setHeader("Content-Disposition","attachment");
            this.response.setDateHeader("Expires", 0L);
            this.response.setContentType(this.forIE ? contentTypeForIE : contentType);
            writer = this.response.getWriter();
            writer.write(this.jsonText);
        } catch (IOException var3) {
            throw new RenderException(var3);
        }
    }


}
