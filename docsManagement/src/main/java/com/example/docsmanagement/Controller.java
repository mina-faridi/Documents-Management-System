package com.example.docsmanagement;

import com.example.docsmanagement.dto.DocumentRequest;
import com.example.docsmanagement.dto.DocumentResponse;
import com.example.docsmanagement.dto.TagResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/documents")
public class Controller {

    private final DocumentsService documentsService;

    public Controller(DocumentsService documentsService) {
        this.documentsService = documentsService;
    }

    @PostMapping
    public ResponseEntity<Long> createDocument(@RequestBody DocumentRequest request) {
        Long id = documentsService.createDocument(request);
        return ResponseEntity.ok(id);
    }

    @GetMapping
    public ResponseEntity<List<DocumentResponse>> getAllDocuments() {
        return ResponseEntity.ok(documentsService.getAllDocuments());
    }

    @GetMapping("/search")
    public ResponseEntity<List<DocumentResponse>> searchDocuments(
            @RequestParam String query,
            @RequestParam(defaultValue = "all") String mode
    ) {
        return ResponseEntity.ok(documentsService.advancedSearch(query, mode));
    }
}
