package jash.service;

import java.io.File;
import java.io.FileInputStream;
import java.util.List;

import jash.parser.Parser;
import jash.parser.Project;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("planService")
public class PlanServiceImpl implements PlanService {
    private static final Logger LOG = LoggerFactory.getLogger(PlanServiceImpl.class);
    @Autowired
    private CacheService<Project> projectCacheService;
    public Project getProject(String filePath) {
        return getProject(filePath, null, null, null, false);
    }

    public Project getProject(String filePath, String man, String status, List<String> keywords, boolean reverse) {
        File file = new File(filePath);
        // get from cache
        Project fullProject = projectCacheService.get(filePath);
        if (fullProject == null) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Read project(" + filePath + ") from disk.");
            }

            try (FileInputStream stream = new FileInputStream(file)) {
                List<String> lines = IOUtils.readLines(stream, "UTF-8");
                Parser parser = new Parser();
                fullProject = parser.parse(lines);

                projectCacheService.set(filePath, fullProject);
            } catch (Exception e) {
                LOG.error("", e);
                return null;
            }
        }

        return filterProject(fullProject, man, status, keywords, reverse);
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
}
