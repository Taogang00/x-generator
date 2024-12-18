package com.github.xg.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class XGTabInfo {

    /**
     * 类型
     */
    private String type;

    /**
     * 内容
     */
    private String content;

    /**
     * 顺序号
     */
    private Integer orderNo;
}
