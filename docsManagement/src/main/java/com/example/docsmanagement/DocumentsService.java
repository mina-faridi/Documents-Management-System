package com.example.docsmanagement;

import com.example.docsmanagement.dto.DocumentRequest;
import com.example.docsmanagement.dto.DocumentResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocumentsService {

    private final DocumentRepository documentRepository;
    private final TagRepository tagRepository;

    public DocumentsService(DocumentRepository documentRepository, TagRepository tagRepository) {
        this.documentRepository = documentRepository;
        this.tagRepository = tagRepository;
    }

    @Transactional
    public Long createDocument(DocumentRequest request) {

        // Prepare list of Tag entities
        List<Tag> tagEntities = new ArrayList<>();

        for (String t : request.getTags()) {
            String tagName = t.trim();

            Tag tag = tagRepository
                    .findByName(tagName)
                    .orElseGet(() -> {
                        Tag newTag = new Tag();
                        newTag.setName(tagName);
                        return tagRepository.save(newTag);
                    });
            tagEntities.add(tag);
        }

        // Create Document
        Document doc = new Document();
        doc.setTitle(request.getTitle());
        doc.setContent(request.getContent());
        doc.setTags(tagEntities);
        doc.setCreatedAt(LocalDateTime.now());

        Document saved = documentRepository.save(doc);

        return saved.getId();
    }


    //get all docs
    public List<DocumentResponse> getAllDocuments() {
        return documentRepository.findAll().stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<DocumentResponse> advancedSearch(String query, String mode) {
        switch (mode.toLowerCase()) {
            case "title":
                return documentRepository.findByTitleContainingIgnoreCase(query)
                        .stream()
                        .map(this::convertToResponse)
                        .collect(Collectors.toList());

            case "content":
                return documentRepository.findByContentContainingIgnoreCase(query)
                        .stream()
                        .map(this::convertToResponse)
                        .collect(Collectors.toList());
            case "tag":
                Optional<Tag> tag = tagRepository.findByNameContainingIgnoreCase(query);
                return tag
                        .map(t -> t.getDocuments().stream()
                                .map(this::convertToResponse)
                                .collect(Collectors.toList()))
                        .orElseGet(ArrayList::new);
            case "all":
                return documentRepository.searchAll(query)
                        .stream()
                        .map(this::convertToResponse)
                        .collect(Collectors.toList());
            default:
                throw new IllegalArgumentException("Invalid search mode: " + mode);
        }
    }

    private DocumentResponse convertToResponse(Document document) {
        DocumentResponse dto = new DocumentResponse();
        dto.setId(document.getId());
        dto.setTitle(document.getTitle());
        dto.setContent(document.getContent());
        dto.setCreatedAt(document.getCreatedAt());
        dto.setTags(
                document.getTags()
                        .stream()
                        .map(Tag::getName)
                        .collect(Collectors.toList())
        );
        return dto;
    }
}