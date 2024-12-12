package com.github.xg.action;

import com.github.xg.ui.XGMainDialog;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;

public class XGAnAction extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project project = e.getProject();
        if (project != null) {
            ApplicationManager.getApplication().invokeLater(() -> {
                ApplicationManager.getApplication().invokeLater(() -> {
                    XGMainDialog dialog = new XGMainDialog(project);
                    dialog.show();
                });
            });
        }
    }
}
