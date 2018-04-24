package org.xumingmingv.markdownplanner.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

import com.alibaba.fastjson.JSONObject;

import org.apache.commons.io.IOUtils;
import org.apache.tomcat.util.buf.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service("configService")
public class ConfigServiceImpl implements ConfigService {
    private static final Logger LOG = LoggerFactory.getLogger(ConfigServiceImpl.class);

    @Override
    public boolean isEditEnabled() {
        JSONObject config = getConfig();
        if (config == null || !config.containsKey("editable")) {
            return false;
        }

        return config.getBoolean("editable");
    }

    @Override
    public boolean isCacheEnabled() {
        JSONObject config = getConfig();
        if (config == null || !config.containsKey("editable")) {
            return true;
        }

        return config.getBoolean("useCache");
    }

    static JSONObject getConfig() {
        if (!new File(getConfigFilePath()).exists()) {
            return null;
        }

        try (InputStream input = new FileInputStream(getConfigFilePath())) {
            List<String> configLines = IOUtils.readLines(input, Charset.forName("UTF-8"));
            String str = StringUtils.join(configLines, '\n');
            JSONObject config = JSONObject.parseObject(str);

            return config;
        } catch (Exception e) {
            LOG.error("", e);
            return null;
        }
    }

    private static String getConfigFilePath() {
        return getUserHome() + "/" + ".markdownplanner.json";
    }

    private static String getUserHome() {
        return System.getProperties().getProperty("user.home");
    }

    public static void main(String[] args) {
        System.out.println(new ConfigServiceImpl().isEditEnabled());
    }
}
