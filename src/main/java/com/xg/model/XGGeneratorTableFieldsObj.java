package com.xg.model;

import lombok.Data;

/**
 * 列信息
 *
 * @author taogang
 * @date 2024/11/14
 */
@Data
public class XGGeneratorTableFieldsObj {

    /**
     * 字段名
     */
    private String propertyName;

    /**
     * 数据类型-java
     */
    private String propertyType;

    /**
     * 数据类路径
     */
    private String propertyClassPath;

    /**
     * 列注释
     */
    private String comment;

    /**
     * 是主键
     */
    private Boolean primaryKey;
}