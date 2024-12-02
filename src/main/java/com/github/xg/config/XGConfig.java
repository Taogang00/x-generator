package com.github.xg.config;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Tuple;
import com.github.xg.constant.XGConstants;
import com.github.xg.model.XGTabInfo;
import com.github.xg.utils.XGFreemarkerUtil;
import lombok.Data;

import java.util.ArrayList;
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
    private String createTime;

    /**
     * 数据库列与Java类型映射
     */
    private Map<String, Tuple> columnJavaTypeMapping;

    public static List<XGTabInfo> getTabList() {
        List<XGTabInfo> infoList = new ArrayList<>();
        if (CollUtil.isEmpty(infoList) || infoList.size() < 6) {
            infoList.add(0, new XGTabInfo(XGConstants.CONTROLLER, XGFreemarkerUtil.getTemplateContent("controller.java"), 1));
            infoList.add(0, new XGTabInfo(XGConstants.SERVICE, XGFreemarkerUtil.getTemplateContent("service.java"), 2));
            infoList.add(0, new XGTabInfo(XGConstants.SERVICE_IMPL, XGFreemarkerUtil.getTemplateContent("serviceImpl.java"), 3));
            infoList.add(0, new XGTabInfo(XGConstants.ENTITY, XGFreemarkerUtil.getTemplateContent("entity.java"), 4));
            infoList.add(0, new XGTabInfo(XGConstants.MAPPER, XGFreemarkerUtil.getTemplateContent("mapper.java"), 5));
            infoList.add(0, new XGTabInfo(XGConstants.XML, XGFreemarkerUtil.getTemplateContent("mapper.xml"), 6));
        }
        return infoList;
    }
}
