package ch.nexusnet.postmanager.controller;

import ch.nexusnet.postmanager.service.LikeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/likes")
public class LikeController {

    private final LikeService likeService;

    @Autowired
    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/post/{postId}")
    public ResponseEntity<?> likePost(@PathVariable String postId, @RequestParam String userId) {
        likeService.likePost(postId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/post/{postId}")
    public ResponseEntity<?> unlikePost(@PathVariable String postId, @RequestParam String userId) {
        likeService.unlikePost(postId, userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/comment/{commentId}")
    public ResponseEntity<?> likeComment(@PathVariable String commentId, @RequestParam String userId) {
        likeService.likeComment(commentId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/comment/{commentId}")
    public ResponseEntity<?> unlikeComment(@PathVariable String commentId, @RequestParam String userId) {
        likeService.unlikeComment(commentId, userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user/{userId}/posts")
    public ResponseEntity<?> getLikedPosts(@PathVariable String userId) {
        return ResponseEntity.ok(likeService.getLikedPostsByUserSortedByRecency(userId));
    }

    @GetMapping("/post/{postId}/hasLiked")
    public ResponseEntity<Boolean> hasUserLikedPost(@PathVariable String postId, @RequestParam String userId) {
        boolean hasLiked = likeService.checkUserLikeStatusForPost(postId, userId);
        return ResponseEntity.ok(hasLiked);
    }

    @GetMapping("/comment/{commentId}/hasLiked")
    public ResponseEntity<Boolean> hasUserLikedComment(@PathVariable String commentId, @RequestParam String userId) {
        boolean hasLiked = likeService.checkUserLikeStatusForComment(commentId, userId);
        return ResponseEntity.ok(hasLiked);
    }
}
