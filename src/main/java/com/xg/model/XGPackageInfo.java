package com.xg.model;

import lombok.Data;

@Data
public class XGPackageInfo {

    /**
     * 源码路径
     */
    private final String sourcePath = "src.main.java";

    /**
     * 资源路径
     */
    private final String resourcePath = "src.main.resources";

    /**
     * 父包模块名。
     */
    private String modulePackageName;

    /**
     * Controller包名
     */
    private String controllerPackageName;

    /**
     * Entity包名
     */
    private String entityPackageName;

    /**
     * EntityDTO包名
     */
    private String dtoPackageName;

    /**
     * query
     */
    private String queryPackageName;

    /**
     * mapstruct包名
     */
    private String mapstructPackageName;

    /**
     * Service包名
     */
    private String servicePackageName;

    /**
     * Service Impl包名
     */
    private String serviceImplPackageName;

    /**
     * Mapper包名
     */
    private String mapperPackageName;

    /**
     * Mapper XML包名
     */
    private String mapperXmlPackage = "mapper";
}
