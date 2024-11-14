package com.xg.ui;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONArray;
import com.intellij.openapi.project.Project;
import org.jetbrains.idea.maven.project.MavenProject;
import org.jetbrains.idea.maven.project.MavenProjectsManager;

import javax.swing.*;
import java.util.List;

public class XGCodeGeneratorUI {

    private JPanel root;
    private JComboBox projectModuleComboBox;
    private JTextField controllerPathTextField;
    private JTextField ignoreTablePrefixTextField;
    private JCheckBox allCheckBox;
    private JTextField searchTextField;
    private JTextField textField3;
    private JTextField ignoreColumnPrefixTextField;
    private JComboBox configComboBox;
    private JButton settingButton;
    private JRadioButton ignoreRadioButton;
    private JRadioButton coverRadioButton;
    private JCheckBox controllerCheckBox;
    private JCheckBox serviceCheckBox;
    private JCheckBox mapperCheckBox;
    private JCheckBox entityCheckBox;
    private JCheckBox dtoCheckBox;
    private JCheckBox queryCheckBox;
    private JCheckBox mapStructCheckBox;
    private JTextField servicePathTextField;
    private JTextField mapperPathTextField;
    private JTextField entityPathTextField;
    private JTextField dtoPathTextField;
    private JTextField queryPathTextField;
    private JTextField mapStructPathTextField;
    private JTextField codeGeneratorPathTextField;
    private JButton importBtn;

    private Project project;

    public XGCodeGeneratorUI(Project project) {
        this.project = project;

        MavenProjectsManager manager = MavenProjectsManager.getInstance(project);
        List<MavenProject> projects = manager.getProjects();
        if (CollUtil.isNotEmpty(projects)) {
            JSONArray jsonArray = new JSONArray();
            for (MavenProject mavenProject : projects) {
                projectModuleComboBox.addItem(mavenProject.getMavenId().getArtifactId());
//                JSONObject jsonObject = new JSONObject();
//                jsonObject.set("GroupId", mavenProject.getMavenId().getGroupId());
//                jsonObject.set("ArtifactId", mavenProject.getMavenId().getArtifactId());
//                jsonObject.set("MavenProject.SourcePath", mavenProject.getSources().get(0));
//                jsonObject.set("MavenProject.ResourcesPath", mavenProject.getResources().get(0).getDirectory());
//                jsonObject.set("MavenProject.Name", mavenProject.getName());
//                jsonArray.add(jsonObject);
            }
        }
    }

    public JPanel getRoot() {
        return root;
    }
}
