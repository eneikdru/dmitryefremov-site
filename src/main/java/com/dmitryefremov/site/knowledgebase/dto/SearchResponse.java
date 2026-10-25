package com.dmitryefremov.site.knowledgebase.dto;

import java.util.ArrayList;
import java.util.List;

public class SearchResponse {

    private String query;
    private Integer totalResults;
    private List<SearchResultItem> items = new ArrayList<>();

    public SearchResponse() {
    }

    public SearchResponse(String query, Integer totalResults, List<SearchResultItem> items) {
        this.query = query;
        this.totalResults = totalResults;
        this.items = items != null ? items : new ArrayList<>();
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Integer getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Integer totalResults) {
        this.totalResults = totalResults;
    }

    public List<SearchResultItem> getItems() {
        return items;
    }

    public void setItems(List<SearchResultItem> items) {
        this.items = items;
    }
}
