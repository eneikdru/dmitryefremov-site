package com.dmitryefremov.site.telemetry.dto;

public class ScrollytellingIngestionRequest {

    private String resourceId;
    private double depth;
    private String source;

    public ScrollytellingIngestionRequest() {
    }

    public ScrollytellingIngestionRequest(String resourceId, double depth, String source) {
        this.resourceId = resourceId;
        this.depth = depth;
        this.source = source;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public double getDepth() {
        return depth;
    }

    public void setDepth(double depth) {
        this.depth = depth;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
