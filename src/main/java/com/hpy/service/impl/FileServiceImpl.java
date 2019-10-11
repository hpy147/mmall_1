package com.hpy.service.impl;

import com.google.common.collect.Lists;
import com.hpy.service.FileService;
import com.hpy.util.FTPUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

/**
 * Author: hpy
 * Date: 2019-10-11
 * Description: <描述>
 */
@Service(value = "fileService")
public class FileServiceImpl implements FileService {

    private Logger logger = LoggerFactory.getLogger(FileServiceImpl.class);

    @Override
    public String upload(MultipartFile file, String path) {
        // 原始文件名
        String originalFilename = file.getOriginalFilename();
        // 文件扩展名
        String fileExtensionName = originalFilename.substring(originalFilename.lastIndexOf("."));
        // 要上传的文件名
        String fileName = UUID.randomUUID().toString().replace("-", "") + fileExtensionName;

        File fileDir = new File(path);
        if (!fileDir.exists()) {
            fileDir.setWritable(true);
            fileDir.mkdirs();
        }

        File targetFile = new File(path, fileName);

        try {
            // 上传到 path 目录下
            file.transferTo(targetFile);

            // 上传到 ftp 服务器
            FTPUtils.uploadFile(Lists.newArrayList(targetFile));

            // 删除上传到 path 目录中的文件
            targetFile.delete();

            return targetFile.getName();

        } catch (IOException e) {
            logger.error("文件上传异常", e);
            return null;
        }
    }
}
