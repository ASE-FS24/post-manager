package ch.nexusnet.postmanager.aws.s3.config;

import lombok.Getter;

@Getter
public enum AllowedFileType {
    JPEG("image/jpeg"),
    PNG("image/png"),
    PDF("application/pdf");

    private final String mimeType;

    AllowedFileType(String mimeType) {
        this.mimeType = mimeType;
    }
}