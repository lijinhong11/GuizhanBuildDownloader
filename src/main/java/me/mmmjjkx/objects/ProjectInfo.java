package me.mmmjjkx.objects;

import lombok.Getter;

@Getter
public class ProjectInfo {
    private String key;
    private String author;
    private String repository;
    private String branch;
    private String type;
    private String[] alias;
}
