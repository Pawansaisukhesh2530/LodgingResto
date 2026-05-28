package com.example.lodgingresto.dto;

public class AiResponseDto {

    private String response;
    private String category;

    public AiResponseDto() {
    }

    public AiResponseDto(String response, String category) {
        this.response = response;
        this.category = category;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
