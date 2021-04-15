package com.sqn.seckill;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.po.TableInfo;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Title: AutoGeneratorCode
 * Description:代码自动生成器
 *
 * @author sqn
 * @version 1.0.0
 * @date 2020/3/21 0021 下午 7:03
 */
public class AutoGeneratorCode {

    /**
     * 开发人员
     */
    private static final String AUTHOR = "sqn";

    /**
     * 数据库连接配置
     */
    private static final String URL = "jdbc:mysql://localhost:3306/seckill?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=Asia/Shanghai";
    private static final String DRIVERNAME = "com.mysql.cj.jdbc.Driver";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";

    /**
     * 表名："t_category"，“t_test”...
     */
    private static final String[] TABELS = {"t_user"};

    /**
     * 模块名
     */
    private static final String MODULENAME = "user";

    /**
     * 父包模块名
     */
    private static final String PARENT = "com.sqn.seckill";

    public static void main(String[] args) throws Exception {
        //执行
        initAutoGenerator().execute();
    }

    /**
     * 初始化一个代码自动生成器对象
     * @return
     */
    private static AutoGenerator initAutoGenerator(){
        //
        AutoGenerator autoGenerator = new AutoGenerator();
        autoGenerator.setGlobalConfig(initGlobalConfig());
        autoGenerator.setDataSource(initDataSourceConfig());
        autoGenerator.setStrategy(initStrategyConfig());
        autoGenerator.setPackageInfo(initPackageConfig());
        //autoGenerator.setTemplate(initTemplateConfig());
        //autoGenerator.setCfg(initInjectionConfig());

        //模板配置，注意！如果您选择了非默认引擎，需要在 AutoGenerator 中 设置模板引擎。
        // set freemarker engine
        //autoGenerator.setTemplateEngine(new FreemarkerTemplateEngine());

        // set beetl engine
        //autoGenerator.setTemplateEngine(new BeetlTemplateEngine());

        // set custom engine (reference class is your custom engine class)
        //autoGenerator.setTemplateEngine(new CustomTemplateEngine());

        return autoGenerator;
    }

    /**
     * 1.全局配置
     * @return globalConfig
     */
    private static GlobalConfig initGlobalConfig() {

        GlobalConfig globalConfig = new GlobalConfig();
        //是否支持ActiveRecord模式
        globalConfig.setActiveRecord(false);
        //生成文件的输出目录
        globalConfig.setOutputDir(System.getProperty("user.dir") + "/src/main/java");
        //开发人员
        globalConfig.setAuthor(AUTHOR);
        //是否使用资源管理器打开输出目录
        globalConfig.setOpen(false);
        //是否覆盖原有文件
        globalConfig.setFileOverride(true);
        //是否支持swagger2
        globalConfig.setSwagger2(true);

        //实体命名方式
        //globalConfig.setEntityName("%sEntity");
        //mapper 命名方式
        //globalConfig.setMapperName("%sDao");
        //service 命名方式
        //globalConfig.setServiceName("%sBusiness");
        //service impl 命名方式
        //globalConfig.setServiceImplName("%sBusinessImpl");
        //controller 命名方式
        //globalConfig.setControllerName("%sAction");

        //去掉Service的I前缀
        globalConfig.setServiceName("%sService");
        //指定生成的主键的ID类型
        globalConfig.setIdType(IdType.AUTO);
        //时间类型对应策略
        globalConfig.setDateType(DateType.ONLY_DATE);

        //开启 BaseResultMap
        globalConfig.setBaseResultMap(true);
        //开启 baseColumnList
        globalConfig.setBaseColumnList(true);

        return globalConfig;
    }

    /**
     * 2.配置数据源
     * @return dataSourceConfig
     */
    private static DataSourceConfig initDataSourceConfig(){
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUrl(URL);
        dataSourceConfig.setDriverName(DRIVERNAME);
        dataSourceConfig.setUsername(USERNAME);
        dataSourceConfig.setPassword(PASSWORD);
        dataSourceConfig.setDbType(DbType.MYSQL);

        return dataSourceConfig;
    }

    /**
     * 3.数据库表配置，通过该配置，可指定需要生成哪些表或者排除哪些表
     * @return strategyConfig
     */
    private static StrategyConfig initStrategyConfig(){

        StrategyConfig strategyConfig = new StrategyConfig();
        //全局大写命名
        strategyConfig.setCapitalMode(true);
        //指定表名是否使用前缀
        strategyConfig.setTablePrefix("t_");
        //设置映射的表名,必填*
        strategyConfig.setInclude();
        //数据库表映射到实体的命名策略
        strategyConfig.setNaming(NamingStrategy.underline_to_camel);
        strategyConfig.setColumnNaming(NamingStrategy.underline_to_camel);

        //你自己的父类实体,没有就不用设置!
        //strategyConfig.setSuperEntityClass(Model.class);
        //strategyConfig.setSuperMapperClass("com.baomidou.mybatisplus.core.mapper.BaseMapper");
        //strategyConfig.setSuperServiceClass(IService.class);
        //strategyConfig.setSuperServiceImplClass(ServiceImpl.class);
        //strategyConfig.setSuperControllerClass(BaseController.class);
        // 写于父类中的公共字段
        //strategyConfig.setSuperEntityColumns("id");

        //默认激活进行sql模糊表名匹配
        strategyConfig.setEnableSqlFilter(true);

        //strategyConfig
        strategyConfig.setEntityLombokModel(true);

        //逻辑删除
        strategyConfig.setLogicDeleteFieldName("deleted");

        //自动填充字段
        TableFill createtime = new TableFill("createtime", FieldFill.INSERT);
        TableFill updatetime = new TableFill("updatetime", FieldFill.INSERT_UPDATE);
        List<TableFill> tableFills = new ArrayList<>();
        tableFills.add(createtime);
        tableFills.add(updatetime);
        strategyConfig.setTableFillList(tableFills);

        //乐观锁
        strategyConfig.setVersionFieldName("version");

        //生成 @RestController 控制器
        strategyConfig.setRestControllerStyle(true);

        //驼峰转连字符 地址风格：localhost:8080/hello_id_2
        strategyConfig.setControllerMappingHyphenStyle(true);

        //是否生成实体时，生成字段注解
        strategyConfig.setEntityTableFieldAnnotationEnable(true);

        return strategyConfig;
    }

    /**
     * 4.包的配置
     * @return packageConfig
     */
    private static PackageConfig initPackageConfig(){
        PackageConfig packageConfig = new PackageConfig();
        //父包模块名
        packageConfig.setModuleName(MODULENAME);
        packageConfig.setParent(PARENT);
        packageConfig.setEntity("entity");
        packageConfig.setMapper("mapper");
        packageConfig.setXml("mapper");
        packageConfig.setService("service");
        packageConfig.setServiceImpl("service.impl");
        packageConfig.setController("controller");

        return packageConfig;
    }

    /**
     * 5.初始化模板配置
     * @return templateConfig
     */
    private static TemplateConfig initTemplateConfig() {
        TemplateConfig templateConfig = new TemplateConfig();
        //使用自定义模板，不想要生成就设置为null,如果不设置null会使用默认模板
        templateConfig.setEntity("mybatisplustemplates/myentity.java.vm");
        templateConfig.setMapper("mybatisplustemplates/mymapper.java.vm");
        templateConfig.setService("mybatisplustemplates/myservice.java.vm");
        templateConfig.setServiceImpl("mybatisplustemplates/myserviceImpl.java.vm");
        templateConfig.setController("mybatisplustemplates/mycontroller.java.vm");
        templateConfig.setXml("mybatisplustemplates/mymapper.xml.vm");

        return templateConfig;
    }

    /**
     *  bug:html文件有些参数无法获取
     */
    private static InjectionConfig initInjectionConfig(){
        //AbstractTemplateEngine
        // 注入自定义配置，可以在 VM 中使用 cfg.abc 设置的值
        InjectionConfig injectionConfig = new InjectionConfig() {
            @Override
            public void initMap() {
                Map<String, Object> map = new HashMap<>();
                map.put("tool", this.getConfig().getGlobalConfig().getAuthor() + "-mp");
                this.setMap(map);
            }
        };


        // 自定义文件生成
        List<FileOutConfig> focList = new ArrayList<>();

        focList.add(new FileOutConfig("mybatisplustemplates/mymapper.xml.vm") {
            //生成xml
            @Override
            public String outputFile(TableInfo tableInfo) {
                // TODO Auto-generated method stub
                return "src/main/resources/mapper/" + tableInfo.getEntityPath() + "Mapper.xml";
            }
        });

        focList.add(new FileOutConfig("mybatisplustemplates/vue.html.vm") {
            //生成html
            @Override
            public String outputFile(TableInfo tableInfo) {
                // TODO Auto-generated method stub
                return "src/main/resources/static/page/" + tableInfo.getEntityPath() + ".html";
            }
        });

        injectionConfig.setFileOutConfigList(focList);

        return injectionConfig;
    }

}