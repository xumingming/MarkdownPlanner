package jash.service;

import java.util.List;
import java.util.stream.Collectors;

import com.google.common.base.Joiner;
import jash.model.Project;
import jash.model.task.AtomicTask;
import jash.model.task.Task;
import jash.parser.Parser;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static jash.Utils.readFile;
import static jash.Utils.writeFile;

@Service("planService")
public class PlanServiceImpl implements PlanService {
    private static final Logger LOG = LoggerFactory.getLogger(PlanServiceImpl.class);
    @Autowired
    private CacheService<Project> projectCacheService;
    public Project getProject(String filePath) {
        return getProject(filePath, null, null, null, false);
    }

    public Project getProject(String filePath, String man, String status, List<String> keywords, boolean reverse) {
        // get from cache
        Project fullProject = projectCacheService.get(filePath);
        if (fullProject == null) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Read project(" + filePath + ") from disk.");
            }

            List<String> lines = readFile(filePath);

            Parser parser = new Parser();
            fullProject = parser.parse(lines);

            projectCacheService.set(filePath, fullProject);
        }

        return filterProject(fullProject, man, status, keywords, reverse);
    }

    @Override
    public void updateTaskProgress(String filePath, String name, int oldProgress, int newProgress, int lineNumber) {
        Project project = getProject(filePath);
        if (project == null) {
            throw new IllegalArgumentException("No such project: " + filePath);
        }

        // 搜索出指定行号的任务
        List<Task> tasks = project.getTasks().stream()
            .filter(t -> !t.isComposite())
            .filter(t -> {
                AtomicTask atomicTask = (AtomicTask) t;
                return atomicTask.getLineNumber() == lineNumber;
            }).collect(Collectors.toList());

        if (tasks.isEmpty() || tasks.size() > 1) {
            throw new IllegalArgumentException("No task or more than one task has the lineNumber: " + lineNumber);
        }

        AtomicTask targetTask = (AtomicTask) tasks.get(0);
        if (!targetTask.getName().equals(name) || targetTask.getProgress() != oldProgress) {
            throw new IllegalArgumentException("Project is modified after you last fetch! Try to refresh the page & try again");
        }

        List<String> lines = readFile(filePath);
        String targetLine = lines.get(lineNumber - 1);

        String newTargetLine;
        // 原来的计划里面因为progress为0所以压根就没有写
        if (oldProgress == 0 && !targetLine.contains("[" + oldProgress + "%]")) {
            newTargetLine = targetLine.trim() + "[" + newProgress + "%]";
        } else {
            newTargetLine = targetLine.replaceAll("\\[" + oldProgress + "%]", "[" + newProgress + "%]");
        }

        lines.set(lineNumber - 1, newTargetLine);
        writeFile(filePath, Joiner.on("\n").join(lines));
    }

    private Project filterProject(Project fullProject, String man,
        String status, List<String> keywords, boolean reverse) {
        Project project = fullProject;
        if (status != null) {
            switch (status) {
                case "Completed":
                    project = project.hideNotCompleted();
                    break;
                case "NotCompleted":
                    project = project.hideCompleted();
                    break;
                case "All":
                default:
                    break;
            }
        }

        if (StringUtils.isNotBlank(man)) {
            project = project.onlyShowTaskForUser(man);
        }

        if (keywords != null && !keywords.isEmpty()) {
            project = project.filterKeywords(keywords, reverse);
        }
        return project;
    }

    public void setProjectCacheService(CacheService<Project> projectCacheService) {
        this.projectCacheService = projectCacheService;
    }
}
