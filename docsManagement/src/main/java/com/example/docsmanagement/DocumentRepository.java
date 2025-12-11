package com.example.docsmanagement;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {


    List<Document> findByTitleContainingIgnoreCase(String query);

    List<Document> findByContentContainingIgnoreCase(String query);

    @Query("""
        SELECT DISTINCT d FROM Document d
        LEFT JOIN d.tags t
        WHERE LOWER(d.title) LIKE LOWER(CONCAT('%', :query, '%'))
        OR LOWER(d.content) LIKE LOWER(CONCAT('%', :query, '%'))
        OR LOWER(t.name) LIKE LOWER(CONCAT('%', :query, '%'))
    """)
    List<Document> searchAll(String query);
}

