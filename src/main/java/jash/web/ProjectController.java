package jash.web;

import java.io.FileInputStream;
import java.util.List;

import jash.parser.Parser;
import jash.parser.Project;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProjectController {
  @RequestMapping("/")
  public String project(Model model,
      @RequestParam(required = false) String status,
      @RequestParam(required = false) String man
  ) throws Exception {
    FileInputStream stream = new FileInputStream("/Users/xumingmingv/local/alipay/alirock/plan/201709/d1.plan.md");
    List<String> lines = IOUtils.readLines(stream, "UTF-8");
    Parser parser = new Parser();
    Project fullProject = parser.parse(lines);
    model.addAttribute("fullProject", fullProject);

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

    model.addAttribute("project", project);
    model.addAttribute("selectedMan", man);
    model.addAttribute("selectedStatus", status);

    return "project";
  }

}
