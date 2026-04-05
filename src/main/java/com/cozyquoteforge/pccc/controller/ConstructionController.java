package com.cozyquoteforge.pccc.controller;

import com.cozyquoteforge.pccc.dto.ConstructionDto;
import com.cozyquoteforge.pccc.service.ConstructionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/constructions")
@RequiredArgsConstructor
public class ConstructionController {
    private final ConstructionService constructionService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<List<ConstructionDto>> getAllConstructions() {
        return ResponseEntity.ok(constructionService.getAllConstructions());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ConstructionDto> getConstructionById(@PathVariable UUID id) {
        return ResponseEntity.ok(constructionService.getConstructionById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ConstructionDto> createConstruction(@RequestBody ConstructionDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(constructionService.createConstruction(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ConstructionDto> updateConstruction(@PathVariable UUID id, @RequestBody ConstructionDto dto) {
        return ResponseEntity.ok(constructionService.updateConstruction(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Void> deleteConstruction(@PathVariable UUID id) {
        constructionService.deleteConstruction(id);
        return ResponseEntity.noContent().build();
    }
}
