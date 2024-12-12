package com.github.xg.config;

import cn.hutool.core.date.DateUtil;
import com.github.xg.model.XGTabInfo;
import lombok.Data;

import java.util.*;

import static com.github.xg.constant.XGConstants.*;
import static com.github.xg.utils.XGTemplateUtil.getTemplateContent;

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
     * 代码模板
     */
    private List<XGTabInfo> xgTabInfoList;

    /**
     * 数据库列与Java类型映射
     */
    private Map<String, String> columnJavaTypeMapping;

    /**
     * 初始化默认的配置内容
     */
    public static void initXGDefaultTemplateManager() {
        XGSettingManager.State state = XGSettingManager.getInstance().getState();
        if (state != null) {
            //为空的状态，表示配置为空，本地没相关配置
            if (state.getXgConfigs() == null) {
                List<XGConfig> list = new ArrayList<>();
                //第一种，作者公司默认的配置
                XGConfig authorXGConfig = new XGConfig();
                authorXGConfig.setCreateTime(DateUtil.formatDateTime(new Date()));
                authorXGConfig.setIsDefault(true);
                authorXGConfig.setName(TEMPLATE_GUANWEI);
                authorXGConfig.setXgTabInfoList(initGuanWeiXgTableInfo());
                authorXGConfig.setColumnJavaTypeMapping(initColumnJavaTypeMapping());
                list.add(authorXGConfig);

                //第二种，mybatisPlus配置
                XGConfig mpXGConfig = new XGConfig();
                mpXGConfig.setCreateTime(DateUtil.formatDateTime(new Date()));
                mpXGConfig.setIsDefault(false);
                mpXGConfig.setName(TEMPLATE_MYBATIS_PLUS);
                mpXGConfig.setXgTabInfoList(initMybatisPlusXgTableInfo());
                mpXGConfig.setColumnJavaTypeMapping(initColumnJavaTypeMapping());
                list.add(mpXGConfig);

                state.setXgConfigs(list);
                XGSettingManager.getInstance().loadState(state);
            } else {
                //不为空，检查相关的配置是否有（后续变动新增的配置项），如果没有，就添加
                for (XGConfig xgConfig : state.getXgConfigs()) {
                    Map<String, String> xgConfigColumnJavaTypeMapping = xgConfig.getColumnJavaTypeMapping();
                    if (xgConfigColumnJavaTypeMapping == null) {
                        xgConfig.setColumnJavaTypeMapping(initColumnJavaTypeMapping());
                    }
                }
            }
        }
    }

    /**
     * 重置选择的指定的配置的代码模板配置内容
     */
    public static void resetSelectedConfigXgTabInfo(String selectedName) {
        XGSettingManager.State state = XGSettingManager.getInstance().getState();
        assert state != null;
        List<XGConfig> xgConfigs = state.getXgConfigs();
        for (XGConfig xgConfig : xgConfigs) {
            if (xgConfig.getName().equals(selectedName)) {
                if (TEMPLATE_GUANWEI.equals(selectedName)) {
                    xgConfig.setXgTabInfoList(initGuanWeiXgTableInfo());
                }
                if (TEMPLATE_MYBATIS_PLUS.equals(selectedName)) {
                    xgConfig.setXgTabInfoList(initMybatisPlusXgTableInfo());
                }
            }
        }
        state.setXgConfigs(xgConfigs);
        XGSettingManager.getInstance().loadState(state);
    }

    /**
     * 初始化南京观为公司代码模板配置
     */
    public static List<XGTabInfo> initGuanWeiXgTableInfo() {
        List<XGTabInfo> guanweiXGTabInfoList = new ArrayList<>();
        guanweiXGTabInfoList.add(new XGTabInfo(CONTROLLER, getTemplateContent("/template/guanwei", "controller.java"), 1));
        guanweiXGTabInfoList.add(new XGTabInfo(SERVICE, getTemplateContent("/template/guanwei", "service.java"), 2));
        guanweiXGTabInfoList.add(new XGTabInfo(SERVICE_IMPL, getTemplateContent("/template/guanwei", "serviceImpl.java"), 3));
        guanweiXGTabInfoList.add(new XGTabInfo(ENTITY, getTemplateContent("/template/guanwei", "entity.java"), 4));
        guanweiXGTabInfoList.add(new XGTabInfo(MAPPER, getTemplateContent("/template/guanwei", "mapper.java"), 5));
        guanweiXGTabInfoList.add(new XGTabInfo(XML, getTemplateContent("/template/guanwei", "mapper.xml"), 6));
        guanweiXGTabInfoList.add(new XGTabInfo(QUERY, getTemplateContent("/template/guanwei", "query.java"), 7));
        guanweiXGTabInfoList.add(new XGTabInfo(DTO, getTemplateContent("/template/guanwei", "dto.java"), 8));
        guanweiXGTabInfoList.add(new XGTabInfo(MAPSTRUCT, getTemplateContent("/template/guanwei", "mapstruct.java"), 9));
        return guanweiXGTabInfoList;
    }

    /**
     * 初始化MybatisPlus代码模板配置
     */
    public static List<XGTabInfo> initMybatisPlusXgTableInfo() {
        List<XGTabInfo> mpXGTabInfoList = new ArrayList<>();
        mpXGTabInfoList.add(new XGTabInfo(CONTROLLER, getTemplateContent("/template/mybatisplus", "controller.java"), 1));
        mpXGTabInfoList.add(new XGTabInfo(SERVICE, getTemplateContent("/template/mybatisplus", "service.java"), 2));
        mpXGTabInfoList.add(new XGTabInfo(SERVICE_IMPL, getTemplateContent("/template/mybatisplus", "serviceImpl.java"), 3));
        mpXGTabInfoList.add(new XGTabInfo(ENTITY, getTemplateContent("/template/mybatisplus", "entity.java"), 4));
        mpXGTabInfoList.add(new XGTabInfo(MAPPER, getTemplateContent("/template/mybatisplus", "mapper.java"), 5));
        mpXGTabInfoList.add(new XGTabInfo(XML, getTemplateContent("/template/mybatisplus", "mapper.xml"), 6));
        return mpXGTabInfoList;
    }

    /**
     * 初始化数据库类型映射
     */
    public static Map<String, String> initColumnJavaTypeMapping() {
        Map<String, String> columnJavaTypeMapping = new HashMap<>();
        // 数据库类型映射
        columnJavaTypeMapping.put("varchar(\\(\\d+\\))?", "java.lang.String");
        columnJavaTypeMapping.put("varchar2(\\(\\d+\\))?", "java.lang.String");
        columnJavaTypeMapping.put("nvarchar(\\(\\d+\\))?", "java.lang.String");
        columnJavaTypeMapping.put("nvarchar2(\\(\\d+\\))?", "java.lang.String");
        columnJavaTypeMapping.put("char(\\(\\d+\\))?", "java.lang.String");
        columnJavaTypeMapping.put("(tiny|medium|long)*text", "java.lang.String");
        columnJavaTypeMapping.put("numeric(\\(\\d+,\\d+\\))?", "java.lang.Double");
        columnJavaTypeMapping.put("numericn(\\(\\d+,\\d+\\))?", "java.lang.Double");
        columnJavaTypeMapping.put("numeric(\\(\\d+\\))?", "java.lang.Integer");
        columnJavaTypeMapping.put("decimal(\\(\\d+,\\d+\\))?", "java.lang.Double");
        columnJavaTypeMapping.put("bigint(\\(\\d+\\))?", "java.lang.Long");
        columnJavaTypeMapping.put("(tiny|small|medium)*int(\\(\\d+\\))?", "java.lang.Integer");
        columnJavaTypeMapping.put("integer", "java.lang.Integer");
        columnJavaTypeMapping.put("date", "java.util.Date");
        columnJavaTypeMapping.put("datetime", "java.util.Date");
        return columnJavaTypeMapping;
    }
}
