package ch.nexusnet.postmanager.service;

import ch.nexusnet.postmanager.aws.dynamodb.model.mapper.CommentMapper;
import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBComment;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBCommentRepository;
import ch.nexusnet.postmanager.exception.ResourceNotFoundException;
import ch.nexusnet.postmanager.model.Comment;
import ch.nexusnet.postmanager.model.dto.CreateCommentDTO;
import ch.nexusnet.postmanager.model.dto.UpdateCommentDTO;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    private final DynamoDBCommentRepository dynamoDBCommentRepository;

    private final AmazonDynamoDB amazonDynamoDB;

    private final ZoneId appZoneId;

    public CommentServiceImpl(DynamoDBCommentRepository dynamoDBCommentRepository, AmazonDynamoDB amazonDynamoDB, @Value("${app.timezone:CET}") ZoneId appZoneId) {
        this.dynamoDBCommentRepository = dynamoDBCommentRepository;
        this.amazonDynamoDB = amazonDynamoDB;
        this.appZoneId = appZoneId;
    }

    /**
     * Creates a comment based on the specified CreateCommentDTO object.
     *
     * @param createCommentDTO the data transfer object containing the comment details
     * @return the created Comment object
     * @throws ResourceNotFoundException if the post with the specified postId is not found
     */
    @Override
    @Transactional
    public Comment createComment(CreateCommentDTO createCommentDTO) {
        DynamoDBComment dynamoDBComment = CommentMapper.convertCreateCommentDTOToDynamoDBComment(createCommentDTO);

        DynamoDBComment savedComment = dynamoDBCommentRepository.save(dynamoDBComment);

        changePostsCommentNumber(createCommentDTO.getPostId(), 1);
        return CommentMapper.convertDynamoDBCommentToComment(savedComment);
    }

    /**
     * Finds all comments associated with a specific post ID.
     *
     * @param postId the ID of the post to retrieve comments for
     * @return a list of Comment objects associated with the specified post ID
     */
    @Override
    public List<Comment> findAllCommentsByPostId(String postId) {
        return mapDynamoDBCommentsToComments(dynamoDBCommentRepository.findByPostId(postId));
    }

    /**
     * Finds all comments associated with a specific author ID.
     *
     * @param authorID the ID of the author to retrieve comments for
     * @return a list of Comment objects associated with the specified author ID
     */
    @Override
    public List<Comment> findAllCommentsByAuthorId(String authorID) {
        return mapDynamoDBCommentsToComments(dynamoDBCommentRepository.findByAuthorId(authorID));
    }

    /**
     * Updates the content of a comment with the specified commentId.
     *
     * @param commentId the ID of the comment to update
     * @param comment   the updated content for the comment
     * @return the updated Comment object
     */
    @Override
    @Transactional
    public Comment updateComment(String commentId, UpdateCommentDTO comment) {
        DynamoDBComment existingComment = findDynamoDBCommentById(commentId);
        existingComment.setContent(comment.getContent());
        return CommentMapper.convertDynamoDBCommentToComment(dynamoDBCommentRepository.save(existingComment));
    }

    /**
     * Deletes the comment with the specified commentId. This method performs the following operations:
     * 1. Find the DynamoDBComment object by commentId using the findDynamoDBCommentById method.
     * 2. Find the corresponding DynamoDBPost object by postId from the DynamoDBComment object.
     * 3. Decrease the commentNumber of the DynamoDBPost object using the decreaseCommentNumber method.
     * 4. Save the updated DynamoDBPost object.
     * 5. Delete the DynamoDBComment object by commentId using the dynamoDBCommentRepository.
     *
     * @param commentId the ID of the comment to delete
     * @throws ResourceNotFoundException if the comment with the specified commentId is not found
     */
    @Override
    @Transactional
    public void deleteComment(String commentId) {
        DynamoDBComment dynamoDBComment = findDynamoDBCommentById(commentId);
        dynamoDBCommentRepository.deleteById(commentId);
        changePostsCommentNumber(dynamoDBComment.getPostId(), -1);
    }

    private List<Comment> mapDynamoDBCommentsToComments(List<DynamoDBComment> dynamoDBComments) {
        return dynamoDBComments.stream()
                .map(CommentMapper::convertDynamoDBCommentToComment)
                .collect(Collectors.toList());
    }

    private DynamoDBComment findDynamoDBCommentById(String id) {
        return dynamoDBCommentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Comment with id " + id + " not found"));
    }


    /**
     * Updates the comment number of a post in the Posts table in DynamoDB.
     *
     * @param postId      the ID of the post to update the comment number for
     * @param changeValue the value by which to change the comment number (positive or negative)
     */
    private void changePostsCommentNumber(String postId, int changeValue) {
        Map<String, AttributeValue> key = new HashMap<>();
        key.put("id", new AttributeValue().withS(postId));

        Map<String, AttributeValueUpdate> attributeUpdates = new HashMap<>();

        attributeUpdates.put("commentNumber",
                new AttributeValueUpdate().withAction(AttributeAction.ADD)
                        .withValue(new AttributeValue().withN(String.valueOf(changeValue))));

        UpdateItemRequest updateItemRequest = new UpdateItemRequest()
                .withTableName("Posts")
                .withKey(key)
                .withAttributeUpdates(attributeUpdates);

        amazonDynamoDB.updateItem(updateItemRequest);
    }
}
