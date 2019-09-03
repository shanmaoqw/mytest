package com.kakarote.crm9.utils;

import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.dialect.MysqlDialect;
import com.jfinal.plugin.activerecord.generator.Generator;
import com.jfinal.plugin.druid.DruidPlugin;
import com.kakarote.crm9.common.config.JfinalConfig;
import com.kakarote.crm9.erp._MetaBuilder;

import javax.sql.DataSource;

/**
 * @author wyq
 * 自动生成 Model、BaseModel、MappingKit、DataDictionary 四类文件
 */
public class _JFinalGenerator {
    public static DataSource getDataSource() {
        DruidPlugin druidPlugin = JfinalConfig.createDruidPlugin();
        druidPlugin.start();
        return druidPlugin.getDataSource();
    }

    public static void main(String[] args) {
        // base model 所使用的包名
        String baseModelPackageName = "com.kakarote.crm9.erp.crm.entity.base";
        // base model 文件保存路径
        String baseModelOutputDir = PathKit.getWebRootPath() + "/src/main/java/com/kakarote/crm9/erp/crm/entity/base";

        // model 所使用的包名 (MappingKit 默认使用的包名)
        String modelPackageName = "com.kakarote.crm9.erp.crm.entity";
        // model 文件保存路径 (MappingKit 与 DataDictionary 文件默认保存路径)
        String modelOutputDir = baseModelOutputDir + "/..";

        // 创建生成器
        Generator generator = new Generator(getDataSource(), baseModelPackageName, baseModelOutputDir, modelPackageName, modelOutputDir);
        // 设置数据库方言
        generator.setDialect(new MysqlDialect());
        // 设置是否生成链式 setter 方法
        generator.setGenerateChainSetter(false);

        // 添加不需要生成的表名,不添加的将全部生成,其对应的方法是：public void addExcludedTable(String... excludedTables)'
//        generator.addExcludedTable("adv");
        //添加需要生成的表名,用于指定生成哪些表的model
        generator.setMetaBuilder(new _MetaBuilder(getDataSource()));

        // 设置是否在 Model 中生成 dao 对象
        generator.setGenerateDaoInModel(true);
        // 设置是否生成字典文件
        generator.setGenerateDataDictionary(false);
        // 设置需要被移除的表名前缀用于生成modelName。例如表名 "osc_user"，移除前缀 "osc_"后生成的model名为 "User"而非 OscUser
        generator.setRemovedTableNamePrefixes("72crm");
        // 生成
        generator.generate();
    }
}
