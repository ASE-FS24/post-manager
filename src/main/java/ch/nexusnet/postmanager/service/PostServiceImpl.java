package ch.nexusnet.postmanager.service;

import ch.nexusnet.postmanager.aws.dynamodb.model.mapper.DynamoPostToPostMapper;
import ch.nexusnet.postmanager.aws.dynamodb.model.mapper.PostToDynamoPostMapper;
import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBPost;
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
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class PostServiceImpl implements PostService {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_DATE_TIME;
    private final DynamoDBPostRepository dynamoDBPostRepository;
    private final ZoneId appZoneId;

    public PostServiceImpl(DynamoDBPostRepository dynamoDBPostRepository, @Value("${app.timezone:CET}") ZoneId appZoneId) {
        this.dynamoDBPostRepository = dynamoDBPostRepository;
        this.appZoneId = appZoneId;
    }

    @Override
    public Post createPost(CreatePostDTO createPostDTO) {
        DynamoDBPost dynamoDBPost = PostToDynamoPostMapper.createPostMap(createPostDTO);
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
        dynamoDBPostRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
        dynamoDBPostRepository.deleteById(id);
    }


    private void updatePostFields(DynamoDBPost post, UpdatePostDTO postDetails) {
        if (postDetails.getDescription() != null) {
            post.setDescription(postDetails.getDescription());
        }
        if (postDetails.getType() != null) {
            post.setType(postDetails.getType().name());
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
