package com.changgou.controller;

import com.changgou.file.FastDFSFile;
import com.changgou.util.FastDFSClient;
import org.apache.commons.io.FilenameUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * @ClassName FileController
 * @Description
 * @Author 传智播客
 * @Date 11:14 2019/8/12
 * @Version 2.1
 **/
@RestController
public class FileController {

    /**
     * @author 栗子
     * @Description 附件上传
     * @Date 11:24 2019/8/12
     * @param file
     * @return java.lang.String
     **/
    /*
    @PostMapping("/upload")
    public String upload(MultipartFile file) throws Exception {

        String name = file.getOriginalFilename();
        byte[] content = file.getBytes();
        String ext = FilenameUtils.getExtension(name);
        FastDFSFile fastDFSFile = new FastDFSFile(name, ext, content);
        // 调用工具类：所在的组 以及 附件所在的目录
        String[] uploadResult = FastDFSClient.uploadFile(fastDFSFile);
        // 拼接该附件的url地址   http://ip:port/xxx/xxx.../xxx.jpg
        String url = FastDFSClient.getServerUrl() + "/" + uploadResult[0] + "/" + uploadResult[1];
        return url;
    }
    */
    @PostMapping("/upload")
    public String upload(MultipartFile file) throws Exception {

        byte[] file_buff = file.getBytes();
        String filename = file.getOriginalFilename();
        String file_ext_name = FilenameUtils.getExtension(filename);
        // 获取到用户信息
        // 调用工具类：所在的组 以及 附件所在的目录
        String[] uploadResult = FastDFSClient.uploadFile(file_buff, file_ext_name, "group1");
        // 拼接该附件的url地址   http://ip:port/xxx/xxx.../xxx.jpg
        String url = FastDFSClient.getTrackerUrl() + "/" + uploadResult[0] + "/" + uploadResult[1];
        return url;
    }
}
