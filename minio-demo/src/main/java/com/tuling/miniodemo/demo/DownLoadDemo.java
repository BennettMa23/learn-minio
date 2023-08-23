package com.tuling.miniodemo.demo;


import io.minio.DownloadObjectArgs;

import io.minio.MinioClient;


/**
 * @author Fox
 */
public class DownLoadDemo {

    public static void main(String[] args) {

        // Create a minioClient with the MinIO server playground, its access key and secret key.
        MinioClient minioClient =
                MinioClient.builder()
                        .endpoint("http://192.168.3.14:9000")
                        .credentials("admin", "12345678")
                        .build();

        // Download object given the bucket, object name and output file name
        try {
            minioClient.downloadObject(
                    DownloadObjectArgs.builder()
                            .bucket("tulingmall")
                            .object("fox.jpg")
                            .filename("minio-demo/fox.jpg")
                            .build());

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
