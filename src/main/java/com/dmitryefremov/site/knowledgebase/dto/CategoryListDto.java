package com.dmitryefremov.site.knowledgebase.dto;

import java.util.List;

public class CategoryListDto {
    private List<String> categories;

    public CategoryListDto() {}

    public CategoryListDto(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getCategories() { return categories; }
    public void setCategories(List<String> categories) { this.categories = categories; }
}
