package com.dmitryefremov.site.knowledgebase.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GraphNodeDetail {

    private GraphNode node;
    private String content;
    private List<GraphLink> outgoingLinks = new ArrayList<>();
    private List<GraphLink> incomingLinks = new ArrayList<>();
    private Map<String, String> machineReadableUrls = new HashMap<>();

    public GraphNodeDetail() {
    }

    public GraphNodeDetail(GraphNode node, String content, List<GraphLink> outgoingLinks, List<GraphLink> incomingLinks, Map<String, String> machineReadableUrls) {
        this.node = node;
        this.content = content;
        this.outgoingLinks = outgoingLinks != null ? outgoingLinks : new ArrayList<>();
        this.incomingLinks = incomingLinks != null ? incomingLinks : new ArrayList<>();
        this.machineReadableUrls = machineReadableUrls != null ? machineReadableUrls : new HashMap<>();
    }

    public GraphNode getNode() {
        return node;
    }

    public void setNode(GraphNode node) {
        this.node = node;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<GraphLink> getOutgoingLinks() {
        return outgoingLinks;
    }

    public void setOutgoingLinks(List<GraphLink> outgoingLinks) {
        this.outgoingLinks = outgoingLinks;
    }

    public List<GraphLink> getIncomingLinks() {
        return incomingLinks;
    }

    public void setIncomingLinks(List<GraphLink> incomingLinks) {
        this.incomingLinks = incomingLinks;
    }

    public Map<String, String> getMachineReadableUrls() {
        return machineReadableUrls;
    }

    public void setMachineReadableUrls(Map<String, String> machineReadableUrls) {
        this.machineReadableUrls = machineReadableUrls;
    }
}
