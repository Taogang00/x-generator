package com.xg.model;

import lombok.Data;

@Data
public class XGGlobalInfo {

    private final String sourcePath = "src.main.java";
    private final String resourcePath = "src.main.resources";

    private String author;
    private String dateTime;
    private String tablePrefix;
    private Boolean ignoreTablePrefix;
    private String codeGeneratorPath;
    private Boolean fileOverride;

    private String modulePackagePath;
    private String controllerPackagePath;
    private String entityPackagePath;
    private String dtoPackagePath;
    private String queryPackagePath;
    private String mapstructPackagePath;
    private String servicePackagePath;
    private String serviceImplPackagePath;
    private String mapperPackagePath;
    private String mapperXmlPackagePath;

    private String superControllerClass;
    private String superServiceClass = "MBaseService";
    private String superServiceImplClass = "MBaseServiceImpl";
    private String superEntityClass;
    private String superMapperClass = "MBaseMapper";
    private String superQueryClass;
    private String superDTOClass;

    private String superControllerClassPackagePath;
    private String superServiceClassPackagePath = "com.guanwei.mybatis.base.service";
    private String superServiceImplClassPackagePath = "com.guanwei.mybatis.base.service";
    private String superEntityClassPackagePath;
    private String superMapperClassPackagePath = "com.guanwei.mybatis.base.mapper";
    private String superQueryClassPackagePath;
    private String superDTOClassPackagePath;

    private Boolean generateController = true;
    private Boolean generateEntity = true;
    private Boolean generateService = true;
    private Boolean generateQuery = true;
    private Boolean generateMapStruct = true;
    private Boolean generateMapper = true;
    private Boolean generateMapperXml = true;
    private Boolean generateDTO = true;

    private String outputControllerPath;
    private String outputEntityPath;
    private String outputServicePath;
    private String outputServiceImplPath;
    private String outputQueryPath;
    private String outputMapStructPath;
    private String outputMapperPath;
    private String outputMapperXmlPath;
    private String outputDTOPath;
}
