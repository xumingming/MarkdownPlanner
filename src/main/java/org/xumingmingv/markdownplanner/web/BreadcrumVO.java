package org.xumingmingv.markdownplanner.web;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Value;
import org.apache.tomcat.util.buf.StringUtils;
import org.xumingmingv.markdownplanner.Utils;

@Value
public class BreadcrumVO {
    private String root;
    private String path;

    public List<FileVO> getLinks() {
        List<FileVO> ret = new ArrayList<>();
        String tmp = path.substring(root.length());
        List<String> parts = Arrays.stream(tmp.split("/")).collect(Collectors.toList());
        for (int i = 0; i < parts.size(); i++) {
            FileVO file = new FileVO(
                Utils.getFileDisplayName(
                    new File(
                        root + "/" + StringUtils.join(parts.subList(0, i + 1), '/')
                    )
                ),
                StringUtils.join(parts.subList(0, i + 1), '/'),
                i < parts.size() - 1
            );
            ret.add(file);
        }

        return ret;
    }
}
