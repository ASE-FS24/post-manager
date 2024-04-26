package ch.nexusnet.postmanager.service;

import ch.nexusnet.postmanager.aws.dynamodb.model.mapper.DynamoPostToPostMapper;
import ch.nexusnet.postmanager.aws.dynamodb.model.mapper.PostToDynamoPostMapper;
import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBPost;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBCommentRepository;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBLikeRepository;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBPostRepository;
import ch.nexusnet.postmanager.exception.ResourceNotFoundException;
import ch.nexusnet.postmanager.model.Post;
import ch.nexusnet.postmanager.model.dto.CreatePostDTO;
import ch.nexusnet.postmanager.model.dto.UpdatePostDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class PostServiceImpl implements PostService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    public static final String POST_NOT_FOUND_MESSAGE = "Post not found with id: ";

    private final DynamoDBPostRepository dynamoDBPostRepository;

    private final DynamoDBLikeRepository dynamoDBLikeRepository;

    private final DynamoDBCommentRepository dynamoDBCommentRepository;
    private final ZoneId appZoneId;

    public PostServiceImpl(DynamoDBPostRepository dynamoDBPostRepository, DynamoDBLikeRepository dynamoDBLikeRepository, DynamoDBCommentRepository dynamoDBCommentRepository, @Value("${app.timezone:CET}") ZoneId appZoneId) {
        this.dynamoDBPostRepository = dynamoDBPostRepository;
        this.dynamoDBLikeRepository = dynamoDBLikeRepository;
        this.dynamoDBCommentRepository = dynamoDBCommentRepository;
        this.appZoneId = appZoneId;
    }

    /**
     * Creates a post based on the specified CreatePostDTO object.
     *
     * @param createPostDTO the data transfer object containing the post details
     * @return the created Post object
     */
    @Override
    public Post createPost(CreatePostDTO createPostDTO) {
        DynamoDBPost dynamoDBPost = PostToDynamoPostMapper.createPostMap(createPostDTO);
        dynamoDBPost.setId(IdGenerator.generatePostId());
        dynamoDBPost.setCreatedDateTime(FORMATTER.format(LocalDateTime.now(appZoneId)));
        dynamoDBPost = dynamoDBPostRepository.save(dynamoDBPost);
        return DynamoPostToPostMapper.map(dynamoDBPost);
    }

    /**
     * Finds all posts.
     *
     * @return a list of Post objects
     */
    @Override
    public List<Post> findAllPosts() {
        return dynamoDBPostRepository.findDynamoDBPostsByIdStartingWith("POST").stream()
                .map(DynamoPostToPostMapper::map)
                .toList();
    }

    /**
     * Finds a post by its ID.
     *
     * @param id the ID of the post to retrieve
     * @return the Post object with the specified ID
     * @throws ResourceNotFoundException if the post with the specified ID is not found
     */
    @Override
    public Post findById(String id) {
        return DynamoPostToPostMapper.map(dynamoDBPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(POST_NOT_FOUND_MESSAGE + id)));
    }

    /**
     * Finds all posts associated with a specific author ID.
     *
     * @param authorId the ID of the author to retrieve posts for
     * @return a list of Post objects associated with the specified author ID
     */
    @Override
    public List<Post> findByAuthorId(String authorId) {
        return dynamoDBPostRepository.findByAuthorId(authorId).stream().map(DynamoPostToPostMapper::map)
                .toList();
    }

    /**
     * Updates a post based on the specified UpdatePostDTO object.
     *
     * @param id          the ID of the post to update
     * @param postDetails the data transfer object containing the post details to update
     * @return the updated Post object
     * @throws ResourceNotFoundException if the post with the specified ID is not found
     */
    @Override
    public Post updatePost(String id, UpdatePostDTO postDetails) {
        Optional<DynamoDBPost> post = dynamoDBPostRepository.findById(id);
        if (post.isEmpty()) {
            throw new ResourceNotFoundException(POST_NOT_FOUND_MESSAGE + id);
        }
        DynamoDBPost updatedPost = post.get();
        updatedPost.setEdited(true);
        updatedPost.setEditedDateTime(FORMATTER.format(LocalDateTime.now(appZoneId)));
        updatePostFields(updatedPost, postDetails);
        return DynamoPostToPostMapper.map(dynamoDBPostRepository.save(updatedPost));
    }

    /**
     * Deletes a post with the specified ID.
     *
     * @param id the ID of the post to delete
     * @throws ResourceNotFoundException if the post with the specified ID is not found
     */
    @Override
    public void deletePost(String id) {
        dynamoDBPostRepository.findById(id)
                .ifPresentOrElse(
                        post -> dynamoDBPostRepository.deleteById(id),
                        () -> {
                            throw new ResourceNotFoundException(POST_NOT_FOUND_MESSAGE + id);
                        }
                );
        dynamoDBLikeRepository.deleteAllByTargetId(id);
        dynamoDBCommentRepository.deleteAllByPostId(id);
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
