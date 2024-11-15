package com.xg.model;

import lombok.Data;

/**
 * 列信息
 *
 * @author taogang
 * @date 2024/11/14
 */
@Data
public class XGColumnInfo {

    /**
     * 列名
     */
    private String name;

    /**
     * 字段名
     */
    private String fieldName;

    /**
     * 数据类型-数据库
     */
    private String fieldType;

    /**
     * 数据类型-java
     */
    private String fieldJavaType;

    /**
     * 列注释
     */
    private String comment;

    /**
     * 是主键
     */
    private boolean primaryKey;
}
