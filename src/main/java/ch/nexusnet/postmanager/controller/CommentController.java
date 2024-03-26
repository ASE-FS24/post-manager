package ch.nexusnet.postmanager.controller;

import ch.nexusnet.postmanager.model.Comment;
import ch.nexusnet.postmanager.model.dto.CreateCommentDTO;
import ch.nexusnet.postmanager.model.dto.UpdateCommentDTO;
import ch.nexusnet.postmanager.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/posts/{postId}")
    public ResponseEntity<Comment> addComment(@PathVariable String postId, @RequestBody @Valid CreateCommentDTO createCommentDTO) {
        Comment comment = commentService.createComment(createCommentDTO);
        URI location = URI.create(String.format("/comments/posts/%s/%s", postId, comment.getId()));
        return ResponseEntity.created(location).body(comment);
    }

    @GetMapping("/posts/{postId}")
    public ResponseEntity<List<Comment>> getAllCommentsByPostId(@PathVariable String postId) {
        List<Comment> comments = commentService.findAllCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<Comment>> getAllCommentsByAuthorId(@PathVariable String authorId) {
        List<Comment> comments = commentService.findAllCommentsByAuthorId(authorId);
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<Comment> updateComment(@PathVariable String commentId, @RequestBody @Valid UpdateCommentDTO updateCommentDTO) {
        Comment comment = commentService.updateComment(commentId, updateCommentDTO);
        return ResponseEntity.ok(comment);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable String commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok().build();
    }

}
