package ch.nexusnet.postmanager;

import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBPostRepository;
import ch.nexusnet.postmanager.aws.s3.config.S3ClientConfiguration;
import ch.nexusnet.postmanager.model.PostStatus;
import ch.nexusnet.postmanager.model.PostType;
import ch.nexusnet.postmanager.model.dto.CreatePostDTO;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.testcontainers.shaded.org.hamcrest.MatcherAssert.assertThat;
import static org.testcontainers.shaded.org.hamcrest.Matchers.containsString;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class SystemTests {

    private static final String AUTHOR_ID = UUID.randomUUID().toString();
    private static final String TITLE = "Initial Post Title";
    private static final String SHORT_DESCRIPTION = "Initial Short Description";
    private static final String DESCRIPTION = "Initial Post Description. Lorem ipsum dolor sit amet.";
    private static final String IMAGE = "Image";
    private static final List<String> HASHTAGS = Arrays.asList("Project", "UZH");
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private DynamoDBPostRepository dynamoDBPostRepository;
    @Autowired
    private S3ClientConfiguration s3ClientConfig;
    @Autowired
    private ObjectMapper objectMapper;
    @Value("${postmanager.aws.s3.bucket}")
    private String bucketName;
    private String fileKey;

    private String toJson(Object obj) throws Exception {
        return objectMapper.writeValueAsString(obj);
    }

    @Test
    public void testFileLifeTime() throws Exception {
        CreatePostDTO createPostDTO = new CreatePostDTO();
        createPostDTO.setAuthorId(AUTHOR_ID);
        createPostDTO.setType(PostType.PROJECT);
        createPostDTO.setStatus(PostStatus.NEW);
        createPostDTO.setImage(IMAGE);
        createPostDTO.setShortDescription(SHORT_DESCRIPTION);
        createPostDTO.setDescription(DESCRIPTION);
        createPostDTO.setTitle(TITLE);
        createPostDTO.setHashtags(HASHTAGS);

        String responseBody = mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(createPostDTO)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.authorId").value(AUTHOR_ID))
                .andExpect(jsonPath("$.type").value(PostType.PROJECT.name()))
                .andExpect(jsonPath("$.status").value(PostStatus.NEW.name()))
                .andExpect(jsonPath("$.image").value(IMAGE))
                .andExpect(jsonPath("$.shortDescription").value(SHORT_DESCRIPTION))
                .andExpect(jsonPath("$.description").value(DESCRIPTION))
                .andExpect(jsonPath("$.title").value(TITLE))
                .andExpect(jsonPath("$.edited").value(false))
                .andExpect(jsonPath("$.editedDateTime").isEmpty())
                .andExpect(jsonPath("$.createdDateTime").exists())
                .andExpect(jsonPath("$.likeNumber").value(0))
                .andExpect(jsonPath("$.hashtags[0]").value(HASHTAGS.get(0)))
                .andExpect(jsonPath("$.hashtags[1]").value(HASHTAGS.get(1)))
                .andReturn().getResponse().getContentAsString();

        String createdPostId = JsonPath.parse(responseBody).read("$.id", String.class);

        byte[] content = Files.readAllBytes(Paths.get("src/test/java/ch/nexusnet/postmanager/files/478px-American_Beaver-1649739069.jpg"));
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", content);

        fileKey = mockMvc.perform(multipart("/posts/{postId}/uploadFile", createdPostId)
                        .file(file)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isCreated()).andReturn().getResponse().getHeader("Location");

        assertThat(fileKey, containsString(createdPostId));

        mockMvc.perform(delete("/posts/{id}", createdPostId))
                .andExpect(status().isOk());

        assertThrows(AmazonS3Exception.class, () -> s3ClientConfig.getS3client().getObject(bucketName, fileKey));
        assertEquals(Optional.empty(), dynamoDBPostRepository.findById(createdPostId));
    }

}
