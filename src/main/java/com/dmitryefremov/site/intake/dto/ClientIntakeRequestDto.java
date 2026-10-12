package com.dmitryefremov.site.intake.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class ClientIntakeRequestDto {

    @NotBlank(message = "clientName must not be blank")
    private String clientName;

    @NotBlank(message = "clientEmail must not be blank")
    @Email(message = "clientEmail must be a well-formed email address")
    private String clientEmail;

    private String companyName;

    @NotBlank(message = "wishlistPayload must not be blank")
    private String wishlistPayload;

    public ClientIntakeRequestDto() {
    }

    public ClientIntakeRequestDto(String clientName, String clientEmail, String companyName, String wishlistPayload) {
        this.clientName = clientName;
        this.clientEmail = clientEmail;
        this.companyName = companyName;
        this.wishlistPayload = wishlistPayload;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getClientEmail() {
        return clientEmail;
    }

    public void setClientEmail(String clientEmail) {
        this.clientEmail = clientEmail;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getWishlistPayload() {
        return wishlistPayload;
    }

    public void setWishlistPayload(String wishlistPayload) {
        this.wishlistPayload = wishlistPayload;
    }
}
