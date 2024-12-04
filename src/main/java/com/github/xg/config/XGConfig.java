package com.github.xg.config;

import cn.hutool.core.lang.Tuple;
import com.github.xg.model.XGTabInfo;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.Map;

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
     * 创建时间
     */
    private Date createTime;

    /**
     * 代码模板
     */
    private List<XGTabInfo> xgTabInfoList;

    /**
     * 数据库列与Java类型映射
     */
    private Map<String, Tuple> columnJavaTypeMapping;
}
