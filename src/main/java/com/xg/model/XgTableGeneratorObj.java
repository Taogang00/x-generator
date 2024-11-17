package com.xg.model;

import lombok.Data;

import java.util.List;

@Data
public class XgTableGeneratorObj {

    private String tableName;

    private String entityClassName;

    private String entityPackageName;

    private String entityPath;

    private String mapperClassName;

    private String mapperPackageName;

    private String mapperPath;

    private String mapXml;

    private String mapXmlPackageName;

    private String mapXmlPath;

    private String serviceClassName;

    private String servicePackageName;

    private String servicePath;

    private String serviceImplClassName;

    private String serviceImplPackageName;

    private String serviceImplPath;

    private String controllerClassName;

    private String controllerPackageName;

    private String controllerPath;

    private String dtoClassName;

    private String dtoPackageName;

    private String dtoPath;

    private String queryClassName;

    private String queryPackageName;

    private String queryPath;

    private String mapstructClassName;

    private String mapstructPackageName;

    private String mapstructPath;

    private List<XGTableFieldsGeneratorObj> fields;
}
