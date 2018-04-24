package org.xumingmingv.markdownplanner.service;

import java.io.File;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import org.xumingmingv.markdownplanner.model.IProject;
import org.xumingmingv.markdownplanner.model.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("projectCacheService")
public class ProjectCacheServiceImpl implements CacheService<IProject> {
    private static final Logger LOG = LoggerFactory.getLogger(ProjectCacheServiceImpl.class);
    private Map<String, CacheItem<IProject>> projectCache = new HashMap<>(16);

    @Override
    public IProject get(String key) {
        CacheItem<IProject> cachedProject = projectCache.get(key);
        if (cachedProject != null) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Found project from cache: " + cachedProject.getItem().getName());
            }

            if (cachedProject.getLastModified() == getLastModified(key)) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Project " + cachedProject.getItem().getName() + " is not modified since last retrieve");
                }
                return cachedProject.getItem();
            } else {
                LOG.info("Project " + cachedProject.getItem().getName() + " IS MODIFIED({} vs {}) since last retrieve",
                    Instant.ofEpochMilli(cachedProject.getLastModified()).atZone(ZoneId.systemDefault()),
                    Instant.ofEpochMilli(getLastModified(key)).atZone(ZoneId.systemDefault())
                );
            }
        }
        return null;
    }

    @Override
    public void set(String key, IProject value) {
        projectCache.put(key, new CacheItem<>(value, getLastModified(key)));
    }

    @Override
    public long getLastModified(String key) {
        return new File(key).lastModified();
    }
}
