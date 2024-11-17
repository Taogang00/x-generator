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

    private String modulePackageName;
    private String controllerPackageName;
    private String entityPackageName;
    private String dtoPackageName;
    private String queryPackageName;
    private String mapstructPackageName;
    private String servicePackageName;
    private String serviceImplPackageName;
    private String mapperPackageName;
    private String mapperXmlPackage;

    private String superControllerClass;
    private String superServiceClass;
    private String superServiceImplClass;
    private String superEntityClass;
    private String superMapperClass;
    private String superQueryClass;
    private String superDTOClass;

    private String superControllerClassPackagePath;
    private String superServiceClassPackagePath;
    private String superServiceImplClassPackagePath;
    private String superEntityClassPackagePath;
    private String superMapperClassPackagePath;
    private String superQueryClassPackagePath;
    private String superDTOClassPackagePath;

    private Boolean generateController;
    private Boolean generateEntity;
    private Boolean generateService;
    private Boolean generateQuery;
    private Boolean generateMapStruct;
    private Boolean generateMapper;
    private Boolean generateMapperXml;
    private Boolean generateDTO;
}
