package jash.service;

public interface CacheService<T> {
    T get(String key);
    void set(String key, T value);
    long getLastModified(String key);
}
