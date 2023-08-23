package com.tuling.miniodemo.controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSON;
import com.tuling.miniodemo.common.Res;
import io.minio.DownloadObjectArgs;
import io.minio.GetObjectArgs;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.Result;
import io.minio.StatObjectArgs;
import io.minio.StatObjectResponse;
import io.minio.UploadObjectArgs;
import io.minio.errors.MinioException;
import io.minio.messages.Item;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Fox
 */
@RestController
@Slf4j
public class MinioController {

    @Autowired
    private MinioClient minioClient;

    @Value("${minio.bucketName}")
    private String bucketName;


    @GetMapping("/list")
    public List<Object> list() throws Exception {
        //获取bucket列表
        Iterable<Result<Item>> myObjects = minioClient.listObjects(
                ListObjectsArgs.builder().bucket(bucketName).build());
        Iterator<Result<Item>> iterator = myObjects.iterator();
        List<Object> items = new ArrayList<>();
        String format = "{'fileName':'%s','fileSize':'%s'}";
        while (iterator.hasNext()) {
            Item item = iterator.next().get();
            items.add(JSON.parse(String.format(format, item.objectName(), formatFileSize(item.size()))));
        }
        return items;
    }

    @PostMapping("/upload")
    public Res upload(@RequestParam(name = "file", required = false) MultipartFile[] file) {


        if (file == null || file.length == 0) {
            return Res.error("上传文件不能为空");
        }

        List<String> orgfileNameList = new ArrayList<>(file.length);

        for (MultipartFile multipartFile : file) {
            String orgfileName = multipartFile.getOriginalFilename();
            orgfileNameList.add(orgfileName);
            try {
                //文件上传
                InputStream in = multipartFile.getInputStream();
                minioClient.putObject(
                        PutObjectArgs.builder().bucket(bucketName).object(orgfileName).stream(
                                in, multipartFile.getSize(), -1)
                                .contentType(multipartFile.getContentType())
                                .build());
                in.close();

            } catch (Exception e) {
                log.error(e.getMessage());
                return Res.error("上传失败");
            }
        }

        Map<String, Object> data = new HashMap<String, Object>();
        data.put("bucketName", bucketName);
        data.put("fileName", orgfileNameList);
        return Res.ok("上传成功",data);
    }

    @RequestMapping("/download/{fileName}")
    public void download(HttpServletResponse response, @PathVariable("fileName") String fileName) {

        InputStream in = null;
        try {
            // 获取对象信息
            StatObjectResponse stat = minioClient.statObject(
                    StatObjectArgs.builder().bucket(bucketName).object(fileName).build());
            response.setContentType(stat.contentType());
            response.setHeader("Content-Disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            //文件下载
            in = minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build());
            IOUtils.copy(in, response.getOutputStream());
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    log.error(e.getMessage());
                }
            }
        }

    }

    @DeleteMapping("/delete/{fileName}")
    public Res delete(@PathVariable("fileName") String fileName) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder().bucket(bucketName).object(fileName).build());
        } catch (Exception e) {
            log.error(e.getMessage());
            return Res.error("删除失败");
        }
        return Res.ok("删除成功",null);
    }

    private static String formatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + " B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + " KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + " MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + " GB";
        }
        return fileSizeString;
    }

}
