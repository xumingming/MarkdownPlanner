package jash.service;

import jash.parser.Project;

public interface PlanService {
    /**
     * 从指定文件路径查询项目
     * @param filePath
     * @return
     */
    Project getProject(String filePath);

    /**
     * 从指定文件路径查询项目，并且对人员和任务状态进行过来
     * @param filePath
     * @param man
     * @param status
     * @return
     */
    Project getProject(String filePath, String man, String status, String keyword, boolean reverse);
}
