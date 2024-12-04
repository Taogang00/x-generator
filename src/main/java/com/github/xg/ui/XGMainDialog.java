package com.github.xg.ui;

import cn.hutool.core.date.DateUtil;
import com.github.xg.config.XGConfig;
import com.github.xg.config.XGSettingManager;
import com.github.xg.constant.XGConstants;
import com.github.xg.model.XGGlobalObj;
import com.github.xg.model.XGTabInfo;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import lombok.Getter;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.github.xg.constant.XGConstants.*;
import static com.github.xg.utils.XGTemplateUtil.getTemplateContent;

public class XGMainDialog extends DialogWrapper {

    private JPanel rootPanel;

    private final Project project;

    private final XGCodeUI codeGeneratorUI;

    @Getter
    private final XGSettingUI xgSettingUI;

    private final List<JPanel> containerPanelList = new ArrayList<>();

    public XGMainDialog(Project project) {
        super(project);
        XGGlobalObj xgGlobalObj = new XGGlobalObj();
        xgGlobalObj.setDateTime(DateUtil.formatDateTime(new Date()));
        xgGlobalObj.setFileOverride(false);

        initXGSettingManager();

        this.setOKButtonText("生成");
        this.setCancelButtonText("取消");
        this.project = project;

        this.setTitle("X-代码生成器 0.0.5");
        this.setSize(999, 666);
        this.setResizable(false);

        xgSettingUI = new XGSettingUI(project, this, xgGlobalObj);
        codeGeneratorUI = new XGCodeUI(project, this, xgGlobalObj);

        containerPanelList.add(codeGeneratorUI.getRootJPanel());
        containerPanelList.add(xgSettingUI.getRootJPanel());
        // 默认切换到第一页
        switchPage(0);
        init();
    }

    public void switchPage(int page) {
        super.setOKActionEnabled(page != 1);
        rootPanel.removeAll();
        JPanel jPanel = containerPanelList.get(page);
        rootPanel.add(jPanel);
        rootPanel.repaint();//刷新页面，重绘面板
        rootPanel.validate();//使重绘的面板确认生效
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return rootPanel;
    }

    @Override
    protected void doOKAction() {
        // 在这里调用 XGCodeGeneratorUI 中的方法
        try {
            codeGeneratorUI.generateCodeAction(project, this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void doHelpAction() {
        // 获取 HelpManager 并打开指定的帮助链接
        BrowserUtil.browse(Objects.requireNonNull(getHelpId()));
    }

    @Override
    protected @NonNls @Nullable String getHelpId() {
        return "https://github.com/Taogang00/x-generator";
    }

    @Override
    protected Action @NotNull [] createActions() {
        return super.createActions();
    }

    public void initXGSettingManager() {
        XGSettingManager.State state = XGSettingManager.getInstance().getState();
        assert state != null;
        if (state.getXgConfigs() == null) {
            List<XGConfig> list = new ArrayList<>();
            //第一种，作者公司默认的配置
            XGConfig authorXGConfig = new XGConfig();
            authorXGConfig.setCreateTime(new Date());
            authorXGConfig.setIsDefault(true);
            authorXGConfig.setName(TEMPLATE_GUANWEI);

            List<XGTabInfo> authorXGTabInfoList = new ArrayList<>();
            authorXGTabInfoList.add(new XGTabInfo(CONTROLLER, getTemplateContent("/template/guanwei", "controller.java"), 1));
            authorXGTabInfoList.add(new XGTabInfo(SERVICE, getTemplateContent("/template/guanwei", "service.java"), 2));
            authorXGTabInfoList.add(new XGTabInfo(SERVICE_IMPL, getTemplateContent("/template/guanwei", "serviceImpl.java"), 3));
            authorXGTabInfoList.add(new XGTabInfo(ENTITY, getTemplateContent("/template/guanwei", "entity.java"), 4));
            authorXGTabInfoList.add(new XGTabInfo(MAPPER, getTemplateContent("/template/guanwei", "mapper.java"), 5));
            authorXGTabInfoList.add(new XGTabInfo(XML, getTemplateContent("/template/guanwei", "mapper.xml"), 6));
            authorXGTabInfoList.add(new XGTabInfo(QUERY, getTemplateContent("/template/guanwei", "query.java"), 7));
            authorXGTabInfoList.add(new XGTabInfo(DTO, getTemplateContent("/template/guanwei", "dto.java"), 8));
            authorXGTabInfoList.add(new XGTabInfo(MAPSTRUCT, getTemplateContent("/template/guanwei", "mapstruct.java"), 9));
            authorXGConfig.setXgTabInfoList(authorXGTabInfoList);
            list.add(authorXGConfig);

            //第二种，mybatisPlus配置...TODO
            XGConfig mpXGConfig = new XGConfig();
            mpXGConfig.setCreateTime(new Date());
            mpXGConfig.setIsDefault(true);
            mpXGConfig.setName(TEMPLATE_MYBATIS_PLUS);
            List<XGTabInfo> mpXGTabInfoList = new ArrayList<>();
            mpXGTabInfoList.add(new XGTabInfo(XGConstants.ENTITY, getTemplateContent("/template/mybatisplus", "entity.java"), 4));
            mpXGConfig.setXgTabInfoList(mpXGTabInfoList);
            list.add(mpXGConfig);

            state.setXgConfigs(list);
            XGSettingManager.getInstance().loadState(state);
        }
    }
}
