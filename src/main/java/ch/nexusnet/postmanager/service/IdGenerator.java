package ch.nexusnet.postmanager.service;

import java.util.UUID;

public class IdGenerator {

    private IdGenerator() {
        // Private constructor to prevent instantiation
    }

    public static String generatePostId() {
        return "POST#" + UUID.randomUUID();
    }

    public static String generateCommentId() {
        return "COMMENT#" + UUID.randomUUID();
    }

    public static String generateLikeId() {
        return "LIKE#" + UUID.randomUUID();
    }
}
