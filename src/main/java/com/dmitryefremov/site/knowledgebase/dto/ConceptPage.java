package com.dmitryefremov.site.knowledgebase.dto;

import java.util.List;

public class ConceptPage {
    private List<ConceptItem> content;
    private Integer page;
    private Integer size;
    private Long totalElements;
    private Integer totalPages;

    public List<ConceptItem> getContent() { return content; }
    public void setContent(List<ConceptItem> content) { this.content = content; }

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }

    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }

    public Long getTotalElements() { return totalElements; }
    public void setTotalElements(Long totalElements) { this.totalElements = totalElements; }

    public Integer getTotalPages() { return totalPages; }
    public void setTotalPages(Integer totalPages) { this.totalPages = totalPages; }
}
