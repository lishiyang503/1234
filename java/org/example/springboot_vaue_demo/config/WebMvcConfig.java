package org.example.springboot_vaue_demo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMvc配置类，用于配置静态资源访问路径
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    /**
     * 图片上传路径
     */
    @Value("${upload.path:/data/upload}")
    private String uploadPath;

    /**
     * 图片访问路径前缀
     */
    @Value("${upload.prefix:/upload}")
    private String uploadPrefix;

    /**
     * 配置静态资源访问路径
     * @param registry 资源处理注册表
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 配置上传图片的访问路径
        registry.addResourceHandler(uploadPrefix + "/**")
                .addResourceLocations("file:" + uploadPath + "/");
    }
}
