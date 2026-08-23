package com.cozyquoteforge.pccc.service;

import com.cozyquoteforge.pccc.dto.ConstructionDetailDto;
import com.cozyquoteforge.pccc.entity.*;
import com.cozyquoteforge.pccc.repository.ConstructionRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ConstructionDetailService {
    private final ConstructionRepository constructionRepository;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public ConstructionDetailDto getConstructionDetails(UUID constructionId) {
        Construction construction = constructionRepository.findByIdWithDetails(constructionId)
                .orElseThrow(() -> new RuntimeException("Construction not found with id: " + constructionId));

        return toDetailDto(construction);
    }

    public ConstructionDetailDto saveConstructionDetails(UUID constructionId, ConstructionDetailDto dto) {
        Construction construction = constructionRepository.findById(constructionId)
                .orElseThrow(() -> new RuntimeException("Constructions not found with id: " + constructionId));

        applyDetails(construction, dto, true);
        return toDetailDto(constructionRepository.save(construction));
    }

    public ConstructionDetailDto createConstructionDetails(ConstructionDetailDto dto) {
        Construction construction = new Construction();
        applyDetails(construction, dto, false);
        constructionRepository.save(construction);
        return toDetailDto(construction);
    }

    private void applyDetails(Construction construction, ConstructionDetailDto dto, boolean replaceExisting) {
        construction.setName(dto.getProjectName());
        construction.setVatPercent(dto.getVatPercent());
        construction.setMaterialPercent(dto.getMaterialPercent());
        construction.setLaborPercent(dto.getLaborPercent());

        if (replaceExisting) {
            construction.getWorkshops().clear();
            construction.getSections().clear();
        }

        if (dto.getWorkshops() != null) {
            for (ConstructionDetailDto.WorkshopDto workshopDto : dto.getWorkshops()) {
                UUID workshopId = replaceExisting
                    ? (workshopDto.getId() != null
                    ? workshopDto.getId() : workshopDto.getIdWorkshops())
                    : null;
                construction.getWorkshops().add(ConstructionWorkshop.builder()
                        .id(workshopId)
                        .orderId(workshopDto.getOrderId())
                        .name(workshopDto.getName())
                        .construction(construction)
                        .build());
            }
        }

        if (dto.getSections() != null) {
            for (ConstructionDetailDto.SectionDto sectionDto : dto.getSections()) {
                UUID sectionId = sectionDto.getId() != null
                        ? sectionDto.getId() : parseUuid(sectionDto.getIdSections());
                ConstructionSection section = ConstructionSection.builder()
                        .id(sectionId != null ? sectionId : UUID.randomUUID())
                        .parentId(sectionDto.getParentId())
                        .name(sectionDto.getName())
                        .orderId(sectionDto.getOrderId())
                        .construction(construction)
                        .rows(serializeRows(sectionDto.getRows()))
                        .build();
                construction.getSections().add(section);
            }
        }
    }

    private String serializeRows(List<ConstructionDetailDto.RowDto> rows) {
        if (rows == null || rows.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(rows);
        } catch (Exception e) {
            return "[]";
        }
    }

    private UUID parseUuid(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(value);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private ConstructionDetailDto toDetailDto(Construction construction) {
        List<ConstructionDetailDto.WorkshopDto> workshopDtos = construction.getWorkshops().stream()
                .map(w -> ConstructionDetailDto.WorkshopDto.builder()
                        .id(w.getId())
                    .idWorkshops(w.getId())
                        .name(w.getName())
                        .orderId(w.getOrderId())
                        .build())
                .collect(Collectors.toList());

        List<ConstructionDetailDto.SectionDto> sectionDtos = construction.getSections().stream()
                .map(s -> {
                    List<ConstructionDetailDto.RowDto> rowDtos = new ArrayList<>();
                    
                    // Deserialize rows from JSON
                    if (s.getRows() != null && !s.getRows().isEmpty()) {
                        try {
                            rowDtos = objectMapper.readValue(s.getRows(), 
                                    objectMapper.getTypeFactory().constructCollectionType(List.class, ConstructionDetailDto.RowDto.class));
                        } catch (Exception e) {
                            rowDtos = new ArrayList<>();
                        }
                    }

                    return ConstructionDetailDto.SectionDto.builder()
                            .id(s.getId())
                            .idSections(s.getId() != null ? s.getId().toString() : null)
                            .parentId(s.getParentId())
                            .name(s.getName())
                            .orderId(s.getOrderId())
                            .rows(rowDtos)
                            .build();
                })
                .collect(Collectors.toList());

        return ConstructionDetailDto.builder()
                .id(construction.getId())
                .projectName(construction.getName())
                .vatPercent(construction.getVatPercent())
                .materialPercent(construction.getMaterialPercent())
                .laborPercent(construction.getLaborPercent())
                .workshops(workshopDtos)
                .sections(sectionDtos)
                .build();
    }
}
