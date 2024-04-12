package ch.nexusnet.postmanager.service;

import ch.nexusnet.postmanager.aws.dynamodb.model.mapper.DynamoPostToPostMapper;
import ch.nexusnet.postmanager.aws.dynamodb.model.mapper.PostToDynamoPostMapper;
import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBPost;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBCommentRepository;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBLikeRepository;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBPostRepository;
import ch.nexusnet.postmanager.aws.s3.config.S3ClientConfiguration;
import ch.nexusnet.postmanager.exception.ResourceNotFoundException;
import ch.nexusnet.postmanager.model.Post;
import ch.nexusnet.postmanager.model.dto.CreatePostDTO;
import ch.nexusnet.postmanager.model.dto.UpdatePostDTO;
import ch.nexusnet.postmanager.util.IdGenerator;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PostServiceImpl implements PostService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    private final DynamoDBPostRepository dynamoDBPostRepository;

    private final DynamoDBLikeRepository dynamoDBLikeRepository;

    private final DynamoDBCommentRepository dynamoDBCommentRepository;

    private final ZoneId appZoneId;

    private final S3ClientConfiguration s3ClientConfig;

    public PostServiceImpl(DynamoDBPostRepository dynamoDBPostRepository, DynamoDBLikeRepository dynamoDBLikeRepository, DynamoDBCommentRepository dynamoDBCommentRepository, @Value("${app.timezone:CET}") ZoneId appZoneId, S3ClientConfiguration s3ClientConfig) {
        this.dynamoDBPostRepository = dynamoDBPostRepository;
        this.dynamoDBLikeRepository = dynamoDBLikeRepository;
        this.dynamoDBCommentRepository = dynamoDBCommentRepository;
        this.appZoneId = appZoneId;
        this.s3ClientConfig = s3ClientConfig;
    }

    @Override
    public Post createPost(CreatePostDTO createPostDTO) {
        DynamoDBPost dynamoDBPost = PostToDynamoPostMapper.createPostMap(createPostDTO);
        dynamoDBPost.setId(IdGenerator.generatePostId());
        dynamoDBPost.setCreatedDateTime(FORMATTER.format(LocalDateTime.now(appZoneId)));
        dynamoDBPost = dynamoDBPostRepository.save(dynamoDBPost);
        return DynamoPostToPostMapper.map(dynamoDBPost);
    }

    @Override
    public List<Post> findAllPosts() {
        return StreamSupport.stream(dynamoDBPostRepository.findAll().spliterator(), false)
                .map(DynamoPostToPostMapper::map)
                .collect(Collectors.toList());
    }

    @Override
    public Post findById(String id) {
        return DynamoPostToPostMapper.map(dynamoDBPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id)));
    }


    @Override
    public List<Post> findByAuthorId(String authorId) {
        return dynamoDBPostRepository.findByAuthorId(authorId).stream().map(DynamoPostToPostMapper::map)
                .collect(Collectors.toList());
    }

    @Override
    public Post updatePost(String id, UpdatePostDTO postDetails) {
        Optional<DynamoDBPost> post = dynamoDBPostRepository.findById(id);
        if (post.isEmpty()) {
            throw new ResourceNotFoundException("Post not found with id: " + id);
        }
        DynamoDBPost updatedPost = post.get();
        updatedPost.setEdited(true);
        updatedPost.setEditedDateTime(FORMATTER.format(LocalDateTime.now(appZoneId)));
        updatePostFields(updatedPost, postDetails);
        return DynamoPostToPostMapper.map(dynamoDBPostRepository.save(updatedPost));
    }

    @Override
    public void deletePost(String id) {
        DynamoDBPost dynamoDBPost = dynamoDBPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
        if (dynamoDBPost.getFileUrls() != null && !dynamoDBPost.getFileUrls().isEmpty()) {
            deleteFilesAssociatedWithPost(dynamoDBPost.getFileUrls());
        }
        dynamoDBPostRepository.deleteById(id);
        dynamoDBLikeRepository.deleteAllByTargetId(id);
        dynamoDBCommentRepository.deleteAllByPostId(id);
    }

    private void deleteFilesAssociatedWithPost(List<String> fileUrls) {
        List<DeleteObjectsRequest.KeyVersion> keysToDelete = fileUrls.stream()
                .map(DeleteObjectsRequest.KeyVersion::new)
                .collect(Collectors.toList());

        DeleteObjectsRequest deleteObjectsRequest = new DeleteObjectsRequest(s3ClientConfig.getBucketName())
                .withKeys(keysToDelete);

        s3ClientConfig.getS3client().deleteObjects(deleteObjectsRequest);
    }

    private void updatePostFields(DynamoDBPost post, UpdatePostDTO postDetails) {
        if (postDetails.getDescription() != null) {
            post.setDescription(postDetails.getDescription());
        }
        if (postDetails.getType() != null) {
            post.setType(postDetails.getType().name());
        }
        if (postDetails.getStatus() != null) {
            post.setStatus(postDetails.getStatus().name());
        }
        if (postDetails.getShortDescription() != null) {
            post.setShortDescription(postDetails.getShortDescription());
        }
        if (postDetails.getTitle() != null) {
            post.setTitle(postDetails.getTitle());
        }
        if (postDetails.getImage() != null) {
            post.setImage(postDetails.getImage());
        }
        if (postDetails.getHashtags() != null) {
            post.setHashtags(postDetails.getHashtags());
        }
    }
}
