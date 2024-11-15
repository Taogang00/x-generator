package com.xg.model;

import lombok.Data;

import java.util.List;

/**
 * 表信息
 *
 * @author taogang
 * @date 2024/11/14
 */
@Data
public class TableInfo {

    /**
     * 表名
     */
    private String name;

    /**
     * 表注释
     */
    private String comment;

    /**
     * 列信息集合
     */
    private List<ColumnInfo> columnList;
}
