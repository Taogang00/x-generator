package com.github.xg.utils;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.Objects;

public class XGModuleUtil {

    public static Module[] getModules(Project project) {
        ModuleManager moduleManager = ModuleManager.getInstance(project);
        return moduleManager.getModules();
    }

    public static String getModuleSourcePath(Project project, String moduleName) {
        Module[] modules = XGModuleUtil.getModules(project);
        for (Module module : modules) {
            if (module.getName().equals(moduleName)) {
                ModuleRootManager rootManager = ModuleRootManager.getInstance(module);
                // 获取所有的ContentEntry
                for (ContentEntry contentEntry : rootManager.getContentEntries()) {
                    // 获取ContentFolder，这些通常是源代码文件夹
                    for (VirtualFile sourceFolder : contentEntry.getSourceFolderFiles()) {
                        if (sourceFolder != null && sourceFolder.isDirectory() && sourceFolder.getPath().contains("src/main/java")) {
                            return sourceFolder.getPath();
                        }
                    }
                    //未找到源路径，返回模块路径
                    return Objects.requireNonNull(contentEntry.getFile()).getPath();
                }
            }
        }
        return null;
    }

    public static String getModuleReSourcePath(Project project, String moduleName) {
        Module[] modules = XGModuleUtil.getModules(project);
        for (Module module : modules) {
            if (module.getName().equals(moduleName)) {
                ModuleRootManager rootManager = ModuleRootManager.getInstance(module);
                // 获取所有的ContentEntry
                for (ContentEntry contentEntry : rootManager.getContentEntries()) {
                    // 获取ContentFolder，这些通常是源代码文件夹
                    for (VirtualFile sourceFolder : contentEntry.getSourceFolderFiles()) {
                        if (sourceFolder != null && sourceFolder.isDirectory() && sourceFolder.getPath().contains("src/main/resources")) {
                            return sourceFolder.getPath();
                        }
                    }
                    //未找到资源路径，返回模块路径
                    return Objects.requireNonNull(contentEntry.getFile()).getPath();
                }
            }
        }
        return null;
    }
}
