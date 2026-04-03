package com.cozyquoteforge.pccc.controller;

import com.cozyquoteforge.pccc.dto.ConstructionDto;
import com.cozyquoteforge.pccc.service.ConstructionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/constructions")
@RequiredArgsConstructor
public class ConstructionController {
    private final ConstructionService constructionService;

    @GetMapping
    public ResponseEntity<List<ConstructionDto>> getAllConstructions() {
        return ResponseEntity.ok(constructionService.getAllConstructions());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ConstructionDto> getConstructionById(@PathVariable UUID id) {
        return ResponseEntity.ok(constructionService.getConstructionById(id));
    }

    @PostMapping
    public ResponseEntity<ConstructionDto> createConstruction(@RequestBody ConstructionDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(constructionService.createConstruction(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ConstructionDto> updateConstruction(@PathVariable UUID id, @RequestBody ConstructionDto dto) {
        return ResponseEntity.ok(constructionService.updateConstruction(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteConstruction(@PathVariable UUID id) {
        constructionService.deleteConstruction(id);
        return ResponseEntity.noContent().build();
    }
}
