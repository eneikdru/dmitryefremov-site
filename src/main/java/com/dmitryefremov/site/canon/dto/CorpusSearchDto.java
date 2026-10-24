package com.dmitryefremov.site.canon.dto;

import java.util.ArrayList;
import java.util.List;

public class CorpusSearchDto {

    private String query;
    private int totalResults;
    private List<SearchResultItemDto> results = new ArrayList<>();

    public CorpusSearchDto() {
    }

    public CorpusSearchDto(String query, int totalResults, List<SearchResultItemDto> results) {
        this.query = query;
        this.totalResults = totalResults;
        this.results = results != null ? results : new ArrayList<>();
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public List<SearchResultItemDto> getResults() {
        return results;
    }

    public void setResults(List<SearchResultItemDto> results) {
        this.results = results;
    }
}
