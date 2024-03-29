package com.yhr.enterprise.controller;

import com.aliyun.oss.OSSClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("upload")
public class UploadController {
    @Autowired
    private OSSClient client;

    @PostMapping("/oss")
    public String ossUpload(@RequestParam("file") MultipartFile file,String folder){

        String bucketName="mypic-12138";
        String fileName= folder+"/"+ UUID.randomUUID()+file.getOriginalFilename();

        try {
            client.putObject(bucketName,fileName,file.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "https://mypic-12138.oss-cn-beijing.aliyuncs.com/"+fileName;
    }
}
