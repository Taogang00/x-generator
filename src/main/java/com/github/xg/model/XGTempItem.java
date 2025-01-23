package com.github.xg.model;

import lombok.Data;

@Data
public class XGTempItem {

    /**
     * 名称
     */
    private String name;

    /**
     * 内容
     */
    private String content;

    /**
     * 包名
     */
    private String packageName;

    /**
     * 顺序号
     */
    private Integer orderNo;

    public XGTempItem(String name, String content, Integer orderNo) {
        this.name = name;
        this.content = content;
        this.orderNo = orderNo;
        this.packageName = name.toLowerCase();
    }

    public XGTempItem(String name, String content, String packageName, Integer orderNo) {
        this.name = name;
        this.content = content;
        this.packageName = packageName;
        this.orderNo = orderNo;
    }
}
