package com.xg.model;

import lombok.Data;

@Data
public class XGGlobalInfo {

    private String author;
    private String dateTime;
    private String tablePrefix;
    private String codeGeneratorPath;
    private Boolean fileOverride;
    private Boolean ignoreTablePrefix;

    private String superControllerClass;
    private String superServiceClass;
    private String superServiceImplClass;
    private String superEntityClass;
    private String superMapperClass;
    private String superQueryClass;
    private String superDTOClass;

    private String superControllerClassPackageName;
    private String superServiceClassPackageName;
    private String superServiceImplClassPackageName;
    private String superEntityClassPackageName;
    private String superMapperClassPackageName;
    private String superQueryClassPackageName;
    private String superDTOClassPackageName;
}
