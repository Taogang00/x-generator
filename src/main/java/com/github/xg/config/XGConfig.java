package com.github.xg.config;

import lombok.Data;

@Data
public class XGConfig {
    /**
     * 下拉配置项名字
     */
    private String name;

    /**
     * 是否是默认选项
     */
    private Boolean isDefault;

    /**
     * 作者
     */
    private String author;
}
