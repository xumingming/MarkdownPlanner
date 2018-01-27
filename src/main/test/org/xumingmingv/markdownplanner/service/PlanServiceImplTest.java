package org.xumingmingv.markdownplanner.service;

import java.io.File;
import java.util.List;

import org.xumingmingv.markdownplanner.model.Project;
import org.junit.Test;
import org.xumingmingv.markdownplanner.Utils;

import static org.junit.Assert.*;

/**
 * {@link PlanServiceImpl}的单元测试。
 */
public class PlanServiceImplTest {
    private static final String TEST_PLAN_CONTENT =
        "# hello\n"
        + "* ProjectStartDate: 2017-09-11\n"
        + "* james -- 2017-09-11 - 2017-09-12\n"
        + "* james -- 2017-09-13 - 2017-09-14\n"
        + "* james -- 2017-09-18 - 2017-09-18\n"
        + "# 任务细分\n"
        + "* task1 -- 2.5[james][50%]\n"
        + "## A\n"
        + "* task2 -- 2.5[bond]\n"
        + "### B\n"
        + "* task3 -- 1[james]\n"
        + "## C\n"
        + "* task4 -- 1[bond]";

    @Test
    public void testUpdateTaskProgress_normal() throws Exception {
        File tmpProjectPath = File.createTempFile("test_project_", null);

        Utils.writeFile(tmpProjectPath.getAbsolutePath(), TEST_PLAN_CONTENT);

        PlanServiceImpl planService = getPlanService();
        planService.updateTaskProgress(
            tmpProjectPath.getAbsolutePath(),
            "task1",
            50,
            70,
            7
        );

        List<String> lines = Utils.readFile(tmpProjectPath.getAbsolutePath());
        assertEquals("* task1 -- 2.5[james][70%]", lines.get(7 - 1));
    }

    @Test
    public void testUpdateTaskProgress_oldProgressIsZero() throws Exception {
        File tmpProjectPath = File.createTempFile("test_project_", null);

        Utils.writeFile(tmpProjectPath.getAbsolutePath(), TEST_PLAN_CONTENT);

        PlanServiceImpl planService = getPlanService();
        planService.updateTaskProgress(
            tmpProjectPath.getAbsolutePath(),
            "task2",
            0,
            80,
            9
        );

        List<String> lines = Utils.readFile(tmpProjectPath.getAbsolutePath());
        assertEquals("* task2 -- 2.5[bond][80%]", lines.get(9 - 1));
    }

    private PlanServiceImpl getPlanService() {
        PlanServiceImpl planService = new PlanServiceImpl();
        planService.setProjectCacheService(new CacheService<Project>() {
            @Override
            public Project get(String key) {
                return null;
            }

            @Override
            public void set(String key, Project value) {

            }

            @Override
            public long getLastModified(String key) {
                return 0;
            }
        });
        return planService;
    }
}