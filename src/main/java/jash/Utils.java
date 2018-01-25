package jash;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
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
        return Application.ROOT + path.substring(5, path.length());
    }

    public static String getCurrentFilePath(HttpServletRequest req) {
        String path = (String)req.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        return Application.ROOT + path;
    }

    public static boolean isRequestingADir(HttpServletRequest req) {
        return new File(getCurrentDirectoryPath(req)).isDirectory();
    }

    public static List<String> readFile(String filePath) {
        List<String> lines;
        File file = new File(filePath);
        try (FileInputStream stream = new FileInputStream(file)) {
            lines = IOUtils.readLines(stream, "UTF-8");
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to read project: " + filePath);
        }
        return lines;
    }

    public static void writeFile(String filePath, String newContent) {
        List<String> lines = new ArrayList<>();
        File file = new File(filePath);
        try (FileOutputStream stream = new FileOutputStream(file)) {
            IOUtils.write(newContent, stream, "UTF-8");
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to write project: " + filePath);
        }
    }
}
