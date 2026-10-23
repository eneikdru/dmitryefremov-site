package com.dmitryefremov.site.intake.dto;

import java.util.List;

public class AgentMetadataDto {

    private String agentId;
    private String agentVersion;
    private String callbackUrl;
    private List<String> capabilities;

    public AgentMetadataDto() {
    }

    public AgentMetadataDto(String agentId, String agentVersion, String callbackUrl, List<String> capabilities) {
        this.agentId = agentId;
        this.agentVersion = agentVersion;
        this.callbackUrl = callbackUrl;
        this.capabilities = capabilities;
    }

    public String getAgentId() {
        return agentId;
    }

    public void setAgentId(String agentId) {
        this.agentId = agentId;
    }

    public String getAgentVersion() {
        return agentVersion;
    }

    public void setAgentVersion(String agentVersion) {
        this.agentVersion = agentVersion;
    }

    public String getCallbackUrl() {
        return callbackUrl;
    }

    public void setCallbackUrl(String callbackUrl) {
        this.callbackUrl = callbackUrl;
    }

    public List<String> getCapabilities() {
        return capabilities;
    }

    public void setCapabilities(List<String> capabilities) {
        this.capabilities = capabilities;
    }
}
