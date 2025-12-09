package com.example.urooz.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor

@Document(collection = "judgments")
public class JudgmentMetadata {

    @Id
    private String id;

    private String title;
    private String court;
    private String date;

    private String facts;
    private List<String> issues;
    private Map<String, Object> arguments;
    private String ratio;
    private String holding;
    private List<String> citations;

    private String originalText;
}