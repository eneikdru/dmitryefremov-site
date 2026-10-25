package com.dmitryefremov.site.knowledgebase.dto;

import java.util.List;

public class TagListDto {
    private List<String> tags;

    public TagListDto() {}

    public TagListDto(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}
