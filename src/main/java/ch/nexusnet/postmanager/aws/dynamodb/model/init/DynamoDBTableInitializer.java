package ch.nexusnet.postmanager.aws.dynamodb.model.init;

import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBComment;
import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBLike;
import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBPost;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.*;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile({"dev", "test"})
public class DynamoDBTableInitializer {

    private final DynamoDBMapper dynamoDBMapper;
    private final AmazonDynamoDB amazonDynamoDB;
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    public DynamoDBTableInitializer(DynamoDBMapper dynamoDBMapper, AmazonDynamoDB amazonDynamoDB) {
        this.dynamoDBMapper = dynamoDBMapper;
        this.amazonDynamoDB = amazonDynamoDB;
    }

    @PostConstruct
    public void init() {
        createTable(DynamoDBPost.class);
        createTable(DynamoDBComment.class);
        createTable(DynamoDBLike.class);
    }

    private void createTable(Class<?> clazz) {
        CreateTableRequest tableRequest = dynamoDBMapper
                .generateCreateTableRequest(clazz)
                .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

        try {
            DescribeTableRequest describeTableRequest = new DescribeTableRequest()
                    .withTableName("Posts");
            DescribeTableResult describeTableResult = amazonDynamoDB.describeTable(describeTableRequest);

            log.info("Table already exists. Table status: " + describeTableResult.getTable().getTableStatus());
        } catch (ResourceNotFoundException e) {
            amazonDynamoDB.createTable(tableRequest);
            log.info("Created DynamoDB table: " + "Posts");
        }
    }
}

