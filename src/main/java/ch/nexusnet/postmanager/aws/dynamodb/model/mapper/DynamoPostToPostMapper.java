package ch.nexusnet.postmanager.aws.dynamodb.model.mapper;

import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBPost;
import ch.nexusnet.postmanager.model.Post;
import ch.nexusnet.postmanager.model.PostStatus;
import ch.nexusnet.postmanager.model.PostType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DynamoPostToPostMapper {

    public static Post map(DynamoDBPost dynamoDBPost) {
        Post post = new Post();

        post.setId(dynamoDBPost.getId());
        post.setAuthorId(dynamoDBPost.getAuthorId());
        if (dynamoDBPost.getType() != null) {
            post.setType(PostType.valueOf(dynamoDBPost.getType()));
        }
        if (dynamoDBPost.getStatus() != null) {
            post.setStatus(PostStatus.valueOf(dynamoDBPost.getStatus()));
        }
        post.setTitle(dynamoDBPost.getTitle());
        post.setImage(dynamoDBPost.getImage());
        post.setShortDescription(dynamoDBPost.getShortDescription());
        post.setDescription(dynamoDBPost.getDescription());
        post.setLikeNumber(dynamoDBPost.getLikeNumber());
        post.setHashtags(dynamoDBPost.getHashtags());

        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        if (dynamoDBPost.getCreatedDateTime() != null && !dynamoDBPost.getCreatedDateTime().isEmpty()) {
            post.setCreatedDateTime(LocalDateTime.parse(dynamoDBPost.getCreatedDateTime(), formatter));
        }
        post.setEdited(dynamoDBPost.isEdited());
        if (dynamoDBPost.getEditedDateTime() != null && !dynamoDBPost.getEditedDateTime().isEmpty()) {
            post.setEditedDateTime(LocalDateTime.parse(dynamoDBPost.getEditedDateTime(), formatter));
        }

        return post;
    }
}
