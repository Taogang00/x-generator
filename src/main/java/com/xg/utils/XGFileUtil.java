package com.xg.utils;

import cn.hutool.core.util.ArrayUtil;
import cn.hutool.core.util.ObjectUtil;
import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;

import java.io.File;

public class XGFileUtil {

    /**
     * 遍历文件
     * 检查文件是否为目录：如果传入的 file 是一个目录，则继续处理。
     * 获取子文件列表：调用 file.listFiles() 获取目录下的所有子文件。
     * 检查子文件列表是否为空：使用 ArrayUtil.isNotEmpty(subFiles) 检查子文件列表是否为空。
     * 处理子文件列表：
     * 如果子文件数量大于1，直接返回当前目录。
     * 如果子文件数量为1，递归调用 walkFiles 处理唯一的子文件。
     * 如果子文件列表为空，返回当前目录。
     * 返回文件：如果传入的 file 不是目录，直接返回该文件
     *
     * @param file 文件
     * @return {@link File }
     */
    public static File walkFiles(File file) {
        if (!file.isDirectory()) {
            return file;
        }
        File[] subFiles = file.listFiles();
        if (ArrayUtil.isEmpty(subFiles)) {
            return file;
        }
        if (subFiles.length == 1 && subFiles[0].isDirectory()) {
            return walkFiles(subFiles[0]);
        }
        return file;
    }

    /**
     * 选择目录
     *
     * @param project 项目
     * @return {@code String}
     */
    public static String chooseDirectory(Project project) {
        FileChooserDescriptor descriptor = new FileChooserDescriptor(false, true, false, false, false, false);
        VirtualFile virtualFile = FileChooser.chooseFile(descriptor, project, null);
        if (ObjectUtil.isNull(virtualFile)) {
            return null;
        }
        return virtualFile.getPath();
    }

    /**
     * 选择文件
     *
     * @param project 项目
     * @return {@code String}
     */
    public static String chooseFile(Project project) {
        FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, false);
        VirtualFile virtualFile = FileChooser.chooseFile(descriptor, project, null);
        if (ObjectUtil.isNull(virtualFile)) {
            return null;
        }
        return virtualFile.getPath();
    }

    /**
     * 选择文件
     *
     * @param project 项目
     * @return {@code VirtualFile}
     */

    public static VirtualFile chooseFileVirtual(Project project) {
        FileChooserDescriptor descriptor = new FileChooserDescriptor(true, false, false, false, false, false);
        return FileChooser.chooseFile(descriptor, project, null);
    }
}
