package io.minio.examples;/*
 * MinIO Java SDK for Amazon S3 Compatible Cloud Storage, (C) 2017, 2018 MinIO, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import io.minio.MinioClient;
import io.minio.SetBucketNotificationArgs;
import io.minio.errors.MinioException;
import io.minio.messages.EventType;
import io.minio.messages.NotificationConfiguration;
import io.minio.messages.QueueConfiguration;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedList;
import java.util.List;

public class SetBucketNotification {
  /** MinioClient.setBucketNotification() example. */
  public static void main(String[] args)
      throws IOException, NoSuchAlgorithmException, InvalidKeyException {
    try {
      /* play.min.io for test and development. */
      MinioClient minioClient =
          MinioClient.builder()
              .endpoint("https://play.min.io")
              .credentials("Q3AM3UQ867SPQQA43P2F", "zuf+tfteSlswRu7BJ86wekitnifILbZam1KYY3TG")
              .build();

      /* Amazon S3: */
      // MinioClient minioClient =
      //     MinioClient.builder()
      //         .endpoint("https://s3.amazonaws.com")
      //         .credentials("YOUR-ACCESSKEY", "YOUR-SECRETACCESSKEY")
      //         .build();

      NotificationConfiguration config = new NotificationConfiguration();

      // Add a new SQS configuration.
      List<QueueConfiguration> queueConfigurationList = config.queueConfigurationList();
      QueueConfiguration queueConfiguration = new QueueConfiguration();
      queueConfiguration.setQueue("arn:minio:sqs::1:webhook");

      List<EventType> eventList = new LinkedList<>();
      eventList.add(EventType.OBJECT_CREATED_PUT);
      eventList.add(EventType.OBJECT_CREATED_COPY);
      queueConfiguration.setEvents(eventList);
      queueConfiguration.setPrefixRule("images");
      queueConfiguration.setSuffixRule("pg");

      queueConfigurationList.add(queueConfiguration);
      config.setQueueConfigurationList(queueConfigurationList);

      // Set updated notification configuration.
      minioClient.setBucketNotification(
          SetBucketNotificationArgs.builder().bucket("my-bucketname").config(config).build());
      System.out.println("Bucket notification is set successfully");
    } catch (MinioException e) {
      System.out.println("Error occurred: " + e);
    }
  }
}
