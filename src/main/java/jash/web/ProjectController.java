package jash.web;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import com.vladsch.flexmark.ast.Node;
import com.vladsch.flexmark.ext.tables.TablesExtension;
import com.vladsch.flexmark.ext.toc.TocExtension;
import com.vladsch.flexmark.html.HtmlRenderer;
import com.vladsch.flexmark.util.options.MutableDataHolder;
import com.vladsch.flexmark.util.options.MutableDataSet;
import jash.Application;
import jash.parser.Parser;
import jash.parser.Project;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.HandlerMapping;

import static jash.Utils.getFileDisplayName;

@Controller
public class ProjectController {
    @RequestMapping(value = "/d/**/")
    public String directory(HttpServletRequest req, Model model) throws Exception {
        String filePath = getCurrentDirectoryPath(req);
        List<FileVO> fileVOs = Arrays
            .stream(
                new File(filePath)
                    .listFiles(f -> !f.getName().startsWith("."))
            )
            .map(
                f -> new FileVO(
                    getFileDisplayName(f),
                    f.getAbsolutePath().substring(Application.ROOT.length()),
                    f.isDirectory()
                )
            )
            .sorted((x, y) -> {
                if (x.isDir() && !y.isDir()) {
                    return -1;
                } else if (!x.isDir() && y.isDir()){
                    return 1;
                } else {
                    return x.getName().compareTo(y.getName());
                }
            })
            .collect(Collectors.toList());

        model.addAttribute("files", fileVOs);
        model.addAttribute("breadcrumb", new BreadcrumVO(Application.ROOT, filePath));
        return "directory";
    }

    @RequestMapping(value = "/**/*.plan.md")
    public String project(HttpServletRequest req,
        @RequestParam(required = false) String status,
        @RequestParam(required = false) String man,
        Model model) throws Exception {
        String filePath = getCurrentFilePath(req);
        try (FileInputStream stream = new FileInputStream(filePath)) {
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

            model.addAttribute("breadcrumb", new BreadcrumVO(Application.ROOT, filePath));
            return "project";
        }
    }

    @RequestMapping(path = {"/**/*.md"}, produces = "text/html")
    public String java(Model model, HttpServletRequest req) throws Exception {
        String filePath = getCurrentFilePath(req);

        final MutableDataHolder OPTIONS = new MutableDataSet()
            .set(HtmlRenderer.INDENT_SIZE, 2)
            .set(HtmlRenderer.PERCENT_ENCODE_URLS, true)

            // for full GFM table compatibility add the following table extension options:
            .set(TablesExtension.COLUMN_SPANS, false)
            .set(TablesExtension.APPEND_MISSING_COLUMNS, true)
            .set(TablesExtension.DISCARD_EXTRA_COLUMNS, true)
            .set(TablesExtension.HEADER_SEPARATOR_COLUMN_MATCH, true)
            .set(TocExtension.IS_TEXT_ONLY, false)
            ;
        com.vladsch.flexmark.parser.Parser parser =
            com.vladsch.flexmark.parser.Parser.builder(OPTIONS).build();
        Node document = parser.parse(
            FileUtils.readLines(new File(filePath), "UTF-8")
            .stream().collect(Collectors.joining("\n"))
        );
        HtmlRenderer renderer = HtmlRenderer.builder().build();
        String html = renderer.render(document);  // "<p>This is <em>Sparta</em></p>\n"
        model.addAttribute("article", html);
        model.addAttribute("breadcrumb", new BreadcrumVO(Application.ROOT, filePath));
        return "markdown";
    }

    private String getCurrentDirectoryPath(HttpServletRequest req) {
        String path = (String)req.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        return Application.ROOT + path.substring(2);
    }

    private String getCurrentFilePath(HttpServletRequest req) {
        String path = (String)req.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        return Application.ROOT + path;
    }
}
