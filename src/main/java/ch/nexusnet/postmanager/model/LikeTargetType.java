package ch.nexusnet.postmanager.model;

import lombok.Getter;

public enum LikeTargetType {
    POST("Posts"),
    COMMENT("Comments");

    @Getter
    final String tableName;

    LikeTargetType(String tableName) {
        this.tableName = tableName;
    }
}
