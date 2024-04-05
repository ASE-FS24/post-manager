package ch.nexusnet.postmanager.controller;

import ch.nexusnet.postmanager.aws.dynamodb.model.table.DynamoDBPost;
import ch.nexusnet.postmanager.aws.dynamodb.repositories.DynamoDBPostRepository;
import ch.nexusnet.postmanager.model.PostStatus;
import ch.nexusnet.postmanager.model.PostType;
import ch.nexusnet.postmanager.util.IdGenerator;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Paths;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// This test is disabled since it uploads a file but does not delete it afterwards
// TODO: Implement a cleanup method to delete the uploaded file
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Disabled
public class FileUploadIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DynamoDBPostRepository dynamoDBPostRepository;

    private String postId;

    @BeforeEach
    void setUp() {
        DynamoDBPost dynamoDBPost = new DynamoDBPost();
        dynamoDBPost.setId(IdGenerator.generatePostId());
        dynamoDBPost.setAuthorId("123");
        dynamoDBPost.setLikeNumber(1);
        dynamoDBPost.setType(PostType.PROJECT.name());
        dynamoDBPost.setStatus(PostStatus.NEW.name());
        dynamoDBPost = dynamoDBPostRepository.save(dynamoDBPost);
        postId = dynamoDBPost.getId();
    }

    @AfterEach
    void tearDown() {
        dynamoDBPostRepository.deleteById(postId);
    }

    @Test
    public void testFileUpload() throws Exception {
        byte[] content = Files.readAllBytes(Paths.get("src/test/java/ch/nexusnet/postmanager/files/478px-American_Beaver-1649739069.jpg"));
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", content);

        mockMvc.perform(multipart("/posts/{postId}/uploadFile", postId)
                        .file(file)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isCreated());

    }

}
