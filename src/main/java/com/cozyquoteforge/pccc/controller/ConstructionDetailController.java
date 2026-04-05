package com.cozyquoteforge.pccc.controller;

import com.cozyquoteforge.pccc.dto.ConstructionDetailDto;
import com.cozyquoteforge.pccc.service.ConstructionDetailService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/constructions")
@RequiredArgsConstructor
public class ConstructionDetailController {
    private final ConstructionDetailService constructionDetailService;

    @GetMapping("/{id}/details")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<ConstructionDetailDto> getConstructionDetails(@PathVariable UUID id) {
        return ResponseEntity.ok(constructionDetailService.getConstructionDetails(id));
    }

    @PutMapping("/{id}/details")
    @PreAuthorize("hasAuthority('ROLE_USER')")
    public ResponseEntity<Map<String, Object>> saveConstructionDetails(
            @PathVariable UUID id,
            @RequestBody ConstructionDetailDto dto) {
        constructionDetailService.saveConstructionDetails(id, dto);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Saved successfully");
        response.put("status", 200);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/create-details")
    public ResponseEntity<Map<String, Object>> postConstructionDetails(
            @RequestBody ConstructionDetailDto dto) {
        constructionDetailService.createConstructionDetails(dto);
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Saved successfully");
        response.put("status", 200);
        return ResponseEntity.ok(response);
    }
}
