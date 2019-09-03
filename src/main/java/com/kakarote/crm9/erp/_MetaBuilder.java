package com.kakarote.crm9.erp;

import com.jfinal.plugin.activerecord.generator.MetaBuilder;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

/**
 * 自定义自动生成哪些表的Model
 * @ClassName _MetaBuilder
 * @Coder lindy
 * @Date 2019/8/8 下午3:00
 * @Version 1.0
 **/
public class _MetaBuilder extends MetaBuilder {

    public _MetaBuilder(DataSource dataSource) {
        super(dataSource);
    }

    /**
     * 通过继承并覆盖此方法，跳过一些不希望处理的 table，定制更加灵活的 table 过滤规则
     * @return 返回 true 时将跳过当前 tableName 的处理
     * true : 不处理
     * false ：处理
     */
    @Override
    protected boolean isSkipTable(String tableName) {
        List<String> createTableList  = new ArrayList<>();
        //哪些表需要处理
        createTableList.add("72crm_crm_technology_project");
        for(String createTable : createTableList) {
            if(createTable.equals(tableName)) {
                return false;
            }
        }
        return true;
    }
}
