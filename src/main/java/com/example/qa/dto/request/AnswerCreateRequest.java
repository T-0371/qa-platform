package com.example.qa.dto.request;

import lombok.Data;

@Data
public class AnswerCreateRequest {
    private String content;
    private Long userId;
}