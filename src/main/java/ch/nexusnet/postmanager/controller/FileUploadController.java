package ch.nexusnet.postmanager.controller;

import ch.nexusnet.postmanager.service.FileStorageService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.HandlerMapping;

import java.io.IOException;
import java.net.URI;

@RestController
@RequestMapping("/posts")
public class FileUploadController {

    private final FileStorageService fileStorageService;

    public FileUploadController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/{postId}/uploadFile")
    public ResponseEntity<URI> uploadFileToPost(@PathVariable String postId, @RequestParam("file") MultipartFile file) throws IOException {
        String fileUrl = fileStorageService.uploadFileToPost(file, postId);
        return ResponseEntity.created(URI.create(fileUrl)).build();
    }

    @DeleteMapping("/deleteFile/**")
    public ResponseEntity<?> deleteFile(HttpServletRequest request) {
        // We manually extract the filekey from the request path, because the filekey can contain slashes
        String path = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        String bestMatchPattern = (String) request.getAttribute(HandlerMapping.BEST_MATCHING_PATTERN_ATTRIBUTE);
        String fileKey = new AntPathMatcher().extractPathWithinPattern(bestMatchPattern, path);

        fileStorageService.deleteFile(fileKey);
        return ResponseEntity.ok().build();
    }
}
