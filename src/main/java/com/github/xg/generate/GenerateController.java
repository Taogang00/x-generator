package com.github.xg.generate;

import cn.hutool.core.bean.BeanUtil;
import com.github.xg.model.XGGlobalObj;
import com.github.xg.model.XGTableObj;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

/**
 * 生成控制器代码
 *
 * @author taogang
 * @date 2025/04/22
 */
public class GenerateController {

    /**
     * 生成代码
     *
     * @param template                        ftl模板
     * @param xgGlobalObj                     全局对象
     * @param xgGeneratorSelectedTableObjList 待生成的表格对象列表
     * @param map                             模板变量
     * @return int 生成的文件数量
     * @throws IOException io异常
     */
    public int generateCode(Template template, XGGlobalObj xgGlobalObj, List<XGTableObj> xgGeneratorSelectedTableObjList, Map<String, Object> map) throws IOException {
        int count = 0;
        if (xgGlobalObj.getGenerateController()) {
            Path path = Paths.get(xgGlobalObj.getOutputControllerPath());
            // 在使用 FileOutputStream 时，如果文件的父目录不存在（即文件所在的文件夹），Java 会抛出 FileNotFoundException，即使你尝试创建一个新的文件。
            // 为了避免这个问题，你需要确保文件的父目录已经存在。如果目录不存在，你需要手动创建它。
            Files.createDirectories(path);

            for (XGTableObj xgTableObj : xgGeneratorSelectedTableObjList) {
                Path filePath = Paths.get(xgTableObj.getControllerAbsolutePath());
                // 检查文件是否存在并且是否允许覆盖
                boolean shouldProcess = Files.exists(filePath) && xgGlobalObj.getFileOverride() || !Files.exists(filePath);
                if (shouldProcess) {
                    try (FileOutputStream fileOutputStream = new FileOutputStream(xgTableObj.getControllerAbsolutePath())) {
                        Map<String, Object> stringObjectMap = BeanUtil.beanToMap(xgTableObj);
                        map.put("table", stringObjectMap);
                        template.process(map, new OutputStreamWriter(fileOutputStream, StandardCharsets.UTF_8));
                        count++;
                    } catch (IOException | TemplateException e) {
                        //ignore Exception
                    }
                }
            }
        }
        return count;
    }
}
