package com.xg.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;

import java.io.IOException;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

public class XGFreemarkerUtil {

    /**
     * 从字符串获取模板
     *
     * @param templateContent 模板内容
     * @param templateName    模板名称
     * @return {@link Template }
     * @throws IOException io异常
     */
    public static Template getFreemarkerTemplate(String templateContent, String templateName) throws IOException {
        // 创建 FreeMarker 配置对象
        Configuration cfg = new Configuration(Configuration.VERSION_2_3_33);
        cfg.setDefaultEncoding(StandardCharsets.UTF_8.displayName());
        // 在 FreeMarker 中，当模板渲染数值时，默认会按照 # 格式化规则来输出。如果数值超过 4 位，默认会加入千位分隔符（即逗号）
        cfg.setNumberFormat("0"); // 设置全局的数值格式为不带逗号的形式

        // 使用 StringReader 将字符串内容转换为 Reader
        StringReader stringReader = new StringReader(templateContent);

        // 创建模板并加载\ templateName 是模板的名称，可以任意指定
        return new Template(templateName, stringReader, cfg);
    }

}
