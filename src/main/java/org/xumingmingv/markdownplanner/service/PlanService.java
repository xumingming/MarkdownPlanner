package org.xumingmingv.markdownplanner.service;

import java.util.List;

import org.xumingmingv.markdownplanner.model.IProject;
import org.xumingmingv.markdownplanner.model.Project;

public interface PlanService {
    /**
     * 从指定文件路径查询项目
     * @param filePath
     * @return
     */
    IProject getProject(String filePath);

    /**
     * 从指定文件路径查询项目，并且对人员和任务状态进行过来
     * @param filePath
     * @param man
     * @param status
     * @return
     */
    IProject getProject(String filePath, String man, String status, List<String> keywords, boolean reverse);

    /**
     * 更新任务进度
     * @param filePath
     * @param name
     * @param oldProgress
     * @param newProgress
     * @param lineNumber
     * @return
     */
    void updateTaskProgress(String filePath, String name, int oldProgress, int newProgress, int lineNumber);
}
