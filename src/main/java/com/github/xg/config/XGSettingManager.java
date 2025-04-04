package com.github.xg.config;

import com.github.xg.model.XGTempItem;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import lombok.Data;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * IntelliJ 平台的数据持久化是将对象数据序列化后存储数据到本地文件，序列化协议采用的是 XML。
 * 当 IDE 在重启时，会将XML文件内容反序列化为对象，以供插件使用。
 * 接口对应于PersistentStateComponent<T>，该接口接收一个需要持久化的数据泛型类型。
 *
 * @author taogang
 * @date 2024/12/01
 */
@Service
@State(name = "x-generator", storages = {@Storage("idea.plugin.x-generator.xml")})
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

    public static XGConfig getSelectXGConfig() {
        State state = getInstance().getState();
        assert state != null;
        return state.getXgConfig();
    }

    public static XGTempItem getSelectXGConfig(XGConfig selectXGConfig, String tabInfoTypeName) {
        List<XGTempItem> xgTempItemList = selectXGConfig.getXgTempItemList();
        for (XGTempItem tabInfo : xgTempItemList) {
            if (tabInfo.getName().equals(tabInfoTypeName)) {
                return tabInfo;
            }
        }
        return null;
    }

    public static XGTempItem getSelectXGConfig(String tabInfoTypeName) {
        XGConfig selectXGConfig = XGSettingManager.getSelectXGConfig();
        List<XGTempItem> xgTempItemList = selectXGConfig.getXgTempItemList();
        for (XGTempItem tabInfo : xgTempItemList) {
            if (tabInfo.getName().equals(tabInfoTypeName)) {
                return tabInfo;
            }
        }
        return null;
    }

    public static void updateXgConfig(XGConfig selectXGConfig) {
        State state = XGSettingManager.getInstance().getState();
        assert state != null;
        state.setXgConfig(selectXGConfig);
    }

    @Data
    public static class State {

        /**
         * 配置映射
         */
        public XGConfig xgConfig;
    }
}
