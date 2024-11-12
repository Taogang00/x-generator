package com.xg.window;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.xg.utils.DialogUtil;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;

public class ErStudioCodeGeneratorDialog extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextArea textProjectInfo;

    public ErStudioCodeGeneratorDialog(AnActionEvent actionEvent) {
        textProjectInfo.setLineWrap(true); // 启用自动换行

        Project project = actionEvent.getProject();
        if (project != null) {
            MavenProjectsManager manager = MavenProjectsManager.getInstance(actionEvent.getProject());
            List<MavenProject> projects = manager.getProjects();
            if (CollUtil.isNotEmpty(projects)) {
                JSONArray jsonArray = new JSONArray();
                for (MavenProject mavenProject : projects) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.set("GroupId", mavenProject.getMavenId().getGroupId());
                    jsonObject.set("ArtifactId", mavenProject.getMavenId().getArtifactId());
                    jsonObject.set("MavenProject.SourcePath", mavenProject.getSources().get(0));
                    jsonObject.set("MavenProject.ResourcesPath", mavenProject.getResources().get(0).getDirectory());
                    jsonObject.set("MavenProject.Name", mavenProject.getName());
                    jsonArray.add(jsonObject);
                }
                textProjectInfo.append(jsonArray.toStringPretty());
            }
        }

        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        setTitle("X 代码生成器");
        setSize(1000, 500);
        DialogUtil.centerShow(this);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    private void onOK() {
        // add your code here
        dispose();
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }
}
