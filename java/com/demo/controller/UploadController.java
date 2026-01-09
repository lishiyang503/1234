package com.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 图片上传Controller
 */
@RestController
@RequestMapping("/upload")
public class UploadController {

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
     * 上传图片
     * @param file 图片文件
     * @return 图片访问路径
     */
    @PostMapping("/image")
    public Map<String, Object> uploadImage(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 检查文件是否为空
            if (file.isEmpty()) {
                result.put("success", false);
                result.put("message", "上传文件不能为空");
                return result;
            }

            // 检查文件类型
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                result.put("success", false);
                result.put("message", "请上传图片类型文件");
                return result;
            }

            // 创建上传目录
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // 生成唯一文件名
            String originalFilename = file.getOriginalFilename();
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            String fileName = UUID.randomUUID().toString() + suffix;

            // 保存文件
            File dest = new File(uploadDir, fileName);
            file.transferTo(dest);

            // 构建图片访问路径
            String imageUrl = uploadPrefix + "/" + fileName;

            // 返回结果
            result.put("success", true);
            result.put("message", "上传成功");
            result.put("data", imageUrl);
            return result;
        } catch (IOException e) {
            result.put("success", false);
            result.put("message", "上传失败：" + e.getMessage());
            return result;
        }
    }
}
