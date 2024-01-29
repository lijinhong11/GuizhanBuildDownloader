package me.mmmjjkx.objects;

import lombok.Getter;

@Getter
public class BuildInfo {
    private int id;
    private String commit;
    private String author;
    private int timestamp;
    private String message;
    private boolean success;
    private int buildTimestamp;
    private String target;
    private String sha1;
}
