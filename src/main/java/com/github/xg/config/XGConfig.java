package com.github.xg.config;

import cn.hutool.core.date.DateUtil;
import com.github.xg.model.XGTempItem;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeMap;

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
    private List<XGTempItem> xgTempItemList;

    /**
     * 数据库列与Java类型映射
     */
    private TreeMap<String, String> columnJavaTypeMapping;

    /**
     * 初始化默认的配置内容
     */
    public static void initXGDefaultTemplateManager() {
        XGSettingManager.State state = XGSettingManager.getInstance().getState();
        if (state != null) {
            XGConfig xgConfig = state.getXgConfigs();
            //为空的状态，表示配置为空，本地没相关配置
            if (xgConfig == null) {
                XGConfig authorXGConfig = new XGConfig();
                authorXGConfig.setCreateTime(DateUtil.formatDateTime(new Date()));
                authorXGConfig.setIsDefault(true);
                authorXGConfig.setName(TEMPLATE_GUANWEI);
                authorXGConfig.setXgTempItemList(initGuanWeiXgTableInfo());
                authorXGConfig.setColumnJavaTypeMapping(initColumnJavaTypeMapping());

                state.setXgConfigs(authorXGConfig);
                XGSettingManager.getInstance().loadState(state);
            } else {
                //不为空，检查相关的配置是否有（后续变动新增的配置项），如果没有，就添加
                if (xgConfig.getColumnJavaTypeMapping() == null) {
                    xgConfig.setColumnJavaTypeMapping(initColumnJavaTypeMapping());
                }
                if (xgConfig.getXgTempItemList() == null) {
                    xgConfig.setXgTempItemList(initGuanWeiXgTableInfo());
                }
            }
        }
    }

    /**
     * 重置选择的指定的配置的代码模板配置内容
     */
    public static void resetSelectedConfigXgTabInfo() {
        XGSettingManager.State state = XGSettingManager.getInstance().getState();
        assert state != null;
        XGConfig xgConfig = state.getXgConfigs();
        xgConfig.setXgTempItemList(initGuanWeiXgTableInfo());
        xgConfig.setColumnJavaTypeMapping(initColumnJavaTypeMapping());
        state.setXgConfigs(xgConfig);
        XGSettingManager.getInstance().loadState(state);
    }

    /**
     * 初始化南京观为公司代码模板配置
     */
    public static List<XGTempItem> initGuanWeiXgTableInfo() {
        List<XGTempItem> guanweiXGTempItemList = new ArrayList<>();
        guanweiXGTempItemList.add(new XGTempItem(CONTROLLER, getTemplateContent("/template/guanwei", "controller.java"), 1));
        guanweiXGTempItemList.add(new XGTempItem(SERVICE, getTemplateContent("/template/guanwei", "service.java"), 2));
        guanweiXGTempItemList.add(new XGTempItem(SERVICE_IMPL, getTemplateContent("/template/guanwei", "serviceImpl.java"), 3));
        guanweiXGTempItemList.add(new XGTempItem(ENTITY, getTemplateContent("/template/guanwei", "entity.java"), 4));
        guanweiXGTempItemList.add(new XGTempItem(MAPPER, getTemplateContent("/template/guanwei", "mapper.java"), 5));
        guanweiXGTempItemList.add(new XGTempItem(XML, getTemplateContent("/template/guanwei", "mapper.xml"), 6));
        guanweiXGTempItemList.add(new XGTempItem(QUERY, getTemplateContent("/template/guanwei", "query.java"), 7));
        guanweiXGTempItemList.add(new XGTempItem(DTO, getTemplateContent("/template/guanwei", "dto.java"), 8));
        guanweiXGTempItemList.add(new XGTempItem(MAPSTRUCT, getTemplateContent("/template/guanwei", "mapstruct.java"), 9));
        return guanweiXGTempItemList;
    }

    /**
     * 初始化数据库类型映射
     */
    public static TreeMap<String, String> initColumnJavaTypeMapping() {
        TreeMap<String, String> columnJavaTypeMapping = new TreeMap<>();
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
