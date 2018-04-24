package org.xumingmingv.markdownplanner.service;

public interface ConfigService {
    /**
     * 可以编辑吗?
     * @return
     */
    boolean isEditEnabled();

    /**
     * 是否使用缓存
     * @return
     */
    boolean isCacheEnabled();
}
