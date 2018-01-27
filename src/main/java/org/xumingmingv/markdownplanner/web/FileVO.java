package org.xumingmingv.markdownplanner.web;

import lombok.Value;

@Value
public class FileVO {
    private String name;
    private String url;
    private boolean isDir;

    public String getUrl() {
        return isDir ? "/tree" + url  : url;
    }
}
