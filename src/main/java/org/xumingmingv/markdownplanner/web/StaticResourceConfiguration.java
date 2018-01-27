package org.xumingmingv.markdownplanner.web;

import org.xumingmingv.markdownplanner.Application;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * Make Jash serve images.
 */
@Configuration
public class StaticResourceConfiguration extends WebMvcConfigurerAdapter {
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
            .addResourceLocations("file:" + Application.IMAGE_DIR + "/");
        super.addResourceHandlers(registry);
    }
}
