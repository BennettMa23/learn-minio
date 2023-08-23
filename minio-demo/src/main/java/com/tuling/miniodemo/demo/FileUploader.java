package com.tuling.miniodemo.demo;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.UploadObjectArgs;
import io.minio.errors.MinioException;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class FileUploader {
  public static void main(String[] args)
          throws IOException, NoSuchAlgorithmException, InvalidKeyException {
    try {
      // Create a minioClient with the MinIO server playground, its access key and secret key.
      MinioClient minioClient =
              MinioClient.builder()
                      .endpoint("http://192.168.3.14:9000")
                      .credentials("admin", "12345678")
                      .build();

      // 创建bucket
      String bucketName = "tulingmall";
      boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
      if (!found) {
        // 不存在，创建bucket
        minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
      }

      // 上传文件
//      minioClient.uploadObject(
//              UploadObjectArgs.builder()
//                      .bucket(bucketName)
//                      .object("tuling-mall-master.zip")
//                      .filename("F:\\mall\\tuling-mall-master.zip")
//                      .build());

      minioClient.uploadObject(
              UploadObjectArgs.builder()
                      .bucket(bucketName)
                      .object("fox.jpg")
                      .filename("fox.jpg") //本地磁盘路径
                      .build());


      System.out.println("上传文件成功");
    } catch (MinioException e) {
      System.out.println("Error occurred: " + e);
      System.out.println("HTTP trace: " + e.httpTrace());
    }
  }
}
