package com.xg.model;

import lombok.Data;

/**
 * 列信息
 *
 * @author taogang
 * @date 2024/11/14
 */
@Data
public class XGXmlElementColumnInfo {

    public static final String XML_ELEMENT_COLUMN_NAME = "Column";
    public static final String XML_ELEMENT_COLUMN_ATTRIBUTE_NAME = "Name";
    public static final String XML_ELEMENT_COLUMN_ATTRIBUTE_TEXT = "Text";
    public static final String XML_ELEMENT_COLUMN_ATTRIBUTE_PRIMARY_KEY = "PrimaryKey";
    public static final String XML_ELEMENT_COLUMN_ATTRIBUTE_DATATYPE = "DataType";

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
     * 列注释
     */
    private String comment;

    /**
     * 是主键
     */
    private Boolean primaryKey;
}