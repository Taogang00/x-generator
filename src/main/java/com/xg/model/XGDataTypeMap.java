package com.xg.model;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 数据库和Java对象类型映射
 *
 * @author taogang
 * @date 2024/11/18
 */
@Data
@AllArgsConstructor
public class XGDataTypeMap {

    /**
     * 数据库类型名称
     */
    private String columnDbTypeName;

    /**
     * Java类型名称
     */
    private String columnJavaTypeName;

    /**
     * Java类型类名全名
     */
    private String columnJavaTypeClass;
}
