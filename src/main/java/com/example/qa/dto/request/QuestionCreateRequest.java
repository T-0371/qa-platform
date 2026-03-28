package com.example.qa.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class QuestionCreateRequest {
    private String title;
    private String content;
    private List<Long> tagIds;
    private Long userId;
    private String tags;
    private String status;
}