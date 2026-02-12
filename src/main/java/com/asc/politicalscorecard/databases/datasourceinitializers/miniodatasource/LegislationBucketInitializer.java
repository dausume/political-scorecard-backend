package com.asc.politicalscorecard.databases.datasourceinitializers.miniodatasource;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;

/**
 * Initializes the legislation bucket in MinIO.
 * Buckets are MinIO's equivalent of tables — each bucket is a distinct storage namespace.
 */
public class LegislationBucketInitializer {

    private final MinioClient minioClient;
    private final String bucketName;

    public LegislationBucketInitializer(MinioClient minioClient, String bucketName) {
        this.minioClient = minioClient;
        this.bucketName = bucketName;
    }

    public void initializeBucket() {
        System.out.println("Initializing legislation bucket...");
        try {
            boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(bucketName).build()
            );
            if (!exists) {
                minioClient.makeBucket(
                    MakeBucketArgs.builder().bucket(bucketName).build()
                );
                System.out.println("Legislation bucket created: " + bucketName);
            } else {
                System.out.println("Legislation bucket already exists: " + bucketName);
            }
        } catch (Exception e) {
            System.out.println("Error creating legislation bucket: " + e.getMessage());
        }
    }
}
