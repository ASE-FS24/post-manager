package ch.nexusnet.postmanager.controller;

import ch.nexusnet.postmanager.model.Post;
import ch.nexusnet.postmanager.model.dto.CreatePostDTO;
import ch.nexusnet.postmanager.model.dto.UpdatePostDTO;
import ch.nexusnet.postmanager.service.PostService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody @Valid CreatePostDTO createPostDTO) {
        Post post = postService.createPost(createPostDTO);
        URI location = URI.create("/posts/" + post.getId());
        return ResponseEntity.created(location).body(post);
    }

    @GetMapping
    public ResponseEntity<List<Post>> getAllPosts() {
        return ResponseEntity.ok(postService.findAllPosts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable String id) {
        return ResponseEntity.ok(postService.findById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Post>> getPostsByUserId(@PathVariable String userId) {
        List<Post> posts = postService.findByUserId(userId);
        return ResponseEntity.ok(posts);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Post> updatePost(@PathVariable String id, @RequestBody UpdatePostDTO post) {
        return ResponseEntity.ok(postService.updatePost(id, post));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePost(@PathVariable String id) {
        postService.deletePost(id);
        return ResponseEntity.ok().build();
    }

}
