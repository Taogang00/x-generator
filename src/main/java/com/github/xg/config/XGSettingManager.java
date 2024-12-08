package com.github.xg.config;

import cn.hutool.core.io.FileUtil;
import com.github.xg.model.XGTabInfo;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.ui.Messages;
import lombok.Data;
import net.minidev.json.JSONObject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

/**
 * IntelliJ 平台的数据持久化是将对象数据序列化后存储数据到本地文件，序列化协议采用的是 XML。
 * 当 IDE 在重启时，会将XML文件内容反序列化为对象，以供插件使用。
 * 接口对应于PersistentStateComponent<T>，该接口接收一个需要持久化的数据泛型类型。
 *
 * @author taogang
 * @date 2024/12/01
 */
@SuppressWarnings("DialogTitleCapitalization")
@Service
@State(name = "x-generator", storages = {@Storage("plugin.x-generator.xml")})
public final class XGSettingManager implements PersistentStateComponent<XGSettingManager.State> {

    private State myState = new State();

    public static XGSettingManager getInstance() {
        return ApplicationManager.getApplication().getService(XGSettingManager.class);
    }

    /**
     * 将配置数据进行持久化，持久化过程是通过XML序列化实现的，
     * 具体的序列化过程IDE直接都帮忙完成了，程序员不必关心
     * <p>
     * getState() 方法在每次修改数据被保存时都会调用，该方法返回配置对象，以XML协议序列化后存储到文件中
     * <p>
     *
     * @return {@link State }
     */
    @Override
    public @Nullable State getState() {
        return myState;
    }

    /**
     * loadState方法用于加载已经序列化的XML文件，将其反序列化为对象供程序员使用，
     * 具体的反序列化过程IDE直接都帮忙完成了，程序员不必关心
     * <p>
     * loadState() 方法，在插件被加载时会被调用，IDE 将数据反序列化为对象，作为方法的入参数
     * <p>
     *
     * @param state State
     */
    @Override
    public void loadState(@NotNull State state) {
        this.myState = state;
    }

    public static void export(String targetPath) {
        JSONObject data = new JSONObject();
        FileUtil.writeString(data.toJSONString(), new File(targetPath + File.separator + "X-Generator.json"), "UTF-8");
        Messages.showDialog("导出成功，请到选择的目录查看", "提示", new String[]{"确定"}, -1, Messages.getInformationIcon());
    }

    public static void importConfig(String path) {
        File file = new File(path);
        if (!file.exists()) {
            Messages.showDialog("文件不存在", "提示", new String[]{"确定"}, -1, Messages.getInformationIcon());
            return;
        }
        Messages.showDialog("导入成功", "提示", new String[]{"确定"}, -1, Messages.getInformationIcon());
    }

    public static XGConfig getSelectXGConfig(String selectedName) {
        State state = getInstance().getState();
        assert state != null;
        List<XGConfig> xgConfigs = state.getXgConfigs();
        return xgConfigs.stream().filter(xgConfig -> xgConfig.getName().equals(selectedName)).findFirst().orElse(null);
    }

    public static XGTabInfo getSelectXGConfig(XGConfig selectXGConfig, String tabInfoTypeName) {
        List<XGTabInfo> xgTabInfoList = selectXGConfig.getXgTabInfoList();
        for (XGTabInfo tabInfo : xgTabInfoList) {
            if (tabInfo.getType().equals(tabInfoTypeName)) {
                return tabInfo;
            }
        }
        return null;
    }

    public static XGTabInfo getSelectXGConfig(String selectedName, String tabInfoTypeName) {
        XGConfig selectXGConfig = XGSettingManager.getSelectXGConfig(selectedName);
        List<XGTabInfo> xgTabInfoList = selectXGConfig.getXgTabInfoList();
        for (XGTabInfo tabInfo : xgTabInfoList) {
            if (tabInfo.getType().equals(tabInfoTypeName)) {
                return tabInfo;
            }
        }
        return null;
    }

    @Data
    public static class State {

        /**
         * 配置映射
         * key=default、mybatis3、mybatisPlus、custom1、...
         */
        public List<XGConfig> xgConfigs;
    }
}
