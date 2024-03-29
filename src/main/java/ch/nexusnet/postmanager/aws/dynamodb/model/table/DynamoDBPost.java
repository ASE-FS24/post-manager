package ch.nexusnet.postmanager.aws.dynamodb.model.table;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAutoGeneratedKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
@DynamoDBTable(tableName = "Posts")
public class DynamoDBPost {
    @DynamoDBHashKey(attributeName = "id")
    @DynamoDBAutoGeneratedKey
    private String id;

    @DynamoDBAttribute(attributeName = "authorId")
    private String authorId;

    @DynamoDBAttribute(attributeName = "type")
    private String type;

    @DynamoDBAttribute(attributeName = "title")
    private String title;

    @DynamoDBAttribute(attributeName = "image")
    private String image;

    @DynamoDBAttribute(attributeName = "shortDescription")
    private String shortDescription;

    @DynamoDBAttribute(attributeName = "description")
    private String description;

    @DynamoDBAttribute(attributeName = "likeNumber")
    private int likeNumber = 0;

    @DynamoDBAttribute(attributeName = "hashtags")
    private List<String> hashtags = new ArrayList<>();

    @DynamoDBAttribute(attributeName = "createdDateTime")
    private String createdDateTime;

    @DynamoDBAttribute(attributeName = "edited")
    private boolean edited;

    @DynamoDBAttribute(attributeName = "editedDateTime")
    private String editedDateTime;

}
