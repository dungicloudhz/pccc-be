package com.cozyquoteforge.pccc.service;

import com.cozyquoteforge.pccc.dto.ConstructionDto;
import com.cozyquoteforge.pccc.entity.Construction;
import com.cozyquoteforge.pccc.repository.ConstructionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ConstructionService {
    private final ConstructionRepository constructionRepository;

    public List<ConstructionDto> getAllConstructions() {
        return constructionRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    public ConstructionDto getConstructionById(UUID id) {
        return constructionRepository.findById(id)
                .map(this::toDto)
                .orElseThrow(() -> new RuntimeException("Construction not found with id: " + id));
    }

    public ConstructionDto createConstruction(ConstructionDto dto) {
        Construction construction = Construction.builder()
                .name(dto.getName())
                .build();
        Construction saved = constructionRepository.save(construction);
        return toDto(saved);
    }

    public ConstructionDto updateConstruction(UUID id, ConstructionDto dto) {
        Construction construction = constructionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Construction not found with id: " + id));
        construction.setName(dto.getName());
        Construction updated = constructionRepository.save(construction);
        return toDto(updated);
    }

    public void deleteConstruction(UUID id) {
        constructionRepository.deleteById(id);
    }

    private ConstructionDto toDto(Construction construction) {
        return ConstructionDto.builder()
                .id(construction.getId())
                .name(construction.getName())
                .createdAt(construction.getCreatedAt())
                .updatedAt(construction.getUpdatedAt())
                .build();
    }
}
