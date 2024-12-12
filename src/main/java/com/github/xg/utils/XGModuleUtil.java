package com.github.xg.utils;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ContentEntry;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VirtualFile;

import java.util.ArrayList;
import java.util.List;

public class XGModuleUtil {

    public static List<Module> getModules(Project project) {
        //gradle 会把 x-generator.main x-generator.test 作为模块，需要去掉
        List<Module> returnModules = new ArrayList<>();
        ModuleManager moduleManager = ModuleManager.getInstance(project);
        Module[] modules = moduleManager.getModules();
        for (Module module : modules) {
            if (!module.getName().endsWith("main") && !module.getName().endsWith("test")) {
                returnModules.add(module);
            }
        }
        return returnModules;
    }

    public static String getModuleSourcePath(Project project, String moduleName) {
        List<Module> modules = XGModuleUtil.getModules(project);
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
                }
                //未找到源路径，返回模块路径
                VirtualFile[] contentRoots = rootManager.getContentRoots();
                if (contentRoots.length > 0) {
                    return contentRoots[0].getPath();
                }
            }
        }
        return null;
    }

    public static String getModuleReSourcePath(Project project, String moduleName) {
        List<Module> modules = XGModuleUtil.getModules(project);
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
                }
                //未找到资源路径，返回模块路径
                VirtualFile[] contentRoots = rootManager.getContentRoots();
                if (contentRoots.length > 0) {
                    return contentRoots[0].getPath();
                }
            }
        }
        return null;
    }
}
