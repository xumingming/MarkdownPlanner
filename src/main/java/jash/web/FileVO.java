package jash.web;

import lombok.Value;

@Value
public class FileVO {
    private String name;
    private String url;
    private boolean isDir;

    public String getUrl() {
        return isDir ? url + "/_" : url;
    }
}
