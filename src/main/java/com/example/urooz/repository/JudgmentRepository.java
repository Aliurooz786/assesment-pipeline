package com.example.urooz.repository;

import com.example.urooz.model.JudgmentMetadata;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JudgmentRepository extends MongoRepository<JudgmentMetadata, String> {
    List<JudgmentMetadata> findByTitleContainingIgnoreCase(String title);
}