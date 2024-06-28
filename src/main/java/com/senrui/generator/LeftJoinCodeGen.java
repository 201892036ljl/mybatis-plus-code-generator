package com.senrui.generator;

import cn.hutool.core.text.CharSequenceUtil;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class LeftJoinCodeGen {
    private static JdbcTemplate jdbcTemplate;

    private static final String DATABASE_HOST = "101.132.155.127";
    private static final String DATABASE_PORT = "3306";
    private static final String DATABASE_USERNAME = "root";
    private static final String DATABASE_PASSWORD = "jlm200065";
    private static final String DATABASE_NAME = "cinema-auth";

    private static final String sql = "SELECT %s FROM %s";
    private static final String dbName = "cinema-auth";
    private static final String resultMap = "<resultMap id=\"\" type=\"\">%s</resultMap>";
    private static final String result = "<result column=\"%s\" property=\"%s\"></result>";

    static {
        //初始化jdbcTemplate
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl(String.format("jdbc:mysql://%s:%s/%s?zeroDateTimeBehavior=convertToNull&useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai&autoReconnect=true", DATABASE_HOST, DATABASE_PORT, DATABASE_NAME));
        dataSource.setUsername(DATABASE_USERNAME);
        dataSource.setPassword(DATABASE_PASSWORD);

        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public static void main(String[] args) {
        //根据表名生成左连接
        //用法：主表, 子表1, 子表2 ...
//        leftJoinCreate("product_package_instance","product_instance","product_instance_attribute", "product_package", "product_category", "product_lib","product_category_attribute");
    }
    private static void leftJoinCreate(String... args){
        String mainTable = args[0];
        StringBuffer columnStr = new StringBuffer();
        StringBuffer tableStr = new StringBuffer();
        StringBuffer resultsStr = new StringBuffer();
        List<Map<String, Object>> fks = jdbcTemplate.queryForList("SELECT TABLE_NAME, COLUMN_NAME, CONSTRAINT_NAME, REFERENCED_TABLE_NAME, REFERENCED_COLUMN_NAME  FROM INFORMATION_SCHEMA.KEY_COLUMN_USAGE WHERE CONSTRAINT_SCHEMA = ?  AND TABLE_NAME = ?  AND CONSTRAINT_NAME != 'PRIMARY';",
                dbName, mainTable);
        Map<Object, Map<String, Object>> fkTableMap = fks.stream().collect(Collectors.toMap(k -> k.get("REFERENCED_TABLE_NAME"), v -> v, (k1, k2) -> k2));
        for (int index= 0;index<args.length; index++){
            String tableName = args[index];
            List<Map<String, Object>> maps = jdbcTemplate.queryForList("select COLUMN_NAME from information_schema.COLUMNS where table_name = ?;", tableName);

            for (Map<String,Object> map: maps){
                columnStr.append(String.format(" %s.`%s` `%s_%s`, ", tableName, map.get("COLUMN_NAME"), tableName, map.get("COLUMN_NAME")));
                if (index == 0){
                    //主表
                    resultsStr.append(String.format(
                            result,
                            tableName+"_"+ map.get("COLUMN_NAME"),
                            CharSequenceUtil.toCamelCase(map.get("COLUMN_NAME").toString())));
                }else {
                    resultsStr.append(String.format(
                            result,
                            tableName+"_"+ map.get("COLUMN_NAME"),
                            CharSequenceUtil.toCamelCase(tableName)+"."+CharSequenceUtil.toCamelCase(map.get("COLUMN_NAME").toString())));
                }
            }
            Map<String, Object> fk = fkTableMap.get(tableName);
            String onStr = "";
            if (fk != null){
                onStr = String.format(" ON %s.`%s` = %s.`%s`", mainTable, fk.get("COLUMN_NAME"), fk.get("REFERENCED_TABLE_NAME"), fk.get("REFERENCED_COLUMN_NAME"));
            }
            tableStr.append(String.format(" LEFT JOIN %s %s",tableName, onStr));
        }
        columnStr.deleteCharAt(columnStr.length() - 2);
        tableStr = new StringBuffer(tableStr.toString().replaceFirst("LEFT JOIN", ""));
        System.out.println(String.format(sql,columnStr.toString(),tableStr.toString()));
        System.out.println(String.format(resultMap,resultsStr.toString()));
    }

}
