package ch.nexusnet.postmanager.service;

import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBPost;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBCommentRepository;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBLikeRepository;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBPostRepository;
import ch.nexusnet.postmanager.aws.s3.config.S3ClientConfiguration;
import ch.nexusnet.postmanager.exception.ResourceNotFoundException;
import ch.nexusnet.postmanager.model.Post;
import ch.nexusnet.postmanager.model.dto.CreatePostDTO;
import ch.nexusnet.postmanager.model.dto.UpdatePostDTO;
import ch.nexusnet.postmanager.utils.TestDataUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneId;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class PostServiceImplTest {

    @Captor
    ArgumentCaptor<DynamoDBPost> dynamoDBPostCaptor;
    @Mock
    private DynamoDBPostRepository dynamoDBPostRepository;
    @Mock
    private DynamoDBCommentRepository dynamoDBCommentRepository;
    @Mock
    private DynamoDBLikeRepository dynamoDBLikeRepository;
    @Mock
    private S3ClientConfiguration s3ClientConfig;

    private PostServiceImpl postService;
    private CreatePostDTO sampleCreatePostDTO;
    private UpdatePostDTO sampleUpdatePostDTO;
    private DynamoDBPost sampleDynamoDBPost;

    @BeforeEach
    void setUp() {
        postService = new PostServiceImpl(dynamoDBPostRepository, dynamoDBLikeRepository, dynamoDBCommentRepository, ZoneId.of("CET"), s3ClientConfig);
        sampleCreatePostDTO = TestDataUtils.createSampleCreatePostDTO();
        sampleUpdatePostDTO = TestDataUtils.createSampleUpdatePostDTO();
        sampleDynamoDBPost = TestDataUtils.createSampleDynamoDBPost();
    }

    @Test
    void createPost_Success() {
        CreatePostDTO createPostDTO = sampleCreatePostDTO;

        DynamoDBPost mockDynamoDBPost = sampleDynamoDBPost;
        given(dynamoDBPostRepository.save(any(DynamoDBPost.class))).willReturn(mockDynamoDBPost);

        Post resultPost = postService.createPost(createPostDTO);

        verify(dynamoDBPostRepository).save(dynamoDBPostCaptor.capture());
        DynamoDBPost savedDynamoDBPost = dynamoDBPostCaptor.getValue();

        assertEquals(TestDataUtils.DEFAULT_AUTHOR_ID, savedDynamoDBPost.getAuthorId());
        assertEquals(TestDataUtils.DEFAULT_POST_TYPE.name(), savedDynamoDBPost.getType());
        assertEquals(TestDataUtils.DEFAULT_POST_STATUS.name(), savedDynamoDBPost.getStatus());
        assertEquals(TestDataUtils.DEFAULT_TITLE, savedDynamoDBPost.getTitle());
        assertEquals(TestDataUtils.DEFAULT_IMAGE, savedDynamoDBPost.getImage());
        assertEquals(TestDataUtils.DEFAULT_SHORT_DESCRIPTION, savedDynamoDBPost.getShortDescription());
        assertEquals(TestDataUtils.DEFAULT_DESCRIPTION, savedDynamoDBPost.getDescription());
        assertEquals(TestDataUtils.DEFAULT_LIKE_NUMBER, savedDynamoDBPost.getLikeNumber());
        assertEquals(TestDataUtils.DEFAULT_HASHTAGS, savedDynamoDBPost.getHashtags());
        assertNotNull(savedDynamoDBPost.getCreatedDateTime());
        assertFalse(savedDynamoDBPost.isEdited());
        assertNull(savedDynamoDBPost.getEditedDateTime());

        assertNotNull(resultPost.getId());
        assertEquals(TestDataUtils.DEFAULT_AUTHOR_ID, resultPost.getAuthorId());
        assertEquals(TestDataUtils.DEFAULT_POST_TYPE, resultPost.getType());
        assertEquals(TestDataUtils.DEFAULT_POST_STATUS, resultPost.getStatus());
        assertEquals(TestDataUtils.DEFAULT_TITLE, resultPost.getTitle());
        assertEquals(TestDataUtils.DEFAULT_IMAGE, resultPost.getImage());
        assertEquals(TestDataUtils.DEFAULT_SHORT_DESCRIPTION, resultPost.getShortDescription());
        assertEquals(TestDataUtils.DEFAULT_DESCRIPTION, resultPost.getDescription());
        assertEquals(TestDataUtils.DEFAULT_LIKE_NUMBER, resultPost.getLikeNumber());
        assertEquals(TestDataUtils.DEFAULT_HASHTAGS, resultPost.getHashtags());
        assertNotNull(resultPost.getCreatedDateTime());
        assertFalse(resultPost.isEdited());
        assertNull(resultPost.getEditedDateTime());
    }

    @Test
    void findAllPosts_Success() {
        List<DynamoDBPost> dbPosts = Arrays.asList(sampleDynamoDBPost, sampleDynamoDBPost);
        given(dynamoDBPostRepository.findAll()).willReturn(dbPosts);

        List<Post> posts = postService.findAllPosts();

        assertNotNull(posts);
        assertEquals(dbPosts.size(), posts.size());
    }

    @Test
    void findById_PostExists() {
        DynamoDBPost dbPost = sampleDynamoDBPost;
        given(dynamoDBPostRepository.findById(dbPost.getId())).willReturn(Optional.of(dbPost));

        Post post = postService.findById(dbPost.getId());

        assertNotNull(post);
        assertEquals(dbPost.getTitle(), post.getTitle());
    }

    @Test
    void findById_PostNotFound_ThrowsException() {
        given(dynamoDBPostRepository.findById(sampleDynamoDBPost.getId())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.findById(sampleDynamoDBPost.getId()));
    }

    @Test
    void findByAuthorId_Success() {
        List<DynamoDBPost> dbPosts = Arrays.asList(sampleDynamoDBPost, sampleDynamoDBPost);
        given(dynamoDBPostRepository.findByAuthorId(TestDataUtils.DEFAULT_AUTHOR_ID)).willReturn(dbPosts);

        List<Post> posts = postService.findByAuthorId(TestDataUtils.DEFAULT_AUTHOR_ID);

        assertNotNull(posts);
        assertEquals(dbPosts.size(), posts.size());
    }

    @Test
    void updatePost_Success() {
        UpdatePostDTO updatePostDTO = sampleUpdatePostDTO;
        Optional<DynamoDBPost> optionalDynamoDBPost = Optional.of(sampleDynamoDBPost);
        given(dynamoDBPostRepository.findById(sampleDynamoDBPost.getId())).willReturn(optionalDynamoDBPost);
        given(dynamoDBPostRepository.save(any(DynamoDBPost.class))).willReturn(sampleDynamoDBPost);

        postService.updatePost(sampleDynamoDBPost.getId(), updatePostDTO);

        verify(dynamoDBPostRepository).save(dynamoDBPostCaptor.capture());
        DynamoDBPost savedDynamoDBPost = dynamoDBPostCaptor.getValue();

        assertEquals(TestDataUtils.DEFAULT_AUTHOR_ID, savedDynamoDBPost.getAuthorId());
        assertEquals(TestDataUtils.UPDATED_POST_TYPE.name(), savedDynamoDBPost.getType());
        assertEquals(TestDataUtils.UPDATED_POST_STATUS.name(), savedDynamoDBPost.getStatus());
        assertEquals(TestDataUtils.UPDATED_TITLE, savedDynamoDBPost.getTitle());
        assertEquals(TestDataUtils.UPDATED_IMAGE, savedDynamoDBPost.getImage());
        assertEquals(TestDataUtils.UPDATED_SHORT_DESCRIPTION, savedDynamoDBPost.getShortDescription());
        assertEquals(TestDataUtils.UPDATED_DESCRIPTION, savedDynamoDBPost.getDescription());
        assertEquals(TestDataUtils.DEFAULT_LIKE_NUMBER, savedDynamoDBPost.getLikeNumber());
        assertEquals(TestDataUtils.UPDATED_HASHTAGS, savedDynamoDBPost.getHashtags());
        assertNotNull(savedDynamoDBPost.getCreatedDateTime());
        assertTrue(savedDynamoDBPost.isEdited());
        assertNotNull(savedDynamoDBPost.getEditedDateTime());
    }

    @Test
    void updatePost_NotFound_ThrowsException() {
        given(dynamoDBPostRepository.findById(sampleDynamoDBPost.getId())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.updatePost(sampleDynamoDBPost.getId(), new UpdatePostDTO()));
    }

    @Test
    void deletePost_PostExists() {
        given(dynamoDBPostRepository.findById(sampleDynamoDBPost.getId())).willReturn(Optional.of(new DynamoDBPost()));

        assertDoesNotThrow(() -> postService.deletePost(sampleDynamoDBPost.getId()));
        verify(dynamoDBPostRepository).deleteById(sampleDynamoDBPost.getId());
    }

    @Test
    void deletePost_PostNotFound_ThrowsException() {
        given(dynamoDBPostRepository.findById(sampleDynamoDBPost.getId())).willReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.deletePost(sampleDynamoDBPost.getId()));
    }


}
