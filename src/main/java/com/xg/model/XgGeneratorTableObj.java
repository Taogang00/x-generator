package com.xg.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.util.List;

@Data
public class XgGeneratorTableObj {

    private String tableName;

    private String tableComment;

    private String controllerMapping;

    private String entityClassName;

    private String entityPackagePath;

    private String entityAbsolutePath;

    private String mapperClassName;

    private String mapperPackagePath;

    private String mapperAbsolutePath;

    private String mapperXml;

    private String mapperXmlPackagePath;

    private String mapperXmlAbsolutePath;

    private String serviceClassName;

    private String servicePackagePath;

    private String serviceAbsolutePath;

    private String serviceImplClassName;

    private String serviceImplPackagePath;

    private String serviceImplAbsolutePath;

    private String controllerClassName;

    private String controllerPackagePath;

    private String controllerAbsolutePath;

    private String dtoClassName;

    private String dtoPackagePath;

    private String dtoAbsolutePath;

    private String queryClassName;

    private String queryPackagePath;

    private String queryAbsolutePath;

    private String mapstructClassName;

    private String mapstructPackagePath;

    private String mapstructAbsolutePath;

    private List<XGGeneratorTableFieldsObj> tableFields;

    @SuppressWarnings("unused")
    public String getControllerMapping() {
        return StrUtil.toUnderlineCase(this.getEntityClassName())
                .replace("_", "-")
                .toLowerCase();
    }
}
