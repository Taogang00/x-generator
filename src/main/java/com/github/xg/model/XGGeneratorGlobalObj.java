package com.github.xg.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

import java.io.File;

import static cn.hutool.core.text.StrPool.DOT;

/**
 * 代码生成器 全局对象
 *
 * @author taogang
 * @date 2024/11/19
 */
@Data
public class XGGeneratorGlobalObj {

    private String author;
    private String dateTime;
    private String removeClassNamePrefix = ""; //需要初始化为空字符串
    private String addClassNamePrefix = ""; //需要初始化为空字符串
    private String sourceCodeGeneratorPath; //src/main/java
    private String resourcesCodeGeneratorPath; //src/main/resources
    private Boolean fileOverride;

    private String controllerPackagePath;
    private String entityPackagePath;
    private String dtoPackagePath;
    private String queryPackagePath;
    private String mapstructPackagePath;
    private String servicePackagePath;
    private String serviceImplPackagePath;
    private String mapperPackagePath;
    private String mapperXmlPackagePath;

    private String superControllerClass;
    private String superServiceClass = "MBaseService";
    private String superServiceImplClass = "MBaseServiceImpl";
    private String superEntityClass;
    private String superMapperClass = "MBaseMapper";
    private String superQueryClass;
    private String superDTOClass;

    private String superControllerClassPackagePath;
    private String superServiceClassPackagePath = "com.guanwei.mybatis.base.service";
    private String superServiceImplClassPackagePath = "com.guanwei.mybatis.base.service";
    private String superMapperClassPackagePath = "com.guanwei.mybatis.base.mapper";
    private String superEntityClassPackagePath;
    private String superQueryClassPackagePath;
    private String superDTOClassPackagePath;

    private Boolean generateController = true;
    private Boolean generateEntity = true;
    private Boolean generateService = true;
    private Boolean generateQuery = true;
    private Boolean generateMapStruct = true;
    private Boolean generateMapper = true;
    private Boolean generateMapperXml = true;
    private Boolean generateDTO = true;

    private String outputControllerPath;
    private String outputEntityPath;
    private String outputServicePath;
    private String outputServiceImplPath;
    private String outputQueryPath;
    private String outputMapStructPath;
    private String outputMapperPath;
    private String outputMapperXmlPath;
    private String outputDTOPath;

    public String getServiceImplPackagePath() {
        return this.getServicePackagePath() + ".impl";
    }

    public String getOutputControllerPath() {
        if (StrUtil.isNotBlank(this.getControllerPackagePath()) && StrUtil.isNotBlank(this.sourceCodeGeneratorPath)) {
            String path = StrUtil.replace(this.getControllerPackagePath(), DOT, File.separator);
            if (!path.startsWith(File.separator)) {
                path = File.separator + path;
            }
            path = this.sourceCodeGeneratorPath + path;
            return path.replace("//", File.separator);
        }
        return null;
    }

    public String getOutputEntityPath() {
        if (StrUtil.isNotBlank(this.getEntityPackagePath()) && StrUtil.isNotBlank(this.sourceCodeGeneratorPath)) {
            String path = StrUtil.replace(this.getEntityPackagePath(), DOT, File.separator);
            if (!path.startsWith(File.separator)) {
                path = File.separator + path;
            }
            path = this.sourceCodeGeneratorPath + path;
            return path.replace("//", File.separator);
        }
        return null;
    }

    public String getOutputServicePath() {
        if (StrUtil.isNotBlank(this.getServicePackagePath()) && StrUtil.isNotBlank(this.sourceCodeGeneratorPath)) {
            String path = StrUtil.replace(this.getServicePackagePath(), DOT, File.separator);
            if (!path.startsWith(File.separator)) {
                path = File.separator + path;
            }
            path = this.sourceCodeGeneratorPath + path;
            return path.replace("//", File.separator);
        }
        return null;
    }

    public String getOutputServiceImplPath() {
        return this.getOutputServicePath() + File.separator + "impl";
    }

    public String getOutputQueryPath() {
        if (StrUtil.isNotBlank(this.getQueryPackagePath()) && StrUtil.isNotBlank(this.sourceCodeGeneratorPath)) {
            String path = StrUtil.replace(this.getQueryPackagePath(), DOT, File.separator);
            if (!path.startsWith(File.separator)) {
                path = File.separator + path;
            }
            path = this.sourceCodeGeneratorPath + path;
            return path.replace("//", File.separator);
        }
        return null;
    }

    public String getOutputMapStructPath() {
        if (StrUtil.isNotBlank(this.getMapstructPackagePath()) && StrUtil.isNotBlank(this.sourceCodeGeneratorPath)) {
            String path = StrUtil.replace(this.getMapstructPackagePath(), DOT, File.separator);
            if (!path.startsWith(File.separator)) {
                path = File.separator + path;
            }
            path = this.sourceCodeGeneratorPath + path;
            return path.replace("//", File.separator);
        }
        return null;
    }

    public String getOutputMapperPath() {
        if (StrUtil.isNotBlank(this.getMapperPackagePath()) && StrUtil.isNotBlank(this.sourceCodeGeneratorPath)) {
            String path = StrUtil.replace(this.getMapperPackagePath(), DOT, File.separator);
            if (!path.startsWith(File.separator)) {
                path = File.separator + path;
            }
            path = this.sourceCodeGeneratorPath + path;
            return path.replace("//", File.separator);
        }
        return null;
    }

    public String getOutputDTOPath() {
        if (StrUtil.isNotBlank(this.getDtoPackagePath()) && StrUtil.isNotBlank(this.sourceCodeGeneratorPath)) {
            String path = StrUtil.replace(this.getDtoPackagePath(), DOT, File.separator);
            if (!path.startsWith(File.separator)) {
                path = File.separator + path;
            }
            path = this.sourceCodeGeneratorPath + path;
            return path.replace("//", File.separator);
        }
        return null;
    }

    public String getOutputMapperXmlPath() {
        if (StrUtil.isNotBlank(this.getMapperXmlPackagePath()) && StrUtil.isNotBlank(this.resourcesCodeGeneratorPath)) {
            String path = StrUtil.replace(this.getMapperXmlPackagePath(), DOT, File.separator);
            if (!path.startsWith(File.separator)) {
                path = File.separator + path;
            }
            path = this.resourcesCodeGeneratorPath + path;
            return path.replace("//", File.separator);
        }
        return null;
    }
}
