package jash.service;

import java.util.Date;

import lombok.Value;

@Value
public class CacheItem<T> {
    private T item;
    private long lastModified;
}
