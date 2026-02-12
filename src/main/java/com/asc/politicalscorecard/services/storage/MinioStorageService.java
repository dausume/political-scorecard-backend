package com.asc.politicalscorecard.services.storage;

import io.minio.*;
import io.minio.http.Method;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

@Service
public class MinioStorageService {

    private static final Logger logger = Logger.getLogger(MinioStorageService.class.getName());

    private final MinioClient minioClient;
    private final String bucket;

    public MinioStorageService(@Qualifier("minioClient") MinioClient minioClient, @Qualifier("minioBucket") String bucket) {
        this.minioClient = minioClient;
        this.bucket = bucket;
    }

    public void uploadFile(String objectKey, InputStream inputStream, String contentType) {
        try {
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .stream(inputStream, -1, 10485760)
                            .contentType(contentType)
                            .build()
            );
            logger.info("Uploaded file to MinIO: " + objectKey);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error uploading file to MinIO: ", e);
            throw new RuntimeException("Failed to upload file to MinIO", e);
        }
    }

    public InputStream downloadFile(String objectKey) {
        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .build()
            );
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error downloading file from MinIO: ", e);
            throw new RuntimeException("Failed to download file from MinIO", e);
        }
    }

    public void deleteFile(String objectKey) {
        try {
            minioClient.removeObject(
                    RemoveObjectArgs.builder()
                            .bucket(bucket)
                            .object(objectKey)
                            .build()
            );
            logger.info("Deleted file from MinIO: " + objectKey);
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error deleting file from MinIO: ", e);
            throw new RuntimeException("Failed to delete file from MinIO", e);
        }
    }

    public String getPresignedUrl(String objectKey) {
        try {
            return minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                            .method(Method.GET)
                            .bucket(bucket)
                            .object(objectKey)
                            .expiry(1, TimeUnit.HOURS)
                            .build()
            );
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Error generating presigned URL: ", e);
            throw new RuntimeException("Failed to generate presigned URL", e);
        }
    }
}
