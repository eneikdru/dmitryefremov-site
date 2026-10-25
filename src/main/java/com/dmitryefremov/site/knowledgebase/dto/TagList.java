package com.dmitryefremov.site.knowledgebase.dto;

import java.util.List;

public class TagList {
    private List<String> tags;

    public TagList() {
    }

    public TagList(List<String> tags) {
        this.tags = tags;
    }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
}
