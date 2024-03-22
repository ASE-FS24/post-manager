package ch.nexusnet.postmanager.service;

import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBComment;
import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBPost;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBCommentRepository;
import ch.nexusnet.postmanager.exception.ResourceNotFoundException;
import ch.nexusnet.postmanager.model.Comment;
import ch.nexusnet.postmanager.model.dto.CreateCommentDTO;
import ch.nexusnet.postmanager.model.dto.UpdateCommentDTO;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CommentServiceImplTest {

    private final static String POST_ID = UUID.randomUUID().toString();
    private final static String AUTHOR_ID = UUID.randomUUID().toString();
    private final static String COMMENT_ID = UUID.randomUUID().toString();
    private final ZoneId appZoneId = ZoneId.of("CET");
    @Mock
    private DynamoDBCommentRepository dynamoDBCommentRepository;
    @Mock
    private AmazonDynamoDB amazonDynamoDB;
    private CommentServiceImpl commentService;

    @BeforeEach
    public void setUp() {
        commentService = new CommentServiceImpl(dynamoDBCommentRepository, amazonDynamoDB, appZoneId);
    }

    @Test
    void shouldCreateComment() {
        CreateCommentDTO createCommentDTO = new CreateCommentDTO();
        createCommentDTO.setAuthorId(AUTHOR_ID);
        createCommentDTO.setPostId(POST_ID);
        createCommentDTO.setContent("Content");

        DynamoDBComment dbComment = new DynamoDBComment();
        dbComment.setCommentId(COMMENT_ID);
        dbComment.setAuthorId(createCommentDTO.getAuthorId());
        dbComment.setPostId(createCommentDTO.getPostId());
        dbComment.setContent(createCommentDTO.getContent());

        ArgumentCaptor<DynamoDBComment> captor = ArgumentCaptor.forClass(DynamoDBComment.class);

        DynamoDBPost dynamoDBPost = mock(DynamoDBPost.class);

        Mockito.when(dynamoDBCommentRepository.save(any(DynamoDBComment.class))).thenReturn(dbComment);

        Comment actualComment = commentService.createComment(createCommentDTO);

        Mockito.verify(dynamoDBCommentRepository).save(captor.capture());
        Mockito.verify(amazonDynamoDB).updateItem(any(UpdateItemRequest.class));

        DynamoDBComment captorValue = captor.getValue();
        assertEquals(createCommentDTO.getAuthorId(), captorValue.getAuthorId());
        assertEquals(createCommentDTO.getPostId(), captorValue.getPostId());
        assertEquals(createCommentDTO.getContent(), captorValue.getContent());

        assertEquals(dbComment.getCommentId(), actualComment.getId());
        assertEquals(createCommentDTO.getAuthorId(), actualComment.getAuthorId());
        assertEquals(createCommentDTO.getContent(), actualComment.getContent());
        assertEquals(createCommentDTO.getPostId(), actualComment.getPostId());
    }

    @Test
    void shouldFindAllCommentsByPostId() {
        List<DynamoDBComment> comments = new ArrayList<>();
        comments.add(new DynamoDBComment());
        Mockito.when(dynamoDBCommentRepository.findByPostId(eq(POST_ID))).thenReturn(comments);

        List<Comment> actualComments = commentService.findAllCommentsByPostId(POST_ID);

        assertEquals(1, actualComments.size());
        Mockito.verify(dynamoDBCommentRepository).findByPostId(anyString());
    }

    @Test
    void shouldReturnEmptyListWhenFindAllCommentsByPostIdForPostWithoutComments() {
        List<DynamoDBComment> comments = new ArrayList<>();
        Mockito.when(dynamoDBCommentRepository.findByPostId(eq(POST_ID))).thenReturn(comments);

        List<Comment> actualComments = commentService.findAllCommentsByPostId(POST_ID);

        assertTrue(actualComments.isEmpty());
        Mockito.verify(dynamoDBCommentRepository).findByPostId(eq(POST_ID));
    }

    @Test
    void shouldFindAllCommentsByAuthorId() {
        List<DynamoDBComment> comments = new ArrayList<>();
        comments.add(new DynamoDBComment());
        Mockito.when(dynamoDBCommentRepository.findByAuthorId(anyString())).thenReturn(comments);

        List<Comment> actualComments = commentService.findAllCommentsByAuthorId(AUTHOR_ID);

        assertEquals(1, actualComments.size());
        Mockito.verify(dynamoDBCommentRepository).findByAuthorId(anyString());
    }

    @Test
    void shouldReturnEmptyListWhenFindAllCommentsByAuthorIdForAuthorWithoutComments() {
        List<DynamoDBComment> comments = new ArrayList<>();
        Mockito.when(dynamoDBCommentRepository.findByAuthorId(anyString())).thenReturn(comments);

        List<Comment> actualComments = commentService.findAllCommentsByAuthorId(AUTHOR_ID);

        assertTrue(actualComments.isEmpty());
        Mockito.verify(dynamoDBCommentRepository).findByAuthorId(anyString());
    }

    @Test
    void shouldUpdateComment() {
        UpdateCommentDTO updateCommentDTO = new UpdateCommentDTO();
        updateCommentDTO.setContent("Updated Content");

        DynamoDBComment dbComment = new DynamoDBComment();
        dbComment.setCommentId(COMMENT_ID);
        dbComment.setContent("Old Content");

        DynamoDBComment updatedComment = new DynamoDBComment();
        updatedComment.setCommentId(COMMENT_ID);
        updatedComment.setContent(updateCommentDTO.getContent());

        when(dynamoDBCommentRepository.findById(eq(COMMENT_ID))).thenReturn(Optional.of(dbComment));
        when(dynamoDBCommentRepository.save(any(DynamoDBComment.class))).thenReturn(updatedComment);

        Comment actualComment = commentService.updateComment(COMMENT_ID, updateCommentDTO);

        assertEquals(updateCommentDTO.getContent(), actualComment.getContent());
    }

    @Test
    void shouldNotUpdateCommentDueToNoComment() {
        Mockito.when(dynamoDBCommentRepository.findById(eq(COMMENT_ID))).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.updateComment(COMMENT_ID, new UpdateCommentDTO()));

    }

    @Test
    void shouldDeleteComment() {
        DynamoDBComment dbComment = new DynamoDBComment();
        dbComment.setPostId(POST_ID);

        Mockito.when(dynamoDBCommentRepository.findById(eq(COMMENT_ID))).thenReturn(Optional.of(dbComment));

        commentService.deleteComment(COMMENT_ID);

        Mockito.verify(dynamoDBCommentRepository).deleteById(COMMENT_ID);
        Mockito.verify(amazonDynamoDB).updateItem(any(UpdateItemRequest.class));
    }

    @Test
    void shouldThrowExceptionWhenDeleteCommentDueNoComment() {
        Mockito.when(dynamoDBCommentRepository.findById(anyString())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> commentService.deleteComment(COMMENT_ID));
    }
}