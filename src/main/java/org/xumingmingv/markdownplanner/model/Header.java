package org.xumingmingv.markdownplanner.model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import lombok.Value;

@Value
public class Header {
    private List<LeveledHeader> headers;
    public Header(List<LeveledHeader> headers) {
        this.headers = headers.stream()
            .sorted(Comparator.comparingInt(LeveledHeader::getLevel))
            .collect(Collectors.toList());
    }

    public static Header create(String... headers) {
        List<LeveledHeader> headerObjs = new ArrayList<>(headers.length);
        for (int i = 0; i < headers.length; i++) {
            headerObjs.add(new LeveledHeader(i, headers[i]));
        }
        return new Header(headerObjs);
    }

    public Header addLeveledHeader(LeveledHeader leveledHeader) {
        List<LeveledHeader> newHeaders = new ArrayList<>();
        int i = 0;
        for (; i < headers.size(); i++) {
            if (headers.get(i).getLevel() >= leveledHeader.getLevel()) {
                break;
            }
        }
        newHeaders.addAll(headers.subList(0, i));
        newHeaders.add(leveledHeader);
        return new Header(newHeaders);
    }

    public String getDisplay() {
        return this.headers.stream().map(LeveledHeader::getDisplay)
            .collect(Collectors.joining(" :: "));
    }
}
