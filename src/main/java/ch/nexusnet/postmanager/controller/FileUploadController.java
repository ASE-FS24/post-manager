package ch.nexusnet.postmanager.controller;

import ch.nexusnet.postmanager.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
}
