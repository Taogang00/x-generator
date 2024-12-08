package com.github.xg.config;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Tuple;
import com.github.xg.model.XGTabInfo;
import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.github.xg.constant.XGConstants.*;
import static com.github.xg.constant.XGConstants.MAPSTRUCT;
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
    private Map<String, Tuple> columnJavaTypeMapping;

    public static void initXGDefaultTemplateManager() {
        XGSettingManager.State state = XGSettingManager.getInstance().getState();
        assert state != null;
        if (state.getXgConfigs() == null) {
            List<XGConfig> list = new ArrayList<>();
            //第一种，作者公司默认的配置
            XGConfig authorXGConfig = new XGConfig();
            authorXGConfig.setCreateTime(DateUtil.formatDateTime(new Date()));
            authorXGConfig.setIsDefault(true);
            authorXGConfig.setName(TEMPLATE_GUANWEI);

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
            authorXGConfig.setXgTabInfoList(guanweiXGTabInfoList);
            list.add(authorXGConfig);

            //第二种，mybatisPlus配置
            XGConfig mpXGConfig = new XGConfig();
            mpXGConfig.setCreateTime(DateUtil.formatDateTime(new Date()));
            mpXGConfig.setIsDefault(false);
            mpXGConfig.setName(TEMPLATE_MYBATIS_PLUS);
            List<XGTabInfo> mpXGTabInfoList = new ArrayList<>();
            mpXGTabInfoList.add(new XGTabInfo(CONTROLLER, getTemplateContent("/template/mybatisplus", "controller.java"), 1));
            mpXGTabInfoList.add(new XGTabInfo(SERVICE, getTemplateContent("/template/mybatisplus", "service.java"), 2));
            mpXGTabInfoList.add(new XGTabInfo(SERVICE_IMPL, getTemplateContent("/template/mybatisplus", "serviceImpl.java"), 3));
            mpXGTabInfoList.add(new XGTabInfo(ENTITY, getTemplateContent("/template/mybatisplus", "entity.java"), 4));
            mpXGTabInfoList.add(new XGTabInfo(MAPPER, getTemplateContent("/template/mybatisplus", "mapper.java"), 5));
            mpXGTabInfoList.add(new XGTabInfo(XML, getTemplateContent("/template/mybatisplus", "mapper.xml"), 6));
            mpXGConfig.setXgTabInfoList(mpXGTabInfoList);
            list.add(mpXGConfig);

            state.setXgConfigs(list);
            XGSettingManager.getInstance().loadState(state);
        }
    }

    public static void initXGDefaultTemplateManager(String selectedName) {
        XGSettingManager.State state = XGSettingManager.getInstance().getState();
        assert state != null;
        List<XGConfig> xgConfigs = state.getXgConfigs();
        for (XGConfig xgConfig : xgConfigs) {
            if (xgConfig.getName().equals(selectedName)) {
                if (TEMPLATE_GUANWEI.equals(selectedName)) {
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
                    xgConfig.setXgTabInfoList(guanweiXGTabInfoList);
                }
                if (TEMPLATE_MYBATIS_PLUS.equals(selectedName)) {
                    List<XGTabInfo> mpXGTabInfoList = new ArrayList<>();
                    mpXGTabInfoList.add(new XGTabInfo(CONTROLLER, getTemplateContent("/template/mybatisplus", "controller.java"), 1));
                    mpXGTabInfoList.add(new XGTabInfo(SERVICE, getTemplateContent("/template/mybatisplus", "service.java"), 2));
                    mpXGTabInfoList.add(new XGTabInfo(SERVICE_IMPL, getTemplateContent("/template/mybatisplus", "serviceImpl.java"), 3));
                    mpXGTabInfoList.add(new XGTabInfo(ENTITY, getTemplateContent("/template/mybatisplus", "entity.java"), 4));
                    mpXGTabInfoList.add(new XGTabInfo(MAPPER, getTemplateContent("/template/mybatisplus", "mapper.java"), 5));
                    mpXGTabInfoList.add(new XGTabInfo(XML, getTemplateContent("/template/mybatisplus", "mapper.xml"), 6));
                    xgConfig.setXgTabInfoList(mpXGTabInfoList);
                }
            }
        }
        state.setXgConfigs(xgConfigs);
        XGSettingManager.getInstance().loadState(state);
    }
}
