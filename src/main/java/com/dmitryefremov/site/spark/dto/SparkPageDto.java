package com.dmitryefremov.site.spark.dto;

import java.util.List;

public class SparkPageDto {

    private List<SparkDto> items;
    private int currentPage;
    private int totalPages;
    private long totalElements;

    public SparkPageDto() {
    }

    public SparkPageDto(List<SparkDto> items, int currentPage, int totalPages, long totalElements) {
        this.items = items;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
    }

    public List<SparkDto> getItems() {
        return items;
    }

    public void setItems(List<SparkDto> items) {
        this.items = items;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(long totalElements) {
        this.totalElements = totalElements;
    }
}
