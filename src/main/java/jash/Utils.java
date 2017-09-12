package jash;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.springframework.util.StringUtils;

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
}
