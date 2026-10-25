package com.dmitryefremov.site.knowledgebase.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AppliedModelCreateRequestDto {
    @NotBlank
    @Size(min = 1, max = 255)
    private String modelName;

    @Size(max = 50)
    private String version = "1.0.0";

    @NotBlank
    private String configurationJson;

    private Boolean isActive = true;

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }
    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }
    public String getConfigurationJson() { return configurationJson; }
    public void setConfigurationJson(String configurationJson) { this.configurationJson = configurationJson; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
