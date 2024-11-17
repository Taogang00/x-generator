package com.xg.model;

import lombok.Data;

import java.util.List;

@Data
public class XgGeneratorTableObj {

    private String tableName;

    private String tableComment;

    private String entityClassName;

    private String entityPackagePath;

    private String entityPath;

    private String mapperClassName;

    private String mapperPackagePath;

    private String mapperPath;

    private String mapXml;

    private String mapXmlPackagePath;

    private String mapXmlPath;

    private String serviceClassName;

    private String servicePackagePath;

    private String servicePath;

    private String serviceImplClassName;

    private String serviceImplPackagePath;

    private String serviceImplPath;

    private String controllerClassName;

    private String controllerPackagePath;

    private String controllerPath;

    private String dtoClassName;

    private String dtoPackagePath;

    private String dtoPath;

    private String queryClassName;

    private String queryPackagePath;

    private String queryPath;

    private String mapstructClassName;

    private String mapstructPackagePath;

    private String mapstructPath;

    private List<XGGeneratorTableFieldsObj> fields;
}
