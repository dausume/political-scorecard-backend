package com.asc.politicalscorecard.databases.datasourceinitializers.miniodatasource;

import io.minio.BucketExistsArgs;
import io.minio.MinioClient;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.asc.politicalscorecard.databases.InitializationState;

@Component
@Scope("singleton")
public class MinioDatasourceInitializer {

    private final MinioClient minioClient;
    private final ApplicationContext applicationContext;
    private final InitializationState initializationState;
    private String minioBucketName = "";

    public MinioDatasourceInitializer(
        MinioClient minioClient,
        ApplicationContext applicationContext,
        InitializationState initializationState
    ) {
        this.minioClient = minioClient;
        this.applicationContext = applicationContext;
        this.initializationState = initializationState;
        this.minioBucketName = this.initializationState.getMinioDatabaseName();
    }

    public void createBucketIfNotExists() {
        System.out.println("In createBucketIfNotExists for MinioDatasourceInitializer.");
        try {
            // Verify MinIO is reachable by attempting a bucket existence check
            boolean exists = minioClient.bucketExists(
                BucketExistsArgs.builder().bucket(minioBucketName).build()
            );
            System.out.println("MinIO is reachable. Bucket '" + minioBucketName + "' exists: " + exists);
            initializationState.setInitializedMinioDatabase(true);
        } catch (Exception e) {
            System.out.println("Error connecting to MinIO: " + e.getMessage());
        }
    }

    public void initialize() {
        System.out.println("Initializing buckets for MinioDatasourceInitializer.");

        // Initialize all buckets (MinIO's equivalent of tables).
        // Future buckets would be added here alongside legislation.
        LegislationBucketInitializer legislationBucketInitializer = new LegislationBucketInitializer(minioClient, minioBucketName);
        legislationBucketInitializer.initializeBucket();

        initializationState.setInitializedMinioTables(true);
    }
}
