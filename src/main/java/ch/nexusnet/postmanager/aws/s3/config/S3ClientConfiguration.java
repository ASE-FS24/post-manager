package ch.nexusnet.postmanager.aws.s3.config;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import lombok.Getter;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
public class S3ClientConfiguration {

    private static final Regions region = Regions.US_EAST_1;
    private final String serviceEndpoint = System.getenv("AMAZON_S3_ENDPOINT");
    private final String accessKey = System.getenv("AMAZON_S3_ACCESS_KEY");
    private final String secretKey = System.getenv("AMAZON_S3_SECRET_KEY");
    @Getter
    private final String bucketName = System.getenv("AMAZON_PM_BUCKET");
    @Getter
    private AmazonS3 s3client;

    @PostConstruct
    private void init() {
        s3client = AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(
                                serviceEndpoint,
                                region.getName()
                        )
                )
                .withPathStyleAccessEnabled(true)
                .build();
    }
}
