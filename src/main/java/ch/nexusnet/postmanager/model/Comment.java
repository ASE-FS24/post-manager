package ch.nexusnet.postmanager.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class Comment {

    private String id;

    private String postId;

    private String authorId;

    private String content;

    private int likeNumber;

    private LocalDateTime createdAt;

}
