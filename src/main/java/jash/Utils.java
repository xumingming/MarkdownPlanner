package jash;

import java.io.File;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerMapping;

public class Utils {
    public static String getFileDisplayName(File file) {
        try {
            if (file.isFile()) {
                if (file.getName().endsWith(".md")) {
                    String firstLine = FileUtils.readLines(file, "UTF-8").get(0);
                    String candidate = StringUtils.trimLeadingCharacter(firstLine, '#').trim();

                    return candidate == null ? file.getName() : candidate;
                }
                return file.getName();
            } else {
                if (new File(file.getPath() + "/.name").exists()) {
                    return FileUtils.readLines(new File(file.getPath() + "/" + ".name"), "UTF-8")
                        .get(0);
                } else {
                    return file.getName();
                }
            }
        } catch (Exception e) {
            return "UNKNOWN";
        }
    }

    public static String getCurrentDirectoryPath(HttpServletRequest req) {
        String path = (String)req.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        return Application.ROOT + path.substring(0, path.length() - 2);
    }

    public static String getCurrentFilePath(HttpServletRequest req) {
        String path = (String)req.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        return Application.ROOT + path;
    }

    public static boolean isRequestingADir(HttpServletRequest req) {
        return new File(getCurrentDirectoryPath(req)).isDirectory();
    }
}
