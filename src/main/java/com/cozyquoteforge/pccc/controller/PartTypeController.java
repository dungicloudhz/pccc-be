package com.cozyquoteforge.pccc.controller;

import com.cozyquoteforge.pccc.dto.PartTypeDto;
import com.cozyquoteforge.pccc.service.PartTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/part-types")
@RequiredArgsConstructor
public class PartTypeController {
    private final PartTypeService partTypeService;

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<List<PartTypeDto>> getAllPartTypes() {
        return ResponseEntity.ok(partTypeService.getAllPartTypes());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<PartTypeDto> getPartTypeById(@PathVariable UUID id) {
        return ResponseEntity.ok(partTypeService.getPartTypeById(id));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<PartTypeDto> createPartType(@RequestBody PartTypeDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(partTypeService.createPartType(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<PartTypeDto> updatePartType(@PathVariable UUID id, @RequestBody PartTypeDto dto) {
        return ResponseEntity.ok(partTypeService.updatePartType(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Void> deletePartType(@PathVariable UUID id) {
        partTypeService.deletePartType(id);
        return ResponseEntity.noContent().build();
    }
}
