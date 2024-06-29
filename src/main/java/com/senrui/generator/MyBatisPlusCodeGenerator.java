package com.senrui.generator;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.core.toolkit.ArrayUtils;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.InjectionConfig;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.config.po.TableFill;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.*;
import java.util.stream.Collectors;

public class MyBatisPlusCodeGenerator {

    protected static final Logger logger = LoggerFactory.getLogger(MyBatisPlusCodeGenerator.class);


    /**
     * Controller url前缀,不需要以/开头，会自动添加开头的/
     */
    private static final String CONTROLLER_URL_PREFIX = "dataAdmin";
    /**
     * 类前缀
     */
    private static final String CLASS_PREFIX = "DataAdmin";
    private static final String URL_PATTERN = "jdbc:mysql://%s:%s/%s?zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&autoReconnect=true";
    private static final String PROJECT_PATH = System.getProperty("user.dir");
    //    private static final String TEMPLATE_PATH = "/templates/mapper.xml.ftl";
    private static final String OUTPUT_DIR = "src/main/java";

    private String packageName;
    private String databaseName;
    private String databaseUsername;
    private String databasePassword;
    private String databaseUrl;
    private boolean fileOverride;
    private String[] include;
    private String[] exclude;
    private boolean ignorePo;
    private Configuration configuration;
    private JdbcTemplate jdbcTemplate;
    private Map<String, String> enumMap = new HashMap<>();
    private Map<String, Object> poXmlData = new HashMap<>();
    private Map<String, List<Map<String, Object>>> tableFieldListMap = new HashMap<>();

    private MyBatisPlusCodeGenerator() {
    }

    public static class ConfigBuilder {

        private String dbHost;
        private String dbName;
        private String dbPort;
        private String userName;
        private String password;
        private String packageName;
        private boolean fileOverride;
        private String[] include;
        private String[] exclude;
        private boolean ignorePo;


        public ConfigBuilder dbHost(final String dbHost) {
            this.dbHost = dbHost;
            return this;
        }

        public ConfigBuilder dbName(final String dbName) {
            this.dbName = dbName;
            return this;
        }

        public ConfigBuilder dbPort(final String dbPort) {
            this.dbPort = dbPort;
            return this;
        }

        public ConfigBuilder userName(final String userName) {
            this.userName = userName;
            return this;
        }

        public ConfigBuilder password(final String password) {
            this.password = password;
            return this;
        }

        public ConfigBuilder packageName(final String packageName) {
            this.packageName = packageName;
            return this;
        }

        public ConfigBuilder fileOverride(final boolean fileOverride){
            this.fileOverride = fileOverride;
            return this;
        }

        public ConfigBuilder include(final String... include){
            this.include = include;
            return this;
        }

        public ConfigBuilder exclude(final String... exclude){
            this.exclude = exclude;
            return this;
        }

        public ConfigBuilder ignorePo(final boolean ignorePo){
            this.ignorePo = ignorePo;
            return this;
        }


        public MyBatisPlusCodeGenerator build() {
            MyBatisPlusCodeGenerator generator = new MyBatisPlusCodeGenerator();
            String databaseHost = Optional.of(this.dbHost).get();
            generator.databaseName = Optional.of(this.dbName).get();
            String databasePort = Optional.of(this.dbPort).get();
            generator.databaseUsername = Optional.of(this.userName).get();
            generator.databasePassword = Optional.of(this.password).get();
            generator.packageName = Optional.of(this.packageName).get();
            generator.fileOverride = this.fileOverride;
            generator.include = this.include;
            generator.exclude = this.exclude;
            generator.ignorePo = this.ignorePo;
            String dbUrl = String.format(URL_PATTERN, databaseHost, databasePort, generator.databaseName);
            generator.databaseUrl = dbUrl;
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            dataSource.setUrl(dbUrl);
            dataSource.setUsername(generator.databaseUsername);
            dataSource.setPassword(generator.databasePassword);
            generator.jdbcTemplate = new JdbcTemplate(dataSource);
            Configuration configuration = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
            configuration.setDefaultEncoding(ConstVal.UTF8);
            configuration.setClassForTemplateLoading(FreemarkerTemplateEngine.class, StringPool.SLASH);
            generator.configuration = configuration;
            return generator;
        }
    }

    public void registerEnum(String filedName, String enumClassName) {
        this.enumMap.put(filedName, enumClassName);
    }


    public void generate() {
        if (!ignorePo) {
            generatPoCode();
        }
        generateCode();

    }

    /**
     * 生成代码
     */
    public void generateCode() {
        AutoGenerator generator = new AutoGenerator();
        generator.setDataSource(dataSourceConfig());
        generator.setGlobalConfig(globalConfig());
        generator.setPackageInfo(packageConfig());
        generator.setStrategy(strategyConfig());
        generator.setTemplateEngine(new FreemarkerTemplateEngine());
        generator.setTemplate(templateConfig());
        generator.setCfg(customerCfg().setFileOutConfigList(fileOutConfig()));
        generator.execute();
    }

    /**
     * 数据源配置
     */
    private DataSourceConfig dataSourceConfig() {
        DataSourceConfig dataSourceConfig = new DataSourceConfig();
        dataSourceConfig.setUrl(databaseUrl);
        dataSourceConfig.setDriverName("com.mysql.cj.jdbc.Driver");
        dataSourceConfig.setUsername(databaseUsername);
        dataSourceConfig.setPassword(databasePassword);
        dataSourceConfig.setKeyWordsHandler(new MySqlKeyWordsHandler());

        return dataSourceConfig;
    }

    /**
     * 全局配置
     */
    private GlobalConfig globalConfig() {
        GlobalConfig globalConfig = new GlobalConfig();
        globalConfig.setOutputDir(OUTPUT_DIR);
        globalConfig.setFileOverride(fileOverride);
        globalConfig.setAuthor("auto gen");
        globalConfig.setOpen(false);
        globalConfig.setSwagger2(true);
        globalConfig.setIdType(IdType.ASSIGN_ID);
        globalConfig.setServiceName(CLASS_PREFIX + "%sService");
        globalConfig.setServiceImplName(CLASS_PREFIX + "%sServiceImpl");
        globalConfig.setMapperName(CLASS_PREFIX + "%sMapper");
        globalConfig.setControllerName(CLASS_PREFIX + "%sController");
        globalConfig.setXmlName(CLASS_PREFIX + "%sMapper");
        globalConfig.setBaseResultMap(true);
        globalConfig.setBaseColumnList(true);
        return globalConfig;
    }

    /**
     * 包配置
     */
    private PackageConfig packageConfig() {
        PackageConfig packageConfig = new PackageConfig();
        packageConfig.setParent(packageName);
        return packageConfig;
    }

    /**
     * 自定义模板配置
     */
    private TemplateConfig templateConfig() {
        TemplateConfig templateConfig = new TemplateConfig();
        templateConfig.disable(TemplateType.XML);
        return templateConfig;
    }

    /**
     * 策略配置
     */
    private StrategyConfig strategyConfig() {
        StrategyConfig strategyConfig = new StrategyConfig();
        strategyConfig.setNaming(NamingStrategy.underline_to_camel);
        strategyConfig.setColumnNaming(NamingStrategy.underline_to_camel);
        strategyConfig.setEntityLombokModel(true);
        strategyConfig.setRestControllerStyle(true);
        strategyConfig.setInclude(include);
        strategyConfig.setExclude(exclude);
        strategyConfig.setEntitySerialVersionUID(false);
        strategyConfig.setTablePrefix("s_", "r_", "t_", "d_");
        strategyConfig.setTableFillList(Arrays.asList(
                new TableFill("create_time", FieldFill.INSERT),
                new TableFill("update_time", FieldFill.INSERT_UPDATE)
        ));
        return strategyConfig;
    }

    /**
     * 自定义配置信息
     */
    private InjectionConfig customerCfg() {
        return new InjectionConfig() {
            @Override
            public void initMap() {
                Map<String, Object> map = new HashMap<>();
                map.put("enumConfig", enumMap);
                map.put("controllerUrlPrefix", CONTROLLER_URL_PREFIX);
                map.put("poPackage", packageName +".po");
                this.setMap(map);
                map.put("poXmlData", poXmlData);
                map.putAll(tableFieldListMap);

            }
        };
    }

    private List<FileOutConfig> fileOutConfig() {
        // 自定义输出配置
        List<FileOutConfig> focList = new ArrayList<>();
        // 自定义配置会被优先输出
        /*focList.add(new FileOutConfig(TEMPLATE_PATH) {
            @Override
            public String outputFile(TableInfo tableInfo) {
                // 自定义输出文件名 ， 如果你 Entity 设置了前后缀、此处注意 xml 的名称会跟着发生变化！！
                return PROJECT_PATH + "/src/main/resources/mapper/" + CLASS_PREFIX + tableInfo.getEntityName() + "Mapper" + StringPool.DOT_XML;
            }
        });*/
        return focList;
    }

    /**
     * 生成Po代码
     */
    private void generatPoCode() {
        generatPoCodeByTable();

    }

    private void generatPoCodeByTable() {
        List<String> tableList;
        List<Map<String, Object>> tableMap;
        if (ArrayUtils.isEmpty(include)) {
            tableMap = jdbcTemplate.queryForList("SELECT * FROM information_schema.`TABLES` WHERE TABLE_SCHEMA = ?;"
                    , databaseName);
            tableList = tableMap.stream().map(k -> k.get("TABLE_NAME").toString()).collect(Collectors.toList());

        } else {
            tableList = Arrays.asList(include);

            tableMap = jdbcTemplate.queryForList("SELECT * FROM information_schema.`TABLES` WHERE TABLE_SCHEMA = ? AND `TABLE_NAME` IN  ("+tableList.stream().map(k -> "'"+k+"'").collect(Collectors.joining(","))+");"
                    , databaseName);
            tableList = tableMap.stream().map(k -> k.get("TABLE_NAME").toString()).collect(Collectors.toList());
        }
        Template template = null;
        try {
            String templatePath = "/templates/entityPo.java.ftl";
            template = configuration.getTemplate(templatePath);
            String packageName = this.packageName + ".po";
            File dir = new File(joinPath(PROJECT_PATH + "/src/main/java", packageName));

            for (int index = 0;index< tableList.size();index++) {
                String table = tableList.get(index);
                String tableComment = tableMap.get(index).get("TABLE_COMMENT")+"";
                Map<String, Object> map = new HashMap<>();
                map.put("package", packageName);
                map.put("tableComment", tableComment);
                if (!entityPoData(map, table)) {
                    continue;
                }
                String clsName = toUpStr(table)+"Po";
                String outputFile = joinPath(PROJECT_PATH + "/src/main/java", packageName+"."+clsName)+".java";
                if (!dir.exists()){
                    dir.mkdirs();
                }
                try (FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {

                    template.process(map, new OutputStreamWriter(fileOutputStream, ConstVal.UTF8));
                    logger.debug("模板:" + templatePath + ";  文件:" + outputFile);

                }
            }

        } catch (IOException e) {
            throw new RuntimeException("io异常", e);
        } catch (TemplateException e) {
            throw new RuntimeException("模板异常", e);
        }


    }
    private static String toUpStr(String str){
        StringBuffer stringBuffer = new StringBuffer(CharSequenceUtil.toCamelCase(str));
        stringBuffer.replace(0,1,Character.toUpperCase(stringBuffer.charAt(0))+"");
        return stringBuffer.toString();
    }
    /**
     * 连接路径字符串
     *
     * @param parentDir   路径常量字符串
     * @param packageName 包名
     * @return 连接后的路径
     */
    private static String joinPath(String parentDir, String packageName) {
        if (StringUtils.isBlank(parentDir)) {
            parentDir = System.getProperty(ConstVal.JAVA_TMPDIR);
        }
        if (!StringUtils.endsWith(parentDir, File.separator)) {
            parentDir += File.separator;
        }
        packageName = packageName.replaceAll("\\.", StringPool.BACK_SLASH + File.separator);
        return parentDir + packageName;
    }

    private boolean entityPoData(Map<String, Object> dataMap, String mainTable) {
        List<String> tableList = new ArrayList<>();
        //外键字段
        List<Map<String, Object>> fields;
        List<String> importPackages;
        tableList.add(mainTable);
        String dbName = databaseName;
        String mainClsName = toUpStr(mainTable)+"Po";
        List<Map<String, Object>> fks = jdbcTemplate.queryForList("SELECT TABLE_NAME, COLUMN_NAME, CONSTRAINT_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME  FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE CONSTRAINT_SCHEMA = ?  AND TABLE_NAME = ? ;",
                dbName, mainTable).stream().filter(k -> k.get("TABLE_NAME") != null
                && k.get("COLUMN_NAME") != null
                && k.get("REFERENCED_TABLE_NAME") != null
                && k.get("REFERENCED_COLUMN_NAME") != null).collect(Collectors.toList());
        if (fks.isEmpty()) {
            poXmlData.put(mainClsName+":"+"poXmlDataFlag",false);
            return false;
        }
        poXmlData.put(mainClsName+":"+"poXmlDataFlag",true);
        tableList.addAll(fks.stream().map(k -> k.get("REFERENCED_TABLE_NAME").toString()).collect(Collectors.toList()));

        fields = fks.stream().map(k -> {
            Map<String, Object> field = new HashMap<>();
            StringBuffer cname = new StringBuffer(k.get("COLUMN_NAME").toString());
            cname.delete(cname.length() - 3, cname.length());
            field.put("propertyName", CharSequenceUtil.toCamelCase(cname.toString()));
            field.put("propertyTableName",cname.toString());
            field.put("propertyType", toUpStr(CharSequenceUtil.toCamelCase(k.get("REFERENCED_TABLE_NAME").toString())));
            field.put("tableInfo", k);
            return field;
        }).collect(Collectors.toList());
        importPackages = tableList.stream().map(k -> this.packageName + ".entity." + toUpStr(CharSequenceUtil.toCamelCase(k))).collect(Collectors.toList());
        List<Map<String, Object>> fieldList = new ArrayList<>();

        for (Map<String, Object> field: fields){

            List<Map<String, Object>> joinTableMapList = new ArrayList<>();

            Map<String, Object> tableMap = (Map<String, Object>) field.get("tableInfo");
            String tableName = (String) tableMap.get("REFERENCED_TABLE_NAME");
            List<Map<String, Object>> maps = jdbcTemplate.queryForList("select COLUMN_NAME from information_schema.COLUMNS where table_schema = ? and table_name = ?;",databaseName, tableName);
            for (Map<String,Object> map: maps){

                Map<String, Object> fieldInfo = new HashMap<>();
                fieldInfo.put("name", map.get("COLUMN_NAME").toString());
                fieldInfo.put("propertyName", StrUtil.toCamelCase(map.get("COLUMN_NAME").toString()));
                joinTableMapList.add(fieldInfo);

            }

            Map<String, Object> joinTableInfo = new HashMap<>();
            joinTableInfo.put("propertyName", field.get("propertyName"));
            joinTableInfo.put("name", field.get("propertyTableName"));
            joinTableInfo.put("fields", joinTableMapList);
            fieldList.add(joinTableInfo);
        }

        tableFieldListMap.put(mainTable + ":fields", fieldList);
        dataMap.put("fields", fields);
        dataMap.put("importPackages", importPackages);
        dataMap.put("entity", toUpStr(CharSequenceUtil.toCamelCase(mainTable)));
        return true;
    }





}