package com.dmitryefremov.site.knowledgebase.dto;

import java.util.List;

public class CategoryList {
    private List<String> categories;

    public CategoryList() {
    }

    public CategoryList(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getCategories() { return categories; }
    public void setCategories(List<String> categories) { this.categories = categories; }
}
