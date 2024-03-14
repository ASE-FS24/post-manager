package ch.nexusnet.postmanager.aws.dynamodb.model.table;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.*;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DynamoDBTableInitializer {

    private final DynamoDBMapper dynamoDBMapper;
    private final AmazonDynamoDB amazonDynamoDB;

    @Autowired
    public DynamoDBTableInitializer(DynamoDBMapper dynamoDBMapper, AmazonDynamoDB amazonDynamoDB) {
        this.dynamoDBMapper = dynamoDBMapper;
        this.amazonDynamoDB = amazonDynamoDB;
    }

    @PostConstruct
    public void init() {
        CreateTableRequest tableRequest = dynamoDBMapper
                .generateCreateTableRequest(DynamoDBPost.class)
                .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));

        try {
            DescribeTableRequest describeTableRequest = new DescribeTableRequest()
                    .withTableName("Posts");
            DescribeTableResult describeTableResult = amazonDynamoDB.describeTable(describeTableRequest);

            System.out.println("Table already exists. Table status: " + describeTableResult.getTable().getTableStatus());
        } catch (ResourceNotFoundException e) {
            amazonDynamoDB.createTable(tableRequest);
            System.out.println("Created DynamoDB table: Posts");
        }
    }
}

