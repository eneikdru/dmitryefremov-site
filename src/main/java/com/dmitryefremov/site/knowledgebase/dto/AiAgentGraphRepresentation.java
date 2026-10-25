package com.dmitryefremov.site.knowledgebase.dto;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

public class AiAgentGraphRepresentation {

    private String version;
    private OffsetDateTime generatedAt;
    private Integer nodeCount;
    private Integer edgeCount;
    private List<GraphNode> nodes = new ArrayList<>();
    private List<GraphLink> edges = new ArrayList<>();

    public AiAgentGraphRepresentation() {
    }

    public AiAgentGraphRepresentation(String version, OffsetDateTime generatedAt, Integer nodeCount, Integer edgeCount, List<GraphNode> nodes, List<GraphLink> edges) {
        this.version = version;
        this.generatedAt = generatedAt;
        this.nodeCount = nodeCount;
        this.edgeCount = edgeCount;
        this.nodes = nodes != null ? nodes : new ArrayList<>();
        this.edges = edges != null ? edges : new ArrayList<>();
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public OffsetDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(OffsetDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }

    public Integer getNodeCount() {
        return nodeCount;
    }

    public void setNodeCount(Integer nodeCount) {
        this.nodeCount = nodeCount;
    }

    public Integer getEdgeCount() {
        return edgeCount;
    }

    public void setEdgeCount(Integer edgeCount) {
        this.edgeCount = edgeCount;
    }

    public List<GraphNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<GraphNode> nodes) {
        this.nodes = nodes;
    }

    public List<GraphLink> getEdges() {
        return edges;
    }

    public void setEdges(List<GraphLink> edges) {
        this.edges = edges;
    }
}
